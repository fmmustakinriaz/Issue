package com.university.issuetracker.ui;

import com.university.issuetracker.dao.CategoryDAO;
import com.university.issuetracker.dao.CommentDAO;
import com.university.issuetracker.dao.IssueDAO;
import com.university.issuetracker.dao.UserDAO;
import com.university.issuetracker.model.*;
import com.university.issuetracker.util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class IssueDetailDialog extends JDialog {
    private IssueDAO issueDAO;
    private CommentDAO commentDAO;
    private CategoryDAO categoryDAO;
    private UserDAO userDAO;
    private int issueId;
    private Issue issue;

    private JLabel titleLabel, priorityLabel, statusLabel, submittedLabel, dateLabel, categoryLabel, assignedLabel;
    private JTextArea descArea;
    private JComboBox<String> statusCombo, priorityCombo, assignedCombo, categoryCombo;
    private DefaultListModel<String> commentListModel;
    private JTextArea commentArea;

    public IssueDetailDialog(JFrame parent, int issueId) {
        super(parent, "Issue #" + issueId, true);
        this.issueId = issueId;
        issueDAO = new IssueDAO();
        commentDAO = new CommentDAO();
        categoryDAO = new CategoryDAO();
        userDAO = new UserDAO();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setSize(600, 650);
        setLocationRelativeTo(getParent());

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(248, 250, 252));

        // Top section
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        titleLabel = new JLabel();
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(new Color(30, 41, 59));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        infoPanel.setBackground(Color.WHITE);
        submittedLabel = new JLabel();
        dateLabel = new JLabel();
        categoryLabel = new JLabel();
        assignedLabel = new JLabel();
        priorityLabel = new JLabel();
        statusLabel = new JLabel();
        infoPanel.add(submittedLabel);
        infoPanel.add(dateLabel);
        infoPanel.add(categoryLabel);
        infoPanel.add(assignedLabel);
        infoPanel.add(priorityLabel);
        infoPanel.add(statusLabel);
        topPanel.add(infoPanel, BorderLayout.CENTER);

        descArea = new JTextArea(4, 40);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setBackground(new Color(248, 250, 252));
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        topPanel.add(new JScrollPane(descArea), BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);

        // Manage section
        boolean canUpdate = SessionManager.isStaff() || SessionManager.isAdmin();
        boolean isAdmin = SessionManager.isAdmin();

        if (canUpdate) {
            JPanel managePanel = new JPanel(new GridLayout(0, 4, 8, 8));
            managePanel.setBackground(Color.WHITE);
            managePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            managePanel.add(new JLabel("Status:"));
            statusCombo = new JComboBox<>(new String[]{"open", "in_progress", "resolved", "closed"});
            managePanel.add(statusCombo);

            managePanel.add(new JLabel("Priority:"));
            priorityCombo = new JComboBox<>(new String[]{"low", "medium", "high"});
            managePanel.add(priorityCombo);

            if (isAdmin) {
                managePanel.add(new JLabel("Assign To:"));
                assignedCombo = new JComboBox<>();
                managePanel.add(assignedCombo);

                managePanel.add(new JLabel("Category:"));
                categoryCombo = new JComboBox<>();
                managePanel.add(categoryCombo);
            }

            JButton updateBtn = new JButton("Update Issue");
            updateBtn.setBackground(new Color(37, 99, 235));
            updateBtn.setForeground(Color.WHITE);
            updateBtn.setFocusPainted(false);
            updateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            managePanel.add(updateBtn);

            updateBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { handleUpdate(); }
            });

            panel.add(managePanel, BorderLayout.CENTER);
        }

        // Comments section
        JPanel commentsPanel = new JPanel(new BorderLayout(5, 5));
        commentsPanel.setBorder(BorderFactory.createTitledBorder("Comments"));

        commentListModel = new DefaultListModel<>();
        JList<String> commentList = new JList<>(commentListModel);
        commentList.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JScrollPane commentScroll = new JScrollPane(commentList);
        commentScroll.setPreferredSize(new Dimension(500, 150));
        commentsPanel.add(commentScroll, BorderLayout.CENTER);

        JPanel addCommentPanel = new JPanel(new BorderLayout(5, 5));
        commentArea = new JTextArea(2, 30);
        commentArea.setLineWrap(true);
        addCommentPanel.add(new JScrollPane(commentArea), BorderLayout.CENTER);

        JButton addCommentBtn = new JButton("Add Comment");
        addCommentBtn.setBackground(new Color(37, 99, 235));
        addCommentBtn.setForeground(Color.WHITE);
        addCommentBtn.setFocusPainted(false);
        addCommentBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addCommentPanel.add(addCommentBtn, BorderLayout.EAST);

        addCommentBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { handleAddComment(); }
        });

        commentsPanel.add(addCommentPanel, BorderLayout.SOUTH);
        panel.add(commentsPanel, BorderLayout.SOUTH);

        add(panel);
    }

    private void loadData() {
        try {
            issue = issueDAO.findById(issueId);
            if (issue == null) {
                JOptionPane.showMessageDialog(this, "Issue not found.", "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }

            titleLabel.setText(issue.getTitle());
            submittedLabel.setText("Submitted by: " + (issue.getCreatedByName() != null ? issue.getCreatedByName() : "Unknown"));
            dateLabel.setText("Date: " + (issue.getCreatedAt() != null ? issue.getCreatedAt() : "—"));
            categoryLabel.setText("Category: " + (issue.getCategoryName() != null ? issue.getCategoryName() : "Uncategorized"));
            assignedLabel.setText("Assigned to: " + (issue.getAssignedToName() != null ? issue.getAssignedToName() : "Unassigned"));
            priorityLabel.setText("Priority: " + issue.getPriority());
            statusLabel.setText("Status: " + issue.getStatus());
            descArea.setText(issue.getDescription());

            if (statusCombo != null) statusCombo.setSelectedItem(issue.getStatus());
            if (priorityCombo != null) priorityCombo.setSelectedItem(issue.getPriority());

            if (assignedCombo != null && SessionManager.isAdmin()) {
                assignedCombo.removeAllItems();
                assignedCombo.addItem("Unassigned");
                List<User> staff = userDAO.findByRole("staff");
                int selectedIndex = 0;
                for (int i = 0; i < staff.size(); i++) {
                    User s = staff.get(i);
                    assignedCombo.addItem(s);
                    if (issue.getAssignedTo() != null && issue.getAssignedTo() == s.getId()) {
                        selectedIndex = i + 1;
                    }
                }
                assignedCombo.setSelectedIndex(selectedIndex);
            }

            if (categoryCombo != null && SessionManager.isAdmin()) {
                categoryCombo.removeAllItems();
                List<Category> cats = categoryDAO.findAll();
                for (Category c : cats) {
                    categoryCombo.addItem(c);
                    if (issue.getCategoryId() == c.getId()) {
                        categoryCombo.setSelectedItem(c);
                    }
                }
            }

            loadComments();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading issue: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadComments() throws Exception {
        commentListModel.clear();
        List<Comment> comments = commentDAO.findByIssueId(issueId);
        for (Comment c : comments) {
            String entry = "[" + (c.getCreatedAt() != null ? c.getCreatedAt() : "") + "] " +
                           (c.getUserName() != null ? c.getUserName() : "Unknown") + ": " + c.getBody();
            commentListModel.addElement(entry);
        }
        if (comments.isEmpty()) {
            commentListModel.addElement("No comments yet.");
        }
    }

    private void handleUpdate() {
        try {
            issueDAO.updateStatus(issueId, (String) statusCombo.getSelectedItem());
            issueDAO.updatePriority(issueId, (String) priorityCombo.getSelectedItem());

            if (SessionManager.isAdmin()) {
                Object assigned = assignedCombo.getSelectedItem();
                if (assigned instanceof User) {
                    issueDAO.updateAssignment(issueId, ((User) assigned).getId());
                } else {
                    issueDAO.updateAssignment(issueId, null);
                }

                Object cat = categoryCombo.getSelectedItem();
                if (cat instanceof Category) {
                    issueDAO.updateCategory(issueId, ((Category) cat).getId());
                }
            }

            JOptionPane.showMessageDialog(this, "Issue updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating issue: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAddComment() {
        String body = commentArea.getText().trim();
        if (body.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a comment.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            commentDAO.create(issueId, SessionManager.getCurrentUser().getId(), body);
            commentArea.setText("");
            loadComments();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding comment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
