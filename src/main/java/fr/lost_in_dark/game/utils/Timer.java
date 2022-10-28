package fr.lost_in_dark.game.utils;

public class Timer {

    public static double getTimeInSeconds() {
        return (double)System.nanoTime() / 1000000000.0d;
    }

}
