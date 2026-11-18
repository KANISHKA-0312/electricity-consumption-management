# Software Requirements Specification (SRS)
## Electricity Consumption Management and Energy Usage Analysis System
**Institution:** Velalar College of Engineering and Technology, Erode - 638012  
**Department:** Information Technology  
**Academic Year:** 2026-27 (ODD) | **Class:** 25IT3A  
**Student Name:** Kanishka | **Register No.:** 44  
**Course Code:** 25ITT33 - Software Engineering  
**Assigned Project Scenario:** Scenario 14 (Register No.: 44)

---

## 1. Introduction

### 1.1 Purpose
The purpose of this document is to specify the complete software requirements for the **Electricity Consumption Management and Energy Usage Analysis System (ECMS)**. This system automates the management of electricity distribution consumers, periodic meter reading recordings, tariff slab-based bill calculations, arrears tracking, high-consumption auditing, and data-driven analytical pattern discovery.

### 1.2 Scope of the System
The software addresses manual accounting inefficiencies faced by electricity distribution circles (e.g. TANGEDCO/TNEB distribution offices). It provides:
- Centralized consumer registration across 4 connection categories: Domestic, Commercial, Industrial, and Agricultural.
- Bi-monthly / monthly meter reading logging with automated validation (`Current Reading >= Previous Reading`).
- Progressive slab-based tariff computation including energy charges, fixed charges, 5% electricity duty, and agricultural subsidies.
- Generation of detailed consumer bill invoices and monthly circle revenue summaries.
- Identification and profiling of high-consumption power consumers.
- Python-powered data analytics pipeline for exploratory data analysis, time-series usage forecasting trends, and visual pattern recognition.

### 1.3 Definitions, Acronyms, and Abbreviations
- **ECMS:** Electricity Consumption Management System
- **kWh:** Kilowatt-hour (standard billing unit of electricity)
- **kW:** Kilowatt (sanctioned load rating)
- **TNEB / TANGEDCO:** Tamil Nadu Electricity Board / Generation and Distribution Corporation
- **IQR:** Interquartile Range (statistical outlier detection method)
- **SRS:** Software Requirements Specification (IEEE 830 standard)
- **DDL / DML:** Data Definition / Manipulation Language

---

## 2. Overall Description

### 2.1 Product Perspective
The system operates as an integrated multi-tier enterprise architecture:
1. **User Interface & Application Layer (Java):** CLI and service layer facilitating operational workflows (consumer registration, reading input, billing).
2. **Persistence Layer (RDBMS / SQL):** Relational schema enforcing referential integrity, constraints, triggers, and analytical views.
3. **Data Analytics & Visualization Layer (Python):** Ingests structured billing history to preprocess data, detect anomalies, and generate charts.

### 2.2 Product Functions
- **Consumer Management:** Create, retrieve, update, and categorize electricity consumers.
- **Meter Reading Administration:** Log monthly meter readings, validate readings against tampering/negative entries, compute net consumption.
- **Bill Calculation Engine:** Compute energy charges using multi-tier telescopic slabs, apply connection-specific fixed charges, statutory duty, and subsidies.
- **Invoice & Ledger Generation:** Output formatted billing statements detailing consumption, due dates, breakdown charges, and payment tracking.
- **Reporting & Auditing:** Summarize monthly revenue and energy demand; isolate high-consumption outliers exceeding statutory thresholds.
- **Visual Analytics:** Produce trends, distributions, category comparisons, and correlation matrices.

### 2.3 User Classes and Characteristics
1. **Distribution Office Admin:** Manages tariff rates, updates consumer master records, monitors circle revenue.
2. **Meter Reader / Field Staff:** Enters meter readings for allotted distribution transformers/feeders.
3. **Billing Officer:** Reviews generated bills, issues duplicate invoices, verifies defaulters.
4. **Energy Data Analyst:** Executes Python pipelines to detect unauthorized commercial loads, seasonal peaks, and consumption shifts.

### 2.4 Operating Environment
- **Operating System:** Cross-platform (Windows 10/11, Linux, macOS).
- **Java Runtime:** Java SE 17 or higher (tested with OpenJDK / Java 25).
- **Database Engine:** MySQL 8.0+ / SQLite 3 / PostgreSQL.
- **Python Environment:** Python 3.9+ with `pandas`, `numpy`, `matplotlib`, `seaborn`.

### 2.5 Design and Implementation Constraints
- Readings must strictly satisfy `current_reading >= previous_reading`.
- Domestic slab calculations must adhere to government progressive tariff structures.
- Sensitive consumer records (phone, meter number) must have unique constraints.

---

## 3. Specific Requirements

### 3.1 Functional Requirements

#### FR-01: Maintain Consumer Details
- **Description:** The system shall record and maintain consumer profile information.
- **Inputs:** Full Name, Address, City, 10-digit Phone, Email, Connection Type (`Domestic`, `Commercial`, `Industrial`, `Agricultural`), Sanctioned Load (kW).
- **Processing:** Validates mandatory inputs, allocates a unique Consumer ID (e.g. `CONS-1001`) and assigns a unique Meter Number (`MTR-ERD-xxxx`).
- **Outputs:** Confirmation of registration with account profile summary.

#### FR-02: Record Meter Readings
- **Description:** The system shall record periodic meter readings for each billing cycle.
- **Inputs:** Consumer ID, Billing Month (1-12), Billing Year (>= 2020), Previous Reading, Current Reading, Reader Staff Name.
- **Processing:** Validates that Current Reading >= Previous Reading; computes `Units Consumed = Current Reading - Previous Reading`.
- **Outputs:** Reading confirmation with unique `reading_id` and verified units.

#### FR-03: Calculate Electricity Bills using Predefined Tariffs
- **Description:** Compute charges according to statutory tariff slabs:
  - **Domestic:**
    - 0 – 100 units: ₹0.00 / Free (100% subsidy)
    - 101 – 200 units: ₹2.25/unit + Fixed charge ₹20
    - 201 – 400 units: ₹4.50/unit + Fixed charge ₹30
    - 401 – 500 units: ₹6.00/unit + Fixed charge ₹50
    - Above 500 units: ₹8.00/unit + Fixed charge ₹80
  - **Commercial:** 0 – 100 units @ ₹6.50/unit (Fixed ₹100); > 100 units @ ₹9.50/unit (Fixed ₹150).
  - **Industrial:** 0 – 500 units @ ₹8.00/unit (Fixed ₹300); > 500 units @ ₹11.50/unit (Fixed ₹500).
  - **Agricultural:** Flat ₹0.50/unit with 100% government power subsidy (Net Payable = ₹0.00).
  - **Statutory Taxes:** 5% Electricity Duty on energy + fixed charges (exempt for agricultural connections).
- **Outputs:** Itemized breakdown of charges and Net Payable Amount.

#### FR-04: Generate Consumer-wise Bills and Invoices
- **Description:** Generate standardized invoice showing meter details, reading dates, slab breakdown, due date, and payment status.

#### FR-05: Monthly Consumption and Revenue Reporting
- **Description:** Aggregate circle metrics per month: Total meters read, total units consumed, average consumption per connection category, total billed amount, collections, and outstanding arrears.

#### FR-06: High-Consumption Consumer Identification
- **Description:** Identify accounts consuming above a specified threshold (e.g. >= 400 units) to monitor heavy power demands and potential residential-to-commercial tariff evasions.

#### FR-07: Python Data Retrieval & Visualizations
- **Description:** Retrieve dataset from the database/CSV, execute IQR outlier detection, and generate 6 distinct analytical charts (Monthly Trend Line Chart, Category Bar Chart, Top-10 High Users Horizontal Bar Chart, Revenue Share Pie Chart, Outlier Box Plot, and Correlation Heatmap).

---

## 4. Non-Functional Requirements

### 4.1 Performance Requirements
- Bill calculation for any single consumer shall execute in under 10 milliseconds.
- Bulk analytical aggregation across 10,000+ reading rows shall execute in under 1 second.

### 4.2 Security Requirements
- Database foreign key constraints prevent orphan meter reading or billing records.
- Input validation sanitizes negative values and malformed phone/email entries.

### 4.3 Reliability and Availability
- Consistent schema normalization to Third Normal Form (3NF/BCNF) prevents update, insertion, and deletion anomalies.

### 4.4 Usability Requirements
- Menu-driven interface with clear prompts, ASCII tables, and descriptive error messages.
