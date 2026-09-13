package com.university.issuetracker.model;

public class Comment {
    private int id;
    private int issueId;
    private int userId;
    private String userName;
    private String body;
    private String createdAt;

    public Comment() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIssueId() { return issueId; }
    public void setIssueId(int issueId) { this.issueId = issueId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
