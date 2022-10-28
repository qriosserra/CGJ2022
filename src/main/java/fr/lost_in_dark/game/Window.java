package fr.lost_in_dark.game;

import fr.lost_in_dark.game.framebuffer.SceneBuffer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

	private long windowID;
	private int width = 0;
	private int height = 0;
	private int widthFramebuffer = 0;
	private int heightFramebuffer = 0;

	private float scale = 0;

	public void exit() {
		glfwFreeCallbacks(this.windowID);
		glfwDestroyWindow(this.windowID);

		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	public void setCallbacks() {
		glfwSetErrorCallback((error, description) -> {
			throw new IllegalStateException(GLFWErrorCallback.getDescription(description));
		});
	}

	public void init(int width, int height, String title) {
		GLFWErrorCallback.createPrint(System.err).set();

		if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

		this.windowID = glfwCreateWindow(width, height, title, NULL, NULL);
		if (this.windowID == NULL) throw new RuntimeException("Failed to create the GLFW window");

		glfwSetWindowSizeLimits(this.windowID, 400, 200, GLFW_DONT_CARE, GLFW_DONT_CARE);

		glfwMaximizeWindow(this.windowID);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

		glfwMakeContextCurrent(this.windowID);
		GL.createCapabilities();
		glfwSwapInterval(1);

		glfwShowWindow(this.windowID);

		this.updateViewport(width, height, -1, -1);
	}

	public boolean shouldClose() {
		return glfwWindowShouldClose(this.getWindowID());
	}

	public void swapBuffers() {
		glfwSwapBuffers(this.getWindowID());
	}

	public void updateViewport(int windowWidth, int windowHeight, int widthFramebuffer, int heightFramebuffer) {
		if (windowWidth == -1 || windowHeight == -1) {
			int[] w = new int[1];
			int[] h = new int[1];
			glfwGetWindowSize(this.windowID, w, h);
			windowWidth = w[0];
			windowHeight = h[0];
			w = null;
			h = null;
		}

		if (widthFramebuffer == -1 || heightFramebuffer == -1) {
			int[] w = new int[1];
			int[] h = new int[1];
			glfwGetFramebufferSize(this.windowID, w, h);
			widthFramebuffer = w[0];
			heightFramebuffer = h[0];
			w = null;
			h = null;
		}

		if (widthFramebuffer <= 0 || heightFramebuffer <= 0) return;

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glViewport(0, 0, widthFramebuffer, heightFramebuffer);

		this.scale = Math.max(widthFramebuffer / 800.0F, heightFramebuffer / 400.0F);
		this.scale = Math.round(this.scale * 2) / 2.0F;
		glOrtho(0, widthFramebuffer * this.scale, heightFramebuffer * this.scale, 0, 1, -1);

		this.width = windowWidth;
		this.height = windowHeight;
		this.widthFramebuffer = widthFramebuffer;
		this.heightFramebuffer = heightFramebuffer;

		if (Game.getInstance().getRenderSystem() != null) {
			Game.getInstance().getRenderSystem().getCamera().updateOrtho2D();
		}

		if (Game.getInstance().getGui() != null) {
			Game.getInstance().getGui().getCamera().updateOrtho2D();
		}

		if (Game.getInstance().getSceneBuffer() != null) {
			Game.getInstance().getSceneBuffer().cleanup();
			try {
				Game.getInstance().setSceneBuffer(new SceneBuffer(this));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public long getWindowID() {
		return this.windowID;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public int getWidthFramebuffer() {
		return this.widthFramebuffer;
	}

	public int getHeightFramebuffer() {
		return this.heightFramebuffer;
	}

	public float getScaledWidth() {
		return this.widthFramebuffer / this.scale;
	}

	public float getScaledHeight() {
		return this.heightFramebuffer / this.scale;
	}
}