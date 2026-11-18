package com.ecms.model;

import java.time.LocalDate;

/**
 * Entity class representing an electricity consumer account.
 * Demonstrates Object-Oriented principles: Encapsulation, Constructors, Accessors.
 */
public class Consumer {
    private String consumerId;
    private String fullName;
    private String address;
    private String city;
    private String phone;
    private String email;
    private ConnectionType connectionType;
    private double sanctionedLoadKw;
    private String meterNumber;
    private LocalDate registrationDate;
    private String status;

    public Consumer() {
        this.registrationDate = LocalDate.now();
        this.status = "Active";
        this.city = "Erode";
    }

    public Consumer(String consumerId, String fullName, String address, String city,
                    String phone, String email, ConnectionType connectionType,
                    double sanctionedLoadKw, String meterNumber, LocalDate registrationDate, String status) {
        this.consumerId = consumerId;
        this.fullName = fullName;
        this.address = address;
        this.city = city;
        this.phone = phone;
        this.email = email;
        this.connectionType = connectionType;
        this.sanctionedLoadKw = sanctionedLoadKw;
        this.meterNumber = meterNumber;
        this.registrationDate = registrationDate;
        this.status = status;
    }

    // Getters and Setters
    public String getConsumerId() { return consumerId; }
    public void setConsumerId(String consumerId) { this.consumerId = consumerId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public ConnectionType getConnectionType() { return connectionType; }
    public void setConnectionType(ConnectionType connectionType) { this.connectionType = connectionType; }

    public double getSanctionedLoadKw() { return sanctionedLoadKw; }
    public void setSanctionedLoadKw(double sanctionedLoadKw) { this.sanctionedLoadKw = sanctionedLoadKw; }

    public String getMeterNumber() { return meterNumber; }
    public void setMeterNumber(String meterNumber) { this.meterNumber = meterNumber; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("Consumer[ID=%s, Name=%s, Type=%s, Meter=%s, Load=%.1f kW, Status=%s]",
                consumerId, fullName, connectionType.getDisplayName(), meterNumber, sanctionedLoadKw, status);
    }
}
