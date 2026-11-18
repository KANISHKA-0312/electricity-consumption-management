package com.ecms.dao;

import com.ecms.model.Bill;
import com.ecms.model.ConnectionType;
import com.ecms.model.Consumer;
import com.ecms.model.MeterReading;
import com.ecms.service.TariffCalculator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.*;

/**
 * Data Access Object (DAO) and In-Memory Data Manager.
 * Manages repository collections of Consumers, MeterReadings, and Bills.
 * Provides file persistence and seed data loading.
 */
public class DatabaseManager {
    private static DatabaseManager instance;

    private final Map<String, Consumer> consumers = new LinkedHashMap<>();
    private final Map<String, MeterReading> meterReadings = new LinkedHashMap<>();
    private final Map<String, Bill> bills = new LinkedHashMap<>();

    private DatabaseManager() {
        initializeSampleData();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    // Consumer Operations
    public void addConsumer(Consumer consumer) {
        consumers.put(consumer.getConsumerId(), consumer);
    }

    public Consumer getConsumer(String consumerId) {
        return consumers.get(consumerId);
    }

    public List<Consumer> getAllConsumers() {
        return new ArrayList<>(consumers.values());
    }

    public boolean existsConsumer(String consumerId) {
        return consumers.containsKey(consumerId);
    }

    // Meter Reading Operations
    public void addMeterReading(MeterReading reading) {
        meterReadings.put(reading.getReadingId(), reading);
    }

    public MeterReading getMeterReading(String readingId) {
        return meterReadings.get(readingId);
    }

    public List<MeterReading> getAllMeterReadings() {
        return new ArrayList<>(meterReadings.values());
    }

    public List<MeterReading> getReadingsByConsumer(String consumerId) {
        List<MeterReading> list = new ArrayList<>();
        for (MeterReading mr : meterReadings.values()) {
            if (mr.getConsumerId().equalsIgnoreCase(consumerId)) {
                list.add(mr);
            }
        }
        return list;
    }

    // Bill Operations
    public void addBill(Bill bill) {
        bills.put(bill.getBillId(), bill);
    }

    public Bill getBill(String billId) {
        return bills.get(billId);
    }

    public List<Bill> getAllBills() {
        return new ArrayList<>(bills.values());
    }

    public List<Bill> getBillsByConsumer(String consumerId) {
        List<Bill> list = new ArrayList<>();
        for (Bill b : bills.values()) {
            if (b.getConsumerId().equalsIgnoreCase(consumerId)) {
                list.add(b);
            }
        }
        return list;
    }

    // Seed Data Initialization
    private void initializeSampleData() {
        // Register Consumers
        addConsumer(new Consumer("CONS-1001", "Ramesh Kumar S", "12 Gandhiji Road, Surampatti", "Erode", "9842100101", "ramesh.k@gmail.com", ConnectionType.DOMESTIC, 3.0, "MTR-ERD-1001", LocalDate.of(2023, 1, 15), "Active"));
        addConsumer(new Consumer("CONS-1002", "Priya Dharshini M", "45 Perundurai Road, Thindal", "Erode", "9842100102", "priya.m@yahoo.com", ConnectionType.DOMESTIC, 4.0, "MTR-ERD-1002", LocalDate.of(2023, 3, 20), "Active"));
        addConsumer(new Consumer("CONS-1003", "Senthil Nathan K", "88 Sathy Road, Veerappanchatram", "Erode", "9842100103", "senthil.k@gmail.com", ConnectionType.DOMESTIC, 2.0, "MTR-ERD-1003", LocalDate.of(2023, 5, 10), "Active"));
        addConsumer(new Consumer("CONS-1004", "Kavitha Ramachandran", "15 Brough Road, Marapalam", "Erode", "9842100104", "kavitha.r@gmail.com", ConnectionType.DOMESTIC, 5.0, "MTR-ERD-1004", LocalDate.of(2023, 7, 1), "Active"));
        addConsumer(new Consumer("CONS-1005", "Arun Prasath V", "72 Chennimalai Road, Solar", "Erode", "9842100105", "arun.prasath@outlook.com", ConnectionType.DOMESTIC, 3.5, "MTR-ERD-1005", LocalDate.of(2023, 9, 12), "Active"));
        addConsumer(new Consumer("CONS-1006", "Meenakshi Sundaram", "23 Park Road, Collectorate Post", "Erode", "9842100106", "meena.sundar@gmail.com", ConnectionType.DOMESTIC, 6.0, "MTR-ERD-1006", LocalDate.of(2024, 1, 8), "Active"));
        addConsumer(new Consumer("CONS-1007", "Vigneshwaran P", "9 Anna Nagar, Modakkurichi", "Erode", "9842100107", "vicky.p@gmail.com", ConnectionType.DOMESTIC, 2.5, "MTR-ERD-1007", LocalDate.of(2024, 2, 14), "Active"));
        addConsumer(new Consumer("CONS-1008", "Deepa Jayakumar", "34 Railway Colony, Junction", "Erode", "9842100108", "deepa.j@gmail.com", ConnectionType.DOMESTIC, 4.0, "MTR-ERD-1008", LocalDate.of(2024, 3, 22), "Active"));

        addConsumer(new Consumer("CONS-2001", "Kongu Supermarket & Bakery", "104 Mettur Road, Bus Stand", "Erode", "9842200201", "kongumart@gmail.com", ConnectionType.COMMERCIAL, 15.0, "MTR-ERD-2001", LocalDate.of(2022, 6, 10), "Active"));
        addConsumer(new Consumer("CONS-2002", "TexValley Retail Showroom", "NH 544, Gangapuram", "Erode", "9842200202", "texvalley.admin@retail.com", ConnectionType.COMMERCIAL, 45.0, "MTR-ERD-2002", LocalDate.of(2022, 11, 5), "Active"));
        addConsumer(new Consumer("CONS-2003", "Aavin Milk Parlour & Cafe", "18 Palani Road, Perundurai", "Erode", "9842200203", "aavin.perundurai@gmail.com", ConnectionType.COMMERCIAL, 10.0, "MTR-ERD-2003", LocalDate.of(2023, 2, 18), "Active"));
        addConsumer(new Consumer("CONS-2004", "Lotus Diagnostic Center", "56 EVN Road, Near GH", "Erode", "9842200204", "lotus.diag@health.in", ConnectionType.COMMERCIAL, 25.0, "MTR-ERD-2004", LocalDate.of(2023, 4, 25), "Active"));
        addConsumer(new Consumer("CONS-2005", "Hotel Saravana Bhavan", "30 Bhavani Main Road", "Erode", "9842200205", "saravana.erode@food.com", ConnectionType.COMMERCIAL, 30.0, "MTR-ERD-2005", LocalDate.of(2023, 8, 30), "Active"));

        addConsumer(new Consumer("CONS-3001", "Bhavani Spinning Mills", "SIPCOT Phase I", "Perundurai", "9842300301", "bhavani.spinning@textile.com", ConnectionType.INDUSTRIAL, 120.0, "MTR-ERD-3001", LocalDate.of(2021, 4, 12), "Active"));
        addConsumer(new Consumer("CONS-3002", "Sakthi Dyeing Unit", "Survey 142, Kasipalayam", "Erode", "9842300302", "sakthi.dyeing@textile.org", ConnectionType.INDUSTRIAL, 150.0, "MTR-ERD-3002", LocalDate.of(2021, 8, 19), "Active"));
        addConsumer(new Consumer("CONS-3003", "Erode Turmeric Agro Ltd", "4 Semmampalayam Road", "Erode", "9842300303", "turmeric.processing@agro.in", ConnectionType.INDUSTRIAL, 85.0, "MTR-ERD-3003", LocalDate.of(2022, 1, 30), "Active"));
        addConsumer(new Consumer("CONS-3004", "Premier Steel Fabricators", "Plot 55 SIDCO, Chennimalai", "Erode", "9842300304", "premier.steel@fabrication.com", ConnectionType.INDUSTRIAL, 95.0, "MTR-ERD-3004", LocalDate.of(2022, 9, 14), "Active"));

        addConsumer(new Consumer("CONS-4001", "Murugesan Farm Borewell 1", "Kavindapadi Village", "Bhavani", "9842400401", "murugesan.farm@agri.in", ConnectionType.AGRICULTURAL, 7.5, "MTR-ERD-4001", LocalDate.of(2020, 3, 10), "Active"));
        addConsumer(new Consumer("CONS-4002", "Chinnasamy Canal Pump", "Sivagiri Fields", "Kodumudi", "9842400402", "chinnasamy.agri@gmail.com", ConnectionType.AGRICULTURAL, 10.0, "MTR-ERD-4002", LocalDate.of(2020, 7, 22), "Active"));
        addConsumer(new Consumer("CONS-4003", "Ramasamy Coconut Plantation", "Panchalingapuram", "Modakkurichi", "9842400403", "ramasamy.agri@gmail.com", ConnectionType.AGRICULTURAL, 5.0, "MTR-ERD-4003", LocalDate.of(2021, 2, 15), "Active"));

        // Seed Recent Billing Cycle Readings & Bills (October 2026)
        seedReadingAndBill("CONS-1001", 10, 2026, 2415.0, 2600.0, "Paid", LocalDate.of(2026, 11, 10));
        seedReadingAndBill("CONS-1002", 10, 2026, 3895.0, 4180.0, "Unpaid", null);
        seedReadingAndBill("CONS-1003", 10, 2026, 1510.0, 1610.0, "Paid", LocalDate.of(2026, 11, 4));
        seedReadingAndBill("CONS-1004", 10, 2026, 5810.0, 6210.0, "Unpaid", null);
        seedReadingAndBill("CONS-1005", 10, 2026, 3105.0, 3315.0, "Paid", LocalDate.of(2026, 11, 12));
        seedReadingAndBill("CONS-1006", 10, 2026, 7250.0, 7700.0, "Unpaid", null);
        seedReadingAndBill("CONS-1007", 10, 2026, 1075.0, 1145.0, "Paid", LocalDate.of(2026, 11, 5));
        seedReadingAndBill("CONS-1008", 10, 2026, 3570.0, 3820.0, "Unpaid", null);

        seedReadingAndBill("CONS-2001", 10, 2026, 22900.0, 24400.0, "Paid", LocalDate.of(2026, 11, 9));
        seedReadingAndBill("CONS-2002", 10, 2026, 80000.0, 85100.0, "Unpaid", null);
        seedReadingAndBill("CONS-2003", 10, 2026, 14810.0, 15690.0, "Paid", LocalDate.of(2026, 11, 11));
        seedReadingAndBill("CONS-2004", 10, 2026, 43650.0, 46500.0, "Paid", LocalDate.of(2026, 11, 13));
        seedReadingAndBill("CONS-2005", 10, 2026, 48850.0, 52050.0, "Unpaid", null);

        seedReadingAndBill("CONS-3001", 10, 2026, 226700.0, 243100.0, "Paid", LocalDate.of(2026, 11, 14));
        seedReadingAndBill("CONS-3002", 10, 2026, 304400.0, 325100.0, "Unpaid", null);
        seedReadingAndBill("CONS-3003", 10, 2026, 122700.0, 131200.0, "Paid", LocalDate.of(2026, 11, 12));
        seedReadingAndBill("CONS-3004", 10, 2026, 145600.0, 156000.0, "Unpaid", null);

        seedReadingAndBill("CONS-4001", 10, 2026, 15720.0, 16220.0, "Paid", LocalDate.of(2026, 11, 5));
        seedReadingAndBill("CONS-4002", 10, 2026, 23190.0, 23900.0, "Paid", LocalDate.of(2026, 11, 6));
        seedReadingAndBill("CONS-4003", 10, 2026, 10360.0, 10700.0, "Paid", LocalDate.of(2026, 11, 7));
    }

    private void seedReadingAndBill(String consumerId, int month, int year, double prev, double curr,
                                   String status, LocalDate payDate) {
        String readingId = String.format("MR-%d%02d-%s", year, month, consumerId.substring(5));
        String billId = String.format("BILL-%d%02d-%s", year, month, consumerId.substring(5));

        MeterReading mr = new MeterReading(readingId, consumerId, month, year, LocalDate.of(year, month, 28), prev, curr, "Staff");
        addMeterReading(mr);

        Consumer c = getConsumer(consumerId);
        TariffCalculator.TariffBreakdown bd = TariffCalculator.calculate(c.getConnectionType(), mr.getUnitsConsumed());

        Bill bill = new Bill(billId, readingId, consumerId, 
                LocalDate.of(year, month, 28).plusDays(1),
                LocalDate.of(year, month, 28).plusDays(21),
                mr.getUnitsConsumed(), bd.energyCharges, bd.fixedCharges, bd.electricityDuty,
                bd.subsidyAmount, bd.totalBillAmount, status, payDate);
        addBill(bill);
    }
}
