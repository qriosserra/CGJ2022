package fr.lost_in_dark.game.listeners;

public interface MouseListener {

    void mousePress(long window, int button, int mods);

    void mouseRelease(long window, int button, int mods);

    void mouseScroll(long window, double xOffset, double yOffset);

    void mouseMove(long window, double x, double y);

}
