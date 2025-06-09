package com.leaslink.models;

import java.time.LocalDate;

public class Payment {
    private int id;
    private int leaseId;
    private LocalDate paymentDate;
    private double amount;
    private String paymentMethod;
    private Integer collectorId;
    private String notes;
    private String createdAt;
    
    // Additional fields for display purposes
    private String collectorName;
    private String customerName;
    private String motorcycleName;

    public Payment() {}

    public Payment(int leaseId, LocalDate paymentDate, double amount, 
                   String paymentMethod, Integer collectorId, String notes) {
        this.leaseId = leaseId;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.collectorId = collectorId;
        this.notes = notes;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLeaseId() {
        return leaseId;
    }

    public void setLeaseId(int leaseId) {
        this.leaseId = leaseId;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Integer getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(Integer collectorId) {
        this.collectorId = collectorId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // Additional display fields
    public String getCollectorName() {
        return collectorName;
    }

    public void setCollectorName(String collectorName) {
        this.collectorName = collectorName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMotorcycleName() {
        return motorcycleName;
    }

    public void setMotorcycleName(String motorcycleName) {
        this.motorcycleName = motorcycleName;
    }

    // Helper method to format amount as currency
    public String getFormattedAmount() {
        return String.format("Rp %,.0f", amount);
    }
}
