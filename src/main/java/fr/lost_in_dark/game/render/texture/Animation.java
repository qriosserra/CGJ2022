package fr.lost_in_dark.game.render.texture;

import fr.lost_in_dark.game.utils.Timer;

public class Animation {

    private Texture[] frames;
    private int pointer;

    private double elapsedTime;
    private double currentTime;
    private double lastTime;
    private double fps;

    public Animation(int amount, int fps, String filename) {
        this.pointer = 0;
        this.elapsedTime = 0;
        this.currentTime = 0;
        this.lastTime = Timer.getTimeInSeconds();
        this.fps = 1.0 / (double) fps;
        this.frames = new Texture[amount];

        for (int i = 0; i < amount; i++) {
            this.frames[i] = new Texture("assets/animations/" + filename + "/" + i + ".png");
        }
    }

    public void bind() {
        this.currentTime = Timer.getTimeInSeconds();
        this.elapsedTime += this.currentTime - this.lastTime;
        this.lastTime = this.currentTime;

        while (this.elapsedTime >= this.fps) {
            this.elapsedTime -= this.fps;
            this.pointer = (this.pointer + 1) % this.frames.length;
        }

        this.frames[this.pointer].bind(0);
    }
}
