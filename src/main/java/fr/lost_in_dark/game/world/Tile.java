package fr.lost_in_dark.game.world;

import fr.lost_in_dark.game.collision.AABB;
import fr.lost_in_dark.game.render.texture.SpriteSheet;

import java.util.ArrayList;
import java.util.List;

public class Tile {

    private final SpriteSheet spriteSheet;
    private final int id;
    private final int u;
    private final int v;
    private final int width;
    private final int height;
    private final List<AABB> collisions = new ArrayList<>();

    private int zIndex = 0;

    private boolean flippedHorizontally = false;
    private boolean flippedVertically = false;

    public Tile(SpriteSheet spriteSheet, int id, int u, int v, int width, int height) {
        this.spriteSheet = spriteSheet;

        this.id = id;
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
    }

    public int getId() {
        return this.id;
    }

    public SpriteSheet getSpriteSheet() {
        return this.spriteSheet;
    }

    public int getU() {
        return this.u;
    }

    public int getV() {
        return this.v;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public List<AABB> getCollisions() {
        return this.collisions;
    }

    public int getZIndex() {
        return this.zIndex;
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public void setFlippedHorizontally(boolean flippedHorizontally) {
        this.flippedHorizontally = flippedHorizontally;
    }

    public void setFlippedVertically(boolean flippedVertically) {
        this.flippedVertically = flippedVertically;
    }

    public boolean isFlippedHorizontally() {
        return this.flippedHorizontally;
    }

    public boolean isFlippedVertically() {
        return this.flippedVertically;
    }
}
