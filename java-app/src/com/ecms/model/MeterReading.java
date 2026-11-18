package com.ecms.model;

import java.time.LocalDate;

/**
 * Entity representing monthly meter reading recorded for a consumer.
 */
public class MeterReading {
    private String readingId;
    private String consumerId;
    private int billingMonth;
    private int billingYear;
    private LocalDate readingDate;
    private double previousReading;
    private double currentReading;
    private double unitsConsumed;
    private String recordedBy;

    public MeterReading() {
        this.readingDate = LocalDate.now();
        this.recordedBy = "Meter Reader Staff";
    }

    public MeterReading(String readingId, String consumerId, int billingMonth, int billingYear,
                        LocalDate readingDate, double previousReading, double currentReading, String recordedBy) {
        if (currentReading < previousReading) {
            throw new IllegalArgumentException(String.format(
                "Invalid meter reading: Current reading (%.2f) cannot be less than previous reading (%.2f)",
                currentReading, previousReading
            ));
        }
        if (billingMonth < 1 || billingMonth > 12) {
            throw new IllegalArgumentException("Billing month must be between 1 and 12.");
        }
        this.readingId = readingId;
        this.consumerId = consumerId;
        this.billingMonth = billingMonth;
        this.billingYear = billingYear;
        this.readingDate = readingDate;
        this.previousReading = previousReading;
        this.currentReading = currentReading;
        this.unitsConsumed = currentReading - previousReading;
        this.recordedBy = recordedBy;
    }

    // Getters and Setters
    public String getReadingId() { return readingId; }
    public void setReadingId(String readingId) { this.readingId = readingId; }

    public String getConsumerId() { return consumerId; }
    public void setConsumerId(String consumerId) { this.consumerId = consumerId; }

    public int getBillingMonth() { return billingMonth; }
    public void setBillingMonth(int billingMonth) { this.billingMonth = billingMonth; }

    public int getBillingYear() { return billingYear; }
    public void setBillingYear(int billingYear) { this.billingYear = billingYear; }

    public LocalDate getReadingDate() { return readingDate; }
    public void setReadingDate(LocalDate readingDate) { this.readingDate = readingDate; }

    public double getPreviousReading() { return previousReading; }
    public void setPreviousReading(double previousReading) { 
        this.previousReading = previousReading; 
        recalculateUnits();
    }

    public double getCurrentReading() { return currentReading; }
    public void setCurrentReading(double currentReading) { 
        this.currentReading = currentReading; 
        recalculateUnits();
    }

    public double getUnitsConsumed() { return unitsConsumed; }

    public String getRecordedBy() { return recordedBy; }
    public void setRecordedBy(String recordedBy) { this.recordedBy = recordedBy; }

    private void recalculateUnits() {
        if (this.currentReading >= this.previousReading) {
            this.unitsConsumed = this.currentReading - this.previousReading;
        }
    }

    @Override
    public String toString() {
        return String.format("MeterReading[ID=%s, Consumer=%s, Period=%02d/%d, Prev=%.1f, Curr=%.1f, Units=%.1f]",
                readingId, consumerId, billingMonth, billingYear, previousReading, currentReading, unitsConsumed);
    }
}
