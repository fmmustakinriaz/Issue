package com.university.issuetracker.ui;

import com.university.issuetracker.dao.IssueDAO;
import com.university.issuetracker.model.Issue;
import com.university.issuetracker.util.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class IssueListPanel extends JPanel {
    private IssueDAO issueDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilter, categoryFilter;
    private JTextField searchField;

    public IssueListPanel() {
        issueDAO = new IssueDAO();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(248, 250, 252));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBackground(new Color(248, 250, 252));

        JLabel titleLabel = new JLabel("Issues");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(30, 41, 59));
        topPanel.add(titleLabel);
        topPanel.add(Box.createHorizontalStrut(20));

        searchField = new JTextField(15);
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);

        statusFilter = new JComboBox<>(new String[]{"All", "open", "in_progress", "resolved", "closed"});
        topPanel.add(new JLabel("Status:"));
        topPanel.add(statusFilter);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(37, 99, 235));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        topPanel.add(refreshBtn);

        JButton viewBtn = new JButton("View Issue");
        viewBtn.setBackground(new Color(22, 163, 74));
        viewBtn.setForeground(Color.WHITE);
        viewBtn.setFocusPainted(false);
        viewBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        topPanel.add(viewBtn);

        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Title", "Category", "Priority", "Status", "Submitted By", "Assigned To", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        add(scrollPane, BorderLayout.CENTER);

        refreshBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { refresh(); }
        });

        viewBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { viewSelectedIssue(); }
        });

        searchField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { refresh(); }
        });

        statusFilter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { refresh(); }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                viewBtn.setEnabled(row >= 0);
            }
        });
    }

    public void refresh() {
        try {
            List<Issue> issues = issueDAO.findAll();
            String status = (String) statusFilter.getSelectedItem();
            String search = searchField.getText().trim().toLowerCase();

            tableModel.setRowCount(0);
            for (Issue issue : issues) {
                if (!status.equals("All") && !issue.getStatus().equals(status)) continue;
                if (!search.isEmpty() && !issue.getTitle().toLowerCase().contains(search)) continue;

                tableModel.addRow(new Object[]{
                    issue.getId(),
                    issue.getTitle(),
                    issue.getCategoryName() != null ? issue.getCategoryName() : "—",
                    issue.getPriority(),
                    issue.getStatus(),
                    issue.getCreatedByName() != null ? issue.getCreatedByName() : "—",
                    issue.getAssignedToName() != null ? issue.getAssignedToName() : "Unassigned",
                    issue.getCreatedAt() != null ? issue.getCreatedAt().substring(0, Math.min(10, issue.getCreatedAt().length())) : "—"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading issues: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewSelectedIssue() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an issue to view.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int issueId = (int) tableModel.getValueAt(row, 0);
        IssueDetailDialog dialog = new IssueDetailDialog((JFrame) SwingUtilities.getWindowAncestor(this), issueId);
        dialog.setVisible(true);
        refresh();
    }
}
