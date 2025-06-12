package com.leaslink.views.common;

import com.leaslink.controllers.ContractController;
import com.leaslink.models.FinancingContract;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ContractFinancingPanel extends JPanel {
    private JTable contractTable;
    private DefaultTableModel tableModel;
    private ContractController controller;
    private JTextField searchField;
    private JPanel detailPanel;
    private JComboBox<String> statusFilter;
    private JButton refreshButton;
    private JButton viewDetailButton;
    private JButton prevButton;
    private JButton nextButton;
    private JLabel contractIdLabel;
    private JLabel contractNameLabel;
    private JLabel contractAmountLabel;
    private JLabel contractDateLabel;
    private JLabel contractDueDateLabel;
    private JLabel contractStatusLabel;
    private JLabel contractTotalLabel;
    private JLabel contractInstallmentLabel;
    
    private List<FinancingContract> currentContracts;
    private int currentContractIndex = 0;

    public ContractFinancingPanel() {
        controller = new ContractController();
        initializeComponents();
        loadAllContracts();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(248, 249, 250));
        
        // Left Panel - Table
        JPanel leftPanel = createTablePanel();
        leftPanel.setPreferredSize(new Dimension(700, 0));
        
        // Right Panel - Detail
        JPanel rightPanel = createDetailPanel();
        rightPanel.setPreferredSize(new Dimension(400, 0));
        
        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(248, 249, 250));
        
        JLabel titleLabel = new JLabel("Kontrak Pembiayaan");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(52, 58, 64));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descLabel = new JLabel("Kelola dan lihat detail kontrak pembiayaan pelanggan");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descLabel.setForeground(new Color(108, 117, 125));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(descLabel);
        headerPanel.add(Box.createVerticalStrut(20));
        
        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(223, 227, 230), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Search and Filter Panel
        JPanel searchPanel = createSearchPanel();
        tablePanel.add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID Kontrak", "Nama Debitur", "NIK", "Jumlah Pinjaman", "Tenor", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        contractTable = new JTable(tableModel);
        contractTable.setRowHeight(35);
        contractTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        contractTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contractTable.setSelectionBackground(new Color(220, 53, 69));
        contractTable.setSelectionForeground(Color.WHITE);
        
        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < columns.length; i++) {
            contractTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Custom renderer for status column
        contractTable.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());
        
        JScrollPane tableScrollPane = new JScrollPane(contractTable);
        tableScrollPane.setPreferredSize(new Dimension(0, 400));
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        // Table selection listener
        contractTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showContractDetail();
            }
        });

        return tablePanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        
        JLabel searchLabel = new JLabel("Cari:");
        searchField = new JTextField(15);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JButton searchButton = new JButton("Cari");
        searchButton.setBackground(new Color(0, 123, 255));
        searchButton.setForeground(Color.WHITE);
        searchButton.setBorderPainted(false);
        searchButton.setFocusPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(e -> performSearch());
        
        JLabel filterLabel = new JLabel("Filter Status:");
        statusFilter = new JComboBox<>(new String[]{"Semua", "Aktif", "Menunggak", "Selesai"});
        statusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusFilter.addActionListener(e -> filterByStatus());
        
        refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(40, 167, 69));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBorderPainted(false);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadAllContracts());
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(filterLabel);
        searchPanel.add(statusFilter);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(refreshButton);
        
        return searchPanel;
    }

    private JPanel createDetailPanel() {
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBackground(Color.WHITE);
        detailPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(223, 227, 230), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JLabel headerLabel = new JLabel("KONTRAK LEASING");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(new Color(52, 58, 64));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Logo placeholder
        JLabel logoLabel = new JLabel("MITRA");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logoLabel.setForeground(new Color(220, 53, 69));
        logoLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        headerPanel.add(logoLabel, BorderLayout.EAST);
        
        // Contract ID
        contractIdLabel = new JLabel("ID: -");
        contractIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contractIdLabel.setForeground(new Color(108, 117, 125));
        
        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        idPanel.setBackground(Color.WHITE);
        idPanel.add(contractIdLabel);
        
        // Detail Fields
        JPanel fieldsPanel = createDetailFields();
        
        // Navigation Buttons
        JPanel navPanel = createNavigationPanel();
        
        detailPanel.add(headerPanel, BorderLayout.NORTH);
        detailPanel.add(idPanel, BorderLayout.CENTER);
        detailPanel.add(fieldsPanel, BorderLayout.CENTER);
        detailPanel.add(navPanel, BorderLayout.SOUTH);
        
        return detailPanel;
    }

    private JPanel createDetailFields() {
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        // Create detail labels with panels
        JPanel namePanel = createDetailLabelPanel("Nama", "");
        contractNameLabel = (JLabel) namePanel.getComponent(1);
        
        JPanel amountPanel = createDetailLabelPanel("Angsuran", "");
        contractAmountLabel = (JLabel) amountPanel.getComponent(1);
        
        JPanel datePanel = createDetailLabelPanel("Tanggal peminjaman", "");
        contractDateLabel = (JLabel) datePanel.getComponent(1);
        
        JPanel dueDatePanel = createDetailLabelPanel("Tanggal jatuh tempo", "");
        contractDueDateLabel = (JLabel) dueDatePanel.getComponent(1);
        
        JPanel totalPanel = createDetailLabelPanel("Total dibayar", "");
        contractTotalLabel = (JLabel) totalPanel.getComponent(1);
        
        JPanel statusPanel = createDetailLabelPanel("Status", "");
        contractStatusLabel = (JLabel) statusPanel.getComponent(1);
        
        JPanel installmentPanel = createDetailLabelPanel("Angsuran per bulan", "");
        contractInstallmentLabel = (JLabel) installmentPanel.getComponent(1);
        
        fieldsPanel.add(namePanel);
        fieldsPanel.add(Box.createVerticalStrut(15));
        fieldsPanel.add(amountPanel);
        fieldsPanel.add(Box.createVerticalStrut(15));
        fieldsPanel.add(datePanel);
        fieldsPanel.add(Box.createVerticalStrut(15));
        fieldsPanel.add(dueDatePanel);
        fieldsPanel.add(Box.createVerticalStrut(15));
        fieldsPanel.add(totalPanel);
        fieldsPanel.add(Box.createVerticalStrut(15));
        fieldsPanel.add(statusPanel);
        fieldsPanel.add(Box.createVerticalStrut(15));
        fieldsPanel.add(installmentPanel);
        
        return fieldsPanel;
    }

    private JPanel createDetailLabelPanel(String title, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(108, 117, 125));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueLabel.setForeground(new Color(52, 58, 64));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(valueLabel);
        
        return panel;
    }

    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        navPanel.setBackground(Color.WHITE);
        
        prevButton = new JButton("◀");
        prevButton.setBackground(new Color(220, 53, 69));
        prevButton.setForeground(Color.WHITE);
        prevButton.setBorderPainted(false);
        prevButton.setFocusPainted(false);
        prevButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        prevButton.setPreferredSize(new Dimension(40, 40));
        prevButton.addActionListener(e -> showPreviousContract());
        
        nextButton = new JButton("▶");
        nextButton.setBackground(new Color(220, 53, 69));
        nextButton.setForeground(Color.WHITE);
        nextButton.setBorderPainted(false);
        nextButton.setFocusPainted(false);
        nextButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nextButton.setPreferredSize(new Dimension(40, 40));
        nextButton.addActionListener(e -> showNextContract());
        
        navPanel.add(prevButton);
        navPanel.add(Box.createHorizontalStrut(10));
        navPanel.add(nextButton);
        
        return navPanel;
    }

    private void loadAllContracts() {
        currentContracts = controller.getAllContracts();
        refreshTable(currentContracts);
        if (!currentContracts.isEmpty()) {
            currentContractIndex = 0;
            contractTable.setRowSelectionInterval(0, 0);
            showContractDetail();
        }
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadAllContracts();
        } else {
            currentContracts = controller.searchContractsByNik(keyword);
            refreshTable(currentContracts);
            if (!currentContracts.isEmpty()) {
                currentContractIndex = 0;
                contractTable.setRowSelectionInterval(0, 0);
                showContractDetail();
            }
        }
    }

    private void filterByStatus() {
        String selectedStatus = (String) statusFilter.getSelectedItem();
        if ("Semua".equals(selectedStatus)) {
            loadAllContracts();
        } else {
            currentContracts = controller.getContractsByStatus(selectedStatus);
            refreshTable(currentContracts);
            if (!currentContracts.isEmpty()) {
                currentContractIndex = 0;
                contractTable.setRowSelectionInterval(0, 0);
                showContractDetail();
            }
        }
    }

    private void refreshTable(List<FinancingContract> contracts) {
        tableModel.setRowCount(0);
        for (FinancingContract contract : contracts) {
            tableModel.addRow(new Object[]{
                contract.getId(),
                contract.getDebtorName(),
                contract.getDebtorNik(),
                formatRupiah(contract.getLoanAmount()),
                contract.getTerm() + " bulan",
                contract.getStatus()
            });
        }
    }

    private void showContractDetail() {
        int selectedRow = contractTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < currentContracts.size()) {
            currentContractIndex = selectedRow;
            FinancingContract contract = currentContracts.get(selectedRow);
            updateDetailView(contract);
        }
    }

    private void showPreviousContract() {
        if (currentContractIndex > 0) {
            currentContractIndex--;
            contractTable.setRowSelectionInterval(currentContractIndex, currentContractIndex);
            updateDetailView(currentContracts.get(currentContractIndex));
        }
    }

    private void showNextContract() {
        if (currentContractIndex < currentContracts.size() - 1) {
            currentContractIndex++;
            contractTable.setRowSelectionInterval(currentContractIndex, currentContractIndex);
            updateDetailView(currentContracts.get(currentContractIndex));
        }
    }

    private void updateDetailView(FinancingContract contract) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        
        contractIdLabel.setText("ID " + contract.getId());
        contractNameLabel.setText(contract.getDebtorName());
        contractAmountLabel.setText(formatRupiah(contract.getLoanAmount()));
        contractDateLabel.setText(sdf.format(contract.getStartDate()));
        contractDueDateLabel.setText(sdf.format(contract.getDueDate()));
        contractTotalLabel.setText(formatRupiah(contract.calculateTotalPayment()));
        contractStatusLabel.setText(contract.getStatus());
        contractInstallmentLabel.setText(formatRupiah(contract.calculateMonthlyInstallment()));
        
        // Update status color
        switch (contract.getStatus()) {
            case "Aktif":
                contractStatusLabel.setForeground(new Color(0, 123, 255));
                break;
            case "Menunggak":
                contractStatusLabel.setForeground(new Color(220, 53, 69));
                break;
            case "Selesai":
                contractStatusLabel.setForeground(new Color(40, 167, 69));
                break;
            default:
                contractStatusLabel.setForeground(new Color(52, 58, 64));
        }
        
        // Update navigation buttons
        prevButton.setEnabled(currentContractIndex > 0);
        nextButton.setEnabled(currentContractIndex < currentContracts.size() - 1);
    }

    private String formatRupiah(double amount) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return nf.format(amount);
    }

    // Custom renderer for status column
    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                     boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (!isSelected) {
                String status = (String) value;
                switch (status) {
                    case "Aktif":
                        setForeground(new Color(0, 123, 255));
                        break;
                    case "Menunggak":
                        setForeground(new Color(220, 53, 69));
                        break;
                    case "Selesai":
                        setForeground(new Color(40, 167, 69));
                        break;
                    default:
                        setForeground(new Color(52, 58, 64));
                }
            }
            
            setHorizontalAlignment(SwingConstants.CENTER);
            return this;
        }
    }
}