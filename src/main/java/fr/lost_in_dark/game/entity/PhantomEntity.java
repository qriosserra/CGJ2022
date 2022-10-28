package fr.lost_in_dark.game.entity;

import fr.lost_in_dark.game.Game;
import fr.lost_in_dark.game.Window;
import fr.lost_in_dark.game.render.camera.Camera;
import fr.lost_in_dark.game.render.texture.SpriteSheet;
import fr.lost_in_dark.game.world.World;

public class PhantomEntity extends EntityEnemy {

    private boolean angry = false;

    public PhantomEntity() {
        super(6, 0.5F, 22 / 18.0F / 2, 0.5F, 22 / 18.0F / 2);

        this.getScale().set(1.0F, 26 / 18.0F, 1.0F);

        SpriteSheet phantom = new SpriteSheet("characters/phantom.png");
        this.loadAnim(phantom, 0, 0, 0, 2, 3, 18, 26);
        this.loadAnimReverse(phantom, 1, 0, 0, 2, 3, 18, 26, 108);

        this.loadAnim(phantom, 2, 0, 26, 2, 3, 18, 26);
        this.loadAnimReverse(phantom, 3, 0, 26, 2, 3, 18, 26, 108);

        this.loadAnim(phantom, 4, 0, 26 * 2, 3, 3, 18, 26);
        this.loadAnimReverse(phantom, 5, 0, 26 * 2, 3, 3, 18, 26, 108);
    }

    private void detectAngry() {
        if (Game.getInstance().getPlayer().getTransform().pos.distance(this.transform.pos) < 10) {

            if (Game.getInstance().getPlayer().getTransform().pos.x < this.transform.pos.x && this.direction != -1.0F) return;
            if (Game.getInstance().getPlayer().getTransform().pos.x > this.transform.pos.x && this.direction != 1.0F) return;

            if (Math.abs(this.transform.pos.y - Game.getInstance().getPlayer().getTransform().pos.y) < 0.5F) {
                this.angry = true;
            }
        }
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        this.detectAngry();

        if (this.angry) delta *= 2;

        super.update(delta, window, camera, world);

        if (this.direction < 0) {
            if (this.angry) this.useAnimation(3);
            else this.useAnimation(1);
        } else {
            if (this.angry) this.useAnimation(2);
            else this.useAnimation(0);
        }
    }

    public static void spawn(float x, float y, World world, String[] args) {
        PhantomEntity enemy = new PhantomEntity();
        enemy.setPosCentered(x, y);
        world.getGameObjects().add(enemy);
    }

}
