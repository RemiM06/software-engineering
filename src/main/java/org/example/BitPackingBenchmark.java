package org.example;

/**
 * Utilitaire pour mesurer les performances
 */
public class BitPackingBenchmark {

    public static void compare(int[] data) {
        System.out.println("=== Comparaison des stratégies ===\n");

        for (CompressionType type : new CompressionType[]{CompressionType.OVERLAP, CompressionType.NO_OVERLAP}) {
            BitPacking bp = BitPackingFactory.create(type);

            // Mesure compression
            long startCompress = System.nanoTime();
            bp.compress(data);
            long compressTime = System.nanoTime() - startCompress;

            // Mesure décompression
            long startDecompress = System.nanoTime();
            bp.decompress();
            long decompressTime = System.nanoTime() - startDecompress;

            // Mesure accès aléatoire
            long startGet = System.nanoTime();
            for (int i = 0; i < data.length; i++) {
                bp.get(i);
            }
            long getTime = System.nanoTime() - startGet;

            // Affichage
            System.out.println(type.getDescription() + ":");
            System.out.println("  Compression: " + compressTime + " ns");
            System.out.println("  Décompression: " + decompressTime + " ns");
            System.out.println("  Accès (x" + data.length + "): " + getTime + " ns");
            System.out.println("  Taille: " + bp.getCompressedSize() + " int32");
            System.out.println();
        }
    }
}
