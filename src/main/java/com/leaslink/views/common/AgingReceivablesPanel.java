package com.leaslink.views.common;

import com.leaslink.controllers.AgingController;
import com.leaslink.models.AgingData;
import com.leaslink.models.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Panel untuk menampilkan Aging Receivables
 * Dapat digunakan oleh Admin (read-only) dan Management (dengan tombol notifikasi)
 */
public class AgingReceivablesPanel extends JPanel {
    private User currentUser;
    private boolean showNotifyButtons;
    private AgingController agingController;
    private JTable agingTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel totalLabel;
    private NumberFormat currencyFormat;

    public AgingReceivablesPanel(User currentUser, boolean showNotifyButtons) {
        this.currentUser = currentUser;
        this.showNotifyButtons = showNotifyButtons;
        this.agingController = new AgingController();
        // Fixed: Use Locale.of() instead of deprecated constructor
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("id", "ID"));
        
        initializeComponents();
        loadAgingData();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Search Panel
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.CENTER);

        // Table Panel
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Laporan Aging Piutang");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(52, 58, 64));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String description = showNotifyButtons ? 
            "Kelola aging piutang dan kirim notifikasi ke penagih" :
            "Lihat laporan aging piutang pelanggan";
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descLabel.setForeground(new Color(108, 117, 125));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(descLabel);
        headerPanel.add(Box.createVerticalStrut(20));

        return headerPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Cari ID:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));

        JButton searchButton = new JButton("Cari");
        searchButton.setBackground(new Color(0, 123, 255));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchButton.setBorderPainted(false);
        searchButton.setFocusPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(e -> performSearch());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(40, 167, 69));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.setBorderPainted(false);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadAgingData());

        searchPanel.add(searchLabel);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(searchButton);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(refreshButton);

        return searchPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Create table
        String[] columnNames;
        if (showNotifyButtons) {
            columnNames = new String[]{
                "ID Pelanggan", "ID Leasing", "Umur Piutang", "0-30 hari", 
                "31-60 hari", "> 60 hari", "Notifikasi"
            };
        } else {
            columnNames = new String[]{
                "ID Pelanggan", "ID Leasing", "Umur Piutang", "0-30 hari", 
                "31-60 hari", "> 60 hari"
            };
        }

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return showNotifyButtons && column == columnNames.length - 1;
            }
        };

        agingTable = new JTable(tableModel);
        agingTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        agingTable.setRowHeight(45);
        agingTable.setGridColor(new Color(223, 227, 230));
        agingTable.setSelectionBackground(new Color(220, 53, 69, 50));
        agingTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        agingTable.getTableHeader().setBackground(new Color(248, 249, 250));
        agingTable.getTableHeader().setForeground(new Color(52, 58, 64));
        agingTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(220, 53, 69)));

        // Set column widths
        TableColumn column;
        int[] columnWidths = showNotifyButtons ? 
            new int[]{120, 120, 100, 100, 100, 100, 100} :
            new int[]{150, 150, 120, 120, 120, 120};

        for (int i = 0; i < columnWidths.length; i++) {
            column = agingTable.getColumnModel().getColumn(i);
            column.setPreferredWidth(columnWidths[i]);
        }

        // Set button renderer and editor for notification column
        if (showNotifyButtons) {
            TableColumn notifyColumn = agingTable.getColumnModel().getColumn(columnNames.length - 1);
            notifyColumn.setCellRenderer(new ButtonRenderer());
            notifyColumn.setCellEditor(new ButtonEditor(new JCheckBox()));
        }

        JScrollPane scrollPane = new JScrollPane(agingTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(223, 227, 230), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Total panel
        JPanel totalPanel = createTotalPanel();

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(totalPanel, BorderLayout.SOUTH);

        return tablePanel;
    }

    private JPanel createTotalPanel() {
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(new Color(220, 53, 69));
        totalPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        totalPanel.setPreferredSize(new Dimension(0, 50));

        JLabel totalTitleLabel = new JLabel("Grand Total");
        totalTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalTitleLabel.setForeground(Color.WHITE);

        totalLabel = new JLabel("Rp 0");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(Color.WHITE);

        totalPanel.add(totalTitleLabel, BorderLayout.WEST);
        totalPanel.add(totalLabel, BorderLayout.EAST);

        return totalPanel;
    }

    private void loadAgingData() {
        List<AgingData> agingDataList = agingController.getAllAgingData();
        populateTable(agingDataList);
    }

    private void performSearch() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            loadAgingData();
            return;
        }

        List<AgingData> searchResults = agingController.searchAgingData(searchText);
        populateTable(searchResults);
    }

    private void populateTable(List<AgingData> agingDataList) {
        // Clear existing data
        tableModel.setRowCount(0);

        double total0_30 = 0;
        double total31_60 = 0;
        double totalOver60 = 0;

        // Add data to table
        for (AgingData data : agingDataList) {
            Object[] rowData;
            
            if (showNotifyButtons) {
                rowData = new Object[]{
                    data.getCustomerId(),
                    data.getLeaseId(),
                    data.getAge() + " hari",
                    formatCurrency(data.getRange0_30()),
                    formatCurrency(data.getRange31_60()),
                    formatCurrency(data.getRangeOver60()),
                    "Notifikasi"
                };
            } else {
                rowData = new Object[]{
                    data.getCustomerId(),
                    data.getLeaseId(),
                    data.getAge() + " hari",
                    formatCurrency(data.getRange0_30()),
                    formatCurrency(data.getRange31_60()),
                    formatCurrency(data.getRangeOver60())
                };
            }

            tableModel.addRow(rowData);

            // Calculate totals
            total0_30 += data.getRange0_30();
            total31_60 += data.getRange31_60();
            totalOver60 += data.getRangeOver60();
        }

        // Update total label
        double grandTotal = total0_30 + total31_60 + totalOver60;
        totalLabel.setText(formatCurrency(grandTotal));
    }

    private String formatCurrency(double amount) {
        if (amount == 0) return "0";
        return String.format("Rp %,.0f", amount);
    }

    private void sendNotification(int row) {
        String customerId = (String) tableModel.getValueAt(row, 0);
        String leaseId = (String) tableModel.getValueAt(row, 1);

        boolean success = agingController.sendNotificationToCollector(customerId, leaseId, currentUser);
        
        if (success) {
            JOptionPane.showMessageDialog(this,
                "Notifikasi berhasil dikirim ke penagih untuk pelanggan " + customerId,
                "Notifikasi Terkirim",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Gagal mengirim notifikasi. Silakan coba lagi.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Button Renderer Class
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(220, 53, 69));
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setBorderPainted(false);
            setFocusPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Notifikasi" : value.toString());
            return this;
        }
    }

    // Button Editor Class
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(new Color(220, 53, 69));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "Notifikasi" : value.toString();
            button.setText(label);
            isPushed = true;
            currentRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                sendNotification(currentRow);
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}