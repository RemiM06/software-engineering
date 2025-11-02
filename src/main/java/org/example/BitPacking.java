package org.example;

/**
 * Interface pour les stratégies de compression Bit Packing.
 * Définit le contrat commun pour toutes les implémentations.
 */
public interface BitPacking {

    /**
     * Compresse un tableau d'entiers
     * @param array tableau à compresser
     * @return tableau compressé (taille réduite)
     */
    int[] compress(int[] array);

    /**
     * Décompresse et retourne le tableau original
     * @return tableau décompressé
     */
    int[] decompress();

    /**
     * Récupère le i-ème élément du tableau original
     * @param index position de l'élément
     * @return valeur de l'élément
     * @throws IndexOutOfBoundsException si index invalide
     */
    int get(int index);

    /**
     * @return nombre d'éléments dans le tableau original
     */
    int getOriginalSize();

    /**
     * @return nombre de bits utilisés par élément
     */
    int getBitsPerElement();

    /**
     * @return taille du tableau compressé
     */
    int getCompressedSize();

    /**
     * @return ratio de compression (taille originale / taille compressée)
     */
    double getCompressionRatio();

    /**
     * @return type de compression utilisé
     */
    CompressionType getType();
}
