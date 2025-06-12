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
        titleLabel.setForeground(Color.WHITE);        // User info and logout with refresh button
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        
        // Add refresh button for data consistency
        JButton refreshButton = new JButton("🔄");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        refreshButton.setBackground(new Color(40, 167, 69));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBorderPainted(false);
        refreshButton.setFocusPainted(false);
        refreshButton.setToolTipText("Refresh semua data");
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setPreferredSize(new Dimension(35, 35));
        refreshButton.addActionListener(e -> {
            refreshAllDashboardData();
            JOptionPane.showMessageDialog(this, 
                "Data berhasil diperbarui!", 
                "Refresh", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        JLabel userLabel = new JLabel("Welcome, " + currentUser.getFullName());
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(0, 86, 179));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> logout());

        userPanel.add(refreshButton);
        userPanel.add(Box.createHorizontalStrut(10));
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
    }    private JPanel createPaymentHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Header section
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        JLabel titleLabel = new JLabel("Riwayat Transaksi");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(33, 37, 41));

        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);

        JTextField searchField = new JTextField("Cari ID", 20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setForeground(new Color(108, 117, 125));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        // Search field placeholder behavior
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Cari ID")) {
                    searchField.setText("");
                    searchField.setForeground(new Color(33, 37, 41));
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Cari ID");
                    searchField.setForeground(new Color(108, 117, 125));
                }
            }
        });

        JButton searchButton = new JButton("🔍");
        searchButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchButton.setBackground(new Color(248, 249, 250));
        searchButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        searchButton.setFocusPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(5));
        searchPanel.add(searchButton);

        headerPanel.add(searchPanel, BorderLayout.EAST);

        // Create table for transaction history
        String[] columnNames = {"ID", "Tanggal peminjaman", "Jatuh tempo", "Kontrak", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(50);
        table.setGridColor(new Color(223, 227, 230));
        table.setSelectionBackground(new Color(0, 123, 255, 30));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        // Table header styling
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.getTableHeader().setForeground(new Color(73, 80, 87));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(223, 227, 230)));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(120);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(180);  // Tanggal peminjaman
        table.getColumnModel().getColumn(2).setPreferredWidth(150);  // Jatuh tempo
        table.getColumnModel().getColumn(3).setPreferredWidth(120);  // Kontrak
        table.getColumnModel().getColumn(4).setPreferredWidth(100);  // Status        // Custom renderer for status column
        table.getColumnModel().getColumn(4).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value != null) {
                    String status = value.toString();
                    JLabel statusLabel = new JLabel(status);
                    statusLabel.setOpaque(true);
                    statusLabel.setHorizontalAlignment(JLabel.CENTER);
                    statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    statusLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
                    
                    if (status.equals("Aktif")) {
                        statusLabel.setBackground(new Color(144, 202, 249));
                        statusLabel.setForeground(Color.WHITE);
                    } else if (status.equals("Nonaktif")) {
                        statusLabel.setBackground(new Color(239, 154, 154));
                        statusLabel.setForeground(Color.WHITE);
                    } else {
                        statusLabel.setBackground(new Color(189, 189, 189));
                        statusLabel.setForeground(Color.WHITE);
                    }
                    
                    if (isSelected) {
                        statusLabel.setBackground(table.getSelectionBackground());
                    }
                    
                    return statusLabel;
                }
                return c;
            }
        });        // Custom renderer for kontrak column (make it look like a link)
        table.getColumnModel().getColumn(3).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel linkLabel = new JLabel();
                linkLabel.setText("<html><u>Lihat kontrak</u></html>");
                linkLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                linkLabel.setHorizontalAlignment(JLabel.CENTER);
                linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                if (isSelected) {
                    linkLabel.setOpaque(true);
                    linkLabel.setBackground(new Color(0, 123, 255, 100)); // Enhanced selection background
                    linkLabel.setForeground(Color.WHITE);
                    linkLabel.setText("<html><u><b>Lihat kontrak</b></u></html>"); // Bold when selected
                } else {
                    linkLabel.setOpaque(false);
                    linkLabel.setForeground(new Color(0, 123, 255));
                }
                
                return linkLabel;
            }
        });

        // Enhanced mouse listener with better visual feedback
        table.addMouseListener(new MouseAdapter() {
            private int hoveredRow = -1;
            private int hoveredCol = -1;
            
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                
                if (row >= 0 && col == 3) { // Kontrak column
                    // Visual feedback - briefly change background
                    table.setSelectionBackground(new Color(40, 167, 69, 150));
                    table.setRowSelectionInterval(row, row);
                    
                    // Restore original selection color after a brief moment
                    Timer timer = new Timer(200, ae -> {
                        table.setSelectionBackground(new Color(0, 123, 255, 30));
                        table.clearSelection();
                    });
                    timer.setRepeats(false);
                    timer.start();
                    
                    String contractId = (String) table.getValueAt(row, 0);
                    showContractDetails(contractId, row);
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                
                if (row >= 0 && col == 3) { // Kontrak column
                    table.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    hoveredRow = row;
                    hoveredCol = col;
                    table.repaint();
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                hoveredRow = -1;
                hoveredCol = -1;
                table.repaint();
            }
        });

        // Load transaction data
        loadTransactionData(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(223, 227, 230), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Search functionality
        searchButton.addActionListener(e -> filterTransactions(tableModel, searchField.getText()));
        searchField.addActionListener(e -> filterTransactions(tableModel, searchField.getText()));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }    private void loadTransactionData(DefaultTableModel tableModel) {
        try {
            List<Lease> leases = CustomerController.getCustomerLeases(currentUser.getId());
            tableModel.setRowCount(0); // Clear existing data
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            
            for (int i = 0; i < leases.size(); i++) {
                Lease lease = leases.get(i);
                String status = getTransactionStatus(lease.getStatus());
                
                // Generate consistent contract ID based on lease data
                String contractId = String.format("LLS%03d%02d", lease.getId(), (i + 1));
                
                Object[] row = {
                    contractId,
                    lease.getStartDate() != null ? lease.getStartDate().format(formatter) : "-",
                    lease.getEndDate() != null ? lease.getEndDate().format(formatter) : "-",
                    "Lihat kontrak", // This will be rendered as a clickable link
                    status
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading transaction data: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getTransactionStatus(String leaseStatus) {
        return switch (leaseStatus.toLowerCase()) {
            case "active" -> "Aktif";
            case "completed", "cancelled", "rejected" -> "Nonaktif";
            case "pending" -> "Aktif"; // Assuming pending contracts are considered active
            default -> "Nonaktif";
        };
    }    private void filterTransactions(DefaultTableModel tableModel, String searchText) {
        if (searchText.equals("Cari ID") || searchText.trim().isEmpty()) {
            loadTransactionData(tableModel);
            return;
        }
        
        try {
            List<Lease> allLeases = CustomerController.getCustomerLeases(currentUser.getId());
            tableModel.setRowCount(0);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            
            for (int i = 0; i < allLeases.size(); i++) {
                Lease lease = allLeases.get(i);
                String contractId = String.format("LLS%03d%02d", lease.getId(), (i + 1));
                
                // Search in contract ID, lease ID, or customer name
                if (contractId.toLowerCase().contains(searchText.toLowerCase()) || 
                    String.valueOf(lease.getId()).contains(searchText) ||
                    (lease.getCustomerName() != null && lease.getCustomerName().toLowerCase().contains(searchText.toLowerCase()))) {
                    
                    String status = getTransactionStatus(lease.getStatus());
                    
                    Object[] row = {
                        contractId,
                        lease.getStartDate() != null ? lease.getStartDate().format(formatter) : "-",
                        lease.getEndDate() != null ? lease.getEndDate().format(formatter) : "-",
                        "Lihat kontrak",
                        status
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error filtering transaction data: " + e.getMessage(),
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
    }    private void refreshPaymentHistory() {
        // Find the transaction history panel and refresh its table
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
                                if (model.getColumnCount() == 5) { // Transaction table has 5 columns
                                    loadTransactionData(model);
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
        }    }    private void showContractDetails(String contractId, int row) {
        JDialog contractDialog = new JDialog(this, "Detail Kontrak - " + contractId, true);
        contractDialog.setLayout(new BorderLayout());
        contractDialog.setSize(550, 450);
        contractDialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        // Header with enhanced styling
        JLabel titleLabel = new JLabel("Detail Kontrak Leasing");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(52, 58, 64));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add success indicator
        JLabel successLabel = new JLabel("✓ Kontrak berhasil diakses");
        successLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        successLabel.setForeground(new Color(40, 167, 69));
        successLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Contract details - refresh data to ensure consistency
        try {
            // Refresh lease data to ensure consistency across tabs
            List<Lease> leases = CustomerController.getCustomerLeases(currentUser.getId());
            if (!leases.isEmpty() && row < leases.size()) {
                Lease lease = leases.get(row); // Use the specific lease from the clicked row
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
                
                JPanel detailsPanel = new JPanel(new GridLayout(7, 2, 10, 10));
                detailsPanel.setOpaque(false);
                detailsPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
                
                detailsPanel.add(createDetailLabel("ID Kontrak:"));
                detailsPanel.add(createDetailValue(contractId));
                
                detailsPanel.add(createDetailLabel("Motor:"));
                detailsPanel.add(createDetailValue(lease.getFullMotorcycleName()));
                
                detailsPanel.add(createDetailLabel("Tanggal Mulai:"));
                detailsPanel.add(createDetailValue(lease.getStartDate() != null ? 
                    lease.getStartDate().format(formatter) : "-"));
                
                detailsPanel.add(createDetailLabel("Tanggal Berakhir:"));
                detailsPanel.add(createDetailValue(lease.getEndDate() != null ? 
                    lease.getEndDate().format(formatter) : "-"));
                
                detailsPanel.add(createDetailLabel("Jumlah Pinjaman:"));
                detailsPanel.add(createDetailValue(String.format("Rp %,.0f", lease.getLeaseAmount())));
                
                detailsPanel.add(createDetailLabel("Cicilan Bulanan:"));
                detailsPanel.add(createDetailValue(String.format("Rp %,.0f", lease.getMonthlyPayment())));
                
                detailsPanel.add(createDetailLabel("Status:"));
                detailsPanel.add(createDetailValue(getTransactionStatus(lease.getStatus())));
                
                contentPanel.add(titleLabel);
                contentPanel.add(Box.createVerticalStrut(10));
                contentPanel.add(successLabel);
                contentPanel.add(detailsPanel);
            } else {
                JLabel errorLabel = new JLabel("Kontrak tidak ditemukan");
                errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                errorLabel.setForeground(new Color(220, 53, 69));
                contentPanel.add(titleLabel);
                contentPanel.add(errorLabel);
            }
        } catch (SQLException e) {
            JLabel errorLabel = new JLabel("Error loading contract details: " + e.getMessage());
            errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            errorLabel.setForeground(new Color(220, 53, 69));
            contentPanel.add(titleLabel);
            contentPanel.add(errorLabel);
        }

        // Enhanced close button
        JButton closeButton = new JButton("Tutup");
        closeButton.setBackground(new Color(108, 117, 125));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.setPreferredSize(new Dimension(100, 35));
        closeButton.addActionListener(e -> {
            contractDialog.dispose();
            // Refresh all dashboard data to maintain consistency
            refreshAllDashboardData();
        });

        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(closeButton);        contractDialog.add(contentPanel, BorderLayout.CENTER);
        contractDialog.setVisible(true);
    }

    private void refreshAllDashboardData() {
        // Refresh lease summary data
        loadData();
        
        // Refresh all panel data to maintain consistency
        refreshPaymentHistory();
        refreshLoanStatus();
        
        // Update overview panel if needed
        Component[] components = contentPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                comp.repaint();
            }
        }
    }

    private JLabel createDetailLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(73, 80, 87));
        return label;
    }

    private JLabel createDetailValue(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(33, 37, 41));
        return label;
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
