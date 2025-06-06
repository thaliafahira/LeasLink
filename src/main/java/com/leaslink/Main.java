package com.leaslink;

import com.formdev.flatlaf.FlatLightLaf;
import com.leaslink.views.LoginForm;
import com.leaslink.utils.DatabaseUtil;

import javax.swing.*;
import java.sql.SQLException;

/**
 * Main entry point untuk aplikasi LeasLink
 * Aplikasi manajemen leasing motor dengan 3 role utama:
 * - Admin: Mengelola seluruh sistem
 * - Management: Mengelola penjualan dan operasional
 * - Collector: Mengelola penagihan
 */
public class Main {
    
    public static void main(String[] args) {
        // Set up Look and Feel
        setupLookAndFeel();
        
        // Initialize database
        initializeDatabase();
        
        // Start the application
        SwingUtilities.invokeLater(() -> {
            showWelcomeInfo();
            new LoginForm().setVisible(true);
        });
    }
    
    private static void setupLookAndFeel() {
        try {
            // Set FlatLaf as Look and Feel
            FlatLightLaf.setup();
            
            // Enable font antialiasing for better text rendering
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
            
            // Set additional UI properties
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("ProgressBar.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            
        } catch (Exception e) {
            System.err.println("Failed to initialize Look and Feel: " + e.getMessage());
            // Fallback to system Look and Feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Failed to set system Look and Feel: " + ex.getMessage());
            }
        }
    }
    
    private static void initializeDatabase() {
        try {
            // Initialize database connection and create tables
            DatabaseUtil.getConnection();
            System.out.println("Database initialized successfully.");
            
            // Print users info for development
            DatabaseUtil.printUsersByRole();
            
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Failed to initialize database: " + e.getMessage(),
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    private static void showWelcomeInfo() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("    LEASLINK - APLIKASI MANAJEMEN LEASING MOTOR");
        System.out.println("=".repeat(50));
        System.out.println("Aplikasi ini memiliki 3 role utama:");
        System.out.println("1. ADMIN     - Mengelola seluruh sistem");
        System.out.println("2. MANAGEMENT - Mengelola penjualan & operasional");
        System.out.println("3. COLLECTOR  - Mengelola penagihan");
        System.out.println("\nDefault Login Credentials:");
        System.out.println("Admin     : admin@leaslink.com / admin");
        System.out.println("Manager   : manager@leaslink.com / management");
        System.out.println("Collector : collector@leaslink.com / collector");
        System.out.println("=".repeat(50) + "\n");
    }
}