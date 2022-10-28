package fr.lost_in_dark.game.render.texture;

import fr.lost_in_dark.game.collision.AABB;
import fr.lost_in_dark.game.utils.IOUtil;
import fr.lost_in_dark.game.world.Tile;
import org.joml.Vector2f;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class TileSet {

    private static HashMap<String, TileSet> tileSets = new HashMap<>();
    public static TileSet TILES = new TileSet(new SpriteSheet("tiles.png"), 16, 16);
    public static TileSet BLOCKS = new TileSet(new SpriteSheet("blocks.png"), 16, 16);

    static {
        TileSet.tileSets.put("tiles", TileSet.TILES);
        TileSet.tileSets.put("blocks", TileSet.BLOCKS);
    }

    private final SpriteSheet spriteSheet;
    private final int tileWidth;
    private final int tileHeight;

    private final Tile[] tiles;

    public TileSet(SpriteSheet spriteSheet, int tileWidth, int tileHeight) {
        this.spriteSheet = spriteSheet;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;

        this.tiles = new Tile[(spriteSheet.getTexture().getWidth() / tileWidth) * (spriteSheet.getTexture().getHeight() / tileHeight)];

        for (int i = 0; i < this.tiles.length; i++) {
            int u = i % (spriteSheet.getTexture().getWidth() / tileWidth) * tileWidth;
            int v = i / (spriteSheet.getTexture().getWidth() / tileWidth) * tileHeight;
            this.tiles[i] = new Tile(this.spriteSheet, i, u, v, tileWidth, tileHeight);
        }

        this.parseJSON();
    }

    public void parseJSON() {
        JSONObject object = new JSONObject(IOUtil.readStreamFile(spriteSheet.getTexture().getImagePath().split("\\.")[0] + ".json"));

        if (!object.has("tiles")) return;

        JSONArray array = object.getJSONArray("tiles");
        for (int i = 0; i < array.length(); i++) {
            JSONObject tile = array.getJSONObject(i);
            int id = tile.getInt("id");

            if (tile.has("objectgroup")) {
                JSONArray objects = tile.getJSONObject("objectgroup").getJSONArray("objects");

                for (int j = 0; j < objects.length(); j++) {
                    JSONObject aabb = objects.getJSONObject(j);
                    float x = aabb.getInt("x") / 16.0F;
                    float y = aabb.getInt("y") / 16.0F;
                    float width = aabb.getInt("width") / 16.0F;
                    float height = aabb.getInt("height") / 16.0F;
                    this.tiles[id].getCollisions().add(new AABB(new Vector2f(x + width / 2.0F, y + height / 2.0F), new Vector2f(width / 2.0F, height / 2.0F)));
                }
            }

            if (tile.has("properties")) {
                JSONArray properties = tile.getJSONArray("properties");

                for (int j = 0; j < properties.length(); j++) {
                    JSONObject property = properties.getJSONObject(j);
                    String name = property.getString("name");

                    if (name.equals("z-index")) {
                        this.tiles[id].setZIndex(property.getInt("value"));
                    }
                }
            }
        }
    }

    public SpriteSheet getSpriteSheet() {
        return this.spriteSheet;
    }

    public int getTileWidth() {
        return this.tileWidth;
    }

    public int getTileHeight() {
        return this.tileHeight;
    }

    public Tile getTileByID(int id) {
        if (id < 0 || id >= this.tiles.length) return null;
        return this.tiles[id];
    }

    public Tile[] getTiles() {
        return this.tiles;
    }

    public static TileSet getTileSet(String name) {
        return TileSet.tileSets.get(name);
    }
}
