package fr.lost_in_dark.game.render;

import fr.lost_in_dark.game.render.camera.Camera;
import org.joml.Matrix4f;

public class RenderSystem {

    private final Camera camera;
    private static MatrixStack stack = new MatrixStack(new Matrix4f());

    public RenderSystem() {
        this.camera = new Camera();
    }

    public Camera getCamera() {
        return this.camera;
    }

    public static void setStack(MatrixStack stack) {
        RenderSystem.getStack().clear();
        RenderSystem.stack = stack;
    }

    public static MatrixStack getStack() {
        return RenderSystem.stack;
    }
}
