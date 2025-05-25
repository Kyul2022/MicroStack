package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.User;
import com.example.demo.service.VpsRentalService;


@Controller
public class VpsController {
    @Autowired private VpsRentalService rentalService;

    @GetMapping("/")
    public String home() {
        return "index"; // Thymeleaf template
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String email, Model model) {
        User user = rentalService.registerUser(username, email, "123456789");
        model.addAttribute("userId", user.getId());
        return "vps-form";
    }

    @PostMapping("/rent")
    public String rent(@RequestParam Long userId, @RequestParam int cpu,
                      @RequestParam int ramMb, @RequestParam int diskGb, Model model) {
        rentalService.rentVps(userId, cpu, ramMb, diskGb);
        model.addAttribute("message", "VPS created! Check your email.");
        return "success";
    }
}