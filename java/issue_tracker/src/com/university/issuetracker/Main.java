package com.university.issuetracker;

import com.university.issuetracker.ui.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelName());
            } catch (Exception e) {
                // Use default look and feel
            }
            new LoginFrame().setVisible(true);
        });
    }
}
