package fr.lost_in_dark.game.render.texture;

import fr.lost_in_dark.game.render.MatrixStack;
import org.joml.Matrix4f;

public class SpriteSheet {

    private final Texture texture;

    public SpriteSheet(String texture) {
        this.texture = new Texture("assets/sheets/" + texture);
    }

    public void bind(MatrixStack stack, int x, int y, int width, int height) {
        Matrix4f target = new Matrix4f();
        target.translate(x / (float) this.texture.getWidth(), y / (float) this.texture.getHeight(), 0);
        target.scale(width / (float) this.texture.getWidth(), height / (float) this.texture.getHeight(), 1);

        stack.sampler(0);
        stack.setUniform("tex_modifier", target);

        this.texture.bind(0);
    }

    public Texture getTexture() {
        return this.texture;
    }
}
