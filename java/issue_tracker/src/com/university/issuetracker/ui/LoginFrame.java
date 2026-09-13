package com.university.issuetracker.ui;

import com.university.issuetracker.dao.UserDAO;
import com.university.issuetracker.model.User;
import com.university.issuetracker.util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private UserDAO userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();
        initComponents();
    }

    private void initComponents() {
        setTitle("UniTrack - Department Issue Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(248, 250, 252));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 20, 8, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("UniTrack");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(37, 99, 235));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Department Issue Tracking System");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(100, 116, 139));
        gbc.gridy = 1;
        panel.add(subtitleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(emailLabel, gbc);

        emailField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(emailField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(passLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1; gbc.gridy = 3;
        panel.add(passwordField, gbc);

        JButton loginBtn = new JButton("Sign In");
        loginBtn.setBackground(new Color(37, 99, 235));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(loginBtn, gbc);

        JButton registerBtn = new JButton("Create New Account");
        registerBtn.setBackground(new Color(241, 245, 249));
        registerBtn.setForeground(new Color(71, 85, 105));
        registerBtn.setFocusPainted(false);
        registerBtn.setBorderPainted(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 5;
        panel.add(registerBtn, gbc);

        loginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        registerBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new RegisterFrame().setVisible(true);
            }
        });

        getRootPane().setDefaultButton(loginBtn);
        add(panel);
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter email and password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            User user = userDAO.authenticate(email, password);
            if (user != null) {
                SessionManager.setCurrentUser(user);
                dispose();
                new MainFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
