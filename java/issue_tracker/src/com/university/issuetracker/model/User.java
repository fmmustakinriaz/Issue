package com.university.issuetracker.model;

public class User {
    private int id;
    private String fullName;
    private String email;
    private String password;
    private String role;
    private String department;
    private String createdAt;

    public User() {}

    public User(int id, String fullName, String email, String role, String department) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.department = department;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
