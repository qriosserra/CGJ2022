package fr.lost_in_dark.game.objects;

import fr.lost_in_dark.game.Game;
import fr.lost_in_dark.game.Window;
import fr.lost_in_dark.game.render.MatrixStack;
import fr.lost_in_dark.game.render.camera.Camera;
import fr.lost_in_dark.game.world.Tile;
import fr.lost_in_dark.game.world.World;

public class TileObject extends GameObject {

    private final Tile tile;
    private final float x;
    private final float y;

    public TileObject(Tile tile, float x, float y, float boundingBoxCenterX, float boundingBoxCenterY, float boundingBoxWidth, float boundingBoxHeight) {
        super(boundingBoxCenterX, boundingBoxCenterY, boundingBoxWidth, boundingBoxHeight);
        this.tile = tile;
        this.x = x;
        this.y = y;

        if (this.tile.getCollisions().size() == 0) {
            this.boundingBoxWidth = 0.0F;
            this.boundingBoxHeight = 0.0F;
            this.boundingBox.getHalf_extent().set(this.boundingBoxWidth, this.boundingBoxHeight);
        }
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {

    }

    @Override
    public void render(MatrixStack stack, Camera camera, World world) {
        Game.getInstance().getTileRenderer().renderTile(this.tile, this.x, this.y, stack, world.getWorldMatrix(), camera);
    }

}
