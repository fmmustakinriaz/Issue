package com.university.issuetracker.ui;

import com.university.issuetracker.dao.CategoryDAO;
import com.university.issuetracker.model.Category;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ManageCategoriesPanel extends JPanel {
    private CategoryDAO categoryDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField nameField, descField;

    public ManageCategoriesPanel() {
        categoryDAO = new CategoryDAO();
        initComponents();
        refresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(248, 250, 252));

        JLabel titleLabel = new JLabel("Manage Categories");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(30, 41, 59));
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Description"};
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

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        formPanel.setBackground(new Color(248, 250, 252));

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField(15);
        formPanel.add(nameField);

        formPanel.add(new JLabel("Description:"));
        descField = new JTextField(20);
        formPanel.add(descField);

        JButton addBtn = new JButton("Add");
        addBtn.setBackground(new Color(37, 99, 235));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formPanel.add(addBtn);

        JButton updateBtn = new JButton("Update Selected");
        updateBtn.setBackground(new Color(22, 163, 74));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFocusPainted(false);
        updateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formPanel.add(updateBtn);

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.setBackground(new Color(220, 38, 38));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formPanel.add(deleteBtn);

        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { handleAdd(); }
        });

        updateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { handleUpdate(); }
        });

        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { handleDelete(); }
        });

        add(formPanel, BorderLayout.SOUTH);
    }

    private void handleAdd() {
        String name = nameField.getText().trim();
        String desc = descField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Category name is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            categoryDAO.create(name, desc);
            nameField.setText("");
            descField.setText("");
            refresh();
            JOptionPane.showMessageDialog(this, "Category added.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a category first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        String name = nameField.getText().trim();
        String desc = descField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Category name is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            categoryDAO.update(id, name, desc);
            refresh();
            JOptionPane.showMessageDialog(this, "Category updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a category first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this category? Issues will become uncategorized.", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            categoryDAO.delete(id);
            refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refresh() {
        try {
            List<Category> cats = categoryDAO.findAll();
            tableModel.setRowCount(0);
            for (Category c : cats) {
                tableModel.addRow(new Object[]{c.getId(), c.getName(), c.getDescription() != null ? c.getDescription() : "—"});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
