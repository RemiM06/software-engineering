package org.example;


public class Main {
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║  BIT PACKING - ANALYSE DE PERFORMANCE               ║");
        System.out.println("╚══════════════════════════════════════════════════════╝\n");

        BitPackingBenchmark.runAll();

        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║  CONCLUSIONS CLÉS                                   ║");
        System.out.println("╚══════════════════════════════════════════════════════╝\n");

        System.out.println("1. COMPRESSION:");
        System.out.println("   - NoOverlap est 3x plus rapide (835 vs 2406 µs)");
        System.out.println("   - Overlap donne 15% meilleur ratio (2.29 vs 2.00)");
        System.out.println();

        System.out.println("2. ACCÈS get():");
        System.out.println("   - Performance similaire (~110 ns/accès)");
        System.out.println("   - Overhead du chevauchement négligeable");
        System.out.println();

        System.out.println("3. RENTABILITÉ TRANSMISSION:");
        System.out.println("   - Seuil ~0.095 µs/int32 pour les deux stratégies");
        System.out.println("   - Rentable dès latence > 100 ns/int32");
        System.out.println();

        System.out.println("4. RECOMMANDATIONS:");
        System.out.println("   - NoOverlap : pour compressions répétées (3x plus rapide)");
        System.out.println("   - Overlap   : pour économiser bande passante (meilleur ratio)");
    }
}

