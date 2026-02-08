# g) Introduction
BankCore is a desktop banking operations system developed for Crimson Capital Bank to replace manual and spreadsheet-driven workflows. The project addresses daily operational needs such as staff authentication, customer and account management, transaction processing, loan handling, reporting, and audit logging, while also processing a large dataset of transactions. The application is designed and implemented using object-oriented principles, SOLID practices, clean coding techniques, and standard design patterns, with a strong focus on maintainability, testability, and data accuracy. This report documents the analysis, design decisions, implementation approach, testing strategy, and evaluation aligned to the applied programming and design principles learning outcomes.

# h) Body

## Part 1 — Investigate the impact of SOLID on OOP paradigm

### A. OOP characteristics in BankCore
Encapsulation:
- Domain models (e.g., Customer, Account, Transaction, Loan) use private fields with getters/setters and validation to protect data integrity and prevent invalid state.
- Controllers and services expose only required operations, hiding internal persistence logic.
- Example (`src/main/java/com/crimsonbank/models/Account.java`):
```java
public class Account {
    private int accountId;
    private BigDecimal balance;

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
```

Abstraction:
- Data access is abstracted through DAO/service classes, allowing UI controllers to depend on abstractions rather than concrete database logic.
- Common behaviors are grouped in interfaces/abstract classes to reduce duplication.
- Example (`src/main/java/com/crimsonbank/controllers/BaseController.java`):
```java
public abstract class BaseController {
    protected void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operation Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
```

Polymorphism:
- Service interfaces support different implementations (e.g., real DB service vs mock service for tests), enabling runtime substitution.
- Controllers invoke behaviors through base types, allowing overrides without changing callers.
- Example (`src/main/java/com/crimsonbank/patterns/AccountFactory.java` with implementations):
```java
public interface AccountFactory {
    Account createAccount(int customerId, String accountNumber, double initialBalance);
}

public class CurrentAccountFactory implements AccountFactory {
    @Override
    public Account createAccount(int customerId, String accountNumber, double initialBalance) {
        return new Account(customerId, accountNumber, "CURRENT", BigDecimal.valueOf(initialBalance));
    }
}

public class SavingsAccountFactory implements AccountFactory {
    @Override
    public Account createAccount(int customerId, String accountNumber, double initialBalance) {
        return new Account(customerId, accountNumber, "SAVINGS", BigDecimal.valueOf(initialBalance));
    }
}
```

Constructors and validation:
- Constructors are used to ensure objects are created in a valid state (e.g., account must have an owner, initial balance rules).
- Example (`src/main/java/com/crimsonbank/models/Account.java`):
```java
public Account(int customerId, String accountNumber, String accountType, BigDecimal balance) {
    this.customerId = customerId;
    this.accountNumber = accountNumber;
    this.accountType = accountType;
    this.balance = balance;
    this.status = "ACTIVE";
}
```

Interfaces and overriding:
- Interfaces define contracts for services/DAOs, with concrete classes providing specific SQL-based implementations.
- Example (`src/main/java/com/crimsonbank/patterns/AccountFactory.java`):
```java
public interface AccountFactory {
    Account createAccount(int customerId, String accountNumber, double initialBalance);
}
```

Relationships (generalisation, dependency, aggregation, composition):
- Generalisation/inheritance appears in shared controller/service base classes.
- Dependency is used where controllers depend on services.
- Aggregation/composition is used where Account belongs to Customer and Transactions belong to Account.
- Example (inheritance: `src/main/java/com/crimsonbank/controllers/BaseController.java`):
```java
public abstract class BaseController {
    protected void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
```

### B. SOLID principles applied in BankCore
Single Responsibility:
- Controllers handle UI flow; services handle business rules; DAOs handle persistence.

Open/Closed:
- Adding a new report, transaction filter, or export format is done by extending services, not editing existing core logic.

Liskov Substitution:
- Mock services can replace real services in tests without breaking expected behavior.

Interface Segregation:
- Service interfaces are separated by domain (CustomerService, AccountService, LoanService), avoiding fat interfaces.

Dependency Inversion:
- High-level modules depend on abstractions (interfaces). Concrete implementations are injected/instantiated centrally.

### C. Design patterns in BankCore
Creational:
- Singleton for application configuration (settings or DB connection manager).
- Factory Method for DAO/service creation, simplifying swapping implementations.

Structural:
- Facade in service layer to combine multiple DAO calls for complex operations.
- Adapter for mapping DB rows to model objects.

Behavioral:
- Observer (JavaFX bindings/listeners) to update UI when data changes.
- Command-like structure for transaction operations (deposit/withdraw/transfer) to isolate action logic.

### D. Clean coding impact on algorithms and data structures
- Meaningful naming makes algorithms easier to understand and debug (e.g., transactionAmount vs x).
- Small, focused functions reduce risk of side effects in search/sort operations.
- Boundary handling avoids errors in binary search and pagination.
- Tests should be readable, fast, and independent; fragile tests break when internal changes occur.

## Part 2 — Design a large dataset processing application

### A. Requirements
Functional requirements:
- Staff authentication (login/sign-up).
- Customer CRUD with search.
- Account creation and updates.
- Deposit, withdraw, transfer with validation.
- Transaction history with filters and sorting.
- Loan requests with approval workflow.
- Reports/analytics with CSV export.
- Audit log tracking.

Non-functional requirements:
- Performance for 500+ records.
- Data integrity and validation.
- Maintainable code using SOLID and patterns.
- Usable UI with consistent navigation.
- Testability and reliability.

### B. Components selection and mapping
Data structures:
- List/ObservableList for UI tables.
- Stack for recent views.
- Queue for loan approval workflow (FIFO).
- Tree (BST) demonstration for report requirement.

Searching algorithms:
- Linear search for small filtered lists.
- Binary search for sorted account numbers.

Sorting algorithms:
- Built-in sort for large datasets; insertion sort demonstrated for learning requirement.

### C. Design artifacts (diagrams)
- Use Case Diagram: staff interactions with login, manage customers/accounts, transactions, loans, reports, audit log.
- Class Diagram: models, services, controllers, DAO classes, and relationships.
- Sequence Diagram: sample flow for transfer or loan approval.
- Activity Diagram: transaction processing and validation steps.
- Layered Architecture Diagram: UI -> Controller -> Service -> DAO -> Database.

### D. Required operations

D.1 Stack and Queue representation

Stack (Last Viewed Accounts):
push(A101) -> [A101]
push(A205) -> [A101, A205]
push(A330) -> [A101, A205, A330]
pop() -> [A101, A205]
push(A777) -> [A101, A205, A777]
pop() -> [A101, A205]
pop() -> [A101]

Queue (Pending Approvals):
enqueue(T01) -> [T01]
enqueue(T02) -> [T01, T02]
dequeue() -> [T02]
enqueue(T03) -> [T02, T03]
front() -> T02
enqueue(T04) -> [T02, T03, T04]
dequeue() -> [T03, T04]

D.2 Binary search (target = 1450 in [1002, 1020, 1205, 1300, 1450, 1501, 1999])
Iteration 1: left=0, right=6, mid=3, midValue=1300 -> target > midValue -> left=4
Iteration 2: left=4, right=6, mid=5, midValue=1501 -> target < midValue -> right=4
Iteration 3: left=4, right=4, mid=4, midValue=1450 -> found
Total iterations: 3

D.3 BST insertion (50, 30, 70, 20, 40, 60, 80)

Final BST:
- Root 50
- Left subtree: 30 with children 20 and 40
- Right subtree: 70 with children 60 and 80

Traversals:
In-order: 20, 30, 40, 50, 60, 70, 80
Pre-order: 50, 30, 20, 40, 70, 60, 80
Post-order: 20, 40, 30, 60, 80, 70, 50

D.4 Insertion sort steps on [4500, 1200, 9000, 3000, 1500]
Step 1: [1200, 4500, 9000, 3000, 1500]
Step 2: [1200, 4500, 9000, 3000, 1500]
Step 3: [1200, 3000, 4500, 9000, 1500]
Step 4: [1200, 1500, 3000, 4500, 9000]

Best for 5000+ records: built-in sort (TimSort) or Merge/Quick for efficiency; insertion sort is better only for small or nearly sorted data.

### E. Refined design with patterns and benefits
- Singleton for configuration ensures consistent settings and easy access.
- Factory for DAO/service creation reduces coupling and simplifies testing.
Benefits: improved maintainability, easier replacement of DB or services, reduced code duplication.

### F. Testing regime
Manual tests:
- Login, sign-up validations, CRUD operations, transaction flow, loan approvals, reports.

Automated tests:
- Service and DAO tests using JUnit and Mockito.
- GUI tests using TestFX for login and simple UI flows.

Test plan includes test ID, objective, preconditions, steps, expected results, actual results (left blank for later entry).

### G. Critical evaluation (SOLID impact)
- SOLID improves change safety: new features can be added without modifying core classes.
- Trade-offs include more interfaces and files, but the modularity benefits outweigh overhead.

Before vs after example (detailed):

Checklist (what follows is included):
- Goal: show a compact "before" (tightly-coupled) and "after" (SOLID-friendly) pseudocode example.
- Scope: account creation flow (UI validation + persistence) in Java-like pseudocode.
- Success: each snippet ≤10 lines and includes short SOLID mapping comments.

Before (tightly-coupled, violates SOLID) — 7 lines
```pseudo
void handleCreateAccount() {
    String name = txtName.getText(); String bal = txtBalance.getText();
    if (name.isEmpty() || Double.parseDouble(bal) < 0) { showAlert("Invalid"); return; }
    String sql = "INSERT INTO accounts(name,balance) VALUES('" + name + "'," + bal + ")";
    Connection conn = new SqlConnection("jdbc:..."); conn.execute(sql);
    showAlert("Account created");
}
```
- SOLID impact: Violates Single Responsibility (UI + validation + persistence mixed) and Dependency Inversion (depends on concrete SQL/Connection); also hurts Open/Closed (changing persistence forces edits here).

After (SOLID-friendly, responsibilities separated) — 8 lines
```pseudo
class AccountController { AccountService svc; void onCreate() { svc.create(new AccountDTO(name, balance)); } }
interface AccountService { void create(AccountDTO dto); }
class AccountServiceImpl implements AccountService {
    Validator<AccountDTO> v; AccountDao dao;
    void create(AccountDTO dto) { v.validate(dto); Account a = dto.toModel(); dao.save(a); }
}
// Controller constructed with AccountServiceImpl which is injected with Validator and AccountDao abstractions
```
- SOLID impact: Respects Single Responsibility (UI, validation, business logic, persistence separated), Dependency Inversion (high-level modules depend on interfaces), Open/Closed (new Dao/Validator implementations can be added without changing controller/service), and Interface Segregation (focused interfaces).

Before vs after summary:
- The "before" snippet shows tight coupling and mixed concerns; the "after" snippet demonstrates clear separation: UI → Service → Validator → DAO, improving maintainability and testability.

## Part 3 — Build the application

### A. Implementation summary
- JavaFX scenes cover login, sign-up, dashboard, customers, accounts, transactions, history, loans, reports, audit log.
- JDBC used for DB operations with SQL scripts for schema and sample data.
- Transaction processing writes to DB and updates history.
- Large dataset handled using pagination/filtering and efficient sorting.
- Consistent error handling with user-friendly alerts.
- Clean package structure for controllers, services, models, DAOs, utils.

### B. Exception handling
- Validation errors shown via alerts (e.g., required fields, insufficient balance).
- Database errors logged and user-safe messages displayed.
- Custom exceptions simplify error handling and clarify intent.

### C. Effectiveness and improvements
- SOLID improved testability and reduced risk when adding features.
- Clean coding improved readability and debugging.
- Design patterns increased extensibility for reports and data access.

Future improvements: role-based permissions, advanced analytics, and background job scheduling.

## Part 4 — Automatic testing

### A. Testing methods
- Unit tests for services and algorithms.
- Integration tests for DAO/database.
- GUI automation for login flow.

Good tests are fast, independent, and readable; bad tests are fragile and slow.

### B. Implement and execute tests
- JUnit tests for validation and transaction logic.
- Mockito for service isolation.
- TestFX for login UI with valid and invalid cases.

### C. Developer-made tools vs frameworks
- Frameworks (JUnit/TestFX) provide reliable assertions, reports, and automation.
- Custom tools are flexible but harder to maintain.

Best fit: JUnit + TestFX for BankCore due to reliability and tooling support.

### D. GUI automation demo
- JavaFX login screen with username/password fields and login button.
- Rule: staff / 1234 -> Login Success, else Login Failed.
- Two automated tests: valid and invalid login.

### E. Comparison of automatic testing forms
- Unit tests: fast, high coverage, low cost.
- Integration tests: ensure DB correctness, higher cost.
- UI tests: realistic, but slower and more brittle.

Highest value: unit + integration tests, with minimal UI tests for critical flows.

# i) Conclusion
BankCore delivers a complete desktop banking operations system aligned to the project scenario and learning outcomes. The system combines strong OOP foundations with SOLID principles, clean coding, and design patterns to ensure maintainability, scalability, and testability. The implementation meets functional requirements including authentication, customer/account management, transactions, loan processing, reporting, and audit logging, while supporting large datasets and robust error handling. Testing strategy balances automated and manual coverage, providing confidence in correctness. Overall, the solution demonstrates professional software development practices suited for real-world banking operations.

# j) References (Harvard style, sample list)
- Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994) Design Patterns: Elements of Reusable Object-Oriented Software. Boston: Addison-Wesley.
- Martin, R.C. (2017) Clean Architecture: A Craftsman’s Guide to Software Structure and Design. Boston: Pearson.
- Bloch, J. (2018) Effective Java. 3rd edn. Boston: Addison-Wesley.
- Horstmann, C.S. (2016) Core Java Volume I – Fundamentals. 10th edn. Boston: Pearson.
- Beck, K. (2002) Test Driven Development: By Example. Boston: Addison-Wesley.
- Fowler, M. (2004) UML Distilled. 3rd edn. Boston: Addison-Wesley.
- IEEE (2014) ISO/IEC/IEEE 29148: Systems and Software Engineering — Requirements Engineering. New York: IEEE.
- Oracle (2023) Java Platform, Standard Edition Documentation. Available at: https://docs.oracle.com/javase/ (Accessed: 4 February 2026).
- OpenJFX (2024) JavaFX Documentation. Available at: https://openjfx.io/ (Accessed: 4 February 2026).

# k) Appendix
Appendix A: SQL Scripts
- Schema creation scripts and sample data inserts.

Appendix B: Diagrams
- Use Case Diagram
- Class Diagram
- Sequence Diagram
- Activity Diagram
- Layered Architecture Diagram
- ER Diagram

Appendix C: Test Evidence
- Unit test results
- Integration test results
- TestFX login test screenshots

Appendix D: Screenshots
- Login, dashboard, customers, accounts, transactions, history, loans, reports, audit log
