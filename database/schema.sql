-- ============================================================================
-- Scenario 14: Electricity Consumption Management and Energy Usage Analysis System
-- Velalar College of Engineering and Technology - Department of IT
-- Course: 25ITT31 Database Management Systems
-- File: schema.sql (Database Definition Language - DDL)
-- ============================================================================

-- Drop existing tables/views if they exist (clean setup)
DROP VIEW IF EXISTS view_consumer_ledger;
DROP VIEW IF EXISTS view_monthly_billing_summary;
DROP VIEW IF EXISTS view_high_consumption_consumers;

DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS meter_readings;
DROP TABLE IF EXISTS tariff_plans;
DROP TABLE IF EXISTS consumers;

-- ============================================================================
-- 1. CONSUMERS TABLE
-- Stores master information about registered electricity consumers
-- ============================================================================
CREATE TABLE consumers (
    consumer_id VARCHAR(20) PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(50) DEFAULT 'Erode',
    phone VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(100),
    connection_type VARCHAR(20) NOT NULL CHECK (connection_type IN ('Domestic', 'Commercial', 'Industrial', 'Agricultural')),
    sanctioned_load_kw DECIMAL(6, 2) NOT NULL CHECK (sanctioned_load_kw > 0),
    meter_number VARCHAR(30) NOT NULL UNIQUE,
    registration_date DATE NOT NULL,
    status VARCHAR(15) DEFAULT 'Active' CHECK (status IN ('Active', 'Suspended', 'Disconnected'))
);

-- ============================================================================
-- 2. TARIFF_PLANS TABLE
-- Maintains tariff slab rates and statutory charges per connection category
-- ============================================================================
CREATE TABLE tariff_plans (
    plan_id INTEGER PRIMARY KEY,
    connection_type VARCHAR(20) NOT NULL,
    slab_min_units INTEGER NOT NULL CHECK (slab_min_units >= 0),
    slab_max_units INTEGER, -- NULL represents no upper limit (e.g. above 500 units)
    rate_per_unit DECIMAL(6, 2) NOT NULL CHECK (rate_per_unit >= 0),
    fixed_charge DECIMAL(8, 2) NOT NULL DEFAULT 0.00,
    tax_percentage DECIMAL(4, 2) NOT NULL DEFAULT 5.00
);

-- ============================================================================
-- 3. METER_READINGS TABLE
-- Records billing period readings and calculates units consumed
-- ============================================================================
CREATE TABLE meter_readings (
    reading_id VARCHAR(25) PRIMARY KEY,
    consumer_id VARCHAR(20) NOT NULL,
    billing_month INTEGER NOT NULL CHECK (billing_month BETWEEN 1 AND 12),
    billing_year INTEGER NOT NULL CHECK (billing_year >= 2020),
    reading_date DATE NOT NULL,
    previous_reading DECIMAL(10, 2) NOT NULL CHECK (previous_reading >= 0),
    current_reading DECIMAL(10, 2) NOT NULL CHECK (current_reading >= 0),
    units_consumed DECIMAL(10, 2) NOT NULL,
    recorded_by VARCHAR(50) DEFAULT 'Meter Reader Staff',
    FOREIGN KEY (consumer_id) REFERENCES consumers(consumer_id) ON DELETE CASCADE,
    CONSTRAINT chk_readings CHECK (current_reading >= previous_reading),
    CONSTRAINT uq_consumer_period UNIQUE (consumer_id, billing_month, billing_year)
);

-- ============================================================================
-- 4. BILLS TABLE
-- Maintains computed electricity invoices, breakdown charges, and payment state
-- ============================================================================
CREATE TABLE bills (
    bill_id VARCHAR(25) PRIMARY KEY,
    reading_id VARCHAR(25) NOT NULL UNIQUE,
    consumer_id VARCHAR(20) NOT NULL,
    bill_date DATE NOT NULL,
    due_date DATE NOT NULL,
    units_consumed DECIMAL(10, 2) NOT NULL,
    energy_charges DECIMAL(10, 2) NOT NULL CHECK (energy_charges >= 0),
    fixed_charges DECIMAL(8, 2) NOT NULL DEFAULT 0.00,
    electricity_duty DECIMAL(8, 2) NOT NULL DEFAULT 0.00,
    subsidy_amount DECIMAL(8, 2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(10, 2) NOT NULL CHECK (total_amount >= 0),
    payment_status VARCHAR(15) DEFAULT 'Unpaid' CHECK (payment_status IN ('Paid', 'Unpaid', 'Overdue')),
    payment_date DATE,
    FOREIGN KEY (reading_id) REFERENCES meter_readings(reading_id) ON DELETE RESTRICT,
    FOREIGN KEY (consumer_id) REFERENCES consumers(consumer_id) ON DELETE RESTRICT,
    CONSTRAINT chk_dates CHECK (due_date >= bill_date)
);

-- ============================================================================
-- 5. INDEXES FOR QUERY OPTIMIZATION (Unit 3 Syllabus Requirement)
-- ============================================================================
CREATE INDEX idx_consumers_conn_type ON consumers(connection_type);
CREATE INDEX idx_readings_consumer ON meter_readings(consumer_id);
CREATE INDEX idx_readings_period ON meter_readings(billing_year, billing_month);
CREATE INDEX idx_bills_status ON bills(payment_status);
CREATE INDEX idx_bills_consumer ON bills(consumer_id);

-- ============================================================================
-- 6. DATABASE VIEWS (Unit 2 Syllabus Requirement)
-- ============================================================================

-- View A: High-Consumption Consumers (Consuming > 400 units in any billing period)
CREATE VIEW view_high_consumption_consumers AS
SELECT 
    c.consumer_id,
    c.full_name,
    c.connection_type,
    c.phone,
    mr.billing_month,
    mr.billing_year,
    mr.units_consumed,
    b.total_amount,
    b.payment_status
FROM consumers c
JOIN meter_readings mr ON c.consumer_id = mr.consumer_id
JOIN bills b ON mr.reading_id = b.reading_id
WHERE mr.units_consumed >= 400.00;

-- View B: Monthly Consumption and Revenue Aggregation Summary
CREATE VIEW view_monthly_billing_summary AS
SELECT 
    mr.billing_year,
    mr.billing_month,
    c.connection_type,
    COUNT(DISTINCT c.consumer_id) AS total_active_consumers,
    ROUND(SUM(mr.units_consumed), 2) AS total_units_consumed,
    ROUND(AVG(mr.units_consumed), 2) AS avg_units_per_consumer,
    ROUND(SUM(b.total_amount), 2) AS total_revenue_billed,
    SUM(CASE WHEN b.payment_status = 'Paid' THEN b.total_amount ELSE 0 END) AS total_revenue_collected,
    SUM(CASE WHEN b.payment_status = 'Unpaid' THEN b.total_amount ELSE 0 END) AS total_outstanding_dues
FROM meter_readings mr
JOIN consumers c ON mr.consumer_id = c.consumer_id
JOIN bills b ON mr.reading_id = b.reading_id
GROUP BY mr.billing_year, mr.billing_month, c.connection_type;

-- View C: Consumer Comprehensive Ledger
CREATE VIEW view_consumer_ledger AS
SELECT 
    c.consumer_id,
    c.full_name,
    c.connection_type,
    c.meter_number,
    mr.billing_month,
    mr.billing_year,
    mr.previous_reading,
    mr.current_reading,
    mr.units_consumed,
    b.bill_id,
    b.total_amount,
    b.due_date,
    b.payment_status
FROM consumers c
JOIN meter_readings mr ON c.consumer_id = mr.consumer_id
JOIN bills b ON mr.reading_id = b.reading_id;
