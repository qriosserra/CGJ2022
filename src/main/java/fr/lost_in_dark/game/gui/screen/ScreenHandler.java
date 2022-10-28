package fr.lost_in_dark.game.gui.screen;

import fr.lost_in_dark.game.Game;
import fr.lost_in_dark.game.listeners.KeyListener;
import fr.lost_in_dark.game.listeners.MouseListener;

public class ScreenHandler implements MouseListener, KeyListener {

    private double x;
    private double y;

    @Override
    public void keyPress(long window, int key, int scancode, int mods) {

    }

    @Override
    public void keyRelease(long window, int key, int scancode, int mods) {

    }

    @Override
    public void textInput(long window, String text) {

    }

    @Override
    public void mousePress(long window, int button, int mods) {
        if (Game.getInstance().getScreen() == null) return;
        Game.getInstance().getScreen().onMouseClick(button, this.x, this.y);
    }

    @Override
    public void mouseRelease(long window, int button, int mods) {
        if (Game.getInstance().getScreen() == null) return;
        Game.getInstance().getScreen().onMouseRelease(button, this.x, this.y);
    }

    @Override
    public void mouseScroll(long window, double xOffset, double yOffset) {
        if (Game.getInstance().getScreen() == null) return;
        Game.getInstance().getScreen().onMouseScroll(xOffset, yOffset);
    }

    @Override
    public void mouseMove(long window, double x, double y) {
        this.x = x;
        this.y = y;

        if (Game.getInstance().getScreen() == null) return;
        Game.getInstance().getScreen().onMouseMove(x, y);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }
}
