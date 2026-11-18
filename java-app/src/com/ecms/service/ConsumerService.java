package com.ecms.service;

import com.ecms.dao.DatabaseManager;
import com.ecms.model.ConnectionType;
import com.ecms.model.Consumer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service providing Consumer management operations and validations.
 */
public class ConsumerService {
    private final DatabaseManager db = DatabaseManager.getInstance();

    public Consumer registerConsumer(String fullName, String address, String city,
                                     String phone, String email, ConnectionType connectionType,
                                     double sanctionedLoadKw) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Consumer full name cannot be empty.");
        }
        if (phone == null || phone.trim().length() < 10) {
            throw new IllegalArgumentException("Valid 10-digit phone number is required.");
        }
        if (sanctionedLoadKw <= 0) {
            throw new IllegalArgumentException("Sanctioned load must be greater than 0 kW.");
        }

        // Generate unique consumer ID based on connection type
        int prefixNumber = switch (connectionType) {
            case DOMESTIC -> 1000;
            case COMMERCIAL -> 2000;
            case INDUSTRIAL -> 3000;
            case AGRICULTURAL -> 4000;
        };

        int count = 1;
        for (Consumer c : db.getAllConsumers()) {
            if (c.getConnectionType() == connectionType) {
                count++;
            }
        }
        String consumerId = String.format("CONS-%d", prefixNumber + count);
        String meterNumber = String.format("MTR-ERD-%04d", 1000 + db.getAllConsumers().size() + 1);

        Consumer consumer = new Consumer(consumerId, fullName.trim(), address.trim(), 
                city.trim(), phone.trim(), email.trim(), connectionType, sanctionedLoadKw,
                meterNumber, LocalDate.now(), "Active");

        db.addConsumer(consumer);
        return consumer;
    }

    public Consumer getConsumerById(String consumerId) {
        Consumer c = db.getConsumer(consumerId);
        if (c == null) {
            throw new IllegalArgumentException("No consumer found with ID: " + consumerId);
        }
        return c;
    }

    public List<Consumer> getAllConsumers() {
        return db.getAllConsumers();
    }

    public List<Consumer> getConsumersByType(ConnectionType type) {
        List<Consumer> filtered = new ArrayList<>();
        for (Consumer c : db.getAllConsumers()) {
            if (c.getConnectionType() == type) {
                filtered.add(c);
            }
        }
        return filtered;
    }
}
