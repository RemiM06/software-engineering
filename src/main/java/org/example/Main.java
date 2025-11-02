package org.example;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        int[] data = {15, 7, 31, 3, 12};
        int[] largeData = new int[10000];
        for (int i = 0; i < largeData.length; i++) {
            largeData[i] = (int) (Math.random() * 100);
        }

        BitPackingBenchmark.compare(largeData);

        System.out.println("=== Test avec Factory ===\n");

        // Méthode 1 : Choix manuel
        testWithType(data, CompressionType.OVERLAP);
        testWithType(data, CompressionType.NO_OVERLAP);

        // Méthode 2 : Choix automatique
        System.out.println("=== Choix automatique ===");
        BitPacking auto = BitPackingFactory.createAuto(data);
        auto.compress(data);
        System.out.println("Type choisi: " + auto.getType().getDescription());
        System.out.println("Compression: " + auto.getCompressionRatio());
        System.out.println("Décompressé: " + Arrays.toString(auto.decompress()));
    }

    private static void testWithType(int[] data, CompressionType type) {
        System.out.println("=== " + type.getDescription() + " ===");

        BitPacking bp = BitPackingFactory.create(type);
        bp.compress(data);

        System.out.println("Données: " + Arrays.toString(data));
        System.out.println("Bits/élément: " + bp.getBitsPerElement());
        System.out.println("Taille compressée: " + bp.getCompressedSize());
        System.out.println("Ratio: " + bp.getCompressionRatio());
        System.out.println("Décompressé: " + Arrays.toString(bp.decompress()));
        System.out.println();
    }
}
