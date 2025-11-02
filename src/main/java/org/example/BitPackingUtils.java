package org.example;

public class BitPackingUtils {
    public static int calculateBitsNeeded(int max) {
        if (max == 0) return 1;
        return 32 - Integer.numberOfLeadingZeros(max);
    }
}
