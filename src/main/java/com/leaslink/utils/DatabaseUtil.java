package com.leaslink.utils;

import java.sql.*;

public class DatabaseUtil {
    private static final String DB_URL = "jdbc:sqlite:leaslink.db";
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DB_URL);
                initializeDatabase();
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQLite JDBC driver not found", e);
            }
        }
        return connection;
    }

    private static void initializeDatabase() throws SQLException {
        // Updated users table with new roles
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                full_name TEXT NOT NULL,
                email TEXT UNIQUE NOT NULL,
                phone TEXT,
                password TEXT NOT NULL,
                role TEXT NOT NULL CHECK(role IN ('admin', 'management', 'collector', 'customer')),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        String createUpdateTrigger = """
            CREATE TRIGGER IF NOT EXISTS update_users_timestamp
            AFTER UPDATE ON users
            BEGIN
                UPDATE users SET updated_at = CURRENT_TIMESTAMP
                WHERE id = NEW.id;
            END
        """;

        // Create motorcycles table
        String createMotorcyclesTable = """
            CREATE TABLE IF NOT EXISTS motorcycles (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                brand TEXT NOT NULL,
                model TEXT NOT NULL,
                year INTEGER NOT NULL,
                engine_capacity TEXT,
                color TEXT,
                chassis_number TEXT UNIQUE,
                engine_number TEXT UNIQUE,
                price DECIMAL(15,2) NOT NULL,
                status TEXT CHECK(status IN ('available', 'leased', 'maintenance')) DEFAULT 'available',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        // Create leases table
        String createLeasesTable = """
            CREATE TABLE IF NOT EXISTS leases (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                customer_id INTEGER NOT NULL,
                motorcycle_id INTEGER NOT NULL,
                lease_amount DECIMAL(15,2) NOT NULL,
                monthly_payment DECIMAL(15,2) NOT NULL,
                lease_duration INTEGER NOT NULL, -- in months
                start_date DATE NOT NULL,
                end_date DATE NOT NULL,
                status TEXT CHECK(status IN ('active', 'completed', 'defaulted')) DEFAULT 'active',
                created_by INTEGER NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (customer_id) REFERENCES users(id),
                FOREIGN KEY (motorcycle_id) REFERENCES motorcycles(id),
                FOREIGN KEY (created_by) REFERENCES users(id)
            )
        """;

        // Create payments table
        String createPaymentsTable = """
            CREATE TABLE IF NOT EXISTS payments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                lease_id INTEGER NOT NULL,
                payment_date DATE NOT NULL,
                amount DECIMAL(15,2) NOT NULL,
                payment_method TEXT,
                collector_id INTEGER,
                notes TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (lease_id) REFERENCES leases(id),
                FOREIGN KEY (collector_id) REFERENCES users(id)
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            // Create all tables
            stmt.execute(createUsersTable);
            stmt.execute(createUpdateTrigger);
            stmt.execute(createMotorcyclesTable);
            stmt.execute(createLeasesTable);
            stmt.execute(createPaymentsTable);
            
            // Create default users if not exists
            createDefaultUsers(stmt);
            
            // Create sample motorcycles if not exists
            createSampleMotorcycles(stmt);
        }
    }

    private static void createDefaultUsers(Statement stmt) throws SQLException {
        // Check if any users exist
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
        if (rs.next() && rs.getInt(1) == 0) {
            // Create default admin
            String createAdmin = """
                INSERT INTO users (full_name, email, phone, password, role)
                VALUES (
                    'Administrator',
                    'admin@leaslink.com',
                    '08123456789',
                    '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918',
                    'admin'
                )
            """;
            
            // Create default management user
            String createManagement = """
                INSERT INTO users (full_name, email, phone, password, role)
                VALUES (
                    'Management Sales',
                    'manager@leaslink.com',
                    '08123456788',
                    '288965a1f2c883c71bff8a4b3a1b76cc77d11e65f70910d5feff411a4e5fe1b3',
                    'management'
                )
            """;
            
            // Create default collector user
            String createCollector = """
                INSERT INTO users (full_name, email, phone, password, role)
                VALUES (
                    'Penagih',
                    'collector@leaslink.com',
                    '08123456787',
                    '0736fd5b7cc7ab7dfe821d3a17f93f2634497770232486155c9c881321c4d22c',
                    'collector'
                )
            """;
            
            // Create sample customer
            String createCustomer = """
                INSERT INTO users (full_name, email, phone, password, role)
                VALUES (
                    'Customer Demo',
                    'customer@leaslink.com',
                    '08123456786',
                    'b6c45863875e34487ca3c155ed145efe12a74581e27befec5aa661b8ee8ca6dd',
                    'customer'
                )
            """;
            
            stmt.execute(createAdmin);
            stmt.execute(createManagement);
            stmt.execute(createCollector);
            stmt.execute(createCustomer);
            
            System.out.println("Default users created:");
            System.out.println("Admin: admin@leaslink.com / admin");
            System.out.println("Management: manager@leaslink.com / management");
            System.out.println("Collector: collector@leaslink.com / collector");
            System.out.println("Customer: customer@leaslink.com / customer");
        }
    }

    private static void createSampleMotorcycles(Statement stmt) throws SQLException {
        // Check if any motorcycles exist
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM motorcycles");
        if (rs.next() && rs.getInt(1) == 0) {
            String[] sampleMotorcycles = {
                "INSERT INTO motorcycles (brand, model, year, engine_capacity, color, chassis_number, engine_number, price) VALUES ('Honda', 'Vario 125', 2023, '125cc', 'Merah', 'VR125001', 'ENG125001', 18500000)",
                "INSERT INTO motorcycles (brand, model, year, engine_capacity, color, chassis_number, engine_number, price) VALUES ('Honda', 'Beat Street', 2023, '110cc', 'Hitam', 'BT110001', 'ENG110001', 16500000)",
                "INSERT INTO motorcycles (brand, model, year, engine_capacity, color, chassis_number, engine_number, price) VALUES ('Yamaha', 'NMAX 155', 2023, '155cc', 'Biru', 'NM155001', 'ENG155001', 28500000)",
                "INSERT INTO motorcycles (brand, model, year, engine_capacity, color, chassis_number, engine_number, price) VALUES ('Yamaha', 'Mio M3', 2023, '125cc', 'Pink', 'MIO125001', 'ENG125002', 15500000)",
                "INSERT INTO motorcycles (brand, model, year, engine_capacity, color, chassis_number, engine_number, price) VALUES ('Suzuki', 'Address 110', 2023, '110cc', 'Putih', 'AD110001', 'ENG110002', 16800000)",
                "INSERT INTO motorcycles (brand, model, year, engine_capacity, color, chassis_number, engine_number, price) VALUES ('Honda', 'PCX 160', 2023, '160cc', 'Silver', 'PCX160001', 'ENG160001', 32500000)",
                "INSERT INTO motorcycles (brand, model, year, engine_capacity, color, chassis_number, engine_number, price) VALUES ('Yamaha', 'Aerox 155', 2023, '155cc', 'Orange', 'AX155001', 'ENG155002', 26500000)",
                "INSERT INTO motorcycles (brand, model, year, engine_capacity, color, chassis_number, engine_number, price) VALUES ('Honda', 'Scoopy', 2023, '110cc', 'Cream', 'SC110001', 'ENG110003', 18200000)"
            };
            
            for (String sql : sampleMotorcycles) {
                stmt.execute(sql);
            }
            
            System.out.println("Sample motorcycles created successfully.");
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Utility method to get user by role
    public static boolean hasUserWithRole(String role) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, role);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // Method to get all users by role
    public static void printUsersByRole() throws SQLException {
        String sql = "SELECT full_name, email, role FROM users ORDER BY role, full_name";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            System.out.println("\n=== DAFTAR PENGGUNA ===");
            String currentRole = "";
            
            while (rs.next()) {
                String role = rs.getString("role");
                if (!role.equals(currentRole)) {
                    currentRole = role;
                    System.out.println("\n" + role.toUpperCase() + ":");
                }
                System.out.println("  - " + rs.getString("full_name") + " (" + rs.getString("email") + ")");
            }
        }
    }
}