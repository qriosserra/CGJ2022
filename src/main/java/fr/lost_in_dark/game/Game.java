package fr.lost_in_dark.game;

import fr.lost_in_dark.game.assets.Assets;
import fr.lost_in_dark.game.entity.Player;
import fr.lost_in_dark.game.framebuffer.SceneBuffer;
import fr.lost_in_dark.game.gui.Gui;
import fr.lost_in_dark.game.gui.dialog.GameDialogHandler;
import fr.lost_in_dark.game.gui.screen.Screen;
import fr.lost_in_dark.game.gui.screen.screens.TestScreen;
import fr.lost_in_dark.game.listeners.KeyListener;
import fr.lost_in_dark.game.listeners.MouseListener;
import fr.lost_in_dark.game.render.RenderSystem;
import fr.lost_in_dark.game.render.shader.GameShaders;
import fr.lost_in_dark.game.sound.SoundBuffer;
import fr.lost_in_dark.game.sound.SoundManager;
import fr.lost_in_dark.game.sound.SoundSource;
import fr.lost_in_dark.game.utils.Input;
import fr.lost_in_dark.game.utils.Timer;
import fr.lost_in_dark.game.world.TileRenderer;
import fr.lost_in_dark.game.world.World;
import org.joml.Matrix4f;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Game {

    private static Game INSTANCE;
    private final Window window;

    private final ArrayList<MouseListener> mouseListeners = new ArrayList<>();
    private final ArrayList<KeyListener> keyListeners = new ArrayList<>();

    private final RenderSystem renderSystem;
    private final Input input;
    private final TileRenderer tileRenderer;

    private static int DEBUG_FPS = 0;

    private World world;
    private final Player player;
    private final Gui gui;

    private final GameDialogHandler gameDialogHandler;

    private boolean pause = false;

    private SoundManager soundManager;

    private SceneBuffer sceneBuffer;

    private Screen screen;

    public static void main(String[] args) throws Exception {
        System.out.println("Running on LWJGL " + Version.getVersion());
        new Game().run();
    }

    public Game() throws Exception {
        INSTANCE = this;
        this.window = new Window();
        this.window.init(800, 400, "Game");
        System.out.println("Created window.");

        this.sceneBuffer = new SceneBuffer(this.window);

        this.keyListeners.add(this.gameDialogHandler = new GameDialogHandler());

        this.renderSystem = new RenderSystem();
        this.input = new Input(this.window.getWindowID());
        this.tileRenderer = new TileRenderer();

        this.keyListeners.add(this.player = new Player());

        this.world = new World("level", "");
        System.out.println("Created world.");
        this.gui = new Gui();
        System.out.println("Created gui.");

        this.screen = new TestScreen();

        this.soundManager = new SoundManager();
        try {
            this.soundManager.init();
            this.setupSounds();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Created sound manager.");

        Assets.initAssets();
    }

    private void setupSounds() throws Exception {
        SoundBuffer buffMusic = new SoundBuffer("assets/sounds/music.ogg");
        this.soundManager.addSoundBuffer(buffMusic);
        SoundSource sourceMusic = new SoundSource(true, false);
        sourceMusic.setBuffer(buffMusic.getBufferId());
        this.soundManager.addSoundSource("music", sourceMusic);

        sourceMusic.setGain(0.2F);
        sourceMusic.play();
    }

    private void run() {
        this.window.setCallbacks();
        this.setupWindowHandler();
        this.setupKeysHandler();
        this.setupMouseHandler();

        this.mouseListeners.add(Screen.HANDLER);
        this.keyListeners.add(Screen.HANDLER);

        this.loop();

        this.getWindow().exit();
    }

    private void setupWindowHandler() {
        glfwSetWindowSizeCallback(this.getWindow().getWindowID(), (window, width, height) -> {
            this.getWindow().updateViewport(width, height, -1, -1);
        });

        glfwSetFramebufferSizeCallback(this.getWindow().getWindowID(), (window, width, height) -> {
            this.getWindow().updateViewport(-1, -1, width, height);
        });
    }

    private void setupKeysHandler() {
        glfwSetKeyCallback(this.getWindow().getWindowID(), (window, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                this.keyListeners.forEach(listener -> listener.keyPress(window, key, scancode, mods));
            } else if (action == GLFW_RELEASE) {
                this.keyListeners.forEach(listener -> listener.keyRelease(window, key, scancode, mods));
            }
        });

        glfwSetCharCallback(this.getWindow().getWindowID(), (window, codepoint) -> {
            this.keyListeners.forEach(listener -> listener.textInput(window, String.valueOf(Character.toChars(codepoint))));
        });
    }

    private void setupMouseHandler() {
        glfwSetMouseButtonCallback(this.getWindow().getWindowID(), (window, button, action, mods) -> {
            if (action == GLFW_PRESS) {
                this.mouseListeners.forEach(listener -> listener.mousePress(window, button, mods));
            } else if (action == GLFW_RELEASE) {
                this.mouseListeners.forEach(listener -> listener.mouseRelease(window, button, mods));
            }
        });

        glfwSetScrollCallback(this.getWindow().getWindowID(), (window, xoffset, yoffset) -> {
            this.mouseListeners.forEach(listener -> listener.mouseScroll(window, xoffset, yoffset));
        });

        glfwSetCursorPosCallback(this.getWindow().getWindowID(), (window, x, y) -> {
            this.mouseListeners.forEach(listener -> listener.mouseMove(window, x, y));
        });
    }

    private void loop() {
        glClearColor(20 / 255f, 20 / 255f, 20 / 255f, 0f);

        double frame_cap = 1.0 / 144.0;
        double time = Timer.getTimeInSeconds();
        double unprocessed = 0;

        double frame_time = 0;
        int frames = 0;

        while (!this.getWindow().shouldClose()) {
            double time_2 = Timer.getTimeInSeconds();
            double passed = time_2 - time;
            unprocessed += passed;
            frame_time += passed;
            time = time_2;

            glfwPollEvents();

            if (unprocessed >= frame_cap) {
                this.render((float) Math.min(0.08F, unprocessed));

                unprocessed = 0;
                frames++;
            }

            if (frame_time >= 1.0) {
                frame_time = 0;
                Game.DEBUG_FPS = frames;
                frames = 0;
                System.out.println("FPS: " + Game.DEBUG_FPS);
            }

            Thread.yield();
        }

        Assets.deleteAssets();
        this.soundManager.cleanup();
    }

    private void render(float delta) {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.sceneBuffer.getBufferId());

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (!this.pause) {
            this.world.update(delta, this.window, this.renderSystem.getCamera());
        }

        this.world.fixCamera(this.renderSystem.getCamera(), this.window);
        this.world.render(delta, this.tileRenderer, RenderSystem.getStack(), this.renderSystem.getCamera(), this.window);

        this.gui.render();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        glBindTexture(GL_TEXTURE_2D, this.sceneBuffer.getTextureId());
        RenderSystem.getStack().push();
        RenderSystem.getStack().scale(this.window.getWidthFramebuffer(), -this.window.getHeightFramebuffer(), 1);
        RenderSystem.getStack().translate(0, -1, 0);

        RenderSystem.getStack().setShader(GameShaders.COLOR_TEXTURE);
        RenderSystem.getStack().sampler(0);
        RenderSystem.getStack().color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.getStack().setUniform("tex_modifier", new Matrix4f().scale(1.0F));
        RenderSystem.getStack().applyMatrix();

        Assets.getModel().render();

        RenderSystem.getStack().color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.getStack().pop();

        this.world.postProcess(delta, RenderSystem.getStack());

        this.getWindow().swapBuffers();
    }

    public ArrayList<KeyListener> getKeyListeners() {
        return this.keyListeners;
    }

    public ArrayList<MouseListener> getMouseListeners() {
        return this.mouseListeners;
    }

    public static Game getInstance() {
        return Game.INSTANCE;
    }

    public Window getWindow() {
        return this.window;
    }

    public RenderSystem getRenderSystem() {
        return this.renderSystem;
    }

    public Input getInput() {
        return this.input;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Gui getGui() {
        return this.gui;
    }

    public World getWorld() {
        return this.world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public TileRenderer getTileRenderer() {
        return this.tileRenderer;
    }

    public GameDialogHandler getGameDialogHandler() {
        return this.gameDialogHandler;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public boolean isPause() {
        return this.pause;
    }

    public SceneBuffer getSceneBuffer() {
        return this.sceneBuffer;
    }

    public void setSceneBuffer(SceneBuffer sceneBuffer) {
        this.sceneBuffer = sceneBuffer;
    }

    public Screen getScreen() {
        return this.screen;
    }
}
