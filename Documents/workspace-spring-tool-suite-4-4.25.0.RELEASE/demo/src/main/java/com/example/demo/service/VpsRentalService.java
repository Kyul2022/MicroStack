package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.User;
import com.example.demo.model.VpsInstance;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VpsInstanceRepository;

@Service
public class VpsRentalService {
    @Autowired private UserRepository userRepo;
    @Autowired private VpsInstanceRepository vpsRepo;
    @Autowired private MicroStackService microStackService;
    @Autowired private PaymentService paymentService;
    @Autowired private EmailService emailService;

    public User registerUser(String username, String email, String mdp) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(mdp);
        user.setEmail(email);
        String microstackUserId = microStackService.createUser(username, mdp);
        user.setMicrostackUserId(microstackUserId);
     // Step 2: Create a project for the user
        String projectId = microStackService.createProject(username);
        user.setProjectId(projectId);
        microStackService.assignUserToProject(microstackUserId, projectId); // Link user to project
        // Step 3: Create a subnet using the user's scoped token
        String subnetId = microStackService.createSubnet(username, mdp, projectId);
        user.setSubnetId(subnetId);
        return userRepo.save(user);
    }  

    public void rentVps(Long userId, int cpu, int ramMb, int diskGb) {
        User user = userRepo.findByUsername("dado");
        double fakeAmount = cpu * 5 + ramMb / 1024 * 10 + diskGb * 2; // Dummy pricing
        if (paymentService.processPayment(fakeAmount)) {
            String vmDetails = microStackService.createVm(user.getUsername(),user.getPassword(), user.getProjectId(), user.getSubnetId(), cpu, ramMb, diskGb);
            VpsInstance vps = new VpsInstance();
            vps.setUser(user);
            //vps.setVmId(microStackService.extractVmId(vmDetails));
            //vps.setIpAddress(microStackService.extractIp(vmDetails));
            //vps.setPassword(microStackService.extractPassword(vmDetails));
            vps.setCpu(cpu);
            vps.setRamMb(ramMb);
            vps.setDiskGb(diskGb);
            vpsRepo.save(vps);
            //emailService.sendVpsDetails(user.getEmail(), vps.getIpAddress(), vps.getPassword());
        }
    }
}