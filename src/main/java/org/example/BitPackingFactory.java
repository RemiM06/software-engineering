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
                throw new UnsupportedOperationException("Overflow pas encore implémenté");
            default:
                throw new IllegalArgumentException("Type inconnu: " + type);
        }
    }

    /**
     * Sélectionne automatiquement la meilleure stratégie selon les données
     * @param array données à compresser
     * @return instance de BitPacking optimale
     */
    public static BitPacking createAuto(int[] array) {
        if (array == null || array.length == 0) {
            return new BitPackingNoOverlap();
        }

        // Calcul du nombre de bits nécessaires
        int max = 0;
        for (int value : array) {
            if (value > max) max = value;
        }

        int bitsNeeded = (max == 0) ? 1 : 32 - Integer.numberOfLeadingZeros(max);
        int elementsPerInt32 = 32 / bitsNeeded;

        // Choix de la stratégie
        // Si peu d'éléments par int32, le chevauchement est avantageux
        if (elementsPerInt32 <= 2) {
            return new BitPackingOverlap();
        } else {
            return new BitPackingNoOverlap();
        }
    }
}
