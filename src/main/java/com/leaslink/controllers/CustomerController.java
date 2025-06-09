package com.leaslink.controllers;

import com.leaslink.models.Lease;
import com.leaslink.models.Payment;
import com.leaslink.utils.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerController {

    /**
     * Get all active leases for a specific customer
     */
    public static List<Lease> getCustomerLeases(int customerId) throws SQLException {
        List<Lease> leases = new ArrayList<>();
        
        String sql = """
            SELECT l.*, m.brand, m.model, m.year, m.color,
                   u.full_name as customer_name,
                   COALESCE(SUM(p.amount), 0) as total_paid
            FROM leases l
            JOIN motorcycles m ON l.motorcycle_id = m.id
            JOIN users u ON l.customer_id = u.id
            LEFT JOIN payments p ON l.id = p.lease_id
            WHERE l.customer_id = ?
            GROUP BY l.id, m.brand, m.model, m.year, m.color, u.full_name
            ORDER BY l.start_date DESC
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Lease lease = new Lease();
                lease.setId(rs.getInt("id"));
                lease.setCustomerId(rs.getInt("customer_id"));
                lease.setMotorcycleId(rs.getInt("motorcycle_id"));                lease.setLeaseAmount(rs.getDouble("lease_amount"));
                lease.setMonthlyPayment(rs.getDouble("monthly_payment"));                lease.setLeaseDuration(rs.getInt("lease_duration"));
                
                // Handle potential null dates - parse string dates directly
                String startDateStr = rs.getString("start_date");
                if (startDateStr != null && !startDateStr.isEmpty()) {
                    lease.setStartDate(LocalDate.parse(startDateStr));
                }
                
                String endDateStr = rs.getString("end_date");
                if (endDateStr != null && !endDateStr.isEmpty()) {
                    lease.setEndDate(LocalDate.parse(endDateStr));
                }
                
                lease.setStatus(rs.getString("status"));
                lease.setCreatedBy(rs.getInt("created_by"));
                lease.setCreatedAt(rs.getString("created_at"));
                lease.setUpdatedAt(rs.getString("updated_at"));
                
                // Set additional display fields
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
                
                leases.add(lease);
            }
        }
        
        return leases;
    }

    /**
     * Get payment history for a specific lease
     */
    public static List<Payment> getLeasePayments(int leaseId) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        
        String sql = """
            SELECT p.*, u.full_name as collector_name
            FROM payments p
            LEFT JOIN users u ON p.collector_id = u.id
            WHERE p.lease_id = ?
            ORDER BY p.payment_date DESC
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {            pstmt.setInt(1, leaseId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Payment payment = new Payment();
                payment.setId(rs.getInt("id"));
                payment.setLeaseId(rs.getInt("lease_id"));
                
                // Handle potential null payment date - parse string date directly
                String paymentDateStr = rs.getString("payment_date");
                if (paymentDateStr != null && !paymentDateStr.isEmpty()) {
                    payment.setPaymentDate(LocalDate.parse(paymentDateStr));
                }
                
                payment.setAmount(rs.getDouble("amount"));
                payment.setPaymentMethod(rs.getString("payment_method"));
                payment.setCollectorId(rs.getObject("collector_id", Integer.class));
                payment.setNotes(rs.getString("notes"));
                payment.setCreatedAt(rs.getString("created_at"));
                payment.setCollectorName(rs.getString("collector_name"));
                
                payments.add(payment);
            }
        }
        
        return payments;
    }

    /**
     * Get all payment history for a specific customer
     */
    public static List<Payment> getCustomerPayments(int customerId) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        
        String sql = """
            SELECT p.*, u.full_name as collector_name,
                   m.brand || ' ' || m.model || ' ' || m.year as motorcycle_name,
                   uc.full_name as customer_name
            FROM payments p
            JOIN leases l ON p.lease_id = l.id
            JOIN motorcycles m ON l.motorcycle_id = m.id
            JOIN users uc ON l.customer_id = uc.id
            LEFT JOIN users u ON p.collector_id = u.id
            WHERE l.customer_id = ?
            ORDER BY p.payment_date DESC
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Payment payment = new Payment();
                payment.setId(rs.getInt("id"));
                payment.setLeaseId(rs.getInt("lease_id"));
                
                // Handle potential null payment date - parse string date directly
                String paymentDateStr = rs.getString("payment_date");
                if (paymentDateStr != null && !paymentDateStr.isEmpty()) {
                    payment.setPaymentDate(LocalDate.parse(paymentDateStr));
                }
                
                payment.setAmount(rs.getDouble("amount"));
                payment.setPaymentMethod(rs.getString("payment_method"));
                payment.setCollectorId(rs.getObject("collector_id", Integer.class));
                payment.setNotes(rs.getString("notes"));
                payment.setCreatedAt(rs.getString("created_at"));
                payment.setCollectorName(rs.getString("collector_name"));
                payment.setMotorcycleName(rs.getString("motorcycle_name"));
                payment.setCustomerName(rs.getString("customer_name"));
                
                payments.add(payment);
            }
        }
        
        return payments;
    }

    /**
     * Get lease summary statistics for a customer
     */
    public static LeaseSummary getCustomerLeaseSummary(int customerId) throws SQLException {
        LeaseSummary summary = new LeaseSummary();
        
        String sql = """
            SELECT 
                COUNT(l.id) as total_leases,
                COALESCE(SUM(l.lease_amount), 0) as total_lease_amount,
                COALESCE(SUM(p.amount), 0) as total_paid,
                COUNT(CASE WHEN l.status = 'active' THEN 1 END) as active_leases,
                COUNT(CASE WHEN l.status = 'completed' THEN 1 END) as completed_leases
            FROM leases l
            LEFT JOIN payments p ON l.id = p.lease_id
            WHERE l.customer_id = ?
            GROUP BY l.customer_id
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                summary.setTotalLeases(rs.getInt("total_leases"));
                summary.setTotalLeaseAmount(rs.getDouble("total_lease_amount"));
                summary.setTotalPaid(rs.getDouble("total_paid"));
                summary.setActiveLeases(rs.getInt("active_leases"));
                summary.setCompletedLeases(rs.getInt("completed_leases"));
                summary.setRemainingAmount(summary.getTotalLeaseAmount() - summary.getTotalPaid());
            }
        }
        
        return summary;
    }

    /**
     * Check if customer has any overdue payments
     */
    public static boolean hasOverduePayments(int customerId) throws SQLException {
        String sql = """
            SELECT COUNT(*) as overdue_count
            FROM leases l
            WHERE l.customer_id = ? 
            AND l.status = 'active'
            AND EXISTS (
                SELECT 1 FROM (
                    SELECT 
                        l.id,
                        l.start_date,
                        l.monthly_payment,
                        COALESCE(SUM(p.amount), 0) as total_paid,
                        CAST((julianday('now') - julianday(l.start_date)) / 30.44 AS INTEGER) + 1 as months_passed
                    FROM leases l
                    LEFT JOIN payments p ON l.id = p.lease_id
                    WHERE l.id = l.id
                    GROUP BY l.id, l.start_date, l.monthly_payment
                ) calc
                WHERE calc.total_paid < (calc.months_passed * l.monthly_payment)
            )
        """;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("overdue_count") > 0;
            }
        }
        
        return false;
    }

    /**
     * Inner class for lease summary data
     */
    public static class LeaseSummary {
        private int totalLeases;
        private int activeLeases;
        private int completedLeases;
        private double totalLeaseAmount;
        private double totalPaid;
        private double remainingAmount;

        // Getters and Setters
        public int getTotalLeases() { return totalLeases; }
        public void setTotalLeases(int totalLeases) { this.totalLeases = totalLeases; }

        public int getActiveLeases() { return activeLeases; }
        public void setActiveLeases(int activeLeases) { this.activeLeases = activeLeases; }

        public int getCompletedLeases() { return completedLeases; }
        public void setCompletedLeases(int completedLeases) { this.completedLeases = completedLeases; }

        public double getTotalLeaseAmount() { return totalLeaseAmount; }
        public void setTotalLeaseAmount(double totalLeaseAmount) { this.totalLeaseAmount = totalLeaseAmount; }

        public double getTotalPaid() { return totalPaid; }
        public void setTotalPaid(double totalPaid) { this.totalPaid = totalPaid; }

        public double getRemainingAmount() { return remainingAmount; }
        public void setRemainingAmount(double remainingAmount) { this.remainingAmount = remainingAmount; }

        public double getPaymentProgress() {
            if (totalLeaseAmount <= 0) return 0.0;
            return (totalPaid / totalLeaseAmount) * 100.0;
        }
    }
}
