package com.leaslink.views.common;

import com.leaslink.controllers.ContractController;
import com.leaslink.models.FinancingContract;

import javax.swing.*;
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
    private JLabel[] detailLabels;
    private JLabel placeholderDetail;

    public ContractFinancingPanel() {
        controller = new ContractController();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Kontrak Pembiayaan");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(52, 58, 64));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel descLabel = new JLabel("Lihat daftar kontrak pembiayaan dari pelanggan.");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descLabel.setForeground(new Color(108, 117, 125));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(descLabel);
        headerPanel.add(Box.createVerticalStrut(20));
        add(headerPanel, BorderLayout.NORTH);


        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Cari");
        searchButton.addActionListener(e -> performSearch());
        searchPanel.add(new JLabel("Cari berdasarkan NIK: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        mainPanel.add(searchPanel);

        // Tabel
        String[] columns = {"ID Kontrak", "NIK", "Jumlah", "Tenor", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        contractTable = new JTable(tableModel);
        contractTable.setSelectionBackground(new Color(220, 53, 69)); // Merah
        contractTable.setSelectionForeground(Color.WHITE);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < columns.length; i++) {
            contractTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        JScrollPane tableScroll = new JScrollPane(contractTable);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(tableScroll, BorderLayout.CENTER);
        mainPanel.add(tablePanel);

        // Detail
        detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createTitledBorder("Detail Kontrak"));
        detailPanel.setBackground(Color.WHITE);
        JPanel grid = new JPanel(new GridLayout(0, 2, 10, 5));
        grid.setBackground(Color.WHITE);
        detailLabels = new JLabel[]{
            new JLabel(), new JLabel(), new JLabel(), new JLabel(),
            new JLabel(), new JLabel(), new JLabel(), new JLabel()
        };
        String[] titles = {
            "ID Kontrak:", "NIK Debitur:", "Jumlah Pinjaman:", "Suku Bunga:",
            "Tenor:", "Tanggal Mulai:", "Tanggal Jatuh Tempo:", "Status:"
        };
        for (int i = 0; i < titles.length; i++) {
            grid.add(new JLabel(titles[i]));
            grid.add(detailLabels[i]);
        }
        placeholderDetail = new JLabel("Silakan pilih kontrak untuk melihat detail.");
        placeholderDetail.setFont(new Font("SansSerif", Font.ITALIC, 12));
        placeholderDetail.setHorizontalAlignment(SwingConstants.CENTER);
        detailPanel.add(placeholderDetail, BorderLayout.CENTER);
        mainPanel.add(detailPanel);

        add(mainPanel, BorderLayout.CENTER);

        contractTable.getSelectionModel().addListSelectionListener(e -> showDetail());

        loadAllContracts();
    }

    private void loadAllContracts() {
        List<FinancingContract> contracts = controller.getAllContracts();
        refreshTable(contracts);
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        List<FinancingContract> contracts = keyword.isEmpty()
            ? controller.getAllContracts()
            : controller.searchContractsByNik(keyword);
        refreshTable(contracts);
    }

    private void refreshTable(List<FinancingContract> contracts) {
        tableModel.setRowCount(0);
        for (FinancingContract fc : contracts) {
            tableModel.addRow(new Object[]{
                fc.getId(),
                fc.getDebtorNik(),
                formatRupiah(fc.getLoanAmount()),
                fc.getTerm() + " bulan",
                fc.getStatus()
            });
        }
        showPlaceholder();
    }

    private void showPlaceholder() {
        placeholderDetail.setVisible(true);
        for (JLabel label : detailLabels) label.setText("-");
        detailPanel.removeAll();
        detailPanel.setPreferredSize(new Dimension(100, 173));
        detailPanel.add(placeholderDetail, BorderLayout.CENTER);
        detailPanel.revalidate();
        detailPanel.repaint();
    }

    private void showDetail() {
        int row = contractTable.getSelectedRow();
        if (row >= 0) {
            String id = (String) tableModel.getValueAt(row, 0);
            String nik = (String) tableModel.getValueAt(row, 1);
            FinancingContract fc = controller.getContractDetail(id, nik);

            if (fc != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                detailLabels[0].setText(fc.getId());
                detailLabels[1].setText(fc.getDebtorNik());
                detailLabels[2].setText(formatRupiah(fc.getLoanAmount()));
                detailLabels[3].setText(fc.getInterestRate() + "%");
                detailLabels[4].setText(fc.getTerm() + " bulan");
                detailLabels[5].setText(sdf.format(fc.getStartDate()));
                detailLabels[6].setText(sdf.format(fc.getDueDate()));
                detailLabels[7].setText(fc.getStatus());

                detailPanel.removeAll();
                detailPanel.setPreferredSize(new Dimension(100, 165));
                JPanel grid = new JPanel(new GridLayout(0, 2, 10, 5));
                grid.setBackground(Color.WHITE);
                String[] titles = {
                    "ID Kontrak:", "NIK Debitur:", "Jumlah Pinjaman:", "Suku Bunga:",
                    "Tenor:", "Tanggal Mulai:", "Tanggal Jatuh Tempo:", "Status:"
                };
                for (int i = 0; i < titles.length; i++) {
                    grid.add(new JLabel(titles[i]));
                    grid.add(detailLabels[i]);
                }
                detailPanel.add(grid, BorderLayout.CENTER);
                detailPanel.revalidate();
                detailPanel.repaint();
            }
        }
    }

    private String formatRupiah(double amount) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
        return nf.format(amount);
    }
}
