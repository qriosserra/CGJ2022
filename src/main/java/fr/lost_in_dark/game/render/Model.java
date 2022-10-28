package fr.lost_in_dark.game.render;

import fr.lost_in_dark.game.utils.ModelUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

public class Model {

    private final int draw_count;

    private final int v_id;
    private final int t_id;
    private final int i_id;

    public Model(float[] vertices, float[] tex_coords, int[] indices) {
        this.draw_count = indices.length;

        this.v_id = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.v_id);
        glBufferData(GL_ARRAY_BUFFER, ModelUtils.floatArrayToBuffer(vertices), GL_STATIC_DRAW);

        this.t_id = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.t_id);
        glBufferData(GL_ARRAY_BUFFER, ModelUtils.floatArrayToBuffer(tex_coords), GL_STATIC_DRAW);

        this.i_id = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.i_id);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, ModelUtils.intArrayToBuffer(indices), GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void render() {
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, this.v_id);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, this.t_id);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.i_id);
        glDrawElements(GL_TRIANGLES, this.draw_count, GL_UNSIGNED_INT, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);
    }

    public void delete() {
        glDeleteBuffers(this.v_id);
        glDeleteBuffers(this.t_id);
        glDeleteBuffers(this.i_id);
    }

}
