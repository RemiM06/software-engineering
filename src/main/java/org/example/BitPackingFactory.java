package org.example;

/**
 * Factory pour créer des instances de BitPacking
 * Utilise le pattern Simple Factory
 */
public class BitPackingFactory {

    /**
     * Crée une instance de BitPacking selon le type demandé
     * @param type type de compression
     * @return instance de BitPacking
     * @throws IllegalArgumentException si le type n'est pas supporté
     */
    public static BitPacking create(CompressionType type) {
        switch (type) {
            case OVERLAP:
                return new BitPackingOverlap();
            case NO_OVERLAP:
                return new BitPackingNoOverlap();
            case OVERFLOW:
                return new BitPackingOverflow();
            default:
                throw new IllegalArgumentException("Type inconnu: " + type);
        }
    }
}
