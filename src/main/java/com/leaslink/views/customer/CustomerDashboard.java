package com.leaslink.views.customer;

import com.leaslink.controllers.CustomerController;
import com.leaslink.models.Lease;
import com.leaslink.models.Payment;
import com.leaslink.models.User;
import com.leaslink.views.LoginForm;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CustomerDashboard extends JFrame {
    private User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private CustomerController.LeaseSummary leaseSummary;

    public CustomerDashboard(User user) {
        this.currentUser = user;
        initializeFrame();
        loadData();
        createComponents();
    }

    private void initializeFrame() {
        setTitle("LeasLink - Customer Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    private void loadData() {
        try {
            leaseSummary = CustomerController.getCustomerLeaseSummary(currentUser.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            leaseSummary = new CustomerController.LeaseSummary();
        }
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
        contentPanel.add(createLoanStatusPanel(), "loans");
        contentPanel.add(createPaymentHistoryPanel(), "payments");
        contentPanel.add(createContractDetailsPanel(), "contracts");

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(new Color(255, 0, 0)); 
        navbar.setPreferredSize(new Dimension(0, 60));
        navbar.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Logo and title
        JLabel titleLabel = new JLabel("LeasLink Customer Portal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        // User info and logout
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        
        JLabel userLabel = new JLabel("Welcome, " + currentUser.getFullName());
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(128, 35, 51));
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

        String[] menuItems = {
            "Dashboard", "Status Pinjaman", "Riwayat Pembayaran", "Detail Kontrak"
        };
        String[] menuKeys = {
            "dashboard", "loans", "payments", "contracts"
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

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(0, 123, 255));
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(52, 58, 64));
            }
        });

        button.addActionListener(e -> {
            cardLayout.show(contentPanel, key);
            // Refresh data when switching panels
            if ("payments".equals(key)) {
                refreshPaymentHistory();
            } else if ("loans".equals(key)) {
                refreshLoanStatus();
            }
        });

        return button;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Dashboard Customer");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(52, 58, 64));

        // Stats cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(30, 0, 30, 0));

        statsPanel.add(createStatsCard("Total Pinjaman", 
            String.valueOf(leaseSummary.getTotalLeases()), new Color(0, 123, 255)));
        statsPanel.add(createStatsCard("Pinjaman Aktif", 
            String.valueOf(leaseSummary.getActiveLeases()), new Color(40, 167, 69)));
        statsPanel.add(createStatsCard("Total Dibayar", 
            String.format("Rp %,.0f", leaseSummary.getTotalPaid()), new Color(255, 193, 7)));
        statsPanel.add(createStatsCard("Sisa Pembayaran", 
            String.format("Rp %,.0f", leaseSummary.getRemainingAmount()), new Color(220, 53, 69)));

        // Quick overview panel
        JPanel overviewPanel = createOverviewPanel();

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);
        panel.add(overviewPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(223, 227, 230), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel("Ringkasan Pinjaman");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(52, 58, 64));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        // Progress bar for overall payment
        JLabel progressLabel = new JLabel("Progress Pembayaran: " + 
            String.format("%.1f%%", leaseSummary.getPaymentProgress()));
        progressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        progressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int) leaseSummary.getPaymentProgress());
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(40, 167, 69));
        progressBar.setBackground(new Color(240, 240, 240));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Payment status
        JLabel statusLabel;
        try {
            boolean hasOverdue = CustomerController.hasOverduePayments(currentUser.getId());
            if (hasOverdue) {
                statusLabel = new JLabel("⚠️ Anda memiliki pembayaran yang tertunggak");
                statusLabel.setForeground(new Color(220, 53, 69));
            } else if (leaseSummary.getActiveLeases() > 0) {
                statusLabel = new JLabel("✅ Pembayaran Anda lancar");
                statusLabel.setForeground(new Color(40, 167, 69));
            } else {
                statusLabel = new JLabel("ℹ️ Tidak ada pinjaman aktif");
                statusLabel.setForeground(new Color(108, 117, 125));
            }
        } catch (SQLException e) {
            statusLabel = new JLabel("❌ Error mengecek status pembayaran");
            statusLabel.setForeground(new Color(220, 53, 69));
        }
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(progressLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(progressBar);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(statusLabel);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

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

    private JPanel createLoanStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Status Pinjaman");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(52, 58, 64));

        // Create table for loans
        String[] columnNames = {"Motor", "Jumlah Pinjaman", "Cicilan/Bulan", 
                               "Total Dibayar", "Sisa", "Status", "Tanggal Mulai"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.setSelectionBackground(new Color(0, 123, 255, 50));

        // Load loan data
        loadLoanData(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(223, 227, 230), 1));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(20), BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);

        return panel;
    }

    private void loadLoanData(DefaultTableModel tableModel) {
        try {
            List<Lease> leases = CustomerController.getCustomerLeases(currentUser.getId());
            tableModel.setRowCount(0); // Clear existing data
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            for (Lease lease : leases) {                Object[] row = {
                    lease.getFullMotorcycleName(),
                    String.format("Rp %,.0f", lease.getLeaseAmount()),
                    String.format("Rp %,.0f", lease.getMonthlyPayment()),
                    String.format("Rp %,.0f", lease.getTotalPaid()),
                    String.format("Rp %,.0f", lease.getRemainingAmount()),
                    lease.getStatus().toUpperCase(),
                    lease.getStartDate() != null ? lease.getStartDate().format(formatter) : "-"
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading loan data: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createPaymentHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Riwayat Pembayaran");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(52, 58, 64));

        // Create table for payment history
        String[] columnNames = {"Tanggal", "Motor", "Jumlah", "Metode", "Kolektor", "Catatan"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.setSelectionBackground(new Color(0, 123, 255, 50));

        // Load payment data
        loadPaymentData(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(223, 227, 230), 1));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(20), BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);

        return panel;
    }

    private void loadPaymentData(DefaultTableModel tableModel) {
        try {
            List<Payment> payments = CustomerController.getCustomerPayments(currentUser.getId());
            tableModel.setRowCount(0); // Clear existing data
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
              for (Payment payment : payments) {
                Object[] row = {
                    payment.getPaymentDate() != null ? payment.getPaymentDate().format(formatter) : "-",
                    payment.getMotorcycleName(),
                    payment.getFormattedAmount(),
                    payment.getPaymentMethod() != null ? payment.getPaymentMethod() : "-",
                    payment.getCollectorName() != null ? payment.getCollectorName() : "System",
                    payment.getNotes() != null ? payment.getNotes() : ""
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading payment data: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createContractDetailsPanel() {
        return createPlaceholderPanel("Detail Kontrak", 
            "Lihat detail kontrak pinjaman dan persyaratan");
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

    private void refreshPaymentHistory() {
        // Find the payment history panel and refresh its table
        Component[] components = contentPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                Component[] panelComponents = panel.getComponents();
                for (Component panelComp : panelComponents) {
                    if (panelComp instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) panelComp;
                        if (scrollPane.getViewport().getView() instanceof JTable) {
                            JTable table = (JTable) scrollPane.getViewport().getView();
                            if (table.getModel() instanceof DefaultTableModel) {
                                DefaultTableModel model = (DefaultTableModel) table.getModel();
                                if (model.getColumnCount() == 6) { // Payment table has 6 columns
                                    loadPaymentData(model);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void refreshLoanStatus() {
        // Refresh loan summary data
        loadData();
        
        // Find the loan status panel and refresh its table
        Component[] components = contentPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                Component[] panelComponents = panel.getComponents();
                for (Component panelComp : panelComponents) {
                    if (panelComp instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) panelComp;
                        if (scrollPane.getViewport().getView() instanceof JTable) {
                            JTable table = (JTable) scrollPane.getViewport().getView();
                            if (table.getModel() instanceof DefaultTableModel) {
                                DefaultTableModel model = (DefaultTableModel) table.getModel();
                                if (model.getColumnCount() == 7) { // Loan table has 7 columns
                                    loadLoanData(model);
                                }
                            }
                        }
                    }
                }
            }
        }
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
