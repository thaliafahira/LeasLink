package com.leaslink.models;

import java.time.LocalDate;

public class Lease {
    private int id;
    private int customerId;
    private int motorcycleId;
    private double leaseAmount;
    private double monthlyPayment;
    private int leaseDuration;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private int createdBy;
    private String createdAt;
    private String updatedAt;
    
    // Additional fields for display purposes
    private String customerName;
    private String motorcycleBrand;
    private String motorcycleModel;
    private String motorcycleYear;
    private String motorcycleColor;
    private double totalPaid;
    private double remainingAmount;
    private int remainingMonths;

    public Lease() {}

    public Lease(int customerId, int motorcycleId, double leaseAmount, 
                 double monthlyPayment, int leaseDuration, LocalDate startDate, 
                 LocalDate endDate, String status, int createdBy) {
        this.customerId = customerId;
        this.motorcycleId = motorcycleId;
        this.leaseAmount = leaseAmount;
        this.monthlyPayment = monthlyPayment;
        this.leaseDuration = leaseDuration;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getMotorcycleId() {
        return motorcycleId;
    }

    public void setMotorcycleId(int motorcycleId) {
        this.motorcycleId = motorcycleId;
    }

    public double getLeaseAmount() {
        return leaseAmount;
    }

    public void setLeaseAmount(double leaseAmount) {
        this.leaseAmount = leaseAmount;
    }

    public double getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(double monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public int getLeaseDuration() {
        return leaseDuration;
    }

    public void setLeaseDuration(int leaseDuration) {
        this.leaseDuration = leaseDuration;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Additional display fields
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMotorcycleBrand() {
        return motorcycleBrand;
    }

    public void setMotorcycleBrand(String motorcycleBrand) {
        this.motorcycleBrand = motorcycleBrand;
    }

    public String getMotorcycleModel() {
        return motorcycleModel;
    }

    public void setMotorcycleModel(String motorcycleModel) {
        this.motorcycleModel = motorcycleModel;
    }

    public String getMotorcycleYear() {
        return motorcycleYear;
    }

    public void setMotorcycleYear(String motorcycleYear) {
        this.motorcycleYear = motorcycleYear;
    }

    public String getMotorcycleColor() {
        return motorcycleColor;
    }

    public void setMotorcycleColor(String motorcycleColor) {
        this.motorcycleColor = motorcycleColor;
    }

    public double getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(double totalPaid) {
        this.totalPaid = totalPaid;
    }

    public double getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(double remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public int getRemainingMonths() {
        return remainingMonths;
    }

    public void setRemainingMonths(int remainingMonths) {
        this.remainingMonths = remainingMonths;
    }

    // Helper method to get full motorcycle name
    public String getFullMotorcycleName() {
        return motorcycleBrand + " " + motorcycleModel + " " + motorcycleYear + " (" + motorcycleColor + ")";
    }

    // Helper method to calculate progress percentage
    public int getProgressPercentage() {
        if (leaseAmount <= 0) return 0;
        return (int) ((totalPaid / leaseAmount) * 100);
    }
}
