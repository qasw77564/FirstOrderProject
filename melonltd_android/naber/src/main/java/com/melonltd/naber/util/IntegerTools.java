package com.melonltd.naber.util;

public class IntegerTools {

    public static int parseInt(String intStr, int dflt) {
        if (intStr == null) {
            return dflt;
        }

        try {
            return Integer.parseInt(intStr);
        } catch (NumberFormatException e) {
            return dflt;
        }
    }
}
