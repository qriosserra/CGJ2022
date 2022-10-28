package fr.lost_in_dark.game.render.shader;

import fr.lost_in_dark.game.utils.IOUtil;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.HashMap;

import static org.lwjgl.opengl.GL20.*;

public class Shader {

    private final int program;
    private final int vertexShader;
    private final int fragmentShader;

    private final HashMap<String, Integer> uniforms = new HashMap<>();
    private final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);

    public Shader(String filename) {
        this.program = glCreateProgram();

        this.vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(this.vertexShader, IOUtil.readStreamFile("assets/shaders/" + filename + ".vs"));
        glCompileShader(this.vertexShader);

        if (glGetShaderi(this.vertexShader, GL_COMPILE_STATUS) != GL_TRUE) {
            System.err.println(glGetShaderInfoLog(this.vertexShader));
            System.exit(1);
        }

        this.fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(this.fragmentShader, IOUtil.readStreamFile("assets/shaders/" + filename + ".fs"));
        glCompileShader(this.fragmentShader);

        if (glGetShaderi(this.fragmentShader, GL_COMPILE_STATUS) != GL_TRUE) {
            System.err.println(glGetShaderInfoLog(this.fragmentShader));
            System.exit(1);
        }

        glAttachShader(this.program, this.vertexShader);
        glAttachShader(this.program, this.fragmentShader);

        glBindAttribLocation(this.program, 0, "vertices");
        glBindAttribLocation(this.program, 1, "textures");

        glLinkProgram(this.program);
        if (glGetProgrami(this.program, GL_LINK_STATUS) != GL_TRUE) {
            System.err.println(glGetProgramInfoLog(this.program));
            System.exit(1);
        }

        glValidateProgram(this.program);
        if (glGetProgrami(this.program, GL_VALIDATE_STATUS) != GL_TRUE) {
            System.err.println(glGetProgramInfoLog(this.program));
            System.exit(1);
        }
    }

    public void setUniform(String name, Vector3f value) {
        int location = this.getUniformLocation(name);
        if  (location != -1) glUniform3f(location, value.x, value.y, value.z);
    }

    public void setUniform(String name, int value) {
        int location = this.getUniformLocation(name);
        if  (location != -1) glUniform1i(location, value);
    }

    public void setUniform(String name, float value) {
        int location = this.getUniformLocation(name);
        if  (location != -1) glUniform1f(location, value);
    }

    public void setUniform(String name, Vector2f value) {
        int location = this.getUniformLocation(name);
        if  (location != -1) glUniform2f(location, value.x, value.y);
    }

    public void setUniform(String name, Vector4f value) {
        int location = this.getUniformLocation(name);
        if  (location == -1) return;

        glUniform4f(location, value.x, value.y, value.z, value.w);
    }

    public void setUniform(String name, Matrix4f value) {
        int location = this.getUniformLocation(name);
        if  (location == -1) return;

        value.get(this.floatBuffer);
        glUniformMatrix4fv(location, false, this.floatBuffer);
    }

    private int getUniformLocation(String name) {
        return this.uniforms.computeIfAbsent(name, (n) -> glGetUniformLocation(this.program, n));
    }

    public void bind() {
        glUseProgram(this.program);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void delete() {
        glDetachShader(this.program, this.vertexShader);
        glDetachShader(this.program, this.fragmentShader);

        glDeleteShader(this.vertexShader);
        glDeleteShader(this.fragmentShader);

        glDeleteProgram(this.program);
    }

    public void sampler(int id) {
        this.setUniform("sampler", id);
    }

    public void projection(Matrix4f projection) {
        this.setUniform("projection", projection);
    }

    public void color(float r, float g, float b, float a) {
        this.setUniform("color", new Vector4f(r, g, b, a));
    }

}
