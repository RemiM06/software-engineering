package org.example;

import java.util.*;

/**
 * Compression avec zone d'overflow pour les valeurs exceptionnelles
 */
public class BitPackingOverflow implements BitPacking {
    private int[] mainZone;           // Zone principale compressée
    private int[] overflowZone;       // Zone contenant les grandes valeurs
    private int bitsPerElement;       // Bits pour valeurs normales
    private int overflowBits;         // Bits pour indexer l'overflow
    private int originalSize;
    private int elementsPerInt32;

    /**
     * Détermine le seuil d'overflow (90e percentile par défaut)
     */
    private int calculateThreshold(int[] array) {
        int[] sorted = array.clone();
        Arrays.sort(sorted);
        int percentile90Index = (int) (sorted.length * 0.9);
        return sorted[percentile90Index];
    }

    /**
     * Compte combien de valeurs dépassent le seuil
     */
    private int countOverflows(int[] array, int threshold) {
        int count = 0;
        for (int value : array) {
            if (value > threshold) count++;
        }
        return count;
    }

    /**
     * Compresse avec gestion des overflows
     */
    @Override
    public int[] compress(int[] array) {
        if (array == null || array.length == 0) {
            return new int[0];
        }

        this.originalSize = array.length;

        // Étape 1 : Déterminer le seuil et compter les overflows
        int threshold = calculateThreshold(array);
        int overflowCount = countOverflows(array, threshold);

        // Étape 2 : Calculer les bits nécessaires
        this.bitsPerElement = BitPackingUtils.calculateBitsNeeded(threshold);
        this.overflowBits = overflowCount == 0 ? 0 :
                (32 - Integer.numberOfLeadingZeros(overflowCount - 1));

        // Étape 3 : Bits total = 1 bit flag + bitsPerElement ou overflowBits
        int totalBitsPerValue = 1 + Math.max(bitsPerElement, overflowBits);
        this.elementsPerInt32 = 32 / totalBitsPerValue;

        // Étape 4 : Créer les zones
        int mainSize = (originalSize + elementsPerInt32 - 1) / elementsPerInt32;
        this.mainZone = new int[mainSize];
        this.overflowZone = new int[overflowCount];

        // Étape 5 : Remplir les zones
        int overflowIndex = 0;
        for (int i = 0; i < originalSize; i++) {
            if (array[i] > threshold) {
                // Valeur overflow
                overflowZone[overflowIndex] = array[i];
                writeValueToMain(i, overflowIndex, true);
                overflowIndex++;
            } else {
                // Valeur normale
                writeValueToMain(i, array[i], false);
            }
        }

        // Retourner la concaténation des deux zones
        int[] result = new int[mainSize + overflowCount];
        System.arraycopy(mainZone, 0, result, 0, mainSize);
        System.arraycopy(overflowZone, 0, result, mainSize, overflowCount);
        return result;
    }

    private void writeValueToMain(int index, int value, boolean isOverflow) {
        int intIndex = index / elementsPerInt32;
        int elementPos = index % elementsPerInt32;
        int totalBitsPerValue = 1 + Math.max(bitsPerElement, overflowBits);
        int bitPos = elementPos * totalBitsPerValue;

        // Écrire le flag (1 bit)
        if (isOverflow) {
            mainZone[intIndex] |= (1 << bitPos);
        }

        // Écrire la valeur (décalée de 1 bit)
        mainZone[intIndex] |= (value << (bitPos + 1));
    }

    @Override
    public int get(int index) {
        int intIndex = index / elementsPerInt32;
        int elementPos = index % elementsPerInt32;
        int totalBitsPerValue = 1 + Math.max(bitsPerElement, overflowBits);
        int bitPos = elementPos * totalBitsPerValue;

        // Lire le flag
        boolean isOverflow = ((mainZone[intIndex] >> bitPos) & 1) == 1;

        // Lire la valeur
        int bitsToRead = isOverflow ? overflowBits : bitsPerElement;
        int mask = (1 << bitsToRead) - 1;
        int value = (mainZone[intIndex] >> (bitPos + 1)) & mask;

        // Si overflow, aller chercher la vraie valeur
        if (isOverflow) {
            return overflowZone[value];
        }
        return value;
    }

    @Override
    public int[] decompress() {
        int[] result = new int[originalSize];
        for (int i = 0; i < originalSize; i++) {
            result[i] = get(i);
        }
        return result;
    }

    @Override
    public CompressionType getType() {
        return CompressionType.OVERFLOW;
    }

    @Override
    public int getOriginalSize() { return originalSize; }

    @Override
    public int getBitsPerElement() { return bitsPerElement; }

    @Override
    public int getCompressedSize() { return mainZone.length + overflowZone.length; }

    @Override
    public double getCompressionRatio() {
        return (double) originalSize / getCompressedSize();
    }

}