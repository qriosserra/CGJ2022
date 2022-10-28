package fr.lost_in_dark.game.render.texture;

import fr.lost_in_dark.game.render.MatrixStack;
import fr.lost_in_dark.game.utils.Timer;
import org.joml.Vector2i;

import java.util.List;

public class SpriteAnimation {

    private final SpriteSheet spriteSheet;

    private final List<Vector2i> uvs;
    private final int width;
    private final int height;

    private int pointer;

    private double elapsedTime;
    private double currentTime;
    private double lastTime;
    private double fps;

    private boolean playing = true;

    public SpriteAnimation(List<Vector2i> uvs, int fps, SpriteSheet spriteSheet, int width, int height) {
        this.pointer = 0;
        this.elapsedTime = 0;
        this.currentTime = 0;
        this.lastTime = Timer.getTimeInSeconds();
        this.fps = 1.0 / (double) fps;
        this.uvs = uvs;
        this.spriteSheet = spriteSheet;
        this.width = width;
        this.height = height;
    }

    public void bind(MatrixStack stack) {
        if (this.playing) {
            this.currentTime = Timer.getTimeInSeconds();
            this.elapsedTime += this.currentTime - this.lastTime;
            this.lastTime = this.currentTime;

            while (this.elapsedTime >= this.fps) {
                this.elapsedTime -= this.fps;
                this.pointer = (this.pointer + 1) % this.uvs.size();
            }
        } else {
            this.lastTime = Timer.getTimeInSeconds();
            this.elapsedTime = this.fps;
        }

        this.spriteSheet.bind(stack, this.uvs.get(this.pointer).x, this.uvs.get(this.pointer).y, this.width, this.height);
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public boolean isPlaying() {
        return this.playing;
    }

    public void setPointer(int pointer) {
        this.currentTime = Timer.getTimeInSeconds();
        this.lastTime = this.currentTime;
        this.elapsedTime = 0;
        this.pointer = pointer;
    }

    public void setFps(int fps) {
        this.fps = 1.0 / (double) fps;
    }
}
