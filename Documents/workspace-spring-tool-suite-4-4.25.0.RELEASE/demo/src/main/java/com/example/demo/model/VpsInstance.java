package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class VpsInstance {
    public VpsInstance() {
		super();
		// TODO Auto-generated constructor stub
	}
	public VpsInstance(Long id, User user, String vmId, String ipAddress, String password, int cpu, int ramMb,
			int diskGb) {
		super();
		this.id = id;
		this.user = user;
		this.vmId = vmId;
		this.ipAddress = ipAddress;
		this.password = password;
		this.cpu = cpu;
		this.ramMb = ramMb;
		this.diskGb = diskGb;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getVmId() {
		return vmId;
	}
	public void setVmId(String vmId) {
		this.vmId = vmId;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getCpu() {
		return cpu;
	}
	public void setCpu(int cpu) {
		this.cpu = cpu;
	}
	public int getRamMb() {
		return ramMb;
	}
	public void setRamMb(int ramMb) {
		this.ramMb = ramMb;
	}
	public int getDiskGb() {
		return diskGb;
	}
	public void setDiskGb(int diskGb) {
		this.diskGb = diskGb;
	}
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    private String vmId; // MicroStack VM ID
    private String ipAddress;
    private String password;
    private int cpu;
    private int ramMb;
    private int diskGb;

    // Getters, setters, constructors
}