package fr.lost_in_dark.game.objects;

import fr.lost_in_dark.game.Game;
import fr.lost_in_dark.game.Window;
import fr.lost_in_dark.game.collision.Collision;
import fr.lost_in_dark.game.entity.Player;
import fr.lost_in_dark.game.render.MatrixStack;
import fr.lost_in_dark.game.render.camera.Camera;
import fr.lost_in_dark.game.world.World;

public class WarpObject extends GameObject {

    private final String warpName;

    public WarpObject(String warpName, float boundingBoxCenterX, float boundingBoxCenterY, float boundingBoxWidth, float boundingBoxHeight) {
        super(boundingBoxCenterX, boundingBoxCenterY, boundingBoxWidth, boundingBoxHeight);
        this.warpName = warpName;
    }

    @Override
    public void onCollideObject(GameObject object, Collision collision) {
        if (!(object instanceof Player)) return;

        Game.getInstance().setPause(true);
        Game.getInstance().getWorld().switchWorld(this.warpName, collision, this.getBoundingBox());
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {

    }

    @Override
    public void render(MatrixStack stack, Camera camera, World world) {

    }
}
