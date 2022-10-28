package fr.lost_in_dark.game.collision;

import org.joml.Vector2f;

public class AABB {

    private Vector2f center;
    private Vector2f half_extent;

    public AABB(Vector2f center, Vector2f half_extent) {
        this.center = center;
        this.half_extent = half_extent;
    }

    public Collision getCollision(AABB box2) {
        Vector2f distance = box2.center.sub(this.center, new Vector2f());
        distance.x = Math.abs(distance.x);
        distance.y = Math.abs(distance.y);

        distance.sub(this.half_extent.add(box2.half_extent, new Vector2f()));

        return new Collision(distance, distance.x < 0 && distance.y < 0);
    }

    public float[] correctPosition(AABB box2, Collision collision) {
        Vector2f correction = box2.center.sub(this.center, new Vector2f());
        float[] collide = new float[] {0, 0};
        if (collision.distance.x > collision.distance.y) {
            collide[0] = correction.x;
            if (correction.x > 0) {
                this.center.add(collision.distance.x, 0);
            } else {
                this.center.add(-collision.distance.x, 0);
            }
        } else {
            collide[1] = correction.y;
            if (correction.y > 0) {
                this.center.add(0, collision.distance.y);
            } else {
                this.center.add(0, -collision.distance.y);
            }
        }

        return collide;
    }

    public Vector2f getCenter() {
        return this.center;
    }

    public Vector2f getHalf_extent() {
        return this.half_extent;
    }
}
