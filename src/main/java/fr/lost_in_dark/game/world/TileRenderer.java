package fr.lost_in_dark.game.world;

import fr.lost_in_dark.game.assets.Assets;
import fr.lost_in_dark.game.render.MatrixStack;
import fr.lost_in_dark.game.render.camera.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class TileRenderer {

    public void renderTile(Tile tile, float x, float y, MatrixStack stack, Matrix4f world, Camera camera) {
        if (tile == null) return;

        tile.getSpriteSheet().bind(stack, tile.getU(), tile.getV(), tile.getWidth(), tile.getHeight());

        stack.push();
        Matrix4f tile_pos = new Matrix4f().translate(new Vector3f(x, y, 0));
        if (tile.isFlippedHorizontally()) tile_pos.ortho2D(1.0F, -1.0F, -1.0F, 1.0F).translate(new Vector3f(-1, 0, 0));
        if (tile.isFlippedVertically()) tile_pos.ortho2D(-1.0F, 1.0F,1.0F, -1.0F).translate(new Vector3f(0, -1, 0));

        camera.getProjection().mul(world, stack.getMatrix());
        stack.mul(tile_pos);
        stack.applyMatrix();

        Assets.getModel().render();
        stack.pop();
    }

}
