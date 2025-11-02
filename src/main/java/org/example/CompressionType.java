package org.example;

/**
 * Types de compression disponibles
 */
public enum CompressionType {
    OVERLAP("Avec chevauchement"),
    NO_OVERLAP("Sans chevauchement"),
    OVERFLOW("Avec zone de débordement");

    private final String description;

    CompressionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
