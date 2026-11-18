# Software Testing and Verification Report
## Electricity Consumption Management and Energy Usage Analysis System
**Institution:** Velalar College of Engineering and Technology, Erode - 638012  
**Department:** Information Technology  
**Course Code:** 25ITT33 - Software Engineering | **Academic Year:** 2026-27 (ODD) | **Class:** 25IT3A  
**Student Name:** Kanishka | **Register No.:** 44  
**Assigned Project Scenario:** Scenario 14 (Register No.: 44)

---

## 1. Testing Strategy and Techniques

Testing was conducted across the software development lifecycle adhering to IEEE verification and validation standards:

1. **Unit Testing:** Validated individual business logic methods in isolation (`TariffCalculator.calculate()`, `MeterReading` validation, and POJO getters/setters).
2. **Integration Testing:** Verified module interaction between `ConsumerService`, `BillingService`, and data persistence in `DatabaseManager`.
3. **System Testing:** End-to-end verification of user flows from consumer registration to reading ingestion, bill issuance, and analytical report generation.
4. **Black-Box Testing Techniques:**
   - **Equivalence Partitioning (EP):** Partitioned consumption inputs into distinct tiers (e.g., 0-100 units, 101-200 units, 201-400 units, 401-500 units, > 500 units).
   - **Boundary Value Analysis (BVA):** Evaluated exact slab boundaries (0, 100, 101, 200, 201, 400, 401, 500, 501 units).
5. **White-Box Testing Techniques:**
   - Evaluated cyclomatic complexity and tested every logical branch in the switch-case tariff structure and condition statements (`currentReading < previousReading`).

---

## 2. Test Cases and Execution Results

| Test Case ID | Test Scenario / Objective | Test Technique | Test Input | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|---|
| **TC-01** | Register domestic consumer with valid details | Equivalence Partitioning | Name: "Karthik S", Load: 4.5 kW, Phone: "9842555666", Category: Domestic | Consumer registered; allocated ID `CONS-1009`, meter `MTR-ERD-1021`, status "Active" | Consumer registered successfully with expected IDs | **PASS** |
| **TC-02** | Reject consumer registration with negative load | Boundary Value Analysis | Name: "Ravi", Load: -2.0 kW | Throw `IllegalArgumentException("Sanctioned load must be greater than 0 kW")` | Exception caught; user informed with error message | **PASS** |
| **TC-03** | Reject consumer registration with invalid phone | Input Validation | Phone: "98421" (< 10 digits) | Throw `IllegalArgumentException("Valid 10-digit phone number is required")` | Rejected with validation error | **PASS** |
| **TC-04** | Record meter reading with normal valid consumption | Functional / BVA | Prev: 4180.0, Curr: 4495.0 (Diff = 315.0 units) | Units computed = 315.0 kWh; reading persisted | Units = 315.00 kWh calculated correctly | **PASS** |
| **TC-05** | Reject invalid meter reading where current < previous | Boundary / Defensive | Prev: 2500.0, Curr: 2400.0 | System rejects entry with `Invalid meter reading: Current cannot be less than previous` | Rejected with appropriate validation message | **PASS** |
| **TC-06** | Domestic slab tier 1: Exactly 100 units (Free) | BVA / EP | Units: 100.0, Type: Domestic | Energy: ₹0.00, Fixed: ₹0.00, Tax: ₹0.00, Net: ₹0.00 | Net Payable = ₹0.00 | **PASS** |
| **TC-07** | Domestic slab tier 2: 185 units | EP | Units: 185.0, Type: Domestic | Energy: (85 * 2.25) = ₹191.25, Fixed: ₹20, Tax (5%): ₹10.56, Total: ₹221.81 | Total = ₹221.81 | **PASS** |
| **TC-08** | Domestic slab tier 3: 350 units | EP | Units: 350.0, Type: Domestic | Energy: (225 + 150*4.50) = ₹900.00, Fixed: ₹30, Tax: ₹46.50, Total: ₹976.50 | Total = ₹976.50 | **PASS** |
| **TC-09** | Domestic slab tier 4: 480 units | EP | Units: 480.0, Type: Domestic | Energy: (225 + 900 + 80*6) = ₹1605.00, Fixed: ₹50, Tax: ₹82.75, Total: ₹1737.75 | Total = ₹1737.75 | **PASS** |
| **TC-10** | Domestic slab tier 5: 750 units (>500) | EP | Units: 750.0, Type: Domestic | Energy: (225 + 900 + 600 + 250*8) = ₹3725.00, Fixed: ₹80, Tax: ₹190.25, Total: ₹3995.25 | Total = ₹3995.25 | **PASS** |
| **TC-11** | Commercial tariff calculation (1500 units) | EP | Units: 1500.0, Type: Commercial | Energy: (100*6.50 + 1400*9.50) = ₹13,950, Fixed: ₹150, Tax: ₹705, Total: ₹14,805.00 | Total = ₹14,805.00 | **PASS** |
| **TC-12** | Agricultural power subsidy verification (750 units) | EP | Units: 750.0, Type: Agricultural | Energy: ₹375.00, Subsidy: ₹375.00, Net Payable: ₹0.00 | Net Payable = ₹0.00 (100% subsidized) | **PASS** |
| **TC-13** | Identify High-Consumption consumers (> 400 units) | Analytical Query | Threshold: 400.0 units | Filters all accounts with units >= 400.0 in descending order | 13 records returned, sorted correctly | **PASS** |
| **TC-14** | Monthly circle billing & revenue report | Aggregation Query | Month: 10, Year: 2026 | Group metrics by Category: Consumer count, total units, revenue billed, and revenue collected | Summary table rendered with accurate arithmetic totals | **PASS** |
| **TC-15** | Python dataset retrieval and nulls verification | Data Preprocessing | CSV / SQLite dataset | 120 total records ingested; nulls present only in `payment_date` for unpaid accounts | Clean ingest, zero duplicate billing cycles | **PASS** |
| **TC-16** | Outlier detection via IQR on Domestic sector | Data Science / EDA | Domestic usage distribution | Identifies users above Q3 + 1.5*IQR (Flagged CONS-1004 and CONS-1006) | Outliers detected and reported accurately | **PASS** |

---

## 3. Test Summary and Defect Analysis
- **Total Test Cases Executed:** 16
- **Passed:** 16 (100%)
- **Failed:** 0
- **Defect Density:** 0 defects identified post-integration.
- **Conclusion:** The system satisfies all functional requirements specified in the problem statement and adheres to the Software Engineering syllabus standards.
