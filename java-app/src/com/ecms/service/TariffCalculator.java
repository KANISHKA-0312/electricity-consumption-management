package com.ecms.service;

import com.ecms.model.ConnectionType;

/**
 * Service to calculate energy charges, fixed charges, government subsidies,
 * and electricity duty based on statutory tariff slabs.
 */
public class TariffCalculator {

    public static class TariffBreakdown {
        public double energyCharges;
        public double fixedCharges;
        public double electricityDuty;
        public double subsidyAmount;
        public double totalBillAmount;

        public TariffBreakdown(double energyCharges, double fixedCharges, 
                               double electricityDuty, double subsidyAmount, double totalBillAmount) {
            this.energyCharges = Math.round(energyCharges * 100.0) / 100.0;
            this.fixedCharges = Math.round(fixedCharges * 100.0) / 100.0;
            this.electricityDuty = Math.round(electricityDuty * 100.0) / 100.0;
            this.subsidyAmount = Math.round(subsidyAmount * 100.0) / 100.0;
            this.totalBillAmount = Math.round(totalBillAmount * 100.0) / 100.0;
        }
    }

    /**
     * Compute full billing breakdown for given units and connection type.
     */
    public static TariffBreakdown calculate(ConnectionType type, double units) {
        if (units < 0) {
            throw new IllegalArgumentException("Units consumed cannot be negative: " + units);
        }

        double energyCharges = 0.0;
        double fixedCharges = 0.0;
        double subsidyAmount = 0.0;
        double taxPercentage = 5.0; // 5% Electricity duty

        switch (type) {
            case DOMESTIC:
                if (units <= 100) {
                    energyCharges = 0.0;
                    fixedCharges = 0.0;
                    taxPercentage = 0.0;
                } else if (units <= 200) {
                    // First 100 units free, next 1-100 units @ 2.25
                    energyCharges = (units - 100) * 2.25;
                    fixedCharges = 20.0;
                } else if (units <= 400) {
                    // 101 to 200: 100 * 2.25 = 225, remaining @ 4.50
                    energyCharges = (100 * 2.25) + ((units - 200) * 4.50);
                    fixedCharges = 30.0;
                } else if (units <= 500) {
                    // 101 to 200: 225, 201 to 400: 200 * 4.50 = 900, remaining @ 6.00
                    energyCharges = 225.0 + 900.0 + ((units - 400) * 6.00);
                    fixedCharges = 50.0;
                } else {
                    // Above 500 units
                    energyCharges = 225.0 + 900.0 + (100 * 6.00) + ((units - 500) * 8.00);
                    fixedCharges = 80.0;
                }
                break;

            case COMMERCIAL:
                if (units <= 100) {
                    energyCharges = units * 6.50;
                    fixedCharges = 100.0;
                } else {
                    energyCharges = (100 * 6.50) + ((units - 100) * 9.50);
                    fixedCharges = 150.0;
                }
                break;

            case INDUSTRIAL:
                if (units <= 500) {
                    energyCharges = units * 8.00;
                    fixedCharges = 300.0;
                } else {
                    energyCharges = (500 * 8.00) + ((units - 500) * 11.50);
                    fixedCharges = 500.0;
                }
                break;

            case AGRICULTURAL:
                energyCharges = units * 0.50;
                fixedCharges = 0.0;
                subsidyAmount = energyCharges; // 100% government agricultural power subsidy
                taxPercentage = 0.0;
                break;
        }

        double duty = (energyCharges + fixedCharges) * (taxPercentage / 100.0);
        double total = (energyCharges + fixedCharges + duty) - subsidyAmount;
        if (total < 0) total = 0.0;

        return new TariffBreakdown(energyCharges, fixedCharges, duty, subsidyAmount, total);
    }
}
