package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vps_user") // Explicitly name the table to avoid 'user' keyword
public class User {
	
    public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	public User(Long id, String username, String email, String microstackUserId, String subnetId, String password, String projectId) {
		super();
		this.id = id;
		this.username = username;
		this.email = email;
		this.microstackUserId = microstackUserId;
		this.subnetId = subnetId;
		this.password = password;
		this.projectId = projectId;

	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMicrostackUserId() {
		return microstackUserId;
	}
	public void setMicrostackUserId(String microstackUserId) {
		this.microstackUserId = microstackUserId;
	}
	public String getSubnetId() {
		return subnetId;
	}
	public void setSubnetId(String subnetId) {
		this.subnetId = subnetId;
	}
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;
    private String projectId;
    public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	private String microstackUserId; // ID in MicroStack
    private String subnetId; // Dedicated subnet for this user

    // Getters, setters, constructors
}