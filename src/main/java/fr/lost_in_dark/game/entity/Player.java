package fr.lost_in_dark.game.entity;

import fr.lost_in_dark.game.Game;
import fr.lost_in_dark.game.Window;
import fr.lost_in_dark.game.collision.Collision;
import fr.lost_in_dark.game.listeners.KeyListener;
import fr.lost_in_dark.game.objects.GameObject;
import fr.lost_in_dark.game.render.MatrixStack;
import fr.lost_in_dark.game.render.camera.Camera;
import fr.lost_in_dark.game.render.shader.GameShaders;
import fr.lost_in_dark.game.render.texture.SpriteSheet;
import fr.lost_in_dark.game.world.World;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends Entity implements KeyListener {

    private boolean gliding = false;

    private long attackTime = 0L;
    private final long attackDuration = 333L;
    private final long attackCooldown = 1000L;

    public Player() {
        super(10, 14 / 16.0F / 2.0F, 24 / 16.0F / 2.0F, 14 / 16.0F / 2.0F, 24 / 16.0F / 2.0F);
        this.loadAnim();

        this.getScale().set(1.0F, 26 / 16.0F, 1.0F);

        this.setMovable(true);
    }

    public void loadAnim() {
        SpriteSheet character = new SpriteSheet("characters/personnage_animation.png");

        // IDLE RIGHT
        loadAnim(character, 0, 0, 0, 2, 2, 16, 26);
        // IDLE LEFT
        loadAnimReverse(character, 1, 64, 0, 2, 2, 16, 26, 64);

        // SPRINT RIGHT
        loadAnim(character, 2, 0, 26, 4, 4, 16, 26);
        // SPRINT LEFT
        loadAnimReverse(character, 3, 64, 26, 4, 4, 16, 26, 64);

        // JUMP RIGHT
        loadAnim(character, 4, 0, 26 * 2, 3, 6, 16, 26);
        // JUMP LEFT
        loadAnimReverse(character, 5, 64, 26 * 2, 3, 6, 16, 26, 64);

        // ATTACK RIGHT
        loadAnim(character, 6, 0, 26 * 3, 4, 12, 16, 26);
        // ATTACK LEFT
        loadAnimReverse(character, 7, 64, 26 * 3, 4, 12, 16, 26, 64);

        // WALL RIGHT
        loadAnim(character, 8, 0, 26 * 4, 1, 1, 16, 26);
        // WALL LEFT
        loadAnimReverse(character, 9, 64, 26 * 4, 1, 1, 16, 26, 64);
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        if (this.hp <= 0) {
            this.hp = 3;

            try {
                Game.getInstance().getWorld().loadLevel(Game.getInstance().getWorld().getLevelName(), Game.getInstance().getWorld().getFromName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Vector2f movement = new Vector2f();
        float speed = delta * 8;

        this.setSprinting(Game.getInstance().getInput().isKeyDown(GLFW_KEY_LEFT_SHIFT));

        boolean sprintLeft = false;
        boolean sprintRight = false;

        if (Game.getInstance().getInput().isKeyDown(GLFW_KEY_SPACE)) {
            if (!this.isAttacking() && this.canAttack()) {
                this.attackTime = System.currentTimeMillis();
                this.getAnimations()[6].setPointer(0);
                this.getAnimations()[7].setPointer(0);
            }
        }

        if (this.isAttacking()) {
            if (this.getCurrentAnimation() % 2 == 0) this.boundingBoxCenterX = 14 / 16.0F;
            else this.boundingBoxCenterX = 0.0F;

            this.boundingBoxWidth = 14 / 16.0F;
        } else {
            this.boundingBoxCenterX = 14 / 16.0F / 2.0F;
            this.boundingBoxWidth = 14 / 16.0F / 2.0F;
        }

        if (Game.getInstance().getInput().isKeyDown(GLFW_KEY_RIGHT)) {
            sprintRight = true;
            movement.add(speed, 0);
        }

        if (Game.getInstance().getInput().isKeyDown(GLFW_KEY_LEFT)) {
            sprintLeft = true;
            movement.add(-speed, 0);
        }

        if (sprintRight && movement.x != 0) this.useAnimation(2);
        else if (sprintLeft && movement.x != 0) this.useAnimation(3);

        if (this.isJumping()) {
            if (this.getCurrentAnimation() % 2 == 0) this.useAnimation(4);
            else this.useAnimation(5);

            if (this.jumpDirection != 0.0F) {
                movement.set(speed * 1.5F * this.jumpDirection, movement.y);
            }

            if (this.collideTop) {
                this.jumpTime = System.currentTimeMillis() - this.jumpDuration;
            }

            float progress = (System.currentTimeMillis() - this.jumpTime) / (float) this.jumpDuration;
            movement.add(0, -(speed * 2.0F) * (1.0F - progress));
        } else if (this.canWallJump && (this.collideLeft || this.collideRight)) {
            if (this.getCurrentAnimation() % 2 == 0) this.useAnimation(9);
            else this.useAnimation(8);

            this.lastGroundTime = System.currentTimeMillis();
        } else {
            this.canWallJump = true;
            this.updateGravity(movement, speed);

            if (this.isOnGround()) {
                this.canWallJump = false;

                if (this.getCurrentAnimation() > 3) {
                    if (this.getCurrentAnimation() % 2 == 0) this.useAnimation(0);
                    else this.useAnimation(1);
                }
            }
        }

        if (this.isAttacking()) {
            if (this.getCurrentAnimation() % 2 == 0) this.useAnimation(6);
            else this.useAnimation(7);
        }

        if (this.isOnGround() && !this.isJumping()) {
            this.setCanJump(true);
        }

        if (Game.getInstance().getInput().isKeyDown(GLFW_KEY_UP)) {
            this.jump();
        } else {
            this.setCanJump(true);
        }

        this.move(movement);
        if (movement.x == 0 && movement.y >= 0) {
            if (this.getCurrentAnimation() == 2) this.useAnimation(0);
            else if (this.getCurrentAnimation() == 3) this.useAnimation(1);
        }

        if (this.isHit()) {
            this.visible = (int) ((System.currentTimeMillis() - this.hitTime) / (float) this.hitDuration * 10) % 2 == 0;
        } else {
            this.visible = true;
        }

        camera.getPosition().lerp(this.transform.pos.add(this.transform.scale.x / 2.0F, this.transform.scale.y / 2.0F, 0, new Vector3f()).mul(-world.getScale()).add(window.getScaledWidth() / 2.0F, window.getScaledHeight() / 2.0F, 0.0F), delta * 4);
    }

    @Override
    public void onCollideObject(GameObject object, Collision collision) {
        if (this.isHit()) {
            collision.distance.set(0, collision.distance.y);
        }
    }

    public static void spawn(float x, float y, World world, String[] args) {
        Vector3f lerp = Game.getInstance().getRenderSystem().getCamera().getPosition().sub(Game.getInstance().getPlayer().transform.pos.mul(-world.getScale(), new Vector3f()).add(Game.getInstance().getWindow().getScaledWidth() / 2.0F, Game.getInstance().getWindow().getScaledHeight() / 2.0F, 0.0F), new Vector3f());

        Game.getInstance().getPlayer().hp = 3;
        Game.getInstance().getPlayer().onGround = false;
        Game.getInstance().getPlayer().lastGroundTime = 0;
        Game.getInstance().getPlayer().jumpTime = 0;
        Game.getInstance().getPlayer().setPosCentered(x, y);

        Game.getInstance().getRenderSystem().getCamera().getPosition().set(Game.getInstance().getPlayer().transform.pos.mul(-world.getScale(), new Vector3f()).add(Game.getInstance().getWindow().getScaledWidth() / 2.0F, Game.getInstance().getWindow().getScaledHeight() / 2.0F, 0.0F));
        Game.getInstance().getRenderSystem().getCamera().getPosition().add(lerp);

        if (!world.getGameObjects().contains(Game.getInstance().getPlayer())) {
            world.getGameObjects().add(Game.getInstance().getPlayer());
        }
    }

    @Override
    public void updateGravity(Vector2f movement, float speed) {
        float progress = (System.currentTimeMillis() - this.lastGroundTime) / (float) this.jumpDuration;
        if (this.lastGroundTime <= 0) progress = 0.1F;

        if (Game.getInstance().getInput().isKeyDown(GLFW_KEY_UP)
            && !this.canJump && !this.doubleJump) {
            if (progress > 1.0F) this.lastGroundTime = System.currentTimeMillis() - this.jumpTime;

            movement.div(1F + (1.0F - Math.min(1.0F, progress / 2.0F)), 1.0F);
            progress = 0.3F * Math.min(1.0F, progress);
            this.canWallJump = false;

            if (this.getCurrentAnimation() % 2 == 0) this.useAnimation(4);
            else this.useAnimation(5);

            this.gliding = true;
        } else if (this.gliding) {
            this.gliding = false;
            this.lastGroundTime = System.currentTimeMillis();
            progress = 0.0F;
        }

        movement.add(0, (speed * 2.0F) * Math.max(0, Math.min(progress, 1)));

        if (this.lastGroundTime <= 0) this.lastGroundTime = System.currentTimeMillis() + 300L;
    }

    @Override
    public void render(MatrixStack stack, Camera camera, World world) {
        stack.setShader(GameShaders.GEMME);
        if (this.hp >= 3) stack.color(0.0F, 0.95F, 0.0F, 1.0F);
        else if (this.hp >= 2) stack.color(1.0F, 0.75F, 0.0F, 1.0F);
        else stack.color(1.0F, 0.0F, 0.0F, 1.0F);

        super.render(stack, camera, world);

        stack.setShader(GameShaders.COLOR_TEXTURE);
    }

    public boolean canAttack() {
        return this.attackTime + this.attackCooldown < System.currentTimeMillis();
    }

    public boolean isAttacking() {
        return this.attackTime + this.attackDuration > System.currentTimeMillis();
    }

    public void renderPost(MatrixStack stack, World world) {
//        Vector3f playerPos = this.transform.pos;
//        Vector3f playerScale = this.transform.scale;
//        float windowScaledWidth = Game.getInstance().getWindow().getScaledWidth();
//        float windowScaledHeight = Game.getInstance().getWindow().getScaledHeight();
//        float windowFrameWidth = Game.getInstance().getWindow().getWidthFramebuffer();
//        float windowFramedHeight = Game.getInstance().getWindow().getHeightFramebuffer();
//
//        Vector3f lerp = Game.getInstance().getRenderSystem().getCamera().getPosition().sub(playerPos.mul(-world.getScale(), new Vector3f()).add(windowScaledWidth / 2.0F, windowScaledHeight / 2.0F, 0.0F), new Vector3f()).div(windowScaledWidth, windowScaledHeight, 1);
//
//        stack.push();
//        stack.unbindImage();
//        stack.scale(windowFrameWidth, windowFramedHeight, 1);
//
//        stack.setShader(GameShaders.ROUNDED_MASK);
//        stack.sampler(0);
//        stack.setUniform("radius", (float) (0F * Math.sqrt(windowFrameWidth * windowFrameWidth + windowFramedHeight * windowFramedHeight)));
//        stack.setUniform("center", new Vector2f(0.5F + lerp.x + ((playerScale.x / 2.0F) / windowScaledWidth) * world.getScale(), 0.5F + lerp.y + ((playerScale.y / 2.0F) / windowScaledHeight) * world.getScale()));
//        stack.setUniform("resolution", new Vector2f(windowFrameWidth, windowFramedHeight));
//        stack.setUniform("gradient", 1);
//        stack.applyMatrix();
//
//        Assets.getModel().render();
//
//        stack.color(1.0F, 1.0F, 1.0F, 1.0F);
//        stack.pop();
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
}
