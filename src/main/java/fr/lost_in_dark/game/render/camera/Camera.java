package fr.lost_in_dark.game.render.camera;

import fr.lost_in_dark.game.Game;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    private Vector3f position;
    private Matrix4f projection;

    public Camera() {
        this.position = new Vector3f(0, 0, 0);
        this.projection = new Matrix4f();

        this.updateOrtho2D();
    }

    public void updateOrtho2D() {
//        this.projection.setOrtho2D(-width / 2.0F, width / 2.0F, height / 2.0F, -height / 2.0F);
        this.projection.setOrtho2D(0, Game.getInstance().getWindow().getScaledWidth(), Game.getInstance().getWindow().getScaledHeight(), 0);
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void addPosition(Vector3f position) {
        this.position.add(position);
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public Matrix4f getUntransformedProjection() {
        return this.projection;
    }

    public Matrix4f getProjection() {
        Matrix4f target = new Matrix4f();

        Matrix4f pos = new Matrix4f().setTranslation(Camera.roundToWindowPixelsFromScaled(this.position));

        this.projection.mul(pos, target);
        return target;
    }

    public static Vector3f roundToWindowPixelsFromScaled(Vector3f vector3f) {
        float scaledWidth = Game.getInstance().getWindow().getScaledWidth();
        float scaledHeight = Game.getInstance().getWindow().getScaledHeight();
        int width = Game.getInstance().getWindow().getWidthFramebuffer();
        int height = Game.getInstance().getWindow().getHeightFramebuffer();

        return vector3f
                .mul(width / scaledWidth, height / scaledHeight, 1, new Vector3f())
                .round()
                .div(width / scaledWidth, height / scaledHeight, 1);
    }

    public static Vector3f roundToWindowPixels(Vector3f vector3f, Vector3f scale) {
        float scaledWidth = Game.getInstance().getWindow().getScaledWidth();
        float scaledHeight = Game.getInstance().getWindow().getScaledHeight();
        int width = Game.getInstance().getWindow().getWidthFramebuffer();
        int height = Game.getInstance().getWindow().getHeightFramebuffer();

        return vector3f
                .mul(scale, new Vector3f())
                .mul(width / scaledWidth, height / scaledHeight, 1)
                .round()
                .div(width / scaledWidth, height / scaledHeight, 1)
                .div(scale);
    }
}
