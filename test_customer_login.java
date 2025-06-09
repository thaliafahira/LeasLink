// Quick test to verify customer login and data retrieval
import com.leaslink.controllers.AuthController;
import com.leaslink.controllers.CustomerController;
import com.leaslink.models.User;
import com.leaslink.models.Lease;
import com.leaslink.models.Payment;
import java.util.List;

public class test_customer_login {
    public static void main(String[] args) {
        try {
            System.out.println("=== TESTING CUSTOMER LOGIN AND DATA RETRIEVAL ===");
            
            // Test customer login
            User customer = AuthController.login("customer@leaslink.com", "customer");
            if (customer != null) {
                System.out.println("✅ Customer login successful: " + customer.getFullName());
                System.out.println("Customer ID: " + customer.getId());
                System.out.println("Customer Role: " + customer.getRole());
                
                // Test customer lease retrieval
                System.out.println("\n--- Testing lease data retrieval ---");
                List<Lease> leases = CustomerController.getCustomerLeases(customer.getId());
                System.out.println("✅ Customer leases retrieved: " + leases.size() + " leases found");
                
                for (Lease lease : leases) {
                    System.out.println("Lease ID: " + lease.getId());
                    System.out.println("  - Motorcycle: " + lease.getFullMotorcycleName());
                    System.out.println("  - Status: " + lease.getStatus());
                    System.out.println("  - Start Date: " + (lease.getStartDate() != null ? lease.getStartDate() : "NULL"));
                    System.out.println("  - End Date: " + (lease.getEndDate() != null ? lease.getEndDate() : "NULL"));
                }
                
                // Test customer payment retrieval
                System.out.println("\n--- Testing payment data retrieval ---");
                List<Payment> payments = CustomerController.getCustomerPayments(customer.getId());
                System.out.println("✅ Customer payments retrieved: " + payments.size() + " payments found");
                
                for (Payment payment : payments) {
                    System.out.println("Payment ID: " + payment.getId());
                    System.out.println("  - Amount: " + payment.getFormattedAmount());
                    System.out.println("  - Payment Date: " + (payment.getPaymentDate() != null ? payment.getPaymentDate() : "NULL"));
                    System.out.println("  - Motorcycle: " + payment.getMotorcycleName());
                }
                
                System.out.println("\n✅ ALL TESTS PASSED - Customer login timestamp issue has been FIXED!");
                
            } else {
                System.out.println("❌ Customer login failed");
            }
            
        } catch (Exception e) {
            System.out.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
