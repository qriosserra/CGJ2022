package fr.lost_in_dark.game.utils;

import fr.lost_in_dark.game.assets.Assets;
import fr.lost_in_dark.game.render.MatrixStack;
import fr.lost_in_dark.game.render.shader.GameShaders;

public class RenderUtil {

    public static void drawRect(MatrixStack stack, int x, int y, int x2, int y2, int color) {
        if (x2 < x) {
            int t = x;
            x = x2;
            x2 = t;
        }

        if (y2 < y) {
            int t = y;
            y = y2;
            y2 = t;
        }

        Color c = new Color(color, true);

        stack.push();
        stack.setShader(GameShaders.COLOR);
        stack.unbindImage();
        stack.translate(x, y, 0);
        stack.scale(x2 - x, y2 - y, 1.0F);
        stack.color(c.getRed() / 255.0F, c.getGreen() / 255.0F, c.getBlue() / 255.0F, c.getAlpha() / 255.0F);
        stack.applyMatrix();

        Assets.getModel().render();

        stack.color(1.0F, 1.0F, 1.0F, 1.0F);
        stack.pop();
    }

}
