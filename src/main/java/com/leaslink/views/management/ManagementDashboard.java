package com.leaslink.views.management;

import com.leaslink.models.User;
import com.leaslink.views.LoginForm;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ManagementDashboard extends JFrame {
    private User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public ManagementDashboard(User user) {
        this.currentUser = user;
        initializeFrame();
        createComponents();
    }

    private void initializeFrame() {
        setTitle("LeasLink - Management Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    private void createComponents() {
        setLayout(new BorderLayout());

        // Create navbar
        JPanel navbar = createNavbar();
        add(navbar, BorderLayout.NORTH);

        // Create sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Create main content area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(248, 249, 250));
        
        // Add different panels for each menu
        contentPanel.add(createDashboardPanel(), "dashboard");
        contentPanel.add(createSalesPanel(), "sales");
        contentPanel.add(createInventoryPanel(), "inventory");
        contentPanel.add(createCustomersPanel(), "customers");
        contentPanel.add(createFinancePanel(), "finance");
        contentPanel.add(createReportsPanel(), "reports");

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(new Color(40, 167, 69)); // Green theme for management
        navbar.setPreferredSize(new Dimension(0, 60));
        navbar.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Logo and title
        JLabel titleLabel = new JLabel("LeasLink Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        // User info and logout
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        
        JLabel userLabel = new JLabel("Welcome, " + currentUser.getFullName());
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(34, 139, 58));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> logout());

        userPanel.add(userLabel);
        userPanel.add(Box.createHorizontalStrut(15));
        userPanel.add(logoutButton);

        navbar.add(titleLabel, BorderLayout.WEST);
        navbar.add(userPanel, BorderLayout.EAST);

        return navbar;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(52, 58, 64));
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));

        // Menu items for management
        String[] menuItems = {
            "Dashboard", "Penjualan", "Inventori", 
            "Pelanggan", "Keuangan", "Laporan"
        };
        String[] menuKeys = {
            "dashboard", "sales", "inventory", 
            "customers", "finance", "reports"
        };

        for (int i = 0; i < menuItems.length; i++) {
            JButton menuButton = createMenuButton(menuItems[i], menuKeys[i]);
            sidebar.add(menuButton);
            sidebar.add(Box.createVerticalStrut(5));
        }

        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    private JButton createMenuButton(String text, String key) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(230, 45));
        button.setPreferredSize(new Dimension(230, 45));
        button.setBackground(new Color(52, 58, 64));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(40, 167, 69));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 58, 64));
            }
        });

        button.addActionListener(e -> cardLayout.show(contentPanel, key));

        return button;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Dashboard Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(52, 58, 64));

        // Stats cards for management
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(30, 0, 30, 0));

        statsPanel.add(createStatsCard("Penjualan Hari Ini", "12", new Color(40, 167, 69)));
        statsPanel.add(createStatsCard("Target Bulanan", "75%", new Color(0, 123, 255)));
        statsPanel.add(createStatsCard("Pelanggan Baru", "8", new Color(255, 193, 7)));
        statsPanel.add(createStatsCard("Omzet Hari Ini", "Rp 85M", new Color(220, 53, 69)));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatsCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(223, 227, 230), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(108, 117, 125));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);

        return card;
    }

    private JPanel createSalesPanel() {
        return createPlaceholderPanel("Manajemen Penjualan", "Kelola proses penjualan dan kontrak leasing");
    }

    private JPanel createInventoryPanel() {
        return createPlaceholderPanel("Manajemen Inventori", "Kelola stok motor dan spare part");
    }

    private JPanel createCustomersPanel() {
        return createPlaceholderPanel("Manajemen Pelanggan", "Kelola data dan riwayat pelanggan");
    }

    private JPanel createFinancePanel() {
        return createPlaceholderPanel("Keuangan", "Kelola pembayaran, cicilan, dan laporan keuangan");
    }

    private JPanel createReportsPanel() {
        return createPlaceholderPanel("Laporan", "Lihat laporan penjualan dan performa tim");
    }

    private JPanel createPlaceholderPanel(String title, String description) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(52, 58, 64));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descLabel.setForeground(new Color(108, 117, 125));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(descLabel);

        // Content area
        JPanel contentArea = new JPanel();
        contentArea.setBackground(Color.WHITE);
        contentArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(223, 227, 230), 1),
            new EmptyBorder(40, 40, 40, 40)
        ));

        JLabel placeholderLabel = new JLabel("Fitur " + title + " akan segera tersedia");
        placeholderLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        placeholderLabel.setForeground(new Color(108, 117, 125));
        contentArea.add(placeholderLabel);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(20), BorderLayout.CENTER);
        panel.add(contentArea, BorderLayout.SOUTH);

        return panel;
    }

    private void logout() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Apakah Anda yakin ingin logout?",
            "Konfirmasi Logout",
            JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            dispose();
            new LoginForm().setVisible(true);
        }
    }
}