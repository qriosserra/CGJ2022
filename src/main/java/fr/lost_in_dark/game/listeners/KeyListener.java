package fr.lost_in_dark.game.listeners;

public interface KeyListener {

    void keyPress(long window, int key, int scancode, int mods);

    void keyRelease(long window, int key, int scancode, int mods);

    void textInput(long window, String text);

}
