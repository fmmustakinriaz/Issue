package com.university.issuetracker.ui;

import com.university.issuetracker.dao.IssueDAO;
import com.university.issuetracker.model.Issue;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {
    private IssueDAO issueDAO;
    private JLabel totalLabel, openLabel, inProgressLabel, resolvedLabel, closedLabel, highPriorityLabel;
    private JPanel recentPanel;

    public DashboardPanel() {
        issueDAO = new IssueDAO();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(248, 250, 252));

        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(30, 41, 59));
        add(titleLabel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        statsPanel.setBackground(new Color(248, 250, 252));

        totalLabel = createStatCard("Total Issues", new Color(100, 116, 139), statsPanel);
        openLabel = createStatCard("Open", new Color(37, 99, 235), statsPanel);
        inProgressLabel = createStatCard("In Progress", new Color(217, 119, 6), statsPanel);
        resolvedLabel = createStatCard("Resolved", new Color(22, 163, 74), statsPanel);
        closedLabel = createStatCard("Closed", new Color(100, 116, 139), statsPanel);
        highPriorityLabel = createStatCard("High Priority", new Color(220, 38, 38), statsPanel);

        add(statsPanel, BorderLayout.CENTER);

        recentPanel = new JPanel();
        recentPanel.setLayout(new BoxLayout(recentPanel, BoxLayout.Y_AXIS));
        recentPanel.setBackground(Color.WHITE);
        recentPanel.setBorder(BorderFactory.createTitledBorder("Recent Issues"));

        JScrollPane scrollPane = new JScrollPane(recentPanel);
        scrollPane.setPreferredSize(new Dimension(getWidth(), 200));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(scrollPane, BorderLayout.SOUTH);
    }

    private JLabel createStatCard(String label, Color color, JPanel parent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel numLabel = new JLabel("—", SwingConstants.LEFT);
        numLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        numLabel.setForeground(new Color(30, 41, 59));
        card.add(numLabel, BorderLayout.CENTER);

        JLabel descLabel = new JLabel(label);
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        descLabel.setForeground(new Color(100, 116, 139));
        card.add(descLabel, BorderLayout.SOUTH);

        JLabel dotLabel = new JLabel("  *");
        dotLabel.setForeground(color);
        dotLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        card.add(dotLabel, BorderLayout.NORTH);

        parent.add(card);
        return numLabel;
    }

    public void refresh() {
        try {
            totalLabel.setText(String.valueOf(issueDAO.countAll()));
            openLabel.setText(String.valueOf(issueDAO.countByStatus("open")));
            inProgressLabel.setText(String.valueOf(issueDAO.countByStatus("in_progress")));
            resolvedLabel.setText(String.valueOf(issueDAO.countByStatus("resolved")));
            closedLabel.setText(String.valueOf(issueDAO.countByStatus("closed")));
            highPriorityLabel.setText(String.valueOf(issueDAO.countByPriority("high")));

            recentPanel.removeAll();
            List<Issue> recent = issueDAO.findAll();
            int count = 0;
            for (Issue issue : recent) {
                if (count >= 5) break;
                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(Color.WHITE);
                row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241, 245, 249)));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

                JLabel titleLabel = new JLabel("  " + issue.getTitle());
                titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
                titleLabel.setForeground(new Color(30, 41, 59));
                row.add(titleLabel, BorderLayout.WEST);

                JLabel infoLabel = new JLabel(
                    (issue.getCategoryName() != null ? issue.getCategoryName() : "Uncategorized") +
                    " - " + (issue.getCreatedByName() != null ? issue.getCreatedByName() : "Unknown") +
                    " - " + issue.getPriority() + " - " + issue.getStatus() + "  ");
                infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
                infoLabel.setForeground(new Color(100, 116, 139));
                row.add(infoLabel, BorderLayout.EAST);

                recentPanel.add(row);
                count++;
            }
            if (recent.isEmpty()) {
                JLabel empty = new JLabel("No issues yet");
                empty.setForeground(new Color(148, 163, 184));
                empty.setFont(new Font("SansSerif", Font.PLAIN, 13));
                empty.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                recentPanel.add(empty);
            }
            recentPanel.revalidate();
            recentPanel.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading dashboard: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
