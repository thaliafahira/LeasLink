package com.leaslink.views.admin;

import com.leaslink.controllers.LeaseController;
import com.leaslink.models.Lease;
import com.leaslink.models.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Panel untuk mengelola kontrak leasing
 * Menyediakan fitur CRUD lengkap dan approval workflow
 */
public class LeaseManagementPanel extends JPanel {
    private User currentUser;
    private JTable leaseTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JLabel totalLeasesLabel;
    private JLabel activeLeasesLabel;
    private JLabel pendingLeasesLabel;
    private NumberFormat currencyFormat;

    private final String[] columnNames = {
        "ID", "Customer", "Motor", "Jumlah Lease", "Pembayaran/Bulan", 
        "Durasi", "Total Dibayar", "Status", "Tanggal Mulai", "Aksi"
    };

    public LeaseManagementPanel(User currentUser) {
        this.currentUser = currentUser;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("id", "ID"));
        
        initializeComponents();
        loadLeaseData();
        loadSummaryData();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header Panel
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Main Content Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);

        // Summary Panel
        mainPanel.add(createSummaryPanel(), BorderLayout.NORTH);

        // Filter and Search Panel
        mainPanel.add(createFilterPanel(), BorderLayout.CENTER);

        // Table Panel
        mainPanel.add(createTablePanel(), BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Manajemen Kontrak Leasing");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(52, 58, 64));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton addLeaseButton = new JButton("+ Tambah Kontrak");
        addLeaseButton.setBackground(new Color(40, 167, 69));
        addLeaseButton.setForeground(Color.WHITE);
        addLeaseButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addLeaseButton.setBorderPainted(false);
        addLeaseButton.setFocusPainted(false);
        addLeaseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addLeaseButton.setPreferredSize(new Dimension(150, 40));
        addLeaseButton.addActionListener(e -> showAddLeaseDialog());

        JButton refreshButton = new JButton("🔄 Refresh");
        refreshButton.setBackground(new Color(108, 117, 125));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.setBorderPainted(false);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setPreferredSize(new Dimension(100, 40));
        refreshButton.addActionListener(e -> {
            loadLeaseData();
            loadSummaryData();
        });

        buttonPanel.add(refreshButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(addLeaseButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        return headerPanel;
    }    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Total Leases Card
        JPanel totalCard = createSummaryCard("Total Kontrak", "0", new Color(0, 123, 255));
        JPanel totalContentPanel = (JPanel) totalCard.getComponent(0); // Get CENTER component
        totalLeasesLabel = (JLabel) totalContentPanel.getComponent(2); // valueLabel is at index 2

        // Active Leases Card
        JPanel activeCard = createSummaryCard("Kontrak Aktif", "0", new Color(40, 167, 69));
        JPanel activeContentPanel = (JPanel) activeCard.getComponent(0); // Get CENTER component
        activeLeasesLabel = (JLabel) activeContentPanel.getComponent(2); // valueLabel is at index 2

        // Pending Leases Card
        JPanel pendingCard = createSummaryCard("Menunggu Persetujuan", "0", new Color(255, 193, 7));
        JPanel pendingContentPanel = (JPanel) pendingCard.getComponent(0); // Get CENTER component
        pendingLeasesLabel = (JLabel) pendingContentPanel.getComponent(2); // valueLabel is at index 2

        summaryPanel.add(totalCard);
        summaryPanel.add(activeCard);
        summaryPanel.add(pendingCard);

        return summaryPanel;
    }

    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(223, 227, 230), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(108, 117, 125));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(valueLabel);

        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.setOpaque(false);
        filterPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Cari:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.addActionListener(e -> filterLeases());

        JButton searchButton = new JButton("Cari");
        searchButton.setBackground(new Color(0, 123, 255));
        searchButton.setForeground(Color.WHITE);
        searchButton.setBorderPainted(false);
        searchButton.setFocusPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(e -> filterLeases());

        searchPanel.add(searchLabel);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(searchButton);

        // Filter Panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setOpaque(false);

        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        statusFilter = new JComboBox<>(new String[]{"ALL", "pending", "active", "completed", "cancelled", "rejected"});
        statusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusFilter.addActionListener(e -> filterLeases());

        statusPanel.add(statusLabel);
        statusPanel.add(Box.createHorizontalStrut(10));
        statusPanel.add(statusFilter);

        filterPanel.add(searchPanel, BorderLayout.WEST);
        filterPanel.add(statusPanel, BorderLayout.EAST);

        return filterPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);

        // Create table model
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == columnNames.length - 1; // Only action column is editable
            }
        };

        leaseTable = new JTable(tableModel);
        leaseTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        leaseTable.setRowHeight(50);
        leaseTable.setGridColor(new Color(223, 227, 230));
        leaseTable.setSelectionBackground(new Color(0, 123, 255, 50));
        leaseTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        leaseTable.getTableHeader().setBackground(new Color(248, 249, 250));
        leaseTable.getTableHeader().setForeground(new Color(52, 58, 64));

        // Set column widths
        int[] columnWidths = {50, 150, 200, 120, 120, 80, 120, 100, 100, 200};
        for (int i = 0; i < columnWidths.length; i++) {
            TableColumn column = leaseTable.getColumnModel().getColumn(i);
            column.setPreferredWidth(columnWidths[i]);
        }

        // Set action column renderer and editor
        leaseTable.getColumn("Aksi").setCellRenderer(new ActionButtonRenderer());
        leaseTable.getColumn("Aksi").setCellEditor(new ActionButtonEditor());

        JScrollPane scrollPane = new JScrollPane(leaseTable);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(223, 227, 230), 1));

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private void loadLeaseData() {
        try {
            String statusFilterValue = (String) statusFilter.getSelectedItem();
            String searchQuery = searchField.getText().trim();
            
            List<Lease> leases = LeaseController.getAllLeases(
                statusFilterValue.equals("ALL") ? null : statusFilterValue,
                searchQuery.isEmpty() ? null : searchQuery
            );
            
            populateTable(leases);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Gagal memuat data lease: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSummaryData() {
        try {
            LeaseController.LeaseSummary summary = LeaseController.getLeaseSummary();
            
            totalLeasesLabel.setText(String.valueOf(summary.getTotalLeases()));
            activeLeasesLabel.setText(String.valueOf(summary.getActiveLeases()));
            pendingLeasesLabel.setText(String.valueOf(summary.getPendingLeases()));
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateTable(List<Lease> leases) {
        tableModel.setRowCount(0);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Lease lease : leases) {
            Object[] rowData = {
                lease.getId(),
                lease.getCustomerName(),
                lease.getFullMotorcycleName(),
                formatCurrency(lease.getLeaseAmount()),
                formatCurrency(lease.getMonthlyPayment()),
                lease.getLeaseDuration() + " bulan",
                formatCurrency(lease.getTotalPaid()),
                getStatusLabel(lease.getStatus()),
                lease.getStartDate().format(formatter),
                "Actions" // Placeholder for action buttons
            };
            
            tableModel.addRow(rowData);
        }
    }

    private String getStatusLabel(String status) {
        return switch (status.toLowerCase()) {
            case "pending" -> "Menunggu";
            case "active" -> "Aktif";
            case "completed" -> "Selesai";
            case "cancelled" -> "Dibatalkan";
            case "rejected" -> "Ditolak";
            default -> status;
        };
    }

    private String formatCurrency(double amount) {
        return currencyFormat.format(amount);
    }

    private void filterLeases() {
        loadLeaseData();
    }

    private void showAddLeaseDialog() {
        LeaseFormDialog dialog = new LeaseFormDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            "Tambah Kontrak Baru",
            null,
            currentUser
        );
        
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            loadLeaseData();
            loadSummaryData();
        }
    }

    private void showEditLeaseDialog(int leaseId) {
        try {
            Lease lease = LeaseController.getLeaseById(leaseId);
            if (lease != null) {
                LeaseFormDialog dialog = new LeaseFormDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    "Edit Kontrak",
                    lease,
                    currentUser
                );
                
                dialog.setVisible(true);
                
                if (dialog.isConfirmed()) {
                    loadLeaseData();
                    loadSummaryData();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Gagal memuat data lease: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void approveLease(int leaseId) {
        int result = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin menyetujui kontrak ini?",
            "Konfirmasi Persetujuan",
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                boolean success = LeaseController.approveLease(leaseId, currentUser.getId());
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Kontrak berhasil disetujui!",
                        "Berhasil",
                        JOptionPane.INFORMATION_MESSAGE);
                    loadLeaseData();
                    loadSummaryData();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Gagal menyetujui kontrak. Kontrak mungkin sudah tidak dalam status pending.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Gagal menyetujui kontrak: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void rejectLease(int leaseId) {
        String reason = JOptionPane.showInputDialog(this,
            "Masukkan alasan penolakan:",
            "Tolak Kontrak",
            JOptionPane.QUESTION_MESSAGE);
        
        if (reason != null && !reason.trim().isEmpty()) {
            try {
                boolean success = LeaseController.rejectLease(leaseId, currentUser.getId(), reason);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Kontrak berhasil ditolak!",
                        "Berhasil",
                        JOptionPane.INFORMATION_MESSAGE);
                    loadLeaseData();
                    loadSummaryData();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Gagal menolak kontrak. Kontrak mungkin sudah tidak dalam status pending.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Gagal menolak kontrak: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteLease(int leaseId) {
        int result = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin menghapus kontrak ini?\n" +
            "Jika kontrak sudah memiliki pembayaran, maka akan dibatalkan bukan dihapus.",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                boolean success = LeaseController.deleteLease(leaseId, currentUser.getId());
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Kontrak berhasil dihapus/dibatalkan!",
                        "Berhasil",
                        JOptionPane.INFORMATION_MESSAGE);
                    loadLeaseData();
                    loadSummaryData();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Gagal menghapus kontrak: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Action Button Renderer
    class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            removeAll();
            
            // Get lease status from the status column
            String status = (String) table.getValueAt(row, 7);
            
            if (status.equals("Menunggu")) {
                JButton approveBtn = createActionButton("✓", new Color(40, 167, 69));
                JButton rejectBtn = createActionButton("✗", new Color(220, 53, 69));
                add(approveBtn);
                add(rejectBtn);
            }
            
            JButton editBtn = createActionButton("✎", new Color(0, 123, 255));
            JButton deleteBtn = createActionButton("🗑", new Color(220, 53, 69));
            
            add(editBtn);
            add(deleteBtn);
            
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            
            return this;
        }
        
        private JButton createActionButton(String text, Color color) {
            JButton button = new JButton(text);
            button.setPreferredSize(new Dimension(30, 30));
            button.setBackground(color);
            button.setForeground(Color.WHITE);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            return button;
        }
    }    // Action Button Editor
    class ActionButtonEditor extends DefaultCellEditor {
        private JPanel panel;

        public ActionButtonEditor() {
            super(new JCheckBox());
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            
            panel.removeAll();
            
            // Get lease ID and status
            int leaseId = (Integer) table.getValueAt(row, 0);
            String status = (String) table.getValueAt(row, 7);
            
            if (status.equals("Menunggu")) {
                JButton approveBtn = createActionButton("✓", new Color(40, 167, 69));
                approveBtn.addActionListener(e -> {
                    stopCellEditing();
                    approveLease(leaseId);
                });
                
                JButton rejectBtn = createActionButton("✗", new Color(220, 53, 69));
                rejectBtn.addActionListener(e -> {
                    stopCellEditing();
                    rejectLease(leaseId);
                });
                
                panel.add(approveBtn);
                panel.add(rejectBtn);
            }
            
            JButton editBtn = createActionButton("✎", new Color(0, 123, 255));
            editBtn.addActionListener(e -> {
                stopCellEditing();
                showEditLeaseDialog(leaseId);
            });
            
            JButton deleteBtn = createActionButton("🗑", new Color(220, 53, 69));
            deleteBtn.addActionListener(e -> {
                stopCellEditing();
                deleteLease(leaseId);
            });
            
            panel.add(editBtn);
            panel.add(deleteBtn);
            
            return panel;
        }
        
        private JButton createActionButton(String text, Color color) {
            JButton button = new JButton(text);
            button.setPreferredSize(new Dimension(30, 30));
            button.setBackground(color);
            button.setForeground(Color.WHITE);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }
    }
}
