package com.leaslink.views;

import com.formdev.flatlaf.FlatLightLaf;
import com.leaslink.controllers.AuthController;
import com.leaslink.models.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import javax.imageio.ImageIO;

public class RegisterForm extends JFrame {
    private JTextField fullNameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleComboBox;
    private JButton registerButton;
    private JLabel loginLabel;
    private BufferedImage backgroundImage;
    private JToggleButton togglePasswordButton;
    private JToggleButton toggleConfirmPasswordButton;

    public RegisterForm() {
        loadBackgroundImage();
        initializeFrame();
        createComponents();
        setupEventListeners();
    }

    private void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/images/motorcycle-bg.jpg"));
        } catch (Exception e) {
            backgroundImage = createGradientBackground();
        }
    }

    private BufferedImage createGradientBackground() {
        BufferedImage img = new BufferedImage(1200, 700, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(45, 45, 45),
            1200, 700, new Color(80, 80, 80)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 1200, 700);
        
        g2d.setColor(new Color(255, 255, 255, 5));
        for (int i = 0; i < 1200; i += 100) {
            for (int j = 0; j < 700; j += 100) {
                g2d.fillOval(i, j, 2, 2);
            }
        }
        
        g2d.dispose();
        return img;
    }

    private void initializeFrame() {
        setTitle("LeasLink - Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void createComponents() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setOpaque(false);

        JPanel leftPanel = createLeftPanel();
        JPanel rightPanel = createRightPanel();

        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(700, 700));
        leftPanel.setOpaque(false);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(120, 80, 120, 80));
        
        JLabel titleLabel = new JLabel("Bergabung dengan");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 52));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("LeasLink");
        subtitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 52));
        subtitleLabel.setForeground(new Color(220, 53, 69));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descLabel = new JLabel("<html>Daftar sekarang dan nikmati kemudahan leasing motor<br>dengan proses yang cepat dan terpercaya.</html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        descLabel.setForeground(new Color(240, 240, 240));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(40));
        contentPanel.add(descLabel);
        
        leftPanel.add(contentPanel, BorderLayout.CENTER);
        
        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(500, 700));
        rightPanel.setOpaque(false);
        
        // Transparent form container with scroll
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        JPanel formContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(0, 0, 0, 120));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                
                g2d.dispose();
            }
        };
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setOpaque(false);
        formContainer.setBorder(new EmptyBorder(40, 50, 40, 50));
        
        // Position the form container
        JPanel centeringPanel = new JPanel(new GridBagLayout());
        centeringPanel.setOpaque(false);
        centeringPanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        
        // Logo
        JPanel logoPanel = createLogoPanel();
        
        // Title
        JLabel titleLabel = new JLabel("Buat Akun Baru");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Form fields
        JPanel fullNamePanel = createInputPanel("Nama Lengkap", fullNameField = new JTextField(), "Masukkan nama lengkap");
        JPanel emailPanel = createInputPanel("Email", emailField = new JTextField(), "username@gmail.com");
        JPanel rolePanel = createRolePanel();
        JPanel passwordPanel = createInputPanel("Kata Sandi", passwordField = new JPasswordField(), "password");
        JPanel confirmPasswordPanel = createInputPanel("Konfirmasi Kata Sandi", confirmPasswordField = new JPasswordField(), "password");
        
        // Register button
        registerButton = createStyledButton("Daftar Sekarang");
        
        // Login link
        JPanel loginPanel = createLoginPanel();
        
        // Add components to form
        formContainer.add(logoPanel);
        formContainer.add(Box.createVerticalStrut(20));
        formContainer.add(titleLabel);
        formContainer.add(Box.createVerticalStrut(25));
        formContainer.add(fullNamePanel);
        formContainer.add(Box.createVerticalStrut(20));
        formContainer.add(emailPanel);
        formContainer.add(Box.createVerticalStrut(20));
        formContainer.add(rolePanel);
        formContainer.add(Box.createVerticalStrut(20));
        formContainer.add(passwordPanel);
        formContainer.add(Box.createVerticalStrut(20));
        formContainer.add(confirmPasswordPanel);
        formContainer.add(Box.createVerticalStrut(30));
        formContainer.add(registerButton);
        formContainer.add(Box.createVerticalStrut(20));
        formContainer.add(loginPanel);
        
        scrollPane.setViewportView(formContainer);
        centeringPanel.add(scrollPane);
        rightPanel.add(centeringPanel, BorderLayout.CENTER);
        
        return rightPanel;
    }

    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setOpaque(false);
        logoPanel.setMaximumSize(new Dimension(400, 80));
        
        JLabel logoLabel = new JLabel();
        logoLabel.setText("<html><span style='color: #DC3545; font-size: 28px; font-weight: bold;'>M</span><span style='color: white; font-size: 20px; font-weight: bold;'>ITRA</span></html>");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        
        logoPanel.add(logoLabel);
        return logoPanel;
    }

    private JPanel createRolePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(350, 85));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel label = new JLabel("Role");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String[] roles = {"admin", "management", "collector"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        roleComboBox.setMaximumSize(new Dimension(350, 48));
        roleComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        roleComboBox.setBackground(new Color(255, 255, 255, 15));
        roleComboBox.setForeground(Color.WHITE);
        roleComboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 60), 1),
            BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));
        
        panel.add(label);
        panel.add(Box.createVerticalStrut(8));
        panel.add(roleComboBox);
        
        return panel;
    }

    private JPanel createInputPanel(String label, JTextField field, String placeholder) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JLabel titleLabel = new JLabel(label);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BorderLayout());
        fieldPanel.setOpaque(false);
        fieldPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 50), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        field.setOpaque(false);
        field.setBorder(null);
        field.setForeground(Color.WHITE);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(300, 25));
        setupPlaceholder(field, placeholder);
        
        fieldPanel.add(field, BorderLayout.CENTER);
        
        // Add toggle password button if this is a password field
        if (field instanceof JPasswordField) {
            JToggleButton toggleButton;
            if (field == passwordField) {
                togglePasswordButton = new JToggleButton("\uD83D\uDC41"); // Eye emoji
                toggleButton = togglePasswordButton;
            } else {
                toggleConfirmPasswordButton = new JToggleButton("\uD83D\uDC41"); // Eye emoji
                toggleButton = toggleConfirmPasswordButton;
            }
            
            toggleButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            toggleButton.setForeground(Color.WHITE);
            toggleButton.setOpaque(false);
            toggleButton.setContentAreaFilled(false);
            toggleButton.setBorder(null);
            toggleButton.setFocusPainted(false);
            toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            toggleButton.addActionListener(e -> {
                JPasswordField passField = (JPasswordField) field;
                if (toggleButton.isSelected()) {
                    passField.setEchoChar((char) 0); // Show password
                    toggleButton.setText("\uD83D\uDC42"); // Eye with line emoji
                } else {
                    passField.setEchoChar('•'); // Hide password
                    toggleButton.setText("\uD83D\uDC41"); // Eye emoji
                }
            });
            
            fieldPanel.add(toggleButton, BorderLayout.EAST);
        }
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(fieldPanel);
        
        return panel;
    }

    private void setupPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(new Color(255, 255, 255, 120));
        
        if (field instanceof JPasswordField) {
            JPasswordField passField = (JPasswordField) field;
            passField.setEchoChar((char) 0);
            passField.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    if (new String(passField.getPassword()).equals(placeholder)) {
                        passField.setText("");
                        passField.setEchoChar('•');
                        passField.setForeground(Color.WHITE);
                    }
                }
                public void focusLost(java.awt.event.FocusEvent evt) {
                    if (passField.getPassword().length == 0) {
                        passField.setText(placeholder);
                        passField.setEchoChar((char) 0);
                        passField.setForeground(new Color(255, 255, 255, 120));
                    }
                }
            });
        } else {
            field.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    if (field.getText().equals(placeholder)) {
                        field.setText("");
                        field.setForeground(Color.WHITE);
                    }
                }
                public void focusLost(java.awt.event.FocusEvent evt) {
                    if (field.getText().isEmpty()) {
                        field.setText(placeholder);
                        field.setForeground(new Color(255, 255, 255, 120));
                    }
                }
            });
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(new Color(220, 53, 69));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setMaximumSize(new Dimension(350, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(200, 35, 51));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(220, 53, 69));
            }
        });
        
        return button;
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginPanel.setOpaque(false);
        loginPanel.setMaximumSize(new Dimension(350, 40));
        
        JLabel loginTextLabel = new JLabel("Sudah punya akun? ");
        loginTextLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loginTextLabel.setForeground(new Color(255, 255, 255, 180));
        
        loginLabel = new JLabel("Masuk di sini");
        loginLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginLabel.setForeground(new Color(220, 53, 69));
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        loginPanel.add(loginTextLabel);
        loginPanel.add(loginLabel);
        
        return loginPanel;
    }

    private void setupEventListeners() {
        registerButton.addActionListener(e -> handleRegister());
        
        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openLoginForm();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                loginLabel.setForeground(new Color(200, 35, 51));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                loginLabel.setForeground(new Color(220, 53, 69));
            }
        });
        
        getRootPane().setDefaultButton(registerButton);
    }

    private void handleRegister() {
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String role = (String) roleComboBox.getSelectedItem();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validate input
        if (fullName.isEmpty() || fullName.equals("Masukkan nama lengkap") ||
            email.isEmpty() || email.equals("username@gmail.com") ||
            password.isEmpty() || password.equals("password") ||
            confirmPassword.isEmpty() || confirmPassword.equals("password")) {
            showError("Mohon isi semua field dengan benar");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Konfirmasi password tidak cocok");
            return;
        }

        if (password.length() < 6) {
            showError("Password minimal 6 karakter");
            return;
        }

        try {
            User user = new User();
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPassword(password);
            user.setRole(role);
            
            boolean success = AuthController.register(user);
            if (success) {
                showSuccess("Registrasi berhasil! Silakan login.");
                openLoginForm();
            } else {
                showError("Email sudah terdaftar");
            }
        } catch (SQLException ex) {
            showError("Database error: " + ex.getMessage());
        }
    }

    private void openLoginForm() {
        new LoginForm().setVisible(true);
        dispose();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Sukses",
            JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();
        
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            new RegisterForm().setVisible(true);
        });
    }
}