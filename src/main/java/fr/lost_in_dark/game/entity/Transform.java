package fr.lost_in_dark.game.entity;

import fr.lost_in_dark.game.render.camera.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform {

    public Vector3f pos;
    public Vector3f scale;

    public Transform() {
        this.pos = new Vector3f();
        this.scale = new Vector3f(1, 1, 1);
    }

    public Matrix4f getProjection(Matrix4f target) {
        target.translate(this.pos);
        target.scale(this.scale);
        return target;
    }

    public Matrix4f getProjection(Matrix4f target, float scale) {
        target.translate(Camera.roundToWindowPixels(this.pos, new Vector3f(scale, scale, 1)));
        target.scale(this.scale);
        return target;
    }
}
