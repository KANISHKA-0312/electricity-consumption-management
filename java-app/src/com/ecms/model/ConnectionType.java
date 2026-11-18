package com.ecms.model;

/**
 * Enum representing the electricity connection categories.
 */
public enum ConnectionType {
    DOMESTIC("Domestic", "Residential homes and apartments"),
    COMMERCIAL("Commercial", "Shops, offices, restaurants, shopping complexes"),
    INDUSTRIAL("Industrial", "Manufacturing plants, textile mills, factories"),
    AGRICULTURAL("Agricultural", "Farming pump sets and irrigation facilities");

    private final String displayName;
    private final String description;

    ConnectionType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static ConnectionType fromString(String text) {
        for (ConnectionType b : ConnectionType.values()) {
            if (b.displayName.equalsIgnoreCase(text) || b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unknown connection type: " + text + ". Allowed: Domestic, Commercial, Industrial, Agricultural");
    }
}
