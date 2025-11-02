package org.example;

import java.util.Random;

public class BitPackingBenchmark {

    public static void runAll() {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║  BENCHMARKS - BIT PACKING COMPRESSION               ║");
        System.out.println("╚══════════════════════════════════════════════════════╝\n");

        // 1. Comparaison des stratégies (ESSENTIEL)
        System.out.println("=== Comparaison des stratégies ===");
        compareStrategies();

        // 2. Analyse de rentabilité (DEMANDÉ)
        System.out.println("\n=== Analyse de rentabilité transmission ===");
        analyzeTransmission();
    }

    private static void compareStrategies() {
        int[] data = generateWithOutliers(10000, 0, 100, 10000, 0.05);

        System.out.println("Dataset : 10000 éléments, 95% entre 0-100, 5% outliers ~10000\n");

        for (CompressionType type : new CompressionType[]{
                CompressionType.OVERLAP, CompressionType.NO_OVERLAP}) {

            BitPacking bp = BitPackingFactory.create(type);

            // Compression
            long startComp = System.nanoTime();
            bp.compress(data);
            long compTime = System.nanoTime() - startComp;

            // Décompression
            long startDecomp = System.nanoTime();
            bp.decompress();
            long decompTime = System.nanoTime() - startDecomp;

            // Accès aléatoires
            Random rnd = new Random(42);
            long startGet = System.nanoTime();
            for (int i = 0; i < 1000; i++) {
                bp.get(rnd.nextInt(data.length));
            }
            long getTime = System.nanoTime() - startGet;

            System.out.println(type.getDescription() + ":");
            System.out.printf("  Compression   : %8.2f µs\n", compTime / 1000.0);
            System.out.printf("  Décompression : %8.2f µs\n", decompTime / 1000.0);
            System.out.printf("  1000 get()    : %8.2f µs (%.2f ns/get)\n",
                    getTime / 1000.0, getTime / 1000.0);
            System.out.printf("  Taille        : %d int32 (ratio %.2f)\n",
                    bp.getCompressedSize(), bp.getCompressionRatio());
            System.out.println();
        }
    }

    private static void analyzeTransmission() {
        int[] data = generateUniform(10000, 0, 100);

        for (CompressionType type : new CompressionType[]{
                CompressionType.OVERLAP, CompressionType.NO_OVERLAP}) {

            BitPacking bp = BitPackingFactory.create(type);

            long start = System.nanoTime();
            bp.compress(data);
            long compTime = System.nanoTime() - start;

            TransmissionAnalysis.analyzeTransmission(bp, compTime);
            System.out.println();
        }
    }

    // Générateurs de données
    private static int[] generateUniform(int size, int min, int max) {
        Random rnd = new Random(42);
        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            result[i] = min + rnd.nextInt(max - min + 1);
        }
        return result;
    }

    private static int[] generateWithOutliers(int size, int normalMin, int normalMax,
                                              int outlierValue, double outlierRatio) {
        Random rnd = new Random(42);
        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            if (rnd.nextDouble() < outlierRatio) {
                result[i] = outlierValue + rnd.nextInt(1000);
            } else {
                result[i] = normalMin + rnd.nextInt(normalMax - normalMin + 1);
            }
        }
        return result;
    }
}
