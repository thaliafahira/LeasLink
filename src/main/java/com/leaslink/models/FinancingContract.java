package com.leaslink.models;

import java.util.Date;

public class FinancingContract {
    private String id;
    private String debtorNik;
    private double loanAmount;
    private double interestRate;
    private int term;
    private Date startDate;
    private Date dueDate;
    private String status;

    // Constructor
    public FinancingContract(String id, String debtorNik, double loanAmount, double interestRate, int term, Date startDate, Date dueDate, String status) {
        this.id = id;
        this.debtorNik = debtorNik;
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.term = term;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.status = status;
    }

    // Getters
    public String getId() { return id; }
    public String getDebtorNik() { return debtorNik; }
    public double getLoanAmount() { return loanAmount; }
    public double getInterestRate() { return interestRate; }
    public int getTerm() { return term; }
    public Date getStartDate() { return startDate; }
    public Date getDueDate() { return dueDate; }
    public String getStatus() { return status; }

    // Setters
    public void setStatus(String status) {
        this.status = status;
    }

    // Utility
    public double calculateMonthlyInstallment() {
        double monthlyRate = interestRate / 100 / 12;
        return (loanAmount * monthlyRate) / (1 - Math.pow(1 + monthlyRate, -term));
    }

    @Override
    public String toString() {
        return String.format("ID: %s, Jumlah: %.2f, Status: %s", id, loanAmount, status);
    }
}
