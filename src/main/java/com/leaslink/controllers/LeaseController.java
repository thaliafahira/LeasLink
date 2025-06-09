package com.leaslink.controllers;

import com.leaslink.models.Lease;
import com.leaslink.utils.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller untuk mengelola operasi CRUD kontrak leasing
 * Mendukung manajemen lengkap kontrak leasing dengan workflow persetujuan
 */
public class LeaseController {

    /**
     * Membuat kontrak lease baru
     */
    public static boolean createLease(Lease lease) throws SQLException {
        String sql = """
            INSERT INTO leases (customer_id, motorcycle_id, lease_amount, monthly_payment, 
                               lease_duration, start_date, end_date, status, created_by) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, lease.getCustomerId());
            pstmt.setInt(2, lease.getMotorcycleId());
            pstmt.setDouble(3, lease.getLeaseAmount());
            pstmt.setDouble(4, lease.getMonthlyPayment());
            pstmt.setInt(5, lease.getLeaseDuration());
            pstmt.setDate(6, Date.valueOf(lease.getStartDate()));
            pstmt.setDate(7, Date.valueOf(lease.getEndDate()));
            pstmt.setString(8, lease.getStatus());
            pstmt.setInt(9, lease.getCreatedBy());
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                // Update motorcycle status to 'leased'
                updateMotorcycleStatus(lease.getMotorcycleId(), "leased");
                return true;
            }
            
            return false;
        }
    }

    /**
     * Mengupdate kontrak lease yang sudah ada
     */
    public static boolean updateLease(Lease lease) throws SQLException {
        String sql = """
            UPDATE leases SET 
                customer_id = ?, motorcycle_id = ?, lease_amount = ?, monthly_payment = ?,
                lease_duration = ?, start_date = ?, end_date = ?, status = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, lease.getCustomerId());
            pstmt.setInt(2, lease.getMotorcycleId());
            pstmt.setDouble(3, lease.getLeaseAmount());
            pstmt.setDouble(4, lease.getMonthlyPayment());
            pstmt.setInt(5, lease.getLeaseDuration());
            pstmt.setDate(6, Date.valueOf(lease.getStartDate()));
            pstmt.setDate(7, Date.valueOf(lease.getEndDate()));
            pstmt.setString(8, lease.getStatus());
            pstmt.setInt(9, lease.getId());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Menghapus/membatalkan kontrak lease
     */
    public static boolean deleteLease(int leaseId, int deletedBy) throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Get motorcycle ID before deleting lease
                int motorcycleId = getMotorcycleIdByLeaseId(leaseId);
                
                // Check if lease has payments
                if (hasPayments(leaseId)) {
                    // Don't delete, just mark as cancelled
                    String updateSql = """
                        UPDATE leases SET status = 'cancelled', updated_at = CURRENT_TIMESTAMP 
                        WHERE id = ?
                    """;
                    try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                        pstmt.setInt(1, leaseId);
                        pstmt.executeUpdate();
                    }
                } else {
                    // Safe to delete if no payments
                    String deleteSql = "DELETE FROM leases WHERE id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                        pstmt.setInt(1, leaseId);
                        pstmt.executeUpdate();
                    }
                }
                
                // Update motorcycle status back to available
                updateMotorcycleStatus(motorcycleId, "available");
                
                conn.commit();
                return true;
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    /**
     * Menyetujui kontrak lease (mengubah status dari pending ke active)
     */
    public static boolean approveLease(int leaseId, int approvedBy) throws SQLException {
        String sql = """
            UPDATE leases SET 
                status = 'active', 
                updated_at = CURRENT_TIMESTAMP 
            WHERE id = ? AND status = 'pending'
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, leaseId);
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                // Log approval action
                logLeaseAction(leaseId, "APPROVED", approvedBy, "Lease approved by management");
                return true;
            }
            
            return false;
        }
    }

    /**
     * Menolak kontrak lease
     */
    public static boolean rejectLease(int leaseId, int rejectedBy, String reason) throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Update lease status to rejected
                String updateSql = """
                    UPDATE leases SET 
                        status = 'rejected', 
                        updated_at = CURRENT_TIMESTAMP 
                    WHERE id = ? AND status = 'pending'
                """;
                
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    pstmt.setInt(1, leaseId);
                    int result = pstmt.executeUpdate();
                    
                    if (result > 0) {
                        // Release motorcycle
                        int motorcycleId = getMotorcycleIdByLeaseId(leaseId);
                        updateMotorcycleStatus(motorcycleId, "available");
                        
                        // Log rejection
                        logLeaseAction(leaseId, "REJECTED", rejectedBy, reason);
                        
                        conn.commit();
                        return true;
                    }
                }
                
                conn.rollback();
                return false;
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    /**
     * Mendapatkan semua kontrak lease dengan filter dan pencarian
     */
    public static List<Lease> getAllLeases(String statusFilter, String searchQuery) throws SQLException {
        List<Lease> leases = new ArrayList<>();
        
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("""
            SELECT l.*, 
                   u.full_name as customer_name,
                   m.brand, m.model, m.year, m.color,
                   uc.full_name as created_by_name,
                   COALESCE(SUM(p.amount), 0) as total_paid
            FROM leases l
            JOIN users u ON l.customer_id = u.id
            JOIN motorcycles m ON l.motorcycle_id = m.id
            JOIN users uc ON l.created_by = uc.id
            LEFT JOIN payments p ON l.id = p.lease_id
            WHERE 1=1
        """);
        
        List<Object> params = new ArrayList<>();
        
        if (statusFilter != null && !statusFilter.equals("ALL")) {
            sqlBuilder.append(" AND l.status = ?");
            params.add(statusFilter);
        }
        
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sqlBuilder.append(" AND (u.full_name LIKE ? OR m.brand LIKE ? OR m.model LIKE ?)");
            String searchPattern = "%" + searchQuery.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        sqlBuilder.append("""
            GROUP BY l.id, u.full_name, m.brand, m.model, m.year, m.color, uc.full_name
            ORDER BY l.created_at DESC
        """);
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Lease lease = mapResultSetToLease(rs);
                leases.add(lease);
            }
        }
        
        return leases;
    }

    /**
     * Mendapatkan kontrak lease berdasarkan ID
     */
    public static Lease getLeaseById(int leaseId) throws SQLException {
        String sql = """
            SELECT l.*, 
                   u.full_name as customer_name,
                   m.brand, m.model, m.year, m.color,
                   uc.full_name as created_by_name,
                   COALESCE(SUM(p.amount), 0) as total_paid
            FROM leases l
            JOIN users u ON l.customer_id = u.id
            JOIN motorcycles m ON l.motorcycle_id = m.id
            JOIN users uc ON l.created_by = uc.id
            LEFT JOIN payments p ON l.id = p.lease_id
            WHERE l.id = ?
            GROUP BY l.id, u.full_name, m.brand, m.model, m.year, m.color, uc.full_name
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, leaseId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToLease(rs);
            }
            
            return null;
        }
    }

    /**
     * Mendapatkan semua motor yang tersedia untuk leasing
     */
    public static List<String[]> getAvailableMotorcycles() throws SQLException {
        List<String[]> motorcycles = new ArrayList<>();
        
        String sql = """
            SELECT id, brand, model, year, color, price 
            FROM motorcycles 
            WHERE status = 'available'
            ORDER BY brand, model
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                motorcycles.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("brand"),
                    rs.getString("model"),
                    rs.getString("year"),
                    rs.getString("color"),
                    String.valueOf(rs.getDouble("price"))
                });
            }
        }
        
        return motorcycles;
    }

    /**
     * Mendapatkan semua customer untuk dropdown
     */
    public static List<String[]> getAllCustomers() throws SQLException {
        List<String[]> customers = new ArrayList<>();
        
        String sql = """
            SELECT id, full_name, email 
            FROM users 
            WHERE role = 'CUSTOMER'
            ORDER BY full_name
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                customers.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("full_name"),
                    rs.getString("email")
                });
            }
        }
        
        return customers;
    }

    /**
     * Validasi business rules untuk kontrak lease
     */
    public static String validateLease(Lease lease) {
        if (lease.getCustomerId() <= 0) {
            return "Customer harus dipilih";
        }
        
        if (lease.getMotorcycleId() <= 0) {
            return "Motor harus dipilih";
        }
        
        if (lease.getLeaseAmount() <= 0) {
            return "Jumlah lease harus lebih dari 0";
        }
        
        if (lease.getMonthlyPayment() <= 0) {
            return "Pembayaran bulanan harus lebih dari 0";
        }
        
        if (lease.getLeaseDuration() <= 0 || lease.getLeaseDuration() > 60) {
            return "Durasi lease harus antara 1-60 bulan";
        }
        
        if (lease.getStartDate() == null || lease.getEndDate() == null) {
            return "Tanggal mulai dan berakhir harus diisi";
        }
        
        if (lease.getStartDate().isAfter(lease.getEndDate())) {
            return "Tanggal mulai tidak boleh lebih dari tanggal berakhir";
        }
        
        // Validate total payment calculation
        double totalExpectedPayment = lease.getMonthlyPayment() * lease.getLeaseDuration();
        if (Math.abs(totalExpectedPayment - lease.getLeaseAmount()) > lease.getLeaseAmount() * 0.1) {
            return "Total pembayaran tidak sesuai dengan durasi dan pembayaran bulanan";
        }
        
        return null; // No validation errors
    }

    // === Helper Methods ===

    private static void updateMotorcycleStatus(int motorcycleId, String status) throws SQLException {
        String sql = "UPDATE motorcycles SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, motorcycleId);
            pstmt.executeUpdate();
        }
    }

    private static int getMotorcycleIdByLeaseId(int leaseId) throws SQLException {
        String sql = "SELECT motorcycle_id FROM leases WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, leaseId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("motorcycle_id");
            }
            
            throw new SQLException("Lease not found");
        }
    }

    private static boolean hasPayments(int leaseId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM payments WHERE lease_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, leaseId);
            ResultSet rs = pstmt.executeQuery();
            
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private static void logLeaseAction(int leaseId, String action, int userId, String notes) throws SQLException {
        String sql = """
            INSERT INTO lease_audit_log (lease_id, action, user_id, notes, created_at) 
            VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, leaseId);
            pstmt.setString(2, action);
            pstmt.setInt(3, userId);
            pstmt.setString(4, notes);
            
            try {
                pstmt.executeUpdate();
            } catch (SQLException e) {
                // Log table might not exist, ignore for now
                System.out.println("Warning: Could not log lease action - " + e.getMessage());
            }
        }
    }

    private static Lease mapResultSetToLease(ResultSet rs) throws SQLException {
        Lease lease = new Lease();
        lease.setId(rs.getInt("id"));        lease.setCustomerId(rs.getInt("customer_id"));
        lease.setMotorcycleId(rs.getInt("motorcycle_id"));
        lease.setLeaseAmount(rs.getDouble("lease_amount"));
        lease.setMonthlyPayment(rs.getDouble("monthly_payment"));
        lease.setLeaseDuration(rs.getInt("lease_duration"));
        
        // Handle date conversion more safely
        String startDateStr = rs.getString("start_date");
        String endDateStr = rs.getString("end_date");
        lease.setStartDate(java.time.LocalDate.parse(startDateStr));
        lease.setEndDate(java.time.LocalDate.parse(endDateStr));
        
        lease.setStatus(rs.getString("status"));
        lease.setCreatedBy(rs.getInt("created_by"));
        lease.setCreatedAt(rs.getString("created_at"));
        lease.setUpdatedAt(rs.getString("updated_at"));
        
        // Set display fields
        lease.setCustomerName(rs.getString("customer_name"));
        lease.setMotorcycleBrand(rs.getString("brand"));
        lease.setMotorcycleModel(rs.getString("model"));
        lease.setMotorcycleYear(rs.getString("year"));
        lease.setMotorcycleColor(rs.getString("color"));
        lease.setTotalPaid(rs.getDouble("total_paid"));
        lease.setRemainingAmount(lease.getLeaseAmount() - lease.getTotalPaid());
        
        // Calculate remaining months
        int paidMonths = (int) (lease.getTotalPaid() / lease.getMonthlyPayment());
        lease.setRemainingMonths(Math.max(0, lease.getLeaseDuration() - paidMonths));
        
        return lease;
    }

    /**
     * Mendapatkan ringkasan statistik lease
     */
    public static LeaseSummary getLeaseSummary() throws SQLException {
        LeaseSummary summary = new LeaseSummary();
        
        String sql = """
            SELECT 
                COUNT(*) as total_leases,
                COUNT(CASE WHEN status = 'pending' THEN 1 END) as pending_leases,
                COUNT(CASE WHEN status = 'active' THEN 1 END) as active_leases,
                COUNT(CASE WHEN status = 'completed' THEN 1 END) as completed_leases,
                COUNT(CASE WHEN status = 'cancelled' THEN 1 END) as cancelled_leases,
                COUNT(CASE WHEN status = 'rejected' THEN 1 END) as rejected_leases,
                COALESCE(SUM(lease_amount), 0) as total_lease_value,
                COALESCE(SUM(CASE WHEN status = 'active' THEN lease_amount ELSE 0 END), 0) as active_lease_value
            FROM leases
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                summary.setTotalLeases(rs.getInt("total_leases"));
                summary.setPendingLeases(rs.getInt("pending_leases"));
                summary.setActiveLeases(rs.getInt("active_leases"));
                summary.setCompletedLeases(rs.getInt("completed_leases"));
                summary.setCancelledLeases(rs.getInt("cancelled_leases"));
                summary.setRejectedLeases(rs.getInt("rejected_leases"));
                summary.setTotalLeaseValue(rs.getDouble("total_lease_value"));
                summary.setActiveLeaseValue(rs.getDouble("active_lease_value"));
            }
        }
        
        return summary;
    }

    /**
     * Inner class untuk ringkasan statistik lease
     */
    public static class LeaseSummary {
        private int totalLeases;
        private int pendingLeases;
        private int activeLeases;
        private int completedLeases;
        private int cancelledLeases;
        private int rejectedLeases;
        private double totalLeaseValue;
        private double activeLeaseValue;

        // Getters and Setters
        public int getTotalLeases() { return totalLeases; }
        public void setTotalLeases(int totalLeases) { this.totalLeases = totalLeases; }

        public int getPendingLeases() { return pendingLeases; }
        public void setPendingLeases(int pendingLeases) { this.pendingLeases = pendingLeases; }

        public int getActiveLeases() { return activeLeases; }
        public void setActiveLeases(int activeLeases) { this.activeLeases = activeLeases; }

        public int getCompletedLeases() { return completedLeases; }
        public void setCompletedLeases(int completedLeases) { this.completedLeases = completedLeases; }

        public int getCancelledLeases() { return cancelledLeases; }
        public void setCancelledLeases(int cancelledLeases) { this.cancelledLeases = cancelledLeases; }

        public int getRejectedLeases() { return rejectedLeases; }
        public void setRejectedLeases(int rejectedLeases) { this.rejectedLeases = rejectedLeases; }

        public double getTotalLeaseValue() { return totalLeaseValue; }
        public void setTotalLeaseValue(double totalLeaseValue) { this.totalLeaseValue = totalLeaseValue; }

        public double getActiveLeaseValue() { return activeLeaseValue; }
        public void setActiveLeaseValue(double activeLeaseValue) { this.activeLeaseValue = activeLeaseValue; }
    }
}
