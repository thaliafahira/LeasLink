package com.leaslink.controllers;

import com.leaslink.models.FinancingContract;
import com.leaslink.utils.DatabaseUtil;

import java.util.List;

public class ContractController {
    
    /**
     * Get all financing contracts
     */
    public List<FinancingContract> getAllContracts() {
        return DatabaseUtil.getAllContracts();
    }
    
    /**
     * Get contracts by debtor NIK
     */
    public List<FinancingContract> getContractsByDebtorNik(String nik) {
        return DatabaseUtil.getContractsByDebtorNik(nik);
    }
    
    /**
     * Search contracts by NIK or name
     */
    public List<FinancingContract> searchContractsByNik(String keyword) {
        return DatabaseUtil.searchContractsByNik(keyword);
    }
    
    /**
     * Get contract detail by contract ID
     */
    public FinancingContract getContractById(String contractId) {
        return DatabaseUtil.getContractById(contractId);
    }
    
    /**
     * Get contract detail by contract ID and NIK (backward compatibility)
     */
    public FinancingContract getContractDetail(String contractId, String nik) {
        // First try to get by contract ID directly
        FinancingContract contract = getContractById(contractId);
        
        // Verify the NIK matches for security
        if (contract != null && contract.getDebtorNik().equals(nik)) {
            return contract;
        }
        
        // Fallback: search through contracts by NIK
        List<FinancingContract> contracts = getContractsByDebtorNik(nik);
        for (FinancingContract fc : contracts) {
            if (fc.getId().equals(contractId)) {
                return fc;
            }
        }
        
        return null;
    }
    
    /**
     * Get contracts by status
     */
    public List<FinancingContract> getContractsByStatus(String status) {
        return DatabaseUtil.getContractsByStatus(status);
    }
    
    /**
     * Get active contracts
     */
    public List<FinancingContract> getActiveContracts() {
        return getContractsByStatus("Aktif");
    }
    
    /**
     * Get overdue contracts
     */
    public List<FinancingContract> getOverdueContracts() {
        return getContractsByStatus("Menunggak");
    }
    
    /**
     * Get completed contracts
     */
    public List<FinancingContract> getCompletedContracts() {
        return getContractsByStatus("Selesai");
    }
    
    /**
     * Calculate total loan amount for all contracts
     */
    public double getTotalLoanAmount() {
        List<FinancingContract> contracts = getAllContracts();
        return contracts.stream()
                .mapToDouble(FinancingContract::getLoanAmount)
                .sum();
    }
    
    /**
     * Calculate total active loan amount
     */
    public double getTotalActiveLoanAmount() {
        List<FinancingContract> contracts = getActiveContracts();
        return contracts.stream()
                .mapToDouble(FinancingContract::getLoanAmount)
                .sum();
    }
    
    /**
     * Get contract statistics
     */
    public ContractStats getContractStats() {
        List<FinancingContract> allContracts = getAllContracts();
        
        int totalContracts = allContracts.size();
        int activeContracts = (int) allContracts.stream()
                .filter(c -> "Aktif".equals(c.getStatus()))
                .count();
        int overdueContracts = (int) allContracts.stream()
                .filter(c -> "Menunggak".equals(c.getStatus()))
                .count();
        int completedContracts = (int) allContracts.stream()
                .filter(c -> "Selesai".equals(c.getStatus()))
                .count();
        
        double totalAmount = allContracts.stream()
                .mapToDouble(FinancingContract::getLoanAmount)
                .sum();
        
        double activeAmount = allContracts.stream()
                .filter(c -> "Aktif".equals(c.getStatus()))
                .mapToDouble(FinancingContract::getLoanAmount)
                .sum();
        
        return new ContractStats(totalContracts, activeContracts, overdueContracts, 
                               completedContracts, totalAmount, activeAmount);
    }
    
    /**
     * Inner class for contract statistics
     */
    public static class ContractStats {
        private final int totalContracts;
        private final int activeContracts;
        private final int overdueContracts;
        private final int completedContracts;
        private final double totalAmount;
        private final double activeAmount;
        
        public ContractStats(int totalContracts, int activeContracts, int overdueContracts,
                           int completedContracts, double totalAmount, double activeAmount) {
            this.totalContracts = totalContracts;
            this.activeContracts = activeContracts;
            this.overdueContracts = overdueContracts;
            this.completedContracts = completedContracts;
            this.totalAmount = totalAmount;
            this.activeAmount = activeAmount;
        }
        
        // Getters
        public int getTotalContracts() { return totalContracts; }
        public int getActiveContracts() { return activeContracts; }
        public int getOverdueContracts() { return overdueContracts; }
        public int getCompletedContracts() { return completedContracts; }
        public double getTotalAmount() { return totalAmount; }
        public double getActiveAmount() { return activeAmount; }
    }
}