package com.university.issuetracker.ui;

import com.university.issuetracker.dao.UserDAO;
import com.university.issuetracker.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ManageUsersPanel extends JPanel {
    private UserDAO userDAO;
    private JTable table;
    private DefaultTableModel tableModel;

    public ManageUsersPanel() {
        userDAO = new UserDAO();
        initComponents();
        refresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(248, 250, 252));

        JLabel titleLabel = new JLabel("Manage Users");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(30, 41, 59));
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Email", "Department", "Role", "Joined"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(new Color(248, 250, 252));

        bottomPanel.add(new JLabel("Change role to:"));
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"student", "staff", "admin"});
        bottomPanel.add(roleCombo);

        JButton updateBtn = new JButton("Update Role");
        updateBtn.setBackground(new Color(37, 99, 235));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFocusPainted(false);
        updateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bottomPanel.add(updateBtn);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(241, 245, 249));
        refreshBtn.setForeground(new Color(71, 85, 105));
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bottomPanel.add(refreshBtn);

        updateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(ManageUsersPanel.this, "Select a user first.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                int userId = (int) tableModel.getValueAt(row, 0);
                String newRole = (String) roleCombo.getSelectedItem();
                try {
                    userDAO.updateRole(userId, newRole);
                    JOptionPane.showMessageDialog(ManageUsersPanel.this, "Role updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refresh();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ManageUsersPanel.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        refreshBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { refresh(); }
        });

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void refresh() {
        try {
            List<User> users = userDAO.findAll();
            tableModel.setRowCount(0);
            for (User u : users) {
                tableModel.addRow(new Object[]{
                    u.getId(),
                    u.getFullName(),
                    u.getEmail(),
                    u.getDepartment() != null ? u.getDepartment() : "—",
                    u.getRole(),
                    u.getCreatedAt() != null ? u.getCreatedAt().substring(0, Math.min(10, u.getCreatedAt().length())) : "—"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
