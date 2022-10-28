package fr.lost_in_dark.game.utils;

import fr.lost_in_dark.game.render.Model;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class ModelUtils {

    public static Model createQuad(float x, float y, float x2, float y2) {
        return new Model(new float[] {
                x, y2, 0,
                x2, y2, 0,
                x2, y, 0,
                x, y, 0,
        }, new float[] {
                0, 1,
                1, 1,
                1, 0,
                0, 0,
        }, new int[] {
                0, 1, 2,
                2, 3, 0
        });
    }

    public static FloatBuffer floatArrayToBuffer(float[] array) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(array.length);
        buffer.put(array);
        buffer.flip();
        return buffer;
    }

    public static IntBuffer intArrayToBuffer(int[] array) {
        IntBuffer buffer = BufferUtils.createIntBuffer(array.length);
        buffer.put(array);
        buffer.flip();
        return buffer;
    }

}
