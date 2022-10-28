package fr.lost_in_dark.game.gui;

import fr.lost_in_dark.game.Game;
import fr.lost_in_dark.game.gui.screen.Screen;
import fr.lost_in_dark.game.listeners.KeyListener;
import fr.lost_in_dark.game.listeners.MouseListener;
import fr.lost_in_dark.game.render.MatrixStack;
import fr.lost_in_dark.game.render.RenderSystem;
import fr.lost_in_dark.game.render.camera.Camera;
import fr.lost_in_dark.game.render.shader.GameShaders;
import fr.lost_in_dark.game.render.texture.Font;
import fr.lost_in_dark.game.render.texture.SpriteSheet;
import org.joml.Matrix4f;

public class Gui implements KeyListener, MouseListener {

    private static Gui INSTANCE;

    private SpriteSheet sheet;
    private Camera camera;

    private Font font;
    private GuiDialog dialog;

    public Gui() {
        Gui.INSTANCE = this;

        this.sheet = new SpriteSheet("tiles.png");

        this.camera = new Camera() {
            @Override
            public void updateOrtho2D() {
                this.getUntransformedProjection().setOrtho2D(0, Game.getInstance().getWindow().getWidthFramebuffer(), Game.getInstance().getWindow().getHeightFramebuffer(), 0);
            }
        };

        this.font = new Font();
        this.dialog = new GuiDialog();
    }

    public void render() {
        Matrix4f target = new Matrix4f();
        this.camera.getUntransformedProjection().translate(0, 0, 0, target);
        RenderSystem.setStack(new MatrixStack(target));

        MatrixStack stack = RenderSystem.getStack();
        stack.setShader(GameShaders.COLOR_TEXTURE);
        stack.color(1.0F, 1.0F, 1.0F, 1.0F);

        stack.push();
        stack.translate(2, Game.getInstance().getWindow().getHeightFramebuffer() - 38, 0);
        stack.scale(36);

//        this.sheet.bind(stack, 208, 0, 36, 36);
//
//        for (int i = 0; i < 10; i++) {
//            stack.applyMatrix();
//            Assets.getModel().render();
//            stack.translate(1, 0, 0);
//        }

        stack.pop();

        this.dialog.render(stack);

        if (Game.getInstance().getScreen() != null) Game.getInstance().getScreen().render(stack, Screen.HANDLER.getX(), Screen.HANDLER.getY());

        stack.stopShader();
    }

    public Camera getCamera() {
        return this.camera;
    }

    public static Gui getInstance() {
        return Gui.INSTANCE;
    }

    public Font getFont() {
        return this.font;
    }

    public GuiDialog getDialog() {
        return this.dialog;
    }

    public SpriteSheet getSheet() {
        return this.sheet;
    }

    @Override
    public void keyPress(long window, int key, int scancode, int mods) {

    }

    @Override
    public void keyRelease(long window, int key, int scancode, int mods) {

    }

    @Override
    public void textInput(long window, String text) {

    }

    @Override
    public void mousePress(long window, int button, int mods) {

    }

    @Override
    public void mouseRelease(long window, int button, int mods) {

    }

    @Override
    public void mouseScroll(long window, double xOffset, double yOffset) {

    }

    @Override
    public void mouseMove(long window, double x, double y) {
        
    }
}
