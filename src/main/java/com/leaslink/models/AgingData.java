package com.leaslink.models;

import java.time.LocalDate;

/**
 * Model untuk data Aging Piutang
 */
public class AgingData {
    private String customerId;
    private String leaseId;
    private String customerName;
    private String phoneNumber;
    private String address;
    private int age; // umur piutang dalam hari
    private double range0_30; // piutang 0-30 hari
    private double range31_60; // piutang 31-60 hari
    private double rangeOver60; // piutang >60 hari
    private LocalDate lastPaymentDate;
    private double totalAmount;

    // Constructors
    public AgingData() {}

    public AgingData(String customerId, String leaseId, int age, double range0_30, double range31_60, double rangeOver60) {
        this.customerId = customerId;
        this.leaseId = leaseId;
        this.age = age;
        this.range0_30 = range0_30;
        this.range31_60 = range31_60;
        this.rangeOver60 = rangeOver60;
        this.totalAmount = range0_30 + range31_60 + rangeOver60;
    }

    // Getters and Setters
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getLeaseId() {
        return leaseId;
    }

    public void setLeaseId(String leaseId) {
        this.leaseId = leaseId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getRange0_30() {
        return range0_30;
    }

    public void setRange0_30(double range0_30) {
        this.range0_30 = range0_30;
    }

    public double getRange31_60() {
        return range31_60;
    }

    public void setRange31_60(double range31_60) {
        this.range31_60 = range31_60;
    }

    public double getRangeOver60() {
        return rangeOver60;
    }

    public void setRangeOver60(double rangeOver60) {
        this.rangeOver60 = rangeOver60;
    }

    public LocalDate getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(LocalDate lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    public double getTotalAmount() {
        return range0_30 + range31_60 + rangeOver60;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * Mendapatkan kategori aging berdasarkan umur piutang
     */
    public String getAgingCategory() {
        if (age <= 30) {
            return "0-30 hari";
        } else if (age <= 60) {
            return "31-60 hari";
        } else {
            return ">60 hari";
        }
    }

    /**
     * Mendapatkan level risiko berdasarkan umur piutang
     */
    public String getRiskLevel() {
        if (age <= 30) {
            return "Rendah";
        } else if (age <= 60) {
            return "Sedang";
        } else if (age <= 90) {
            return "Tinggi";
        } else {
            return "Sangat Tinggi";
        }
    }

    /**
     * Format currency untuk Indonesia
     */
    public String formatCurrency(double amount) {
        return String.format("Rp %,.0f", amount);
    }

    @Override
    public String toString() {
        return "AgingData{" +
                "customerId='" + customerId + '\'' +
                ", leaseId='" + leaseId + '\'' +
                ", age=" + age +
                ", range0_30=" + range0_30 +
                ", range31_60=" + range31_60 +
                ", rangeOver60=" + rangeOver60 +
                ", totalAmount=" + getTotalAmount() +
                '}';
    }
}