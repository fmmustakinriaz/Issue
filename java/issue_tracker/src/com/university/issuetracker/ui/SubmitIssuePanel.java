package com.university.issuetracker.ui;

import com.university.issuetracker.dao.CategoryDAO;
import com.university.issuetracker.dao.IssueDAO;
import com.university.issuetracker.model.Category;
import com.university.issuetracker.model.Issue;
import com.university.issuetracker.util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SubmitIssuePanel extends JPanel {
    private JTextField titleField;
    private JTextArea descArea;
    private JComboBox<Category> categoryCombo;
    private JComboBox<String> priorityCombo;
    private IssueDAO issueDAO;
    private CategoryDAO categoryDAO;

    public SubmitIssuePanel() {
        issueDAO = new IssueDAO();
        categoryDAO = new CategoryDAO();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(248, 250, 252));

        JLabel titleLabel = new JLabel("Submit New Issue");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(30, 41, 59));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Title:"), gbc);
        titleField = new JTextField(30);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Category:"), gbc);
        categoryCombo = new JComboBox<>();
        loadCategories();
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(categoryCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Priority:"), gbc);
        priorityCombo = new JComboBox<>(new String[]{"low", "medium", "high"});
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(priorityCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(new JLabel("Description:"), gbc);
        descArea = new JTextArea(5, 30);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descArea);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(scrollPane, gbc);

        JButton submitBtn = new JButton("Submit Issue");
        submitBtn.setBackground(new Color(37, 99, 235));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.weightx = 0; gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(submitBtn, gbc);

        submitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { handleSubmit(); }
        });

        add(formPanel, BorderLayout.CENTER);
    }

    private void loadCategories() {
        try {
            List<Category> cats = categoryDAO.findAll();
            for (Category c : cats) categoryCombo.addItem(c);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSubmit() {
        String title = titleField.getText().trim();
        String desc = descArea.getText().trim();
        if (title.isEmpty() || desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in title and description.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Issue issue = new Issue();
            issue.setTitle(title);
            issue.setDescription(desc);
            Category selected = (Category) categoryCombo.getSelectedItem();
            if (selected != null) issue.setCategoryId(selected.getId());
            issue.setPriority((String) priorityCombo.getSelectedItem());
            issue.setCreatedBy(SessionManager.getCurrentUser().getId());
            issueDAO.create(issue);
            JOptionPane.showMessageDialog(this, "Issue submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            titleField.setText("");
            descArea.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error submitting issue: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
