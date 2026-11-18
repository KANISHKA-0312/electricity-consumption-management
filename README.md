# Electricity Consumption Management and Energy Usage Analysis System
### Department of Information Technology — Velalar College of Engineering and Technology, Erode - 12
**Academic Year:** 2026-27 (ODD) | **Class:** 25IT3A  
**Activity:** Project-Based Assessment  
**Assigned Scenario:** **Scenario 14 (Register No.: 44)**  
**Author:** Kanishka (Register No.: 44) | **GitHub:** [@KANISHKA-0312](https://github.com/KANISHKA-0312) | **Email:** kaniska029@gmail.com  
**Integrated Courses:**
- **25ITT31:** Database Management Systems
- **25ITT32:** Java Programming
- **25ITT33:** Software Engineering
- **25ITC31:** Foundations of Data Science

---

## 📌 Project Overview & Problem Statement
An electricity distribution circle office maintains consumer details, meter readings, monthly electricity consumption, and billing records across diverse consumer sectors (Domestic, Commercial, Industrial, and Agricultural). Managing these operations manually causes significant errors in slab-based bill calculations, creates delays in revenue auditing, and makes it challenging to detect abnormal high-consumption power loads and seasonal demand shifts over time.

This project delivers an end-to-end automated **Electricity Consumption Management and Energy Usage Analysis System** developed using **Java**, **DBMS (SQL)**, and **Python (Data Science & Visualizations)**.

---

## 🚀 Key Functional Capabilities

1. **Consumer Profile Management:** Register and maintain consumer records (Consumer ID, Name, Address, City, Phone, Email, Connection Category, Sanctioned Load in kW, Meter Serial Number, and Status).
2. **Meter Reading Administration:** Log previous and current readings with integrity constraints (`Current Reading >= Previous Reading`), computing net consumption (`Units Consumed = Current - Previous`).
3. **Statutory Tariff Calculation Engine:** Accurately calculates charges based on real-world multi-slab rates:
   - **Domestic (Telescopic Slabs):** 0–100 units Free (100% subsidy); 101–200 units @ ₹2.25/unit (Fixed ₹20); 201–400 units @ ₹4.50/unit (Fixed ₹30); 401–500 units @ ₹6.00/unit (Fixed ₹50); Above 500 units @ ₹8.00/unit (Fixed ₹80).
   - **Commercial:** 0–100 units @ ₹6.50/unit; Above 100 units @ ₹9.50/unit (Fixed ₹150).
   - **Industrial:** 0–500 units @ ₹8.00/unit; Above 500 units @ ₹11.50/unit (Fixed ₹500).
   - **Agricultural:** Flat nominal ₹0.50/unit with 100% government power subsidy (Net Payable = ₹0.00).
   - **Statutory Taxes:** 5% Electricity Duty on energy and fixed charges.
4. **Standardized Bill Invoicing:** Generates itemized billing statements displaying units consumed, breakdown charges, subsidy deductions, due dates, and payment settlement tracking.
5. **Circle Consumption & Revenue Reporting:** Produces aggregated monthly circle performance reports detailing consumer counts, total kWh demand, billed revenue, and collection efficiency per sector.
6. **High-Consumption Profiling:** Automatically identifies and ranks accounts exceeding power consumption thresholds (e.g., > 400 kWh) to flag potential domestic-to-commercial tariff violations and heavy loads.
7. **Python Data Science & Visualization Pipeline:** Ingests database history, cleans data, identifies outliers using the Interquartile Range (IQR) technique, and produces 6 publication-ready visualization charts.

---

## 📂 Repository File Structure

```
electricity-consumption-management/
│
├── README.md                          # Master project documentation
│
├── docs/                              # Assessment Documentation Deliverables
│   ├── SRS.md                         # Software Requirements Specification (IEEE 830)
│   ├── System_Design.md               # Use Case, Class, Architecture & Sequence Diagrams
│   ├── Database_Design.md             # ER Diagram, Relational Model & Normalization (1NF to BCNF)
│   └── Testing_Report.md              # Test Strategy, BVA, Equivalence Partitioning & Test Cases
│
├── database/                          # DBMS Deliverables (Course 25ITT31)
│   ├── schema.sql                     # Complete DDL: Tables, Constraints, Indexes, Views
│   ├── seed_data.sql                  # Comprehensive sample dataset (20 consumers, 6 billing cycles)
│   ├── queries.sql                    # Analytical & operational SQL queries (Top-N, joins, views)
│   └── electricity_consumption_dataset.csv  # Preprocessed dataset for immediate analysis
│
├── java-app/                          # Java Application Deliverables (Course 25ITT32)
│   ├── bin/                           # Compiled Java bytecode (.class files)
│   ├── sample_output.txt              # Complete terminal execution transcript with sample I/O
│   └── src/
│       └── com/
│           └── ecms/
│               ├── Main.java          # Interactive menu-driven console application
│               ├── model/
│               │   ├── ConnectionType.java   # Category enum (Domestic, Commercial, etc.)
│               │   ├── Consumer.java         # Consumer entity with OOP encapsulation
│               │   ├── MeterReading.java     # Reading entity with consumption logic
│               │   └── Bill.java             # Billing invoice entity
│               ├── service/
│               │   ├── TariffCalculator.java # Tariff slabs, fixed charges, taxes & subsidies
│               │   ├── ConsumerService.java  # Consumer operations & validations
│               │   └── BillingService.java   # Consumption verification & report generators
│               └── dao/
│                   └── DatabaseManager.java  # Data persistence & repository manager
│
└── python-analysis/                   # Data Science Deliverables (Course 25ITC31)
    ├── requirements.txt               # Dependencies (pandas, numpy, matplotlib, seaborn)
    ├── data_analysis.py               # Complete Python data processing & EDA script
    ├── analysis_notebook.ipynb        # Interactive Jupyter Notebook with analysis & plots
    └── visualizations/                # Generated visualization artifacts
        ├── 01_monthly_consumption_trend.png
        ├── 02_consumption_by_connection_type.png
        ├── 03_top_high_consumers.png
        ├── 04_revenue_distribution_by_type.png
        ├── 05_consumption_outlier_boxplot.png
        └── 06_correlation_heatmap.png
```

---

## 🛠️ Technology Stack

| Domain | Technology / Tool | Version / Standard |
|---|---|---|
| **Core Programming** | Java SE (JDK) | Java 17+ (Compiled & tested on Java 25) |
| **Database Management** | SQL / SQLite / MySQL | ANSI SQL, Relational BCNF schema |
| **Data Science & EDA** | Python | Python 3.9+ (`pandas`, `numpy`, `matplotlib`, `seaborn`) |
| **Software Engineering** | UML / Markdown | IEEE 830 SRS, Mermaid Diagrams, BVA/EP Testing |
| **Version Control** | Git & GitHub | Modular directory structure |

---

## ⚙️ Execution and Setup Instructions

### 1. Java Application Setup & Run
The Java application is self-contained and requires no external third-party libraries:

```bash
# Navigate to the java-app directory
cd java-app

# Compile all source files into bin directory
javac -d bin src/com/ecms/*.java src/com/ecms/model/*.java src/com/ecms/service/*.java src/com/ecms/dao/*.java

# Run the interactive console application
java -cp bin com.ecms.Main
```

### 2. Database (SQL) Setup
You can run the SQL scripts in any standard relational DBMS (MySQL Workbench, phpMyAdmin, PostgreSQL, or SQLite):
```bash
# For SQLite:
sqlite3 electricity_billing.db < database/schema.sql
sqlite3 electricity_billing.db < database/seed_data.sql

# Execute analytical queries:
sqlite3 electricity_billing.db < database/queries.sql
```

### 3. Python Data Analytics & Visualization Setup
```bash
# Navigate to python-analysis directory
cd python-analysis

# Install dependencies
pip install -r requirements.txt

# Run the complete automated EDA and chart generation script
python data_analysis.py

# Or launch the interactive Jupyter Notebook
jupyter notebook analysis_notebook.ipynb
```

---

## 📊 Analytical Visualizations Overview

The Python analytics pipeline generates 6 analytical visualizations saved under `python-analysis/visualizations/`:
1. **`01_monthly_consumption_trend.png`:** Time-series line chart tracking monthly consumption trends (May to October 2026) showcasing peak summer cooling loads and industrial baseloads.
2. **`02_consumption_by_connection_type.png`:** Comparative bar chart highlighting mean energy demand across Domestic, Commercial, Industrial, and Agricultural sectors.
3. **`03_top_high_consumers.png`:** Horizontal bar chart profiling the top 10 highest electricity-consuming industrial and commercial establishments.
4. **`04_revenue_distribution_by_type.png`:** Donut pie chart displaying billed revenue contributions per sector.
5. **`05_consumption_outlier_boxplot.png`:** Statistical box plot and jitter stripplot identifying consumption distribution spreads and outlier accounts.
6. **`06_correlation_heatmap.png`:** Pearson correlation matrix revealing strong relationships between sanctioned load rating, kWh consumed, and billed revenue.

---

## 👥 Student & Author Details
- **Author / Developer:** Kanishka
- **GitHub Username:** [KANISHKA-0312](https://github.com/KANISHKA-0312)
- **Email:** kaniska029@gmail.com
- **Register Number:** 44
- **Class:** 25IT3A
- **Academic Year:** 2026-27 (ODD Semester)
- **Scenario Number:** Scenario 14
- **Institution:** Velalar College of Engineering and Technology (Autonomous), Thindal, Erode - 638012
- **Department:** Department of Information Technology
