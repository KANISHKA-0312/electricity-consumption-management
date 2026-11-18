package com.ecms.model;

import java.time.LocalDate;

/**
 * Entity representing calculated electricity bill invoice.
 */
public class Bill {
    private String billId;
    private String readingId;
    private String consumerId;
    private LocalDate billDate;
    private LocalDate dueDate;
    private double unitsConsumed;
    private double energyCharges;
    private double fixedCharges;
    private double electricityDuty;
    private double subsidyAmount;
    private double totalAmount;
    private String paymentStatus; // "Paid", "Unpaid", "Overdue"
    private LocalDate paymentDate;

    public Bill() {
        this.billDate = LocalDate.now();
        this.dueDate = LocalDate.now().plusDays(20);
        this.paymentStatus = "Unpaid";
    }

    public Bill(String billId, String readingId, String consumerId, LocalDate billDate, LocalDate dueDate,
                double unitsConsumed, double energyCharges, double fixedCharges,
                double electricityDuty, double subsidyAmount, double totalAmount,
                String paymentStatus, LocalDate paymentDate) {
        this.billId = billId;
        this.readingId = readingId;
        this.consumerId = consumerId;
        this.billDate = billDate;
        this.dueDate = dueDate;
        this.unitsConsumed = unitsConsumed;
        this.energyCharges = energyCharges;
        this.fixedCharges = fixedCharges;
        this.electricityDuty = electricityDuty;
        this.subsidyAmount = subsidyAmount;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
    }

    // Getters and Setters
    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }

    public String getReadingId() { return readingId; }
    public void setReadingId(String readingId) { this.readingId = readingId; }

    public String getConsumerId() { return consumerId; }
    public void setConsumerId(String consumerId) { this.consumerId = consumerId; }

    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate billDate) { this.billDate = billDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public double getUnitsConsumed() { return unitsConsumed; }
    public void setUnitsConsumed(double unitsConsumed) { this.unitsConsumed = unitsConsumed; }

    public double getEnergyCharges() { return energyCharges; }
    public void setEnergyCharges(double energyCharges) { this.energyCharges = energyCharges; }

    public double getFixedCharges() { return fixedCharges; }
    public void setFixedCharges(double fixedCharges) { this.fixedCharges = fixedCharges; }

    public double getElectricityDuty() { return electricityDuty; }
    public void setElectricityDuty(double electricityDuty) { this.electricityDuty = electricityDuty; }

    public double getSubsidyAmount() { return subsidyAmount; }
    public void setSubsidyAmount(double subsidyAmount) { this.subsidyAmount = subsidyAmount; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    @Override
    public String toString() {
        return String.format("Bill[ID=%s, Consumer=%s, Units=%.1f, Total=Rs.%.2f, Status=%s]",
                billId, consumerId, unitsConsumed, totalAmount, paymentStatus);
    }
}
