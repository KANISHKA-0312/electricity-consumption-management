# System Design Document
## Electricity Consumption Management and Energy Usage Analysis System
**Institution:** Velalar College of Engineering and Technology, Erode - 638012  
**Department:** Information Technology  
**Course Code:** 25ITT33 - Software Engineering | **Academic Year:** 2026-27 (ODD) | **Class:** 25IT3A  
**Student Name:** Kanishka | **Register No.:** 44  
**Assigned Project Scenario:** Scenario 14 (Register No.: 44)

---

## 1. Overall System Architecture

The Electricity Consumption Management and Energy Usage Analysis System adopts a clean **Three-Tier Architecture**:
1. **Presentation / Interface Layer:** Console CLI providing interactive menus, structured prompts, formatted invoice presentation, and tabular reports.
2. **Business Logic & Service Layer:** Core Java domain services including `ConsumerService`, `BillingService`, and `TariffCalculator`.
3. **Data & Analytics Layer:** Relational DBMS (SQL Schema, Tables, Views, Indexes) and Python Data Science pipeline (`pandas`, `matplotlib`, `seaborn`).

```mermaid
flowchart TD
    subgraph Tier1["Tier 1: Presentation Layer (Java CLI & Client Views)"]
        UI_Main["Main Console Interface (com.ecms.Main)"]
        UI_Menu["Interactive Menu System & Input Handling"]
        UI_Invoice["Standardized Invoice Formatter"]
        UI_Report["Tabular Consumption & Revenue Views"]
    end

    subgraph Tier2["Tier 2: Business Logic & Application Layer (Java OOP)"]
        Service_Consumer["ConsumerService (Registration, Lookups)"]
        Service_Billing["BillingService (Units Validation, Consumption Logic)"]
        Service_Tariff["TariffCalculator (Slab Calculation, Tax, Subsidies)"]
        Model_Entities["Entity Models (Consumer, MeterReading, Bill, ConnectionType)"]
    end

    subgraph Tier3["Tier 3: Data Persistence & Analytics Layer"]
        DB_Manager["DatabaseManager / DAO Interface"]
        SQL_DB[("Relational Database (SQLite / MySQL)")]
        SQL_Views["Database Views (High Consumption, Monthly Summary)"]
        Python_Pipeline["Python Analytics Engine (data_analysis.py)"]
        Visual_Charts["Visualization Artifacts (6 Analytical Charts)"]
    end

    UI_Main --> UI_Menu
    UI_Menu --> Service_Consumer
    UI_Menu --> Service_Billing
    Service_Billing --> Service_Tariff
    Service_Consumer --> Model_Entities
    Service_Billing --> Model_Entities
    Service_Consumer --> DB_Manager
    Service_Billing --> DB_Manager
    DB_Manager --> SQL_DB
    SQL_DB --> SQL_Views
    SQL_DB -.-> Python_Pipeline
    Python_Pipeline --> Visual_Charts
    Service_Billing --> UI_Invoice
    Service_Billing --> UI_Report
```

---

## 2. Use Case Diagram

The system serves three primary actors: **Meter Reader Staff**, **Billing Officer / Admin**, and the **Data Analyst**.

```mermaid
flowchart LR
    MR(("Meter Reader"))
    BO(("Billing Officer / Admin"))
    DA(("Data Analyst"))

    subgraph ECMS["Electricity Consumption Management System"]
        UC1["UC-1: Register New Consumer"]
        UC2["UC-2: View Registered Consumers"]
        UC3["UC-3: Record Meter Readings"]
        UC4["UC-4: Calculate Consumption (Current - Previous)"]
        UC5["UC-5: Calculate Bills using Tariff Slabs"]
        UC6["UC-6: Generate Consumer Bill Invoice"]
        UC7["UC-7: Generate Monthly Circle Revenue Report"]
        UC8["UC-8: Identify High-Consumption Accounts"]
        UC9["UC-9: Run Python EDA & Outlier Detection"]
        UC10["UC-10: Generate Data Visualizations"]
    end

    MR --> UC3
    UC3 -.->|includes| UC4
    UC4 -.->|includes| UC5

    BO --> UC1
    BO --> UC2
    BO --> UC3
    BO --> UC6
    BO --> UC7
    BO --> UC8

    DA --> UC8
    DA --> UC9
    DA --> UC10
```

---

## 3. Class Diagram (UML)

The class structure demonstrates object-oriented design principles: **Encapsulation**, **Inheritance**, **Polymorphism**, and **Modularity**.

```mermaid
classDiagram
    class ConnectionType {
        <<enumeration>>
        DOMESTIC
        COMMERCIAL
        INDUSTRIAL
        AGRICULTURAL
        +getDisplayName() String
        +getDescription() String
        +fromString(String) ConnectionType
    }

    class Consumer {
        -String consumerId
        -String fullName
        -String address
        -String city
        -String phone
        -String email
        -ConnectionType connectionType
        -double sanctionedLoadKw
        -String meterNumber
        -LocalDate registrationDate
        -String status
        +getConsumerId() String
        +getFullName() String
        +getConnectionType() ConnectionType
        +getMeterNumber() String
        +toString() String
    }

    class MeterReading {
        -String readingId
        -String consumerId
        -int billingMonth
        -int billingYear
        -LocalDate readingDate
        -double previousReading
        -double currentReading
        -double unitsConsumed
        -String recordedBy
        +recalculateUnits() void
        +getUnitsConsumed() double
        +getPreviousReading() double
        +getCurrentReading() double
    }

    class Bill {
        -String billId
        -String readingId
        -String consumerId
        -LocalDate billDate
        -LocalDate dueDate
        -double unitsConsumed
        -double energyCharges
        -double fixedCharges
        -double electricityDuty
        -double subsidyAmount
        -double totalAmount
        -String paymentStatus
        -LocalDate paymentDate
        +getTotalAmount() double
        +getPaymentStatus() String
    }

    class TariffCalculator {
        <<utility>>
        +calculate(ConnectionType, double) TariffBreakdown
    }

    class ConsumerService {
        -DatabaseManager db
        +registerConsumer(...) Consumer
        +getConsumerById(String) Consumer
        +getAllConsumers() List~Consumer~
        +getConsumersByType(ConnectionType) List~Consumer~
    }

    class BillingService {
        -DatabaseManager db
        +recordReadingAndGenerateBill(...) Bill
        +getBillsForConsumer(String) List~Bill~
        +getHighConsumptionConsumers(double) List~HighConsumerItem~
        +generateMonthlySummary(int, int) List~MonthlyReportRow~
    }

    class DatabaseManager {
        -static DatabaseManager instance
        -Map~String, Consumer~ consumers
        -Map~String, MeterReading~ meterReadings
        -Map~String, Bill~ bills
        +getInstance() DatabaseManager
        +addConsumer(Consumer) void
        +addMeterReading(MeterReading) void
        +addBill(Bill) void
    }

    Consumer --> ConnectionType
    Consumer "1" *-- "many" MeterReading : has
    MeterReading "1" *-- "1" Bill : triggers
    ConsumerService --> DatabaseManager
    BillingService --> DatabaseManager
    BillingService --> TariffCalculator
    DatabaseManager o-- Consumer
    DatabaseManager o-- MeterReading
    DatabaseManager o-- Bill
```

---

## 4. Sequence Diagram: Meter Reading & Bill Invoicing Flow

```mermaid
sequenceDiagram
    autonumber
    actor Officer as Billing Officer / Reader
    participant UI as Main Interface
    participant BS as BillingService
    participant TC as TariffCalculator
    participant DB as DatabaseManager
    participant Bill as Bill Entity

    Officer ->> UI: Enter consumerId, month, year, prevReading, currReading
    UI ->> BS: recordReadingAndGenerateBill(consumerId, month, year, prev, curr)
    BS ->> DB: getConsumer(consumerId)
    DB -->> BS: Consumer profile
    BS ->> BS: Validate (currReading >= prevReading)
    BS ->> BS: Compute units = (currReading - prevReading)
    BS ->> TC: calculate(consumer.getConnectionType(), units)
    TC -->> BS: TariffBreakdown (energy, fixed, duty, subsidy, total)
    BS ->> DB: addMeterReading(MeterReading)
    BS ->> DB: addBill(Bill)
    BS -->> UI: Return finalized Bill object
    UI ->> Officer: Display formatted Bill Invoice
```
