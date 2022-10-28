package fr.lost_in_dark.game.entity;

import fr.lost_in_dark.game.Game;
import fr.lost_in_dark.game.Window;
import fr.lost_in_dark.game.render.camera.Camera;
import fr.lost_in_dark.game.render.texture.SpriteSheet;
import fr.lost_in_dark.game.world.World;

public class GoblinEntity extends EntityEnemy {

    private boolean angry = false;

    public GoblinEntity() {
        super(6, 0.5F, 40 / 16.0F / 2, 0.5F, 40 / 16.0F / 2);

        this.getScale().set(1.0F, 40 / 16.0F, 1.0F);
        this.setHP(2);

        SpriteSheet phantom = new SpriteSheet("characters/goblin.png");
        this.loadAnim(phantom, 0, 0, 0, 3, 3, 16, 40);
        this.loadAnimReverse(phantom, 1, 0, 0, 3, 3, 16, 40, 96);

        this.loadAnim(phantom, 2, 0, 40, 3, 3, 16, 40);
        this.loadAnimReverse(phantom, 3, 0, 40, 3, 3, 16, 40, 96);

        this.loadAnim(phantom, 4, 0, 40 * 2, 3, 3, 16, 40);
        this.loadAnimReverse(phantom, 5, 0, 40 * 2, 3, 3, 16, 40, 96);
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
        GoblinEntity enemy = new GoblinEntity();
        enemy.setPosCentered(x, y);
        world.getGameObjects().add(enemy);
    }

}
