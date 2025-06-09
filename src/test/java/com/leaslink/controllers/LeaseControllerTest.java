package com.leaslink.controllers;

import com.leaslink.models.Lease;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for LeaseController functionality
 */
public class LeaseControllerTest {
    
    @Test
    void testGetAllLeases() throws SQLException {
        // Test getting all leases
        List<Lease> leases = LeaseController.getAllLeases(null, null);
        assertNotNull(leases, "Leases list should not be null");
        System.out.println("Total leases found: " + leases.size());
        
        if (!leases.isEmpty()) {
            Lease firstLease = leases.get(0);
            System.out.println("First lease - ID: " + firstLease.getId() + 
                             ", Customer: " + firstLease.getCustomerName() + 
                             ", Status: " + firstLease.getStatus());
        }
    }

    @Test
    void testGetLeaseSummary() throws SQLException {
        // Test getting lease summary statistics
        LeaseController.LeaseSummary summary = LeaseController.getLeaseSummary();
        assertNotNull(summary, "Lease summary should not be null");
        
        System.out.println("=== LEASE SUMMARY ===");
        System.out.println("Total Leases: " + summary.getTotalLeases());
        System.out.println("Active Leases: " + summary.getActiveLeases());
        System.out.println("Pending Leases: " + summary.getPendingLeases());
        System.out.println("Completed Leases: " + summary.getCompletedLeases());
        System.out.println("Total Lease Value: " + summary.getTotalLeaseValue());
        System.out.println("=====================");
    }

    @Test
    void testGetAvailableMotorcycles() throws SQLException {
        // Test getting available motorcycles
        List<String[]> motorcycles = LeaseController.getAvailableMotorcycles();
        assertNotNull(motorcycles, "Motorcycles list should not be null");
        System.out.println("Available motorcycles found: " + motorcycles.size());
        
        if (!motorcycles.isEmpty()) {
            String[] firstMotorcycle = motorcycles.get(0);
            System.out.println("First motorcycle - ID: " + firstMotorcycle[0] + 
                             ", Brand: " + firstMotorcycle[1] + 
                             ", Model: " + firstMotorcycle[2]);
        }
    }

    @Test
    void testGetAllCustomers() throws SQLException {
        // Test getting all customers
        List<String[]> customers = LeaseController.getAllCustomers();
        assertNotNull(customers, "Customers list should not be null");
        System.out.println("Customers found: " + customers.size());
        
        if (!customers.isEmpty()) {
            String[] firstCustomer = customers.get(0);
            System.out.println("First customer - ID: " + firstCustomer[0] + 
                             ", Name: " + firstCustomer[1] + 
                             ", Email: " + firstCustomer[2]);
        }
    }

    @Test
    void testLeaseValidation() {
        // Test lease validation with invalid data
        Lease invalidLease = new Lease();
        invalidLease.setCustomerId(0); // Invalid customer ID
        invalidLease.setMotorcycleId(0); // Invalid motorcycle ID
        invalidLease.setLeaseAmount(-1000); // Invalid amount
        
        String validationError = LeaseController.validateLease(invalidLease);
        assertNotNull(validationError, "Validation should return error for invalid lease");
        System.out.println("Validation error (as expected): " + validationError);
    }

    @Test
    void testCreateAndApproveLease() throws SQLException {
        // Test creating a new lease and then approving it
        List<String[]> customers = LeaseController.getAllCustomers();
        List<String[]> motorcycles = LeaseController.getAvailableMotorcycles();
        
        if (!customers.isEmpty() && !motorcycles.isEmpty()) {
            Lease newLease = new Lease();
            newLease.setCustomerId(Integer.parseInt(customers.get(0)[0]));
            newLease.setMotorcycleId(Integer.parseInt(motorcycles.get(0)[0]));
            newLease.setLeaseAmount(50000000.0); // 50 million IDR
            newLease.setMonthlyPayment(2500000.0); // 2.5 million IDR per month
            newLease.setLeaseDuration(24); // 24 months
            newLease.setStartDate(LocalDate.now());
            newLease.setEndDate(LocalDate.now().plusMonths(24));
            newLease.setStatus("pending");
            newLease.setCreatedBy(1); // Admin user
            
            // Validate the lease first
            String validationError = LeaseController.validateLease(newLease);
            if (validationError != null) {
                System.out.println("Lease validation failed: " + validationError);
                return;
            }
            
            // Create the lease
            boolean created = LeaseController.createLease(newLease);
            assertTrue(created, "Lease should be created successfully");
            System.out.println("New lease created successfully");
            
            // Get the created lease (assuming it's the latest one)
            List<Lease> leases = LeaseController.getAllLeases("pending", null);
            if (!leases.isEmpty()) {
                Lease createdLease = leases.get(0);
                
                // Approve the lease
                boolean approved = LeaseController.approveLease(createdLease.getId(), 1);
                assertTrue(approved, "Lease should be approved successfully");
                System.out.println("Lease approved successfully");
                
                // Verify the lease status changed
                Lease updatedLease = LeaseController.getLeaseById(createdLease.getId());
                assertEquals("active", updatedLease.getStatus(), "Lease status should be 'active'");
                System.out.println("Lease status verified as 'active'");
            }
        } else {
            System.out.println("Skipping create/approve test - no customers or motorcycles available");
        }
    }
}
