-- ============================================================================
-- Scenario 14: Electricity Consumption Management and Energy Usage Analysis System
-- Velalar College of Engineering and Technology - Department of IT
-- File: queries.sql (Operational and Analytical SQL Queries)
-- ============================================================================

-- ============================================================================
-- 1. BASIC OPERATIONAL QUERIES
-- ============================================================================

-- Q1.1: Retrieve all active consumers ordered by connection type and name
SELECT consumer_id, full_name, connection_type, sanctioned_load_kw, phone, city
FROM consumers
WHERE status = 'Active'
ORDER BY connection_type, full_name ASC;

-- Q1.2: Retrieve meter reading history for a specific consumer (e.g. CONS-1002)
SELECT 
    reading_id,
    billing_month,
    billing_year,
    previous_reading,
    current_reading,
    units_consumed,
    reading_date
FROM meter_readings
WHERE consumer_id = 'CONS-1002'
ORDER BY billing_year DESC, billing_month DESC;

-- ============================================================================
-- 2. BILLING AND CALCULATION QUERIES
-- ============================================================================

-- Q2.1: Generate detailed bill invoice for a given bill ID (e.g. BILL-202610-1002)
SELECT 
    b.bill_id,
    b.bill_date,
    b.due_date,
    c.consumer_id,
    c.full_name,
    c.address,
    c.meter_number,
    c.connection_type,
    mr.billing_month,
    mr.billing_year,
    mr.previous_reading,
    mr.current_reading,
    mr.units_consumed,
    b.energy_charges,
    b.fixed_charges,
    b.electricity_duty,
    b.subsidy_amount,
    b.total_amount,
    b.payment_status
FROM bills b
JOIN consumers c ON b.consumer_id = c.consumer_id
JOIN meter_readings mr ON b.reading_id = mr.reading_id
WHERE b.bill_id = 'BILL-202610-1002';

-- Q2.2: Identify unpaid and overdue bills along with consumer contact details
SELECT 
    b.bill_id,
    c.consumer_id,
    c.full_name,
    c.phone,
    c.connection_type,
    b.due_date,
    b.total_amount
FROM bills b
JOIN consumers c ON b.consumer_id = c.consumer_id
WHERE b.payment_status = 'Unpaid'
ORDER BY b.due_date ASC, b.total_amount DESC;

-- ============================================================================
-- 3. HIGH CONSUMPTION CONSUMER ANALYSIS (Requirement #5)
-- ============================================================================

-- Q3.1: Top 5 Highest Electricity Consuming Accounts in the Latest Billing Cycle (Oct 2026)
SELECT 
    c.consumer_id,
    c.full_name,
    c.connection_type,
    mr.units_consumed,
    b.total_amount,
    RANK() OVER (ORDER BY mr.units_consumed DESC) as consumption_rank
FROM meter_readings mr
JOIN consumers c ON mr.consumer_id = c.consumer_id
JOIN bills b ON mr.reading_id = b.reading_id
WHERE mr.billing_month = 10 AND mr.billing_year = 2026
LIMIT 5;

-- Q3.2: Domestic Consumers consuming more than 400 units (Flagged for potential commercial misuse or heavy AC load)
SELECT 
    c.consumer_id,
    c.full_name,
    c.address,
    c.sanctioned_load_kw,
    mr.billing_month,
    mr.units_consumed,
    b.total_amount
FROM consumers c
JOIN meter_readings mr ON c.consumer_id = mr.consumer_id
JOIN bills b ON mr.reading_id = b.reading_id
WHERE c.connection_type = 'Domestic' AND mr.units_consumed > 400.00
ORDER BY mr.units_consumed DESC;

-- ============================================================================
-- 4. MONTHLY CONSUMPTION & REVENUE AGGREGATION (Requirement #5)
-- ============================================================================

-- Q4.1: Overall monthly electricity demand and billed revenue trend
SELECT 
    mr.billing_year,
    mr.billing_month,
    COUNT(DISTINCT mr.consumer_id) AS total_meters_read,
    ROUND(SUM(mr.units_consumed), 2) AS total_units_consumed,
    ROUND(AVG(mr.units_consumed), 2) AS avg_units_per_meter,
    ROUND(SUM(b.total_amount), 2) AS total_billed_amount,
    ROUND(SUM(CASE WHEN b.payment_status = 'Paid' THEN b.total_amount ELSE 0 END), 2) AS total_collected
FROM meter_readings mr
JOIN bills b ON mr.reading_id = b.reading_id
GROUP BY mr.billing_year, mr.billing_month
ORDER BY mr.billing_year, mr.billing_month;

-- Q4.2: Sector-wise Consumption and Revenue Breakdown
SELECT 
    c.connection_type,
    COUNT(DISTINCT c.consumer_id) AS active_connections,
    ROUND(SUM(mr.units_consumed), 2) AS total_units,
    ROUND(AVG(mr.units_consumed), 2) AS avg_units_per_connection,
    ROUND(SUM(b.total_amount), 2) AS total_revenue,
    ROUND((SUM(b.total_amount) * 100.0 / (SELECT SUM(total_amount) FROM bills)), 2) AS revenue_share_pct
FROM consumers c
JOIN meter_readings mr ON c.consumer_id = mr.consumer_id
JOIN bills b ON mr.reading_id = b.reading_id
GROUP BY c.connection_type
ORDER BY total_revenue DESC;

-- ============================================================================
-- 5. ADVANCED SUBQUERIES & ANALYTICAL SQL (Unit 2 & 3 Syllabus)
-- ============================================================================

-- Q5.1: Correlated Subquery: Find consumers whose consumption is strictly above the average consumption of their category in May 2026
SELECT 
    c.consumer_id,
    c.full_name,
    c.connection_type,
    mr.units_consumed,
    (SELECT ROUND(AVG(inner_mr.units_consumed), 2) 
     FROM meter_readings inner_mr 
     JOIN consumers inner_c ON inner_mr.consumer_id = inner_c.consumer_id
     WHERE inner_c.connection_type = c.connection_type 
       AND inner_mr.billing_month = 5 
       AND inner_mr.billing_year = 2026) AS category_avg_units
FROM consumers c
JOIN meter_readings mr ON c.consumer_id = mr.consumer_id
WHERE mr.billing_month = 5 AND mr.billing_year = 2026
  AND mr.units_consumed > (
      SELECT AVG(inner_mr.units_consumed) 
      FROM meter_readings inner_mr 
      JOIN consumers inner_c ON inner_mr.consumer_id = inner_c.consumer_id
      WHERE inner_c.connection_type = c.connection_type 
        AND inner_mr.billing_month = 5 
        AND inner_mr.billing_year = 2026
  )
ORDER BY c.connection_type, mr.units_consumed DESC;

-- Q5.2: Month-over-Month Energy Usage Change Analysis
WITH monthly_stats AS (
    SELECT 
        billing_year,
        billing_month,
        SUM(units_consumed) AS monthly_units
    FROM meter_readings
    GROUP BY billing_year, billing_month
)
SELECT 
    billing_year,
    billing_month,
    monthly_units,
    LAG(monthly_units, 1) OVER (ORDER BY billing_year, billing_month) AS previous_month_units,
    ROUND(((monthly_units - LAG(monthly_units, 1) OVER (ORDER BY billing_year, billing_month)) * 100.0 / 
           LAG(monthly_units, 1) OVER (ORDER BY billing_year, billing_month)), 2) AS pct_change_from_prev_month
FROM monthly_stats;
