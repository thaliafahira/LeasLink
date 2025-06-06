# LeasLink - Vehicle Financing Management System

LeasLink is a desktop application designed to support PT Mitra's operational activities in managing vehicle financing receivables and payments. The system provides features for recording financing contracts, automating payment recording and reconciliation, providing loan status information, and generating real-time aging receivables reports.

## Features

- User Authentication (Login/Register)
- Contract Management
- Payment Processing
- Loan Status Tracking
- Real-time Aging Reports
- User Role Management (Admin/Customer)

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

1. Clone the repository (if using Git):

   ```bash
   git clone https://github.com/yourusername/leaslink.git
   cd leaslink
   ```

2. Build the project using Maven:

   ```bash
   mvn clean package
   ```

3. Run the application:
   ```bash
   java -jar target/leaslink-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

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

### 1. Authentication Module

- **Description**: Handles user registration and login
- **Features**:
  - User registration with email validation
  - Secure password handling
  - Role-based access control (Admin/Customer)
- **Screenshots**: [To be added in doc/ directory]

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

[Additional tables will be added as more modules are implemented]

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is proprietary software owned by PT Mitra. All rights reserved.
