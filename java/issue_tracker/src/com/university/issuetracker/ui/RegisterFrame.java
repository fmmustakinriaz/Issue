package com.university.issuetracker.ui;

import com.university.issuetracker.dao.UserDAO;
import com.university.issuetracker.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterFrame extends JFrame {
    private JTextField nameField, emailField, deptField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private UserDAO userDAO;

    public RegisterFrame() {
        userDAO = new UserDAO();
        initComponents();
    }

    private void initComponents() {
        setTitle("Create Account - UniTrack");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 420);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(248, 250, 252));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 20, 6, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(37, 99, 235));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        addLabel(panel, "Full Name:", gbc, 1);
        nameField = new JTextField(20);
        addField(panel, nameField, gbc, 1);

        addLabel(panel, "Email:", gbc, 2);
        emailField = new JTextField(20);
        addField(panel, emailField, gbc, 2);

        addLabel(panel, "Password:", gbc, 3);
        passwordField = new JPasswordField(20);
        addField(panel, passwordField, gbc, 3);

        addLabel(panel, "Role:", gbc, 4);
        roleCombo = new JComboBox<>(new String[]{"student", "staff", "admin"});
        addField(panel, roleCombo, gbc, 4);

        addLabel(panel, "Department:", gbc, 5);
        deptField = new JTextField(20);
        addField(panel, deptField, gbc, 5);

        JButton registerBtn = new JButton("Create Account");
        registerBtn.setBackground(new Color(37, 99, 235));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(registerBtn, gbc);

        JButton backBtn = new JButton("Back to Login");
        backBtn.setBackground(new Color(241, 245, 249));
        backBtn.setForeground(new Color(71, 85, 105));
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 7;
        panel.add(backBtn, gbc);

        registerBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { handleRegister(); }
        });

        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        getRootPane().setDefaultButton(registerBtn);
        add(panel);
    }

    private void addLabel(JPanel p, String text, GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = row;
        p.add(label, gbc);
    }

    private void addField(JPanel p, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 1; gbc.gridy = row;
        p.add(field, gbc);
    }

    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = (String) roleCombo.getSelectedItem();
        String dept = deptField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            User user = new User();
            user.setFullName(name);
            user.setEmail(email);
            user.setRole(role);
            user.setDepartment(dept.isEmpty() ? null : dept);
            boolean success = userDAO.register(user, password);
            if (success) {
                JOptionPane.showMessageDialog(this, "Account created successfully! You can now sign in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new LoginFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Email already registered.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
