# CrimsonBank Final - Banking Management System

A comprehensive JavaFX-based banking management system built with Java 21, featuring customer management, account operations, transaction processing, and loan approvals.

## Technology Stack

- **Language**: Java 21
- **UI Framework**: JavaFX 21.0.10
- **Database**: MySQL 8.4.0
- **Build Tool**: Maven 3.9+
- **Testing**: JUnit 5.10.2

## Project Structure

```
CrimsonBankFinal/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── module-info.java
│   │   │   └── com/crimsonbank/
│   │   │       ├── Main.java
│   │   │       ├── controllers/ (10 FXML controllers)
│   │   │       ├── models/ (Entity classes)
│   │   │       ├── database/ (DAOs and connection pooling)
│   │   │       ├── exceptions/ (Custom exceptions)
│   │   │       ├── services/ (Business logic)
│   │   │       ├── utils/ (Validators and algorithms)
│   │   │       └── patterns/ (Design patterns)
│   │   └── resources/
│   │       └── com/crimsonbank/
│   │           ├── views/ (10 FXML files)
│   │           └── styles.css
│   └── test/
│       └── java/com/crimsonbank/
│           ├── models/ (Unit tests)
│           ├── utils/ (Algorithm tests)
│           └── patterns/ (Data structure tests)
├── database-scripts/ (11 SQL scripts)
└── pom.xml
```

## Database Setup

### Prerequisites
- XAMPP with MySQL installed
- PHPMyAdmin access

### Setup Instructions

1. **Create Database**:
   - Open PHPMyAdmin (http://localhost/phpmyadmin)
   - Execute each SQL script in order:
     1. `01_create_staff.sql`
     2. `02_create_customers.sql`
     3. `03_create_accounts.sql`
     4. `04_create_transactions.sql`
     5. `05_create_loans.sql`
     6. `06_create_audit_log.sql`
     7. `07_insert_sample_data.sql` (contains sample data; includes 500+ transactions)
     8. `08_insert_staff_accounts.sql` (contains default staff accounts)
     9. `09_add_profile_images.sql`
    10. `10_migration_existing_passwords.sql`
    11. `11_insert_loan_data.sql`

2. **Database Credentials** (in DatabaseConnection.java):
   ```
   URL: jdbc:mysql://localhost:3306/crimsonbank
   Username: root
   Password: (empty)
   ```

3. **Default Staff Accounts**:
   - **Admin**: username: `admin`, password: `admin123`
   - **Supervisor**: username: `supervisor`, password: `supervisor123`
   - **Staff**: username: `staff`, password: `staff123`

## Building the Project

```bash
# Clone or navigate to project directory
cd CrimsonBankFinal

# Build the project
mvn clean install

# Run the application
mvn javafx:run

# Run tests
mvn test
```

## Features

### 10 Core Pages

1. **Login** - Staff authentication with audit logging
2. **Signup** - New staff account creation with validation
3. **Dashboard** - System overview with key metrics
4. **Customer Management** - CRUD operations for customers
5. **Account Management** - Create and manage bank accounts
6. **Transactions** - Deposit, withdrawal, and transfer operations
7. **Transaction History** - View 500+ transactions with pagination
8. **Loan Management** - Loan requests with FIFO queue processing
9. **Reports & Analytics** - Charts and CSV export functionality
10. **Audit Log** - Action tracking and system monitoring

### Key Features

- **500+ Transaction Records**: Efficient handling of large datasets
- **Sorting Algorithms**: QuickSort and MergeSort implementation
- **Binary Search**: Fast account lookup
- **Queue Implementation**: FIFO loan approval processing
- **Stack Implementation**: LIFO recent actions tracking
- **Design Patterns**:
  - Singleton: Database connection management
  - Factory: Account type creation (Savings/Current)
  - Observer: Audit log updates
- **Exception Handling**: Custom exceptions with user-friendly alerts
- **Input Validation**: Email, NIC, password, and amount validation
- **SOLID Principles**: Applied throughout the codebase

## Running the Application

### Prerequisites
- Java 21 JDK installed
- Maven configured
- MySQL server running
- Database created with sample data

### Execute

```bash
mvn javafx:run
```

The application will start with the login screen.

## Testing

Run all unit tests:
```bash
mvn test
```

Test Coverage:
- Model entity tests
- Input validation tests
- Sorting algorithm tests
- Data structure tests (Queue, Stack)
- Factory pattern tests

## Database Features

### Tables Created
- **staff**: Staff member accounts (3 default users)
- **customers**: Customer information (10 sample records)
- **accounts**: Bank accounts (20 sample accounts)
- **transactions**: Transaction records (500+ entries)
- **loans**: Loan requests with approval tracking
- **audit_log**: Action tracking (important events only)

### Indexes
- Email and username indexes for fast staff lookup
- NIC index for customer searches
- Account number and status indexes
- Created date indexes for efficient sorting

### Foreign Keys
- Proper referential integrity between all tables
- Cascade delete for related records

## Code Quality

- **No Comments in Source Code**: Clean, self-documenting code with meaningful names
- **Plain Text Passwords**: As per requirements (not hashed)
- **Prepared Statements**: SQL injection prevention
- **Transaction Management**: Financial operations with proper error handling
- **Method Size**: Limited to 30 lines maximum
- **Error Handling**: Try-catch blocks with user-friendly Alert dialogs

## Design Patterns Implemented

1. **Singleton Pattern**: DatabaseConnection for single instance access
2. **Factory Pattern**: SavingsAccountFactory and CurrentAccountFactory
3. **Observer Pattern**: AuditLog for monitoring system actions
4. **Strategy Pattern**: Sorting strategies (QuickSort/MergeSort)

## Algorithms

### Sorting
- **QuickSort**: For efficient sorting of lists
- **MergeSort**: For stable sorting of transactions by date/amount

### Searching
- **Binary Search**: For searching in sorted lists
- **Linear Search**: For account lookups

### Data Structures
- **Queue**: FIFO loan approval queue
- **Stack**: LIFO recent actions stack
- **TreeMap**: Potential for sorted account storage

## Performance Optimizations

1. **Connection Pooling**: Reusable database connections
2. **Pagination**: Large result sets handled efficiently
3. **Indexes**: Database queries optimized with proper indexing
4. **Prepared Statements**: Prevents SQL injection and improves performance

## Validation Rules

### Input Validation
- Email: Standard email format
- Password: Minimum 6 characters
- NIC: Sri Lankan format (9 digits + V/X)
- Phone: Sri Lankan format
- Amounts: Positive numbers with 2 decimal places
- Account Numbers: Unique, formatted as ACC######

### Business Rules
- Cannot withdraw more than balance
- Cannot delete customers with active accounts
- Loan approvals require supervisor role
- Transfer requires both accounts active
- Audit logs cannot be modified

## File Organization

```
src/main/java/com/crimsonbank/
├── controllers/      (10 JavaFX controllers)
├── models/          (Entity POJOs)
├── database/        (DAOs and connection)
├── exceptions/      (Custom exception classes)
├── services/        (Business logic)
├── utils/           (Validators and algorithms)
├── patterns/        (Design pattern implementations)
└── Main.java        (Application entry point)

src/main/resources/com/crimsonbank/
├── views/           (10 FXML files)
└── styles.css       (Professional banking theme)

src/test/java/com/crimsonbank/
├── models/          (Model tests)
├── utils/           (Algorithm tests)
└── patterns/        (Data structure tests)

database-scripts/
├── 01_create_staff.sql
├── 02_create_customers.sql
├── 03_create_accounts.sql
├── 04_create_transactions.sql
├── 05_create_loans.sql
├── 06_create_audit_log.sql
├── 07_insert_sample_data.sql
├── 08_insert_staff_accounts.sql
├── 09_add_profile_images.sql
├── 10_migration_existing_passwords.sql
└── 11_insert_loan_data.sql
```

## Troubleshooting

### Database Connection Error
- Verify MySQL service is running
- Check credentials in DatabaseConnection.java
- Ensure crimsonbank database exists
- Verify user 'root' has no password

### JavaFX Module Error
- Ensure FXML files are in correct path
- Check module-info.java has required declarations
- Verify resources are properly configured in Maven

### FXML File Not Found
- Check file names match exactly (case-sensitive)
- Ensure files are in src/main/resources/com/crimsonbank/views/
- Run `mvn clean install` to rebuild resources

## Development Notes

- All database operations use prepared statements
- Exception handling provides user-friendly error messages
- Audit logs capture only important events (login, approvals, major CRUD)
- Sorting algorithms handle 500+ records efficiently
- Queue implementation ensures FIFO loan processing
- CSS styling provides professional banking appearance

## Future Enhancements

- Export reports to PDF
- Email notifications for loan approvals
- Mobile application integration
- Advanced analytics dashboard
- Two-factor authentication
- Account statement generation

## Support

For issues or questions, review:
1. Database connection settings
2. FXML file paths
3. Maven build output
4. Application logs in console

---

**Version**: 1.0-SNAPSHOT  
**Java Version**: 21  
**Last Updated**: February 8, 2026
