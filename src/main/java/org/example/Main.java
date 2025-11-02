package org.example;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        // Test des deux versions
        int[] original = {15, 7, 31, 3, 12};

        System.out.println("=== Version 1 : Overlap ===");
        testOverlap(original);

        System.out.println("\n=== Version 2 : NoOverlap ===");
        testNoOverlap(original);
    }

    private static void testOverlap(int[] original) {
        BitPackingOverlap bp1 = new BitPackingOverlap();
        bp1.compress(original);

        System.out.println("Tableau: " + Arrays.toString(original));
        System.out.println("Bits par élément: " + bp1.getBitsPerElement());
        System.out.println("Taille compressée: " + bp1.getCompressedSize());
        System.out.println("Décompressé: " + Arrays.toString(bp1.decompress()));
    }

    private static void testNoOverlap(int[] original) {
        BitPackingNoOverlap bp2 = new BitPackingNoOverlap();
        bp2.compress(original);

        System.out.println("Tableau: " + Arrays.toString(original));
        System.out.println("Bits par élément: " + bp2.getBitsPerElement());
        System.out.println("Éléments par int32: " + bp2.getElementsPerInt32());
        System.out.println("Taille compressée: " + bp2.getCompressedSize());
        System.out.println("Décompressé: " + Arrays.toString(bp2.decompress()));
    }
}
