package fr.lost_in_dark.game.render;

import org.lwjgl.opengl.GL11;

public class RenderUtils {

    public static void renderQuad(float x, float y, float x2, float y2, int color) {
        if (x > x2) {
            float t = x2;
            x2 = x;
            x = t;
        }

        if (y > y2) {
            float t = y2;
            y2 = y;
            y = t;
        }

        RenderUtils.setColor(color);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x2, y);
        GL11.glVertex2f(x2, y2);
        GL11.glVertex2f(x, y2);
        GL11.glEnd();
    }

    public static void setColor(int color) {
        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = ((color) & 0xFF) / 255.0F;
        float alpha = ((color >> 24) & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }

}
