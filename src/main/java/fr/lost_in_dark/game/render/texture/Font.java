package fr.lost_in_dark.game.render.texture;

import fr.lost_in_dark.game.assets.Assets;
import fr.lost_in_dark.game.render.MatrixStack;

import java.io.IOException;

public class Font {

    private final SpriteSheet sheet;
    private float[] charWidth = new float[256];

    public Font() {
        this.sheet = new SpriteSheet("font.png");

        try {
            this.charWidth = new float[] { 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 4.0F, 2.0F, 8.0F, 10.0F, 10.0F, 10.0F, 10.0F, 4.0F, 8.0F, 8.0F, 8.0F, 10.0F, 2.0F, 10.0F, 3.0F, 10.0F, 10.0F, 9.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 2.0F, 2.0F, 8.0F, 10.0F, 8.0F, 10.0F, 12.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 6.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 6.0F, 10.0F, 6.0F, 10.0F, 10.0F, 4.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 8.0F, 10.0F, 10.0F, 2.0F, 10.0F, 8.0F, 4.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 6.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 8.0F, 2.0F, 8.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 0.0F, 10.0F, 0.0F, 0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F, 10.0F, 10.0F, 10.0F, 10.0F, 0.0F, 10.0F, 10.0F, 0.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 6.0F, 6.0F, 0.0F, 0.0F, 0.0F, 10.0F, 0.0F, 10.0F, 10.0F, 10.0F, 0.0F, 8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 0.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 0.0F, 0.0F, 0.0F, 0.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 10.0F, 12.0F, 0.0F, 10.0F, 10.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F };
            //this.computeGlyphSize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void computeGlyphSize() throws IOException {
        int charW = this.sheet.getTexture().getWidth() / 16;
        int charH = this.sheet.getTexture().getHeight() / 16;
        float factor = (float) this.sheet.getTexture().getWidth() / 128.0F;

        for (int charID = 0; charID < 256; charID++) {
            int charX = charID % 16;
            int charY = charID / 16;
            int posX;

            for (posX = charW - 1; posX >= 0; posX--) {
                int pixelX = charX * charW + posX;
                boolean empty = true;

                for (int posY = 0; posY < charH && empty; posY++) {
                    int pixelY = charY * charH + posY;
                    int pixel = pixelX + pixelY * this.sheet.getTexture().getWidth();
                    pixel *= 4;

                    if (this.sheet.getTexture().getImage().get(pixel + 3) == -1 || this.sheet.getTexture().getImage().get(pixel + 3) >= 64) {
                        empty = false;
                    }
                }

                if (!empty) {
                    break;
                }
            }

            if (charID == 32) {
                if (charW <= 8) {
                    posX = (int) (2.0F * factor);
                } else {
                    posX = (int) (1.5F * factor);
                }
            }

            this.charWidth[charID] = (float) (posX + 1);
        }
    }

    public void render(MatrixStack stack, String text, int x, int y) {
        this.render(stack, text, x, y, false);
    }

    public void render(MatrixStack stack, String text, int x, int y, boolean shadow) {
        stack.push();
        stack.translate(x, y, 0);

        if (shadow) {
            stack.color(0F, 0F, 0F, 0.4F);
            int posX = 0;
            for (int i = 0; i < text.length(); i++) {
                this.render(stack, text.charAt(i), posX + 2, 2);
                posX += this.getWidth(text.charAt(i)) + 3;
            }
        }

        stack.color(1.0F, 1.0F, 1.0F, 1.0F);
        int posX = 0;
        for (int i = 0; i < text.length(); i++) {
            this.render(stack, text.charAt(i), posX, 0);
            posX += this.getWidth(text.charAt(i)) + 3;
        }

        stack.pop();
    }

    public void render(MatrixStack stack, char c, int x, int y) {
        if (c == ' ') return;

        stack.push();
        stack.translate(x, y, 0);
        stack.scale(16);
        stack.applyMatrix();

        int u = (int) (c) % 16 * 16;
        int v = (int) (c) / 16 * 16;

        this.sheet.bind(stack, u, v, 16, 16);
        Assets.getModel().render();

        stack.pop();
    }

    public float getWidth(String text) {
        float width = 0;
        for (char c : text.toCharArray()) {
            if ((int) c > 0 && (int) c < 256) {
                width += this.charWidth[c] + 1;
            }
        }
        return width;
    }

    public float getWidth(char c) {
        if ((int) c > 0 && (int) c < 256) return this.charWidth[c] + 1;
        return 0;
    }

}
