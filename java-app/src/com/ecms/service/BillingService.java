package com.ecms.service;

import com.ecms.dao.DatabaseManager;
import com.ecms.model.Bill;
import com.ecms.model.ConnectionType;
import com.ecms.model.Consumer;
import com.ecms.model.MeterReading;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service managing meter reading entries, bill calculations,
 * and high-consumption analytical reports.
 */
public class BillingService {
    private final DatabaseManager db = DatabaseManager.getInstance();

    public static class MonthlyReportRow {
        public int month;
        public int year;
        public ConnectionType connectionType;
        public int consumerCount;
        public double totalUnits;
        public double avgUnits;
        public double totalRevenue;
        public double paidRevenue;
        public double unpaidRevenue;
    }

    public static class HighConsumerItem {
        public Consumer consumer;
        public MeterReading reading;
        public Bill bill;

        public HighConsumerItem(Consumer consumer, MeterReading reading, Bill bill) {
            this.consumer = consumer;
            this.reading = reading;
            this.bill = bill;
        }
    }

    /**
     * Records previous and current meter readings, validates consumption,
     * calculates tariff charges, and generates the finalized bill.
     */
    public Bill recordReadingAndGenerateBill(String consumerId, int month, int year,
                                            double previousReading, double currentReading,
                                            String recordedBy) {
        Consumer consumer = db.getConsumer(consumerId);
        if (consumer == null) {
            throw new IllegalArgumentException("Consumer does not exist: " + consumerId);
        }

        if (currentReading < previousReading) {
            throw new IllegalArgumentException(String.format(
                "Invalid readings: Current (%.2f) cannot be less than Previous (%.2f).", 
                currentReading, previousReading
            ));
        }

        String readingId = String.format("MR-%d%02d-%s", year, month, consumerId.replaceAll("[^0-9]", ""));
        String billId = String.format("BILL-%d%02d-%s", year, month, consumerId.replaceAll("[^0-9]", ""));

        MeterReading reading = new MeterReading(readingId, consumerId, month, year, 
                                                LocalDate.now(), previousReading, currentReading, recordedBy);
        db.addMeterReading(reading);

        double units = reading.getUnitsConsumed();
        TariffCalculator.TariffBreakdown breakdown = TariffCalculator.calculate(consumer.getConnectionType(), units);

        Bill bill = new Bill(billId, readingId, consumerId, LocalDate.now(), LocalDate.now().plusDays(20),
                units, breakdown.energyCharges, breakdown.fixedCharges, breakdown.electricityDuty,
                breakdown.subsidyAmount, breakdown.totalBillAmount, "Unpaid", null);
        db.addBill(bill);

        return bill;
    }

    public List<Bill> getBillsForConsumer(String consumerId) {
        return db.getBillsByConsumer(consumerId);
    }

    public List<MeterReading> getReadingsForConsumer(String consumerId) {
        return db.getReadingsByConsumer(consumerId);
    }

    /**
     * Retrieve list of consumers exceeding a specified consumption threshold.
     */
    public List<HighConsumerItem> getHighConsumptionConsumers(double thresholdUnits) {
        List<HighConsumerItem> list = new ArrayList<>();
        for (MeterReading mr : db.getAllMeterReadings()) {
            if (mr.getUnitsConsumed() >= thresholdUnits) {
                Consumer c = db.getConsumer(mr.getConsumerId());
                Bill b = db.getAllBills().stream()
                        .filter(bill -> bill.getReadingId().equals(mr.getReadingId()))
                        .findFirst().orElse(null);
                list.add(new HighConsumerItem(c, mr, b));
            }
        }
        list.sort((a, b) -> Double.compare(b.reading.getUnitsConsumed(), a.reading.getUnitsConsumed()));
        return list;
    }

    /**
     * Generate aggregated consumption & revenue summary per connection category.
     */
    public List<MonthlyReportRow> generateMonthlySummary(int month, int year) {
        Map<ConnectionType, List<MeterReading>> grouped = new EnumMap<>(ConnectionType.class);
        for (ConnectionType ct : ConnectionType.values()) {
            grouped.put(ct, new ArrayList<>());
        }

        for (MeterReading mr : db.getAllMeterReadings()) {
            if (mr.getBillingMonth() == month && mr.getBillingYear() == year) {
                Consumer c = db.getConsumer(mr.getConsumerId());
                if (c != null) {
                    grouped.get(c.getConnectionType()).add(mr);
                }
            }
        }

        List<MonthlyReportRow> report = new ArrayList<>();
        for (Map.Entry<ConnectionType, List<MeterReading>> entry : grouped.entrySet()) {
            ConnectionType ct = entry.getKey();
            List<MeterReading> readings = entry.getValue();

            MonthlyReportRow row = new MonthlyReportRow();
            row.month = month;
            row.year = year;
            row.connectionType = ct;
            row.consumerCount = readings.size();

            double totalU = 0;
            double totalRev = 0;
            double paidRev = 0;
            double unpaidRev = 0;

            for (MeterReading mr : readings) {
                totalU += mr.getUnitsConsumed();
                Bill b = db.getAllBills().stream()
                        .filter(bill -> bill.getReadingId().equals(mr.getReadingId()))
                        .findFirst().orElse(null);
                if (b != null) {
                    totalRev += b.getTotalAmount();
                    if ("Paid".equalsIgnoreCase(b.getPaymentStatus())) {
                        paidRev += b.getTotalAmount();
                    } else {
                        unpaidRev += b.getTotalAmount();
                    }
                }
            }

            row.totalUnits = Math.round(totalU * 100.0) / 100.0;
            row.avgUnits = readings.isEmpty() ? 0 : Math.round((totalU / readings.size()) * 100.0) / 100.0;
            row.totalRevenue = Math.round(totalRev * 100.0) / 100.0;
            row.paidRevenue = Math.round(paidRev * 100.0) / 100.0;
            row.unpaidRevenue = Math.round(unpaidRev * 100.0) / 100.0;

            report.add(row);
        }

        return report;
    }
}
