package dev.tenacity.utils.misc;


public class Random {

    public static int nextInt(final int startInclusive, final int endExclusive) {
        if (endExclusive - startInclusive <= 0)
            return startInclusive;

        return startInclusive + new java.util.Random().nextInt(endExclusive - startInclusive);
    }

}