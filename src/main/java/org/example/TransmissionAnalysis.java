package org.example;

/**
 * Calcule le seuil de rentabilité de la compression
 */
public class TransmissionAnalysis {

    /**
     * Calcule la latence minimale pour que la compression soit rentable
     *
     * Formule :
     * temps_compression + (taille_compressée * latence) < taille_originale * latence
     *
     * Résolution :
     * latence > temps_compression / (taille_originale - taille_compressée)
     *
     * @param compressionTimeNs temps de compression en nanosecondes
     * @param originalSize taille originale en int32
     * @param compressedSize taille compressée en int32
     * @return latence minimale en ns/int32 pour rentabilité
     */
    public static double calculateBreakEvenLatency(
            long compressionTimeNs,
            int originalSize,
            int compressedSize) {

        int economie = originalSize - compressedSize;
        if (economie <= 0) {
            return Double.POSITIVE_INFINITY; // Jamais rentable
        }

        return (double) compressionTimeNs / economie;
    }

    public static void analyzeTransmission(BitPacking bp, long compressionTimeNs) {
        int originalSize = bp.getOriginalSize();
        int compressedSize = bp.getCompressedSize();

        double breakEvenLatency = calculateBreakEvenLatency(
                compressionTimeNs, originalSize, compressedSize
        );

        System.out.println("=== Analyse de transmission ===");
        System.out.println("Type: " + bp.getType().getDescription());
        System.out.println("Seuil de rentabilité: " + (breakEvenLatency / 1000.0) + " µs/int32");
    }

}
