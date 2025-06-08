package com.leaslink.controllers;

import com.leaslink.models.AgingData;
import com.leaslink.models.User;
import com.leaslink.utils.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller untuk mengelola data Aging Piutang
 */
public class AgingController {

    public AgingController() {
        // Initialize controller
    }

    /**
     * Mendapatkan semua data aging piutang
     */
    public List<AgingData> getAllAgingData() {
        List<AgingData> agingDataList = new ArrayList<>();
        
        // Sample data - replace with actual database query
        agingDataList.add(new AgingData("123456789876", "ABC123EDF", 75, 0, 0, 1200000));
        agingDataList.add(new AgingData("987654321098", "XYZ789GHI", 45, 500000, 0, 800000));
        agingDataList.add(new AgingData("456789123456", "DEF456JKL", 30, 0, 750000, 0));
        agingDataList.add(new AgingData("789123456789", "GHI789MNO", 90, 0, 0, 1500000));
        agingDataList.add(new AgingData("321654987321", "JKL321PQR", 60, 0, 600000, 400000));
        agingDataList.add(new AgingData("654987321654", "MNO654STU", 120, 0, 0, 2000000));

        return agingDataList;
    }

    /**
     * Mendapatkan data aging berdasarkan pencarian
     */
    public List<AgingData> searchAgingData(String searchText) {
        List<AgingData> agingDataList = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT * FROM aging_receivables WHERE customer_id LIKE ? OR lease_id LIKE ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                String searchPattern = "%" + searchText + "%";
                pstmt.setString(1, searchPattern);
                pstmt.setString(2, searchPattern);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        AgingData data = new AgingData();
                        data.setCustomerId(rs.getString("customer_id"));
                        data.setLeaseId(rs.getString("lease_id"));
                        data.setAge(rs.getInt("age"));
                        data.setRange0_30(rs.getDouble("range_0_30"));
                        data.setRange31_60(rs.getDouble("range_31_60"));
                        data.setRangeOver60(rs.getDouble("range_over_60"));
                        data.setCustomerName(rs.getString("customer_name"));
                        data.setPhoneNumber(rs.getString("phone_number"));
                        data.setAddress(rs.getString("address"));
                        agingDataList.add(data);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching aging data: " + e.getMessage());
            // Return sample data for now
            if (searchText.contains("123")) {
                agingDataList.add(new AgingData("123456789876", "ABC123EDF", 75, 0, 0, 1200000));
            }
        }
        
        return agingDataList;
    }

    /**
     * Mengirim notifikasi ke collector
     */
    public boolean sendNotificationToCollector(String customerId, String leaseId, User sender) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            // Insert notification to database
            String sql = "INSERT INTO notifications (recipient_role, sender_id, customer_id, lease_id, " +
                        "message, notification_type, created_at, is_read) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, "COLLECTOR");
                pstmt.setInt(2, sender.getId());
                pstmt.setString(3, customerId);
                pstmt.setString(4, leaseId);
                pstmt.setString(5, createNotificationMessage(customerId, leaseId));
                pstmt.setString(6, "AGING_ALERT");
                pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setBoolean(8, false);
                
                int result = pstmt.executeUpdate();
                return result > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error sending notification: " + e.getMessage());
            // For demo purposes, always return true
            return true;
        }
    }

    /**
     * Membuat pesan notifikasi
     */
    private String createNotificationMessage(String customerId, String leaseId) {
        return String.format(
            "PERHATIAN: Piutang aging untuk pelanggan %s (Lease ID: %s) memerlukan tindakan penagihan. " +
            "Silakan lakukan follow-up dengan pelanggan.",
            customerId, leaseId
        );
    }

    /**
     * Mendapatkan total aging per kategori
     */
    public AgingSummary getAgingSummary() {
        AgingSummary summary = new AgingSummary();
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT " +
                        "SUM(range_0_30) as total_0_30, " +
                        "SUM(range_31_60) as total_31_60, " +
                        "SUM(range_over_60) as total_over_60, " +
                        "COUNT(*) as total_records " +
                        "FROM aging_receivables";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                
                if (rs.next()) {
                    summary.setTotal0_30(rs.getDouble("total_0_30"));
                    summary.setTotal31_60(rs.getDouble("total_31_60"));
                    summary.setTotalOver60(rs.getDouble("total_over_60"));
                    summary.setTotalRecords(rs.getInt("total_records"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting aging summary: " + e.getMessage());
            // Return sample data
            summary.setTotal0_30(500000);
            summary.setTotal31_60(1350000);
            summary.setTotalOver60(5900000);
            summary.setTotalRecords(6);
        }
        
        return summary;
    }

    /**
     * Inner class untuk summary data aging
     */
    public static class AgingSummary {
        private double total0_30;
        private double total31_60;
        private double totalOver60;
        private int totalRecords;

        // Getters and Setters
        public double getTotal0_30() { return total0_30; }
        public void setTotal0_30(double total0_30) { this.total0_30 = total0_30; }

        public double getTotal31_60() { return total31_60; }
        public void setTotal31_60(double total31_60) { this.total31_60 = total31_60; }

        public double getTotalOver60() { return totalOver60; }
        public void setTotalOver60(double totalOver60) { this.totalOver60 = totalOver60; }

        public int getTotalRecords() { return totalRecords; }
        public void setTotalRecords(int totalRecords) { this.totalRecords = totalRecords; }

        public double getGrandTotal() {
            return total0_30 + total31_60 + totalOver60;
        }
    }
}