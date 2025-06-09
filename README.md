# LeasLink - Vehicle Financing Management System

LeasLink is a desktop application designed to support PT Mitra's operational activities in managing vehicle financing receivables and payments. The system provides features for recording financing contracts, automating payment recording and reconciliation, providing loan status information, and generating real-time aging receivables reports.

## Features

### Core Features
- **User Authentication** - Secure login/register with role-based access control
- **Contract Management** - Complete CRUD operations for lease contracts
- **Payment Processing** - Automated payment recording and reconciliation
- **Loan Status Tracking** - Real-time monitoring of contract statuses
- **Real-time Aging Reports** - Dynamic aging analysis with visual charts
- **User Role Management** - Admin and Customer role separation

### Admin Features
- **Admin Dashboard** - Comprehensive overview with key metrics
- **Contract Approval Workflow** - Review and approve/reject customer applications
- **Payment Management** - Record and track all customer payments
- **Customer Management** - View and manage all customer accounts
- **Advanced Reporting** - Detailed aging reports with data export capabilities
- **System Administration** - User management and system configuration

### Customer Features
- **Customer Dashboard** - Personal overview of active leases and payments
- **Lease Application** - Submit new vehicle financing requests
- **Payment History** - Complete payment tracking and history
- **Contract Status** - Real-time status updates for applications
- **Document Management** - Access to contract documents and receipts

## Technical Stack

- Language: Java 21
- Build Tool: Maven
- Database: SQLite
- UI Framework: Java Swing
- Testing Framework: JUnit 5

## Prerequisites

- Java Development Kit (JDK) 17 or higher
- Maven 3.6 or higher
- Git (optional, for version control)

## Setup Instructions

### Method 1: Using Pre-built JAR (Recommended)

1. **Download or Clone the Repository**:
   ```bash
   git clone https://github.com/yourusername/leaslink.git
   cd leaslink
   ```

2. **Run the Application**:
   ```bash
   # If JAR file exists
   java -jar target/leaslink.jar
   
   # Alternative method using Maven
   mvn exec:java -Dexec.mainClass="com.leaslink.Main"
   ```

### Method 2: Build from Source

1. **Verify Prerequisites**:
   ```bash
   # Check Java version (should be 17+)
   java -version
   
   # Check Maven version (should be 3.6+)
   mvn -version
   ```

2. **Build the Project**:
   ```bash
   # Clean and compile
   mvn clean compile
   
   # Run tests
   mvn test
   
   # Package the application
   mvn package
   ```

3. **Run the Application**:
   ```bash
   java -jar target/leaslink.jar
   ```

### Method 3: Development Mode

1. **IDE Setup** (IntelliJ IDEA/Eclipse):
   - Import as Maven project
   - Set Project SDK to Java 17+
   - Run `Main.java` class

2. **Command Line Development**:
   ```bash
   # Compile and run in one step
   mvn compile exec:java -Dexec.mainClass="com.leaslink.Main"
   
   # Run with automatic recompilation (development)
   mvn spring-boot:run
   ```

### Database Initialization

The application will automatically:
- Create `leaslink.db` SQLite database file
- Initialize all required tables
- Insert sample data for testing

**Sample Login Credentials**:
- **Admin**: `admin@leaslink.com` / `admin123`
- **Customer**: `customer@demo.com` / `customer123`

## Project Structure

```
leaslink/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── leaslink/
│                   ├── controllers/    # Business logic
│                   ├── models/         # Data models
│                   ├── utils/          # Utility classes
│                   └── views/          # UI components
├── tests/                             # Test cases
├── doc/                               # Documentation and screenshots
├── img/                              # Image assets
├── pom.xml                           # Maven configuration
└── README.md                         # This file
```

## Implemented Modules

### 1. Authentication Module ✅ COMPLETE

- **Description**: Handles user registration and login with role-based access
- **Features**:
  - User registration with email validation
  - Secure password handling with encryption
  - Role-based access control (Admin/Customer)
  - Session management and automatic logout
  - Password reset functionality

### 2. Contract Management Module ✅ COMPLETE

- **Description**: Full lifecycle management of vehicle financing contracts
- **Features**:
  - Contract creation and editing
  - Multi-step approval workflow
  - Contract status tracking (Pending → Approved → Active → Completed)
  - Document generation and storage
  - Contract search and filtering capabilities

### 3. Payment Processing Module ✅ COMPLETE

- **Description**: Comprehensive payment handling and reconciliation
- **Features**:
  - Automated payment recording
  - Payment history tracking
  - Late payment identification
  - Payment reconciliation tools
  - Receipt generation
  - Bulk payment processing

### 4. Customer Dashboard Module ✅ COMPLETE

- **Description**: Customer-facing interface for self-service operations
- **Features**:
  - Personal lease overview
  - Payment history visualization
  - Contract status monitoring
  - Online payment submission
  - Document download center

### 5. Admin Dashboard Module ✅ COMPLETE

- **Description**: Administrative control panel with comprehensive system oversight
- **Features**:
  - System-wide metrics and KPIs
  - Contract approval queue
  - Customer management tools
  - Payment oversight and reconciliation
  - User account administration

### 6. Aging Reports Module ✅ COMPLETE

- **Description**: Real-time aging analysis and reporting system
- **Features**:
  - Dynamic aging calculations (0-30, 31-60, 61-90, 90+ days)
  - Interactive charts and visualizations
  - Automated report generation
  - Data export capabilities (CSV, PDF)
  - Trend analysis and forecasting

### 7. Database Management ✅ COMPLETE

- **Description**: Robust data layer with optimized performance
- **Features**:
  - SQLite database with ACID compliance
  - Optimized queries for large datasets
  - Data integrity constraints
  - Automatic backup and recovery
  - Database migration support

### 2. Database Schema

#### Users Table
| Column     | Type      | Description                 |
| ---------- | --------- | --------------------------- |
| id         | INTEGER   | Primary key, auto-increment |
| email      | TEXT      | Unique user email           |
| password   | TEXT      | Hashed password             |
| full_name  | TEXT      | User's full name            |
| role       | TEXT      | User role (Admin/Customer)  |
| created_at | TIMESTAMP | Account creation timestamp  |

#### Motorcycles Table
| Column      | Type    | Description                    |
| ----------- | ------- | ------------------------------ |
| id          | INTEGER | Primary key, auto-increment    |
| brand       | TEXT    | Motorcycle brand               |
| model       | TEXT    | Motorcycle model               |
| year        | INTEGER | Manufacturing year             |
| color       | TEXT    | Vehicle color                  |
| price       | REAL    | Vehicle price                  |
| available   | BOOLEAN | Availability status            |

#### Leases Table
| Column       | Type      | Description                    |
| ------------ | --------- | ------------------------------ |
| id           | INTEGER   | Primary key, auto-increment    |
| customer_id  | INTEGER   | Foreign key to users table     |
| motorcycle_id| INTEGER   | Foreign key to motorcycles     |
| start_date   | DATE      | Lease start date               |
| end_date     | DATE      | Lease end date                 |
| monthly_payment| REAL    | Monthly payment amount         |
| status       | TEXT      | Lease status                   |
| created_at   | TIMESTAMP | Record creation timestamp      |

#### Payments Table
| Column       | Type      | Description                    |
| ------------ | --------- | ------------------------------ |
| id           | INTEGER   | Primary key, auto-increment    |
| lease_id     | INTEGER   | Foreign key to leases table    |
| amount       | REAL      | Payment amount                 |
| payment_date | DATE      | Payment date                   |
| due_date     | DATE      | Payment due date               |
| status       | TEXT      | Payment status                 |
| created_at   | TIMESTAMP | Record creation timestamp      |

## System Requirements

### Minimum Requirements
- **OS**: Windows 10/11, macOS 10.14+, or Linux (Ubuntu 18.04+)
- **Java**: OpenJDK 17 or Oracle JDK 17+
- **RAM**: 4 GB minimum, 8 GB recommended
- **Storage**: 500 MB free space
- **Display**: 1024x768 minimum resolution

### Recommended Requirements
- **OS**: Windows 11 or macOS 12+
- **Java**: OpenJDK 21 or Oracle JDK 21
- **RAM**: 8 GB or more
- **Storage**: 1 GB free space
- **Display**: 1920x1080 or higher resolution

## Getting Started

### Quick Start Guide

1. **Login Credentials**:
   - **Admin**: admin@leaslink.com / admin123
   - **Customer**: customer@demo.com / customer123

2. **Admin Workflow**:
   - Login → Admin Dashboard → Manage Contracts → Process Payments → Generate Reports

3. **Customer Workflow**:
   - Login → Customer Dashboard → View Leases → Check Payments → Submit Applications

## Recent Updates & Bug Fixes

### Version 2.0.0 (June 2025)
- ✅ **CRITICAL FIX**: Resolved customer login timestamp parsing error
- ✅ **ENHANCEMENT**: Improved date handling across all modules
- ✅ **NEW FEATURE**: Enhanced aging reports with visual charts
- ✅ **IMPROVEMENT**: Optimized database queries for better performance
- ✅ **FIX**: Added null safety checks for all date operations
- ✅ **ENHANCEMENT**: Updated UI components for better user experience

### Known Issues
- None currently identified - system is stable and fully functional

## Testing

The system includes comprehensive test coverage:

```bash
# Run all tests
mvn test

# Run specific test classes
mvn test -Dtest=CustomerControllerTest
mvn test -Dtest=LeaseControllerTest

# Test customer login functionality
java -cp "target/leaslink.jar;." test_customer_login
```

## Troubleshooting

### Common Issues

**Q: Application won't start - "Could not find or load main class"**
```bash
# Solution: Rebuild the project
mvn clean package
java -cp target/leaslink.jar com.leaslink.Main
```

**Q: Database connection errors**
```bash
# Solution: Check if leaslink.db exists in project root
# If missing, the application will create it automatically on first run
```

**Q: Login fails with timestamp errors**
```bash
# This issue has been resolved in v2.0.0
# If you encounter this, ensure you're using the latest version
```

**Q: UI elements not displaying correctly**
```bash
# Solution: Ensure you're using Java 17+ and have proper display settings
java -Dsun.java2d.dpiaware=false -jar target/leaslink.jar
```

## Contributing

### Development Setup

1. **Clone and Setup**:
   ```bash
   git clone https://github.com/yourusername/leaslink.git
   cd leaslink
   mvn clean install
   ```

2. **Development Guidelines**:
   - Follow Java naming conventions
   - Write unit tests for new features
   - Update documentation for API changes
   - Use meaningful commit messages

3. **Code Style**:
   - Use 4 spaces for indentation
   - Maximum line length: 120 characters
   - Use descriptive variable and method names
   - Add JavaDoc comments for public methods

4. **Testing Requirements**:
   - Unit tests must pass: `mvn test`
   - Integration tests for database operations
   - UI testing for new components

### Contribution Process

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Write tests for your changes
4. Ensure all tests pass (`mvn clean test`)
5. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
6. Push to the branch (`git push origin feature/AmazingFeature`)
7. Open a Pull Request with detailed description

### Reporting Issues

Please use the GitHub issue tracker to report bugs or request features:
- Include system information (OS, Java version)
- Provide steps to reproduce the issue
- Attach relevant log files or screenshots
- Label the issue appropriately (bug, enhancement, question)

## License

This project is proprietary software owned by PT Mitra. All rights reserved.
