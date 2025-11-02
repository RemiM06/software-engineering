package org.example;

public class BitPackingNoOverlap implements BitPacking {
    private int[] compressed;
    private int bitsPerElement;
    private int originalSize;
    private int elementsPerInt32;  // Nouveau : éléments par int32

    /**
     * Trouve le nombre de bits nécessaires
     */
    private int calculateBitsNeeded(int[] array) {
        int max = 0;
        for (int value : array) {
            if (value > max) {
                max = value;
            }
        }
        if (max == 0) return 1;

        int bits = 0;
        while (max > 0) {
            bits++;
            max = max / 2;
        }
        return bits;
    }

    /**
     * Compresse le tableau (sans chevauchement)
     */
    public int[] compress(int[] array) {
        if (array == null || array.length == 0) {
            return new int[0];
        }

        this.originalSize = array.length;
        this.bitsPerElement = calculateBitsNeeded(array);
        this.elementsPerInt32 = 32 / bitsPerElement;

        // Calcul du nombre d'int32 nécessaires
        int compressedSize = (originalSize + elementsPerInt32 - 1) / elementsPerInt32;
        this.compressed = new int[compressedSize];

        // Compression de chaque élément
        for (int i = 0; i < originalSize; i++) {
            writeValue(i, array[i]);
        }

        return compressed;
    }

    /**
     * Écrit une valeur (toujours dans un seul int32)
     */
    private void writeValue(int i, int value) {
        int intIndex = i / elementsPerInt32;        // Quel int32 ?
        int elementPos = i % elementsPerInt32;      // Quel élément dedans ?
        int bitPos = elementPos * bitsPerElement;   // Position en bits

        compressed[intIndex] |= (value << bitPos);
    }

    /**
     * Récupère le i-ème élément (lecture simple, un seul int32)
     */
    public int get(int i) {
        int intIndex = i / elementsPerInt32;
        int elementPos = i % elementsPerInt32;
        int bitPos = elementPos * bitsPerElement;

        int mask = (1 << bitsPerElement) - 1;
        return (compressed[intIndex] >> bitPos) & mask;
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

    @Override
    public CompressionType getType() {
        return CompressionType.NO_OVERLAP;
    }

    // Getters
    public int getOriginalSize() { return originalSize; }
    public int getBitsPerElement() { return bitsPerElement; }
    public int getCompressedSize() { return compressed.length; }
    public int getElementsPerInt32() { return elementsPerInt32; }
    public double getCompressionRatio() { return (double) originalSize / compressed.length; }
}