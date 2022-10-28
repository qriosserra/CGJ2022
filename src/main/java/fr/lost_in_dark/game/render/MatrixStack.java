package fr.lost_in_dark.game.render;

import fr.lost_in_dark.game.render.shader.Shader;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Stack;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class MatrixStack {

    private final Stack<Matrix4f> matrixStack = new Stack<>();
    private Matrix4f matrix;
    private Shader currentShader;

    public MatrixStack(Matrix4f matrix) {
        this.matrix = matrix;
        this.push();
    }

    public void pop() {
        if (this.matrixStack.empty()) {
            System.err.println("Empty stack");
            return;
        }
        this.matrix = this.matrixStack.pop();
    }

    public void push() {
        this.matrixStack.push(new Matrix4f(this.matrix));
    }

    public void clear() {
        this.matrixStack.clear();
    }

    public void scale(float x, float y, float z) {
        this.getMatrix().scale(x, y , z);
    }

    public void scale(float scale) {
        this.getMatrix().scale(scale);
    }

    public void translate(int x, int y, int z) {
        this.getMatrix().translate(x, y, z);
    }
    public Matrix4f getMatrix() {
        return this.matrix;
    }

    public void setShader(Shader shader) {
        if (this.currentShader == shader) return;

        this.currentShader = shader;
        shader.bind();
    }

    public void applyMatrix() {
        this.currentShader.projection(this.getMatrix());
    }

    public void stopShader() {
        glUseProgram(0);
        this.currentShader = null;
    }

    public void color(float r, float g, float b, float a) {
        this.currentShader.color(r, g, b, a);
    }

    public Shader getShader() {
        return this.currentShader;
    }

    public void sampler(int id) {
        this.currentShader.sampler(id);
    }

    public void setUniform(String name, Matrix4f value) {
        this.currentShader.setUniform(name, value);
    }

    public void setUniform(String name, float value) {
        this.currentShader.setUniform(name, value);
    }

    public void setUniform(String name, Vector3f value) {
        this.currentShader.setUniform(name, value);
    }

    public void setUniform(String name, Vector2f value) {
        this.currentShader.setUniform(name, value);
    }

    public void setUniform(String name, Vector4f value) {
        this.currentShader.setUniform(name, value);
    }

    public void mul(Matrix4f matrix) {
        this.matrix.mul(matrix);
    }

    public void unbindImage() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

}
