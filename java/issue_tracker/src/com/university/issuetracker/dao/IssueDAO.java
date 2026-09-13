package com.university.issuetracker.dao;

import com.university.issuetracker.model.Issue;
import com.university.issuetracker.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IssueDAO {

    private static final String SELECT_WITH_JOINS =
        "SELECT i.*, c.name AS category_name, " +
        "u.full_name AS created_by_name, a.full_name AS assigned_to_name " +
        "FROM issues i " +
        "LEFT JOIN categories c ON i.category_id = c.id " +
        "LEFT JOIN users u ON i.created_by = u.id " +
        "LEFT JOIN users a ON i.assigned_to = a.id ";

    public List<Issue> findAll() throws SQLException {
        List<Issue> list = new ArrayList<>();
        String sql = SELECT_WITH_JOINS + "ORDER BY i.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Issue> findByCreatedBy(int userId) throws SQLException {
        List<Issue> list = new ArrayList<>();
        String sql = SELECT_WITH_JOINS + "WHERE i.created_by = ? ORDER BY i.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Issue> findByAssignedTo(int userId) throws SQLException {
        List<Issue> list = new ArrayList<>();
        String sql = SELECT_WITH_JOINS + "WHERE i.assigned_to = ? ORDER BY i.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Issue findById(int id) throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE i.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    public void create(Issue issue) throws SQLException {
        String sql = "INSERT INTO issues (title, description, category_id, priority, status, created_by) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, issue.getTitle());
            ps.setString(2, issue.getDescription());
            if (issue.getCategoryId() > 0) ps.setInt(3, issue.getCategoryId()); else ps.setNull(3, Types.INTEGER);
            ps.setString(4, issue.getPriority());
            ps.setString(5, "open");
            ps.setInt(6, issue.getCreatedBy());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) issue.setId(keys.getInt(1));
        }
    }

    public void updateStatus(int id, String status) throws SQLException {
        String sql = "UPDATE issues SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void updateAssignment(int id, Integer assignedTo) throws SQLException {
        String sql = "UPDATE issues SET assigned_to = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (assignedTo != null && assignedTo > 0) ps.setInt(1, assignedTo); else ps.setNull(1, Types.INTEGER);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void updatePriority(int id, String priority) throws SQLException {
        String sql = "UPDATE issues SET priority = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, priority);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void updateCategory(int id, int categoryId) throws SQLException {
        String sql = "UPDATE issues SET category_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (categoryId > 0) ps.setInt(1, categoryId); else ps.setNull(1, Types.INTEGER);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM issues WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public int countByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM issues WHERE status = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM issues";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int countByPriority(String priority) throws SQLException {
        String sql = "SELECT COUNT(*) FROM issues WHERE priority = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, priority);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private Issue mapRow(ResultSet rs) throws SQLException {
        Issue i = new Issue();
        i.setId(rs.getInt("id"));
        i.setTitle(rs.getString("title"));
        i.setDescription(rs.getString("description"));
        i.setCategoryId(rs.getInt("category_id"));
        i.setCategoryName(rs.getString("category_name"));
        i.setPriority(rs.getString("priority"));
        i.setStatus(rs.getString("status"));
        i.setCreatedBy(rs.getInt("created_by"));
        i.setCreatedByName(rs.getString("created_by_name"));
        int assignedId = rs.getInt("assigned_to");
        i.setAssignedTo(rs.wasNull() ? null : assignedId);
        i.setAssignedToName(rs.getString("assigned_to_name"));
        i.setCreatedAt(rs.getString("created_at"));
        i.setUpdatedAt(rs.getString("updated_at"));
        return i;
    }
}
