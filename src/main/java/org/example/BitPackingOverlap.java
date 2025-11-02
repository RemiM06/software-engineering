package org.example;

public class BitPackingOverlap {
    // Variables pour stocker les informations de compression
    private int[] compressed;      // Tableau compressé
    private int bitsPerElement;    // Nombre de bits par élément
    private int originalSize;      // Taille du tableau original

    /**
     * Trouve le nombre de bits nécessaires pour représenter le maximum
     */
    private int calculateBitsNeeded(int[] array) {
        int max = 0;
        for (int value : array) {
            if (value > max) {
                max = value;
            }
        }
        // Si max = 0, on a besoin d'au moins 1 bit
        if (max == 0) return 1;

        // Compte les bits nécessaires pour représenter max
        int bits = 0;
        while (max > 0) {
            bits++;
            max = max / 2;
        }
        return bits;
    }

    /**
     * Compresse le tableau d'entrée
     */
    public int[] compress(int[] array) {
        if (array == null || array.length == 0) {
            return new int[0];
        }

        this.originalSize = array.length;
        this.bitsPerElement = calculateBitsNeeded(array);

        // Calcul du nombre d'int32 nécessaires
        int totalBits = originalSize * bitsPerElement;
        int compressedSize = (totalBits + 31) / 32; // Arrondi supérieur
        this.compressed = new int[compressedSize];

        // Compresse chaque élément
        for (int i = 0; i < originalSize; i++) {
            writeValue(i, array[i]);
        }

        return compressed;
    }

    /**
     * Écrit une valeur à la position i dans le tableau compressé
     */
    private void writeValue(int i, int value) {
        // Calcul de la position en bits
        int bitStart = i * bitsPerElement;
        int intIndex = bitStart / 32;        // Dans quel int32 ?
        int bitPos = bitStart % 32;          // À quelle position ?

        int bitsLeft = 32 - bitPos;          // Bits restants dans cet int32

        if (bitsLeft >= bitsPerElement) {
            // Tout tient dans un seul int32
            compressed[intIndex] |= (value << bitPos);
        } else {
            // Chevauchement : partie dans premier int32 + partie dans second
            int partieBasse = value & ((1 << bitsLeft) - 1);
            compressed[intIndex] |= (partieBasse << bitPos);

            int partieHaute = value >> bitsLeft;
            compressed[intIndex + 1] |= partieHaute;
        }
    }

    /**
     * Récupère le i-ème élément du tableau original
     */
    public int get(int i) {
        // Calcul de la position en bits
        int bitStart = i * bitsPerElement;
        int intIndex = bitStart / 32;
        int bitPos = bitStart % 32;

        int bitsLeft = 32 - bitPos;
        int mask = (1 << bitsPerElement) - 1; // Masque pour isoler les bits

        if (bitsLeft >= bitsPerElement) {
            // Tout est dans un seul int32
            return (compressed[intIndex] >> bitPos) & mask;
        } else {
            // Chevauchement : récupère depuis deux int32
            int partieBasse = compressed[intIndex] >> bitPos;
            int bitsManquants = bitsPerElement - bitsLeft;
            int partieHaute = compressed[intIndex + 1] & ((1 << bitsManquants) - 1);

            return partieBasse | (partieHaute << bitsLeft);
        }
    }

    /**
     * Décompresse tout le tableau
     */
    public int[] decompress() {
        int[] result = new int[originalSize];
        for (int i = 0; i < originalSize; i++) {
            result[i] = get(i);
        }
        return result;
    }

    public int getOriginalSize() { return originalSize; }
    public int getBitsPerElement() { return bitsPerElement; }
    public int getCompressedSize() { return compressed.length; }
    public double getCompressionRatio() { return (double) originalSize / compressed.length; }
}
