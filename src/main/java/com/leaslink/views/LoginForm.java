package com.leaslink.views;

import com.formdev.flatlaf.FlatLightLaf;
import com.leaslink.controllers.AuthController;
import com.leaslink.models.User;
import com.leaslink.views.admin.AdminDashboard;
import com.leaslink.views.management.ManagementDashboard;
import com.leaslink.views.collector.CollectorDashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import javax.imageio.ImageIO;

public class LoginForm extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel switchModeLabel;
    private BufferedImage backgroundImage;
    private BufferedImage logoImage;
    private boolean isLoginAsStaff = true;

    public LoginForm() {
        loadImages();
        initializeFrame();
        createComponents();
        setupEventListeners();
    }

    private void loadImages() {
        try {
            // Load background image from resources
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/img/motorcycle-bg.png"));
        } catch (Exception e) {
            // If image not found, create a gradient background
            backgroundImage = createGradientBackground();
        }

        try {
            // Load logo image from resources
            logoImage = ImageIO.read(getClass().getResourceAsStream("/img/logomitra.png"));
        } catch (Exception e) {
            // Logo will be created as text if image not found
            logoImage = null;
        }
    }

    private BufferedImage createGradientBackground() {
        BufferedImage img = new BufferedImage(1200, 700, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        
        // Create motorcycle-themed gradient
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(60, 60, 60),
            1200, 700, new Color(40, 40, 40)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 1200, 700);
        
        // Add subtle pattern
        g2d.setColor(new Color(255, 255, 255, 3));
        for (int i = 0; i < 1200; i += 80) {
            for (int j = 0; j < 700; j += 80) {
                g2d.fillOval(i, j, 2, 2);
            }
        }
        
        g2d.dispose();
        return img;
    }

    private void initializeFrame() {
        setTitle("LeasLink - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void createComponents() {
        // Create main panel with background
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

        // Left panel with company info
        JPanel leftPanel = createLeftPanel();
        
        // Right panel with login form
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
        
        // Content panel for text
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(100, 60, 100, 60));
        
        // Main title
        JLabel titleLabel = new JLabel("Mitra Perjalanan Anda");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Setiap Hari");
        subtitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Description
        JLabel descLabel = new JLabel("<html>Kami hadir sebagai solusi leasing motor yang aman,<br/>terjangkau, dan terpercaya untuk setiap kebutuhan.</html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        descLabel.setForeground(new Color(230, 230, 230));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(35));
        contentPanel.add(descLabel);
        
        leftPanel.add(contentPanel, BorderLayout.CENTER);
        
        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(500, 700));
        rightPanel.setOpaque(false);
        
        // Center the form container
        JPanel centeringPanel = new JPanel(new GridBagLayout());
        centeringPanel.setOpaque(false);
        centeringPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        
        // Transparent form container
        JPanel formContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Semi-transparent background with rounded corners
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                
                // Subtle border
                g2d.setColor(new Color(255, 255, 255, 25));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);
                
                g2d.dispose();
            }
        };
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setOpaque(false);
        formContainer.setBorder(new EmptyBorder(50, 50, 50, 50));
        formContainer.setPreferredSize(new Dimension(400, 500));
        
        // Logo
        JPanel logoPanel = createLogoPanel();
        
        // Form fields
        JPanel emailPanel = createInputPanel("Email", emailField = new JTextField(), "username@gmail.com");
        JPanel passwordPanel = createPasswordPanel("Kata Sandi", passwordField = new JPasswordField(), "password");
        
        // Login button
        loginButton = createStyledButton("Sign in");
        
        // Mode switch panel
        JPanel modePanel = createModePanel();
        
        // Add components to form
        formContainer.add(logoPanel);
        formContainer.add(Box.createVerticalStrut(30));
        formContainer.add(emailPanel);
        formContainer.add(Box.createVerticalStrut(20));
        formContainer.add(passwordPanel);
        formContainer.add(Box.createVerticalStrut(30));
        formContainer.add(loginButton);
        formContainer.add(Box.createVerticalStrut(20));
        formContainer.add(modePanel);
        
        centeringPanel.add(formContainer);
        rightPanel.add(centeringPanel, BorderLayout.CENTER);
        
        return rightPanel;
    }

    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setOpaque(false);
        logoPanel.setMaximumSize(new Dimension(400, 80));
    
        if (logoImage != null) {
            int logoHeight = 100;
            int logoWidth = (logoImage.getWidth(null) * logoHeight) / logoImage.getHeight(null);
            ImageIcon icon = new ImageIcon(logoImage.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH));
            JLabel logoLabel = new JLabel(icon);
            logoPanel.add(logoLabel);
        } else {
            JLabel logoLabel = new JLabel();
            logoLabel.setText("<html><span style='color: #DC3545; font-size: 32px; font-weight: bold;'>M</span><span style='color: white; font-size: 24px; font-weight: bold;'>ITRA</span></html>");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
            logoPanel.add(logoLabel);
        }
    
        return logoPanel;
    }

    private JPanel createInputPanel(String labelText, JTextField field, String placeholder) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(300, 70));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Field container with transparent background
        JPanel fieldContainer = new JPanel(new BorderLayout());
        fieldContainer.setOpaque(false);
        fieldContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        fieldContainer.setPreferredSize(new Dimension(300, 45));
        fieldContainer.setMaximumSize(new Dimension(300, 45));
        fieldContainer.setMinimumSize(new Dimension(300, 45));
        
        field.setOpaque(false);
        field.setBorder(null);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBackground(new Color(255, 255, 255, 10));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        setupPlaceholder(field, placeholder);
        
        fieldContainer.add(field, BorderLayout.CENTER);
        
        panel.add(label);
        panel.add(Box.createVerticalStrut(8));
        panel.add(fieldContainer);
        
        return panel;
    }

    private JPanel createPasswordPanel(String labelText, JPasswordField field, String placeholder) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(300, 70));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Field container with transparent background
        JPanel fieldContainer = new JPanel(new BorderLayout());
        fieldContainer.setOpaque(false);
        fieldContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        fieldContainer.setPreferredSize(new Dimension(300, 45));
        fieldContainer.setMaximumSize(new Dimension(300, 45));
        fieldContainer.setMinimumSize(new Dimension(300, 45));
        
        field.setOpaque(false);
        field.setBorder(null);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBackground(new Color(255, 255, 255, 10));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        setupPlaceholder(field, placeholder);
        
        // Eye toggle button
        JButton eyeButton = new JButton("👁");
        eyeButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        eyeButton.setForeground(new Color(255, 255, 255, 120));
        eyeButton.setOpaque(false);
        eyeButton.setContentAreaFilled(false);
        eyeButton.setBorder(null);
        eyeButton.setFocusPainted(false);
        eyeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        eyeButton.setPreferredSize(new Dimension(25, 20));
        
        eyeButton.addActionListener(e -> {
            if (field.getEchoChar() == 0) {
                field.setEchoChar('•');
                eyeButton.setText("👁");
            } else {
                field.setEchoChar((char) 0);
                eyeButton.setText("🙈");
            }
        });
        
        fieldContainer.add(field, BorderLayout.CENTER);
        fieldContainer.add(eyeButton, BorderLayout.EAST);
        
        panel.add(label);
        panel.add(Box.createVerticalStrut(8));
        panel.add(fieldContainer);
        
        return panel;
    }

    private void setupPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(new Color(255, 255, 255, 120));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // font tipis
        
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
                        passField.setForeground(new Color(255, 255, 255, 100));
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
                        field.setForeground(new Color(255, 255, 255, 100));
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
        button.setMaximumSize(new Dimension(300, 45));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
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

    private JPanel createModePanel() {
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        modePanel.setOpaque(false);
        modePanel.setMaximumSize(new Dimension(300, 40));
        
        JLabel modeTextLabel = new JLabel("Masuk sebagai ");
        modeTextLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        modeTextLabel.setForeground(new Color(255, 255, 255, 180));
        
        switchModeLabel = new JLabel(isLoginAsStaff ? "Customer" : "Staff");
        switchModeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        switchModeLabel.setForeground(new Color(220, 53, 69));
        switchModeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        modePanel.add(modeTextLabel);
        modePanel.add(switchModeLabel);
        
        return modePanel;
    }

    private void setupEventListeners() {
        loginButton.addActionListener(e -> handleLogin());
        
        switchModeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switchMode();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                switchModeLabel.setForeground(new Color(200, 35, 51));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                switchModeLabel.setForeground(new Color(220, 53, 69));
            }
        });
        
        // Enter key support
        getRootPane().setDefaultButton(loginButton);
    }

    private void switchMode() {
        isLoginAsStaff = !isLoginAsStaff;
        switchModeLabel.setText(isLoginAsStaff ? "Customer" : "Staff");

        // Update title to reflect current mode
        String newTitle = "LeasLink - Login " + (isLoginAsStaff ? "(Staff)" : "(Customer)");   
        setTitle(newTitle);
    }

    private void handleLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty() || 
            email.equals("username@gmail.com") || password.equals("password")) {
            showError("Mohon isi semua field dengan benar");
            return;
        }

        try {
            User user = AuthController.login(email, password);
            if (user != null) {
                // Check if user role matches selected mode
                if (isLoginAsStaff && user.getRole().equals("customer")) {
                    showError("Tidak dapat masuk sebagai staff");
                    return;
                } else if (!isLoginAsStaff && !user.getRole().equals("customer")) {
                    showError("Tidak dapat masuk sebagai customer");
                    return;
                }
                
                showSuccess("Selamat datang, " + user.getFullName() + "!");
                openDashboardByRole(user);
                dispose();
            } else {
                showError("Email atau password salah");
            }
        } catch (SQLException ex) {
            showError("Database error: " + ex.getMessage());
        }
    }

    private void openDashboardByRole(User user) {
        SwingUtilities.invokeLater(() -> {
            switch (user.getRole().toLowerCase()) {
                case "admin":
                    new AdminDashboard(user).setVisible(true);
                    break;
                case "management":
                    new ManagementDashboard(user).setVisible(true);
                    break;
                case "collector":
                    new CollectorDashboard(user).setVisible(true);
                    break;
                case "customer":
                    // For now, show a simple message for customers
                    JOptionPane.showMessageDialog(this, 
                        "Customer dashboard belum tersedia.\nFitur ini akan segera hadir!", 
                        "Info", 
                        JOptionPane.INFORMATION_MESSAGE);
                    break;
                default:
                    showError("Role tidak dikenali: " + user.getRole());
                    return;
            }
        });
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
            new LoginForm().setVisible(true);
        });
    }
}