package fr.lost_in_dark.game.utils;

public class MathUtils {

    public static int fastRound(float value) {
        int v = (int) value;
        if (value < v + 0.5F) return v;
        return v + 1;
    }

}
