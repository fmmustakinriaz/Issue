package com.university.issuetracker.ui;

import com.university.issuetracker.util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private DashboardPanel dashboardPanel;
    private IssueListPanel issueListPanel;
    private SubmitIssuePanel submitIssuePanel;
    private ManageUsersPanel usersPanel;
    private ManageCategoriesPanel categoriesPanel;

    public MainFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("UniTrack - Department Issue Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(700, 500));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(37, 99, 235));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 50));

        JLabel titleLabel = new JLabel("  UniTrack");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel userLabel = new JLabel(SessionManager.getCurrentUser().getFullName() + " (" + SessionManager.getCurrentUser().getRole() + ")    ");
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        userLabel.setForeground(new Color(191, 219, 254));
        headerPanel.add(userLabel, BorderLayout.EAST);

        JButton logoutBtn = new JButton("Sign Out");
        logoutBtn.setBackground(new Color(30, 64, 175));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        logoutBtn.setBorderPainted(false);
        headerPanel.add(logoutBtn, BorderLayout.SOUTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.PLAIN, 13));

        dashboardPanel = new DashboardPanel();
        issueListPanel = new IssueListPanel();
        submitIssuePanel = new SubmitIssuePanel();

        tabbedPane.addTab("Dashboard", dashboardPanel);
        tabbedPane.addTab("Issues", issueListPanel);

        if (SessionManager.isStudent()) {
            tabbedPane.addTab("Submit Issue", submitIssuePanel);
        }

        if (SessionManager.isAdmin()) {
            usersPanel = new ManageUsersPanel();
            categoriesPanel = new ManageCategoriesPanel();
            tabbedPane.addTab("Users", usersPanel);
            tabbedPane.addTab("Categories", categoriesPanel);
        }

        tabbedPane.addChangeListener(e -> {
            Component selected = tabbedPane.getSelectedComponent();
            if (selected instanceof DashboardPanel) ((DashboardPanel) selected).refresh();
            if (selected instanceof IssueListPanel) ((IssueListPanel) selected).refresh();
        });

        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        logoutBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SessionManager.clear();
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
    }
}
