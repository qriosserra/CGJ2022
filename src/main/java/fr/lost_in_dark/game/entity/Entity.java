package fr.lost_in_dark.game.entity;

import fr.lost_in_dark.game.Game;
import fr.lost_in_dark.game.assets.Assets;
import fr.lost_in_dark.game.objects.GameObject;
import fr.lost_in_dark.game.render.MatrixStack;
import fr.lost_in_dark.game.render.camera.Camera;
import fr.lost_in_dark.game.render.shader.GameShaders;
import fr.lost_in_dark.game.render.shader.Shader;
import fr.lost_in_dark.game.render.texture.SpriteAnimation;
import fr.lost_in_dark.game.render.texture.SpriteSheet;
import fr.lost_in_dark.game.world.World;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public abstract class Entity extends GameObject {

    private static final HashMap<String, Class<? extends Entity>> entitiesName = new HashMap<>();

    protected SpriteAnimation[] animations;
    private int current_animation = 0;
    private boolean sprinting = false;

    private boolean canLeaveMap = false;

    protected int hp = 3;

    protected long hitTime = 0L;
    protected final long hitDuration = 1500L;

    static {
        Entity.entitiesName.put("player", Player.class);
        Entity.entitiesName.put("phantom", PhantomEntity.class);
        Entity.entitiesName.put("tuto", TutoEntity.class);
        Entity.entitiesName.put("gobelin", GoblinEntity.class);
        Entity.entitiesName.put("mechante", WifeEntity.class);
    }

    public Entity(int max_animations, float boundingBoxCenterX, float boundingBoxCenterY, float boundingBoxWidth, float boundingBoxHeight) {
        super(boundingBoxCenterX, boundingBoxCenterY, boundingBoxWidth, boundingBoxHeight);
        this.animations = new SpriteAnimation[max_animations];
    }

    protected void loadAnim(SpriteSheet spriteSheet, int id, int x, int y, int frames, int fps, int width, int height) {
        ArrayList<Vector2i> anim = new ArrayList<>();
        for (int xAnim = 0; xAnim < frames; xAnim++) {
            anim.add(new Vector2i(x + xAnim * width, y));
        }

        this.setAnimation(id, new SpriteAnimation(anim, fps, spriteSheet, width, height));
    }

    protected void loadAnimReverse(SpriteSheet spriteSheet, int id, int x, int y, int frames, int fps, int width, int height, int maxWidth) {
        ArrayList<Vector2i> anim = new ArrayList<>();
        for (int xAnim = 0; xAnim < frames; xAnim++) {
            anim.add(new Vector2i(x + (maxWidth - width) - xAnim * width, y));
        }

        this.setAnimation(id, new SpriteAnimation(anim, fps, spriteSheet, width, height));
    }

    public void render(MatrixStack stack, Camera camera, World world) {
        if (!this.visible) return;

        if (DEBUG) {
            Shader shader = stack.getShader();

            stack.push();
            stack.setShader(GameShaders.COLOR);
            camera.getProjection().mul(world.getWorldMatrix(), stack.getMatrix());

            stack.sampler(0);
            stack.getMatrix().translate(this.transform.pos);
            stack.getMatrix().scale(this.boundingBoxWidth * 2, this.boundingBoxHeight * 2, 1.0F);
            stack.applyMatrix();

            stack.color(1.0F, 0.0F, 0.0F, 1.0F);
            Assets.getModel().render();
            stack.pop();

            stack.color(1.0F, 1.0F, 1.0F, 1.0F);
            stack.setShader(shader);
        }

        stack.push();
        camera.getProjection().mul(world.getWorldMatrix(), stack.getMatrix());

        stack.sampler(0);
        this.transform.getProjection(stack.getMatrix());
        stack.applyMatrix();


        if (this.isHit()) {
            stack.color(1.0F, 0.5F, 0.5F, 1.0F);
        }

        this.animations[this.current_animation].bind(stack);
        Assets.getModel().render();
        stack.pop();

        stack.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void move(Vector2f direction) {
        this.transform.pos.add(new Vector3f(direction, 0));

        if (!this.canLeaveMap) {
            this.transform.pos.max(new Vector3f(0, 0, 0));
            if (Game.getInstance().getWorld() != null) {
                this.transform.pos.min(new Vector3f(Game.getInstance().getWorld().getWidth() - this.boundingBoxWidth * 2, Game.getInstance().getWorld().getHeight() - this.boundingBoxHeight * 2, 0));
            }
        }

        this.updateBoundingBoxPos();
    }

    public void setPos(float x, float y) {
        this.transform.pos.set(x, y, 0);
        this.updateBoundingBoxPos();
    }

    public void setPosCentered(float x, float y) {
        this.transform.pos.set(x - this.transform.scale.x / 2.0F, y - this.transform.scale.y / 2.0F, 0);
        this.updateBoundingBoxPos();
    }

    public Vector3f getScale() {
        return this.transform.scale;
    }

    public void setAnimation(int id, SpriteAnimation animation) {
        this.animations[id] = animation;
    }

    public void useAnimation(int id) {
        this.current_animation = id;
    }

    public int getCurrentAnimation() {
        return this.current_animation;
    }

    public SpriteAnimation getAnimation() {
        return this.animations[this.current_animation];
    }

    public SpriteAnimation[] getAnimations() {
        return this.animations;
    }

    public static Class<? extends Entity> getEntityByName(String name) {
        return Entity.entitiesName.get(name.toLowerCase(Locale.ROOT));
    }

    public void setSprinting(boolean sprinting) {
        this.sprinting = sprinting;
    }

    public boolean isSprinting() {
        return this.sprinting;
    }

    public void setCanLeaveMap(boolean canLeaveMap) {
        this.canLeaveMap = canLeaveMap;
    }

    public boolean canLeaveMap() {
        return this.canLeaveMap;
    }

    public boolean isHit() {
        return this.hitTime + this.hitDuration > System.currentTimeMillis();
    }

    public void hit() {
        if (this.isHit()) return;

        this.hp--;
        this.hitTime = System.currentTimeMillis();
    }

    public void setHP(int hp) {
        this.hp = hp;
    }

    public int getHP() {
        return this.hp;
    }

}
