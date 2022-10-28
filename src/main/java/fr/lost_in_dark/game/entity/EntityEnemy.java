package fr.lost_in_dark.game.entity;

import fr.lost_in_dark.game.Game;
import fr.lost_in_dark.game.Window;
import fr.lost_in_dark.game.collision.Collision;
import fr.lost_in_dark.game.objects.GameObject;
import fr.lost_in_dark.game.render.camera.Camera;
import fr.lost_in_dark.game.world.World;
import org.joml.Vector2f;

public class EntityEnemy extends Entity {

    protected float direction = -1.0F;
    protected long deadTime = 0L;
    protected final long deadDuration = 200L;

    public EntityEnemy(int max_animations, float boundingBoxCenterX, float boundingBoxCenterY, float boundingBoxWidth, float boundingBoxHeight) {
        super(max_animations, boundingBoxCenterX, boundingBoxCenterY, boundingBoxWidth, boundingBoxHeight);
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        if (this.isDead()) {
            if (this.mustDestroy()) {
                Game.getInstance().getWorld().getGameObjects().remove(this);
                return;
            }
            return;
        }

        float speed = delta * 2;

        Vector2f movement = new Vector2f(0, 0);

        if (!this.isOnGround()) {
            //this.updateGravity(movement, delta * 8);
        }

        movement.add(speed * this.direction, 0);
        if (this.direction < 0) {
            this.useAnimation(1);
        } else {
            this.useAnimation(0);
        }

        this.move(movement);

        if (this.direction <= 0 && (this.collideLeft || (this.transform.pos.x <= 0 && !this.canLeaveMap()))) {
            this.direction = 1.0F;
        } else if (this.direction > 0 && (this.collideRight || (this.transform.pos.x >= Game.getInstance().getWorld().getWidth() - this.boundingBoxWidth * 2 && !this.canLeaveMap()))) {
            this.direction = -1.0F;
        }
    }

    public boolean isDead() {
        return this.deadTime != 0;
    }

    public boolean mustDestroy() {
        return this.isDead() && this.deadTime + this.deadDuration < System.currentTimeMillis();
    }

    @Override
    public void onCollideObject(GameObject object, Collision collision) {
        if (!(object instanceof Player)) return;
        if (this.isDead() || this.isHit()) return;

        Player player = (Player) object;
        if (player.isAttacking()) {
            this.hit();

            if (this.hp <= 0) this.deadTime = System.currentTimeMillis();
            return;
        }

        player.hit();
    }

}
