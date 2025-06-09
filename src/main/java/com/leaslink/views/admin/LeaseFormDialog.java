package com.leaslink.views.admin;

import com.leaslink.controllers.LeaseController;
import com.leaslink.models.Lease;
import com.leaslink.models.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Dialog untuk menambah atau mengedit kontrak lease
 */
public class LeaseFormDialog extends JDialog {
    private User currentUser;
    private Lease editingLease; // null untuk mode tambah, berisi data untuk mode edit
    private boolean confirmed = false;

    // Form components
    private JComboBox<CustomerItem> customerComboBox;
    private JComboBox<MotorcycleItem> motorcycleComboBox;
    private JTextField leaseAmountField;
    private JTextField monthlyPaymentField;
    private JSpinner durationSpinner;
    private JTextField startDateField;
    private JTextField endDateField;
    private JComboBox<String> statusComboBox;
    private JTextArea notesArea;

    // Buttons
    private JButton saveButton;
    private JButton cancelButton;
    private JButton calculateButton;

    public LeaseFormDialog(JFrame parent, String title, Lease lease, User currentUser) {
        super(parent, title, true);
        this.currentUser = currentUser;
        this.editingLease = lease;
        
        initializeComponents();
        loadComboBoxData();
        
        if (lease != null) {
            populateFields(lease);
        }
        
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        setSize(600, 700);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(248, 249, 250));

        // Form panel
        JPanel formPanel = createFormPanel();
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(223, 227, 230), 1),
            new EmptyBorder(30, 30, 30, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Customer
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Customer:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        customerComboBox = new JComboBox<>();
        customerComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        customerComboBox.setPreferredSize(new Dimension(300, 35));
        formPanel.add(customerComboBox, gbc);

        row++;

        // Motorcycle
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Motor:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        motorcycleComboBox = new JComboBox<>();
        motorcycleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        motorcycleComboBox.setPreferredSize(new Dimension(300, 35));
        motorcycleComboBox.addActionListener(e -> updateLeaseAmountFromMotorcycle());
        formPanel.add(motorcycleComboBox, gbc);

        row++;

        // Lease Amount
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Jumlah Lease (Rp):"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        leaseAmountField = new JTextField();
        leaseAmountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        leaseAmountField.setPreferredSize(new Dimension(300, 35));
        formPanel.add(leaseAmountField, gbc);

        row++;

        // Monthly Payment
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Pembayaran/Bulan (Rp):"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JPanel monthlyPanel = new JPanel(new BorderLayout());
        monthlyPanel.setOpaque(false);
        
        monthlyPaymentField = new JTextField();
        monthlyPaymentField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        monthlyPaymentField.setPreferredSize(new Dimension(200, 35));
        
        calculateButton = new JButton("Hitung");
        calculateButton.setBackground(new Color(0, 123, 255));
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setBorderPainted(false);
        calculateButton.setFocusPainted(false);
        calculateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        calculateButton.addActionListener(e -> calculateMonthlyPayment());
        
        monthlyPanel.add(monthlyPaymentField, BorderLayout.CENTER);
        monthlyPanel.add(calculateButton, BorderLayout.EAST);
        
        formPanel.add(monthlyPanel, gbc);

        row++;

        // Duration
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Durasi (bulan):"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        durationSpinner = new JSpinner(new SpinnerNumberModel(12, 1, 60, 1));
        durationSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ((JSpinner.DefaultEditor) durationSpinner.getEditor()).getTextField().setPreferredSize(new Dimension(300, 35));
        formPanel.add(durationSpinner, gbc);

        row++;

        // Start Date
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Tanggal Mulai (dd/MM/yyyy):"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        startDateField = new JTextField();
        startDateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        startDateField.setPreferredSize(new Dimension(300, 35));
        startDateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        startDateField.addActionListener(e -> calculateEndDate());
        formPanel.add(startDateField, gbc);

        row++;

        // End Date
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Tanggal Berakhir:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        endDateField = new JTextField();
        endDateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        endDateField.setPreferredSize(new Dimension(300, 35));
        endDateField.setEditable(false);
        endDateField.setBackground(new Color(248, 249, 250));
        formPanel.add(endDateField, gbc);

        row++;

        // Status
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Status:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        statusComboBox = new JComboBox<>(new String[]{"pending", "active", "completed", "cancelled"});
        statusComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusComboBox.setPreferredSize(new Dimension(300, 35));
        formPanel.add(statusComboBox, gbc);

        row++;

        // Notes
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Catatan:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        notesArea = new JTextArea(4, 30);
        notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));
        
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        notesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        formPanel.add(notesScrollPane, gbc);

        // Calculate initial end date
        calculateEndDate();

        return formPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        cancelButton = new JButton("Batal");
        cancelButton.setPreferredSize(new Dimension(100, 40));
        cancelButton.setBackground(new Color(108, 117, 125));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setBorderPainted(false);
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> dispose());

        saveButton = new JButton(editingLease == null ? "Simpan" : "Update");
        saveButton.setPreferredSize(new Dimension(100, 40));
        saveButton.setBackground(new Color(40, 167, 69));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setBorderPainted(false);
        saveButton.setFocusPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.addActionListener(e -> saveLease());

        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(saveButton);

        return buttonPanel;
    }

    private void loadComboBoxData() {
        try {
            // Load customers
            List<String[]> customers = LeaseController.getAllCustomers();
            customerComboBox.addItem(new CustomerItem(0, "-- Pilih Customer --", ""));
            for (String[] customer : customers) {
                customerComboBox.addItem(new CustomerItem(
                    Integer.parseInt(customer[0]),
                    customer[1],
                    customer[2]
                ));
            }

            // Load available motorcycles
            List<String[]> motorcycles = LeaseController.getAvailableMotorcycles();
            motorcycleComboBox.addItem(new MotorcycleItem(0, "-- Pilih Motor --", "", "", "", 0));
            for (String[] motorcycle : motorcycles) {
                motorcycleComboBox.addItem(new MotorcycleItem(
                    Integer.parseInt(motorcycle[0]),
                    motorcycle[1], // brand
                    motorcycle[2], // model
                    motorcycle[3], // year
                    motorcycle[4], // color
                    Double.parseDouble(motorcycle[5]) // price
                ));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Gagal memuat data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFields(Lease lease) {
        // Find and select customer
        for (int i = 0; i < customerComboBox.getItemCount(); i++) {
            CustomerItem item = customerComboBox.getItemAt(i);
            if (item.getId() == lease.getCustomerId()) {
                customerComboBox.setSelectedIndex(i);
                break;
            }
        }

        // Find and select motorcycle (may not be in available list if editing)
        boolean foundMotorcycle = false;
        for (int i = 0; i < motorcycleComboBox.getItemCount(); i++) {
            MotorcycleItem item = motorcycleComboBox.getItemAt(i);
            if (item.getId() == lease.getMotorcycleId()) {
                motorcycleComboBox.setSelectedIndex(i);
                foundMotorcycle = true;
                break;
            }
        }

        // If motorcycle not found in available list, add it
        if (!foundMotorcycle) {
            MotorcycleItem currentMotorcycle = new MotorcycleItem(
                lease.getMotorcycleId(),
                lease.getMotorcycleBrand(),
                lease.getMotorcycleModel(),
                lease.getMotorcycleYear(),
                lease.getMotorcycleColor(),
                0 // price not needed for editing
            );
            motorcycleComboBox.addItem(currentMotorcycle);
            motorcycleComboBox.setSelectedItem(currentMotorcycle);
        }

        leaseAmountField.setText(String.valueOf((long) lease.getLeaseAmount()));
        monthlyPaymentField.setText(String.valueOf((long) lease.getMonthlyPayment()));
        durationSpinner.setValue(lease.getLeaseDuration());
        startDateField.setText(lease.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        endDateField.setText(lease.getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        statusComboBox.setSelectedItem(lease.getStatus());
    }

    private void updateLeaseAmountFromMotorcycle() {
        MotorcycleItem selectedMotorcycle = (MotorcycleItem) motorcycleComboBox.getSelectedItem();
        if (selectedMotorcycle != null && selectedMotorcycle.getId() > 0) {
            leaseAmountField.setText(String.valueOf((long) selectedMotorcycle.getPrice()));
            calculateMonthlyPayment();
        }
    }

    private void calculateMonthlyPayment() {
        try {
            double leaseAmount = Double.parseDouble(leaseAmountField.getText().replaceAll("[^\\d.]", ""));
            int duration = (Integer) durationSpinner.getValue();
            
            if (leaseAmount > 0 && duration > 0) {
                double monthlyPayment = leaseAmount / duration;
                monthlyPaymentField.setText(String.valueOf((long) monthlyPayment));
            }
        } catch (NumberFormatException e) {
            // Ignore invalid input
        }
    }

    private void calculateEndDate() {
        try {
            String startDateText = startDateField.getText();
            int duration = (Integer) durationSpinner.getValue();
            
            LocalDate startDate = LocalDate.parse(startDateText, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            LocalDate endDate = startDate.plusMonths(duration);
            
            endDateField.setText(endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } catch (DateTimeParseException e) {
            endDateField.setText("");
        }
    }

    private void saveLease() {
        try {
            // Validate inputs
            String validationError = validateInputs();
            if (validationError != null) {
                JOptionPane.showMessageDialog(this,
                    validationError,
                    "Error Validasi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create lease object
            Lease lease = createLeaseFromInputs();
            
            // Validate business rules
            String businessValidationError = LeaseController.validateLease(lease);
            if (businessValidationError != null) {
                JOptionPane.showMessageDialog(this,
                    businessValidationError,
                    "Error Validasi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Save lease
            boolean success;
            if (editingLease == null) {
                success = LeaseController.createLease(lease);
            } else {
                lease.setId(editingLease.getId());
                success = LeaseController.updateLease(lease);
            }

            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Kontrak berhasil " + (editingLease == null ? "disimpan" : "diupdate") + "!",
                    "Berhasil",
                    JOptionPane.INFORMATION_MESSAGE);
                confirmed = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Gagal " + (editingLease == null ? "menyimpan" : "mengupdate") + " kontrak!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private String validateInputs() {
        CustomerItem customer = (CustomerItem) customerComboBox.getSelectedItem();
        if (customer == null || customer.getId() == 0) {
            return "Customer harus dipilih";
        }

        MotorcycleItem motorcycle = (MotorcycleItem) motorcycleComboBox.getSelectedItem();
        if (motorcycle == null || motorcycle.getId() == 0) {
            return "Motor harus dipilih";
        }

        if (leaseAmountField.getText().trim().isEmpty()) {
            return "Jumlah lease harus diisi";
        }

        if (monthlyPaymentField.getText().trim().isEmpty()) {
            return "Pembayaran bulanan harus diisi";
        }

        if (startDateField.getText().trim().isEmpty()) {
            return "Tanggal mulai harus diisi";
        }

        try {
            Double.parseDouble(leaseAmountField.getText().replaceAll("[^\\d.]", ""));
        } catch (NumberFormatException e) {
            return "Jumlah lease harus berupa angka";
        }

        try {
            Double.parseDouble(monthlyPaymentField.getText().replaceAll("[^\\d.]", ""));
        } catch (NumberFormatException e) {
            return "Pembayaran bulanan harus berupa angka";
        }

        try {
            LocalDate.parse(startDateField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            return "Format tanggal mulai tidak valid (dd/MM/yyyy)";
        }

        return null;
    }

    private Lease createLeaseFromInputs() {
        CustomerItem customer = (CustomerItem) customerComboBox.getSelectedItem();
        MotorcycleItem motorcycle = (MotorcycleItem) motorcycleComboBox.getSelectedItem();
        
        double leaseAmount = Double.parseDouble(leaseAmountField.getText().replaceAll("[^\\d.]", ""));
        double monthlyPayment = Double.parseDouble(monthlyPaymentField.getText().replaceAll("[^\\d.]", ""));
        int duration = (Integer) durationSpinner.getValue();
        LocalDate startDate = LocalDate.parse(startDateField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalDate endDate = LocalDate.parse(endDateField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String status = (String) statusComboBox.getSelectedItem();

        return new Lease(
            customer.getId(),
            motorcycle.getId(),
            leaseAmount,
            monthlyPayment,
            duration,
            startDate,
            endDate,
            status,
            currentUser.getId()
        );
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    // Helper classes for ComboBox items
    private static class CustomerItem {
        private int id;
        private String name;
        private String email;

        public CustomerItem(int id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }

        @Override
        public String toString() {
            return id == 0 ? name : name + " (" + email + ")";
        }
    }

    private static class MotorcycleItem {
        private int id;
        private String brand;
        private String model;
        private String year;
        private String color;
        private double price;

        public MotorcycleItem(int id, String brand, String model, String year, String color, double price) {
            this.id = id;
            this.brand = brand;
            this.model = model;
            this.year = year;
            this.color = color;
            this.price = price;
        }

        public int getId() { return id; }
        public String getBrand() { return brand; }
        public String getModel() { return model; }
        public String getYear() { return year; }
        public String getColor() { return color; }
        public double getPrice() { return price; }

        @Override
        public String toString() {
            return id == 0 ? brand : brand + " " + model + " " + year + " (" + color + ")";
        }
    }
}
