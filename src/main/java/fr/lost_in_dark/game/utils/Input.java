package fr.lost_in_dark.game.utils;

import static org.lwjgl.glfw.GLFW.*;

public class Input {

    private long window;

    private boolean[] keys;

    public Input(long window) {
        this.window = window;
    }

    public boolean isKeyDown(int key) {
        return glfwGetKey(this.window, key) == GLFW_TRUE;
    }

    public boolean isMouseButtonDown(int button) {
        return glfwGetMouseButton(this.window, button) == GLFW_TRUE;
    }

}
