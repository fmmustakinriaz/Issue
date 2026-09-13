package com.university.issuetracker.dao;

import com.university.issuetracker.model.Comment;
import com.university.issuetracker.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {

    public List<Comment> findByIssueId(int issueId) throws SQLException {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name AS user_name FROM comments c " +
                     "LEFT JOIN users u ON c.user_id = u.id " +
                     "WHERE c.issue_id = ? ORDER BY c.created_at ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, issueId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public void create(int issueId, int userId, String body) throws SQLException {
        String sql = "INSERT INTO comments (issue_id, user_id, body) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, issueId);
            ps.setInt(2, userId);
            ps.setString(3, body);
            ps.executeUpdate();
        }
    }

    private Comment mapRow(ResultSet rs) throws SQLException {
        Comment c = new Comment();
        c.setId(rs.getInt("id"));
        c.setIssueId(rs.getInt("issue_id"));
        c.setUserId(rs.getInt("user_id"));
        c.setUserName(rs.getString("user_name"));
        c.setBody(rs.getString("body"));
        c.setCreatedAt(rs.getString("created_at"));
        return c;
    }
}
