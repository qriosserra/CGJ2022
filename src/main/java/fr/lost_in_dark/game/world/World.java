package fr.lost_in_dark.game.world;

import fr.lost_in_dark.game.Game;
import fr.lost_in_dark.game.Window;
import fr.lost_in_dark.game.assets.Assets;
import fr.lost_in_dark.game.collision.AABB;
import fr.lost_in_dark.game.collision.Collision;
import fr.lost_in_dark.game.entity.Entity;
import fr.lost_in_dark.game.objects.DeathObject;
import fr.lost_in_dark.game.objects.GameObject;
import fr.lost_in_dark.game.objects.TileObject;
import fr.lost_in_dark.game.objects.WarpObject;
import fr.lost_in_dark.game.render.MatrixStack;
import fr.lost_in_dark.game.render.camera.Camera;
import fr.lost_in_dark.game.render.shader.GameShaders;
import fr.lost_in_dark.game.render.texture.TileSet;
import fr.lost_in_dark.game.utils.Color;
import fr.lost_in_dark.game.utils.IOUtil;
import fr.lost_in_dark.game.utils.RenderUtil;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class World {

    private Object[] tilesLayers;
//    private AABB[][] boundingBoxes;
    private int width;
    private int height;
    private int scale;

    private Matrix4f world;

    private String levelName;
    private String fromName;

    private CopyOnWriteArrayList<GameObject> gameObjects;

    private TileSet[] tileSets;

    private String teleportTo = "";
    private long transitionTime = -1;
    private AABB transitionBoundingBox = null;
    private Collision transitionCollision = null;
    private String transitionOrigin = "";

    private long endTime = 0L;

    public World(String levelName, String from) throws IOException {
        this.scale = 32;

        this.world = new Matrix4f().setTranslation(new Vector3f(0));
        this.world.scale(this.scale);

        this.loadLevel(levelName, from);
    }

    public void loadLevel(String levelName, String from) throws IOException {
        this.levelName = levelName;
        this.fromName = from;
        this.gameObjects = new CopyOnWriteArrayList<>();

        this.jsonToTiles("levels/" + this.levelName + ".json", from);
    }

    public void jsonToTiles(String path, String from) throws IOException {
        JSONObject object = new JSONObject(IOUtil.readStreamFile(path));
        JSONArray layers = object.getJSONArray("layers");

        this.width = object.getInt("width");
        this.height = object.getInt("height");
        int tileWidth = object.getInt("tilewidth");
        int tileHeight = object.getInt("tileheight");

        JSONArray tilesetsJSON = object.getJSONArray("tilesets");

        this.tileSets = new TileSet[tilesetsJSON.length()];
        for (int i = 0; i < tilesetsJSON.length(); i++) {
            JSONObject tileSet = tilesetsJSON.getJSONObject(i);
            String source = tileSet.getString("source");
            String[] args = source.split("/");
            source = args[args.length - 1].split("\\.")[0];
            this.tileSets[i] = TileSet.getTileSet(source);
        }

        this.tilesLayers = new Object[layers.length()];

        int startX = 0;
        int startY = 0;

        for (int layerIndex = 0; layerIndex < layers.length(); layerIndex++) {
            JSONObject layer = layers.getJSONObject(layerIndex);

            String type = layer.getString("type");

            if (type.equals("tilelayer")) {
                int width = layer.getInt("width");
                int height = layer.getInt("height");
                this.tilesLayers[layerIndex] = new Tile[width * height];

                if (layer.has("data")) {
                    this.readData(layer, width, height, layerIndex, 0, 0);
                } else if (layer.has("chunks")) {
                    startX = 0;
                    startY = 0;

                    JSONArray chunks = layer.getJSONArray("chunks");
                    for (int i = 0; i < chunks.length(); i++) {
                        JSONObject chunk = chunks.getJSONObject(i);

                        int x = chunk.getInt("x");
                        int y = chunk.getInt("y");

                        this.readData(chunk, chunk.getInt("width"), chunk.getInt("height"), layerIndex, x + startX, y + startY);
                    }
                }

            }

            if (type.equals("objectgroup")) {
                this.tilesLayers[layerIndex] = new ArrayList<>();

                JSONArray objects = layer.getJSONArray("objects");
                for (int i = 0; i < objects.length(); i++) {
                    JSONObject gameObject = objects.getJSONObject(i);

                    String objectType = gameObject.getString("type");
                    String objectName = gameObject.getString("name");

                    if (objectName.contains("#") && !objectName.equals(objectName.split("#")[0] + "#" + from)) continue;

                    objectName = objectName.split("#")[0];

                    float x = gameObject.getFloat("x") / (float) tileWidth - startX;
                    float y = gameObject.getFloat("y") / (float) tileHeight - startY;
                    float width = gameObject.getFloat("width") / (float) tileWidth;
                    float height = gameObject.getFloat("height") / (float) tileHeight;

                    if (gameObject.has("gid")) {
                        y -= 1;
                        TileObject tileObject = new TileObject(this.getTileByID(gameObject.getInt("gid") - 1), x, y, x + width / 2.0F, y + height / 2.0F, width / 2.0F, height / 2.0F);
                        tileObject.parseJSON(gameObject);
                        ((ArrayList<GameObject>) this.tilesLayers[layerIndex]).add(tileObject);
                        continue;
                    }

                    if (objectType.equals("entity")) {
                        String[] params = new String[0];

                        if (gameObject.has("properties")) {
                            JSONArray array = gameObject.getJSONArray("properties");
                            params = new String[array.length()];
                            for (int j = 0; j < array.length(); j++) {
                                params[j] = array.getJSONObject(j).getString("value");
                            }
                        }

                        this.spawnEntity(objectName, x, y, params);
                    }

                    if (objectType.equals("warp")) {
                        ((ArrayList<GameObject>) this.tilesLayers[layerIndex]).add(new WarpObject(objectName, x + width / 2.0F, y + height / 2.0F, width / 2.0F, height / 2.0F));
                    }


                    if (objectType.equals("death")) {
                        ((ArrayList<GameObject>) this.tilesLayers[layerIndex]).add(new DeathObject(x + width / 2.0F, y + height / 2.0F, width / 2.0F, height / 2.0F));
                    }
                }
            }
        }
    }

    private void readData(JSONObject layer, int width, int height, int layerIndex, int startX, int startY) {
        JSONArray data = layer.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            int tileID = data.getInt(i) - 1;
            boolean flippedHorizontally = false;
            boolean flippedVertically = false;
            if (tileID != -1 && (tileID >> 31 & 1) == 1) {
                flippedHorizontally = true;
                tileID = tileID & ~(1 << 31);
            }

            if (tileID != -1 && (tileID >> 30 & 1) == 1) {
                flippedVertically = true;
                tileID = tileID & ~(1 << 30);
            }

            Tile tile = this.getTileByID(tileID);
            if (tile != null) {
                tile.setFlippedHorizontally(flippedHorizontally);
                tile.setFlippedVertically(flippedVertically);
            }
            this.setTile(layerIndex, startX + i % width, startY + i / width, tile);
        }
    }

    public void spawnEntity(String name, float x, float y, String[] params) throws IOException {
        Class<? extends Entity> entityClass = Entity.getEntityByName(name);
        if (entityClass == null) return;

        try {
            entityClass.getMethod("spawn", float.class, float.class, World.class, String[].class).invoke(null, x, y, this, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void render(float delta, TileRenderer renderer, MatrixStack stack, Camera camera, Window window) {
        int posX = (int) (camera.getPosition().x) / this.scale;
        int posY = (int) (camera.getPosition().y) / this.scale;

        int bgIndex = 0;
        switch (this.levelName) {
            case "transition_dirt-stone":
            case "level_colere":
                bgIndex = 1;
                break;

            case "transition_stone-nether":
            case "level_marchandage":
                bgIndex = 2;
                break;

            case "transition_end-end":
            case "level_depression":
                bgIndex = 3;
                break;
        }

        stack.setShader(GameShaders.COLOR_TEXTURE);
        stack.sampler(0);

        stack.push();
        stack.scale(window.getScaledWidth(), window.getScaledHeight(), 1.0F);

        Assets.bgs[bgIndex].bind(0);
        Assets.getModel().render();

        stack.pop();

        LinkedHashMap<Object, Vector2i> skip = new LinkedHashMap<>();

        for (int layer = 0; layer < this.tilesLayers.length; layer++) {
            if (this.tilesLayers[layer] instanceof Tile[]) {
                for (int x = -1; x < window.getScaledWidth() / this.scale + 2; x++) {
                    for (int y = -1; y < window.getScaledHeight() / this.scale + 2; y++) {
                        Tile tile = this.getTile(layer, x - posX, y - posY);
                        if (tile == null) continue;

                        if (tile.getZIndex() == 0) renderer.renderTile(tile, x - posX, y - posY, stack, this.world, camera);
                        else skip.put(tile, new Vector2i(x - posX, y - posY));
                    }
                }
            } else if (this.tilesLayers[layer] instanceof List) {
                for (GameObject object : (List<GameObject>) this.tilesLayers[layer]) {
                    if (object.getZIndex() == 0) object.render(stack, camera, this);
                    else skip.put(object, null);
                }
            }
        }

        for (GameObject object : this.gameObjects) {
            object.render(stack, camera, this);
        }

        for (Map.Entry<Object, Vector2i> entry : skip.entrySet()) {
            if (entry.getKey() instanceof Tile) renderer.renderTile((Tile) entry.getKey(), entry.getValue().x, entry.getValue().y, stack, this.world, camera);
            else if (entry.getKey() instanceof GameObject) ((GameObject) entry.getKey()).render(stack, camera, this);
        }

        stack.stopShader();
    }

    public void update(float delta, Window window, Camera camera) {
        List<GameObject> objects = new ArrayList<>();

        for (Object tilesLayer : this.tilesLayers) {
            if (tilesLayer instanceof List) {
                objects.addAll((List<GameObject>) tilesLayer);
                for (GameObject object : (List<GameObject>) tilesLayer) {
                    object.update(delta, window, camera, this);
                }
            }
        }

        objects.addAll(this.gameObjects);
        for (GameObject object : this.gameObjects) {
            object.update(delta, window, camera, this);
        }

        for (int i = 0; i < objects.size(); i++) {
            for (int j = i + 1; j < objects.size(); j++) {
                objects.get(i).collideWithObject(objects.get(j));
            }

            objects.get(i).collideWithTiles(this);
        }
    }

    public void postProcess(float delta, MatrixStack stack) {
        Game.getInstance().getPlayer().renderPost(stack, this);

        this.renderTransition(delta, stack);

        if (this.levelName.equalsIgnoreCase("maison_interieure")) {
            if (this.endTime <= 0L) this.endTime = System.currentTimeMillis();

            if (this.endTime + 2000L < System.currentTimeMillis()) {
                float progress = (System.currentTimeMillis() - (this.endTime + 2000L)) / 1000.0F;

                RenderUtil.drawRect(stack, 0, 0, Game.getInstance().getWindow().getWidthFramebuffer(), Game.getInstance().getWindow().getHeightFramebuffer(), new Color(0, 0, 0, Math.min(1, progress)).getRGB());

                stack.push();
                stack.translate(0, (int) (Game.getInstance().getWindow().getHeightFramebuffer() * (1.0F - (progress / 5.0F))), 0);
                stack.scale(4.0F, 4.0F, 1.0F);

                stack.setShader(GameShaders.COLOR_TEXTURE);
                Game.getInstance().getGui().getFont().render(stack, "Alexandre : quelques ajouts", 30, 0);
                Game.getInstance().getGui().getFont().render(stack, "Quentin : joue à osu", 30, 25);
                Game.getInstance().getGui().getFont().render(stack, "Hugo : dit quand on mange", 30, 50);
                Game.getInstance().getGui().getFont().render(stack, "Mateo : convertit des fichiers", 30, 75);
                Game.getInstance().getGui().getFont().render(stack, "Louis : cuisine", 30, 100);
                stack.pop();
            }
        }
    }

    private void renderTransition(float delta, MatrixStack stack) {
        if (this.transitionTime == -1) return;
        float progress = (this.transitionTime - System.currentTimeMillis()) / 600.0F;
        if (!this.teleportTo.isEmpty()) progress = 1.0F - progress;

        Vector3f playerPos = Game.getInstance().getPlayer().getTransform().pos;
        Vector3f playerScale = Game.getInstance().getPlayer().getTransform().scale;
        float windowScaledWidth = Game.getInstance().getWindow().getScaledWidth();
        float windowScaledHeight = Game.getInstance().getWindow().getScaledHeight();
        float windowFrameWidth = Game.getInstance().getWindow().getWidthFramebuffer();
        float windowFramedHeight = Game.getInstance().getWindow().getHeightFramebuffer();

        Vector3f lerp = Game.getInstance().getRenderSystem().getCamera().getPosition().sub(playerPos.mul(-this.getScale(), new Vector3f()).add(windowScaledWidth / 2.0F, windowScaledHeight / 2.0F, 0.0F), new Vector3f()).div(windowScaledWidth, windowScaledHeight, 1);

        Game.getInstance().getSceneBuffer().bindTexture();

        stack.push();
        stack.scale(windowFrameWidth, -windowFramedHeight, 1);
        stack.translate(0, -1, 0);

        stack.setShader(GameShaders.ROUNDED_MASK);
        stack.sampler(0);
        stack.setUniform("radius", (float) (Math.max(0, Math.min(1, 1.0F - progress)) * Math.sqrt(windowFrameWidth * windowFrameWidth + windowFramedHeight * windowFramedHeight)));
        stack.setUniform("center", new Vector2f(0.5F + lerp.x + ((playerScale.x / 2.0F) / windowScaledWidth) * this.getScale(), 0.5F - lerp.y - ((playerScale.y / 2.0F) / windowScaledHeight) * this.getScale()));
        stack.setUniform("resolution", new Vector2f(windowFrameWidth, windowFramedHeight));
        stack.setUniform("gradient", 0);
        stack.setUniform("color", new Vector3f(0, 0, 0));
        stack.applyMatrix();

        Assets.getModel().render();

        stack.color(1.0F, 1.0F, 1.0F, 1.0F);
        stack.pop();

        if (this.transitionCollision != null) {
            AABB box = Game.getInstance().getPlayer().getBoundingBox();
            AABB box2 = this.transitionBoundingBox;

            Vector2f correction = box2.getCenter().sub(box.getCenter(), new Vector2f());
            if (this.transitionCollision.distance.x > this.transitionCollision.distance.y) {
                if (correction.x > 0) {
                    box.getCenter().add(delta / 3F, 0);
                } else {
                    box.getCenter().add(-delta / 3F, 0);
                }
            } else {
                if (correction.y > 0) {
                    box.getCenter().add(0, delta / 3F);
                } else {
                    box.getCenter().add(0, -delta / 3F);
                }
            }

            if (!Game.getInstance().getPlayer().isOnGround()) {
                //Vector2f pos = new Vector2f(0, 0);
                //Game.getInstance().getPlayer().updateGravity(pos, delta / 3.0F);
                //box.getCenter().add(pos.x, pos.y);
            }

            Game.getInstance().getPlayer().getTransform().pos.set(Game.getInstance().getPlayer().getBoundingBox().getCenter().sub(Game.getInstance().getPlayer().getBoundingBoxCenterX(), Game.getInstance().getPlayer().getBoundingBoxCenterY(), new Vector2f()), 0);
        }

        if (progress < 0 || progress > 1.2F) {
            this.transitionTime = -1;
            this.transitionCollision = null;

            if (!this.teleportTo.isEmpty()) {
                try {
                    Game.getInstance().setPause(false);
                    Game.getInstance().setWorld(new World(this.teleportTo, this.transitionOrigin));
                    Game.getInstance().getWorld().setTransitionTime(System.currentTimeMillis() + 600L);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void fixCamera(Camera camera, Window window) {
        Vector3f pos = camera.getPosition();

        float w = -this.width * this.scale + window.getScaledWidth();
        float h = -this.height * this.scale + window.getScaledHeight();

        pos.x = Math.min(pos.x, 0);
        pos.x = Math.max(pos.x, w);

        pos.y = Math.min(pos.y, 0);
        pos.y = Math.max(pos.y, h);
    }

    public List<GameObject> getGameObjects() {
        return this.gameObjects;
    }

    public void setTile(int layer, int x, int y, Tile tile) {
        if (layer < 0 || layer > this.tilesLayers.length) return;
        if (x < 0 || x >= this.width) return;
        if (y < 0 || y >= this.height) return;
        if (!(this.tilesLayers[layer] instanceof Tile[])) return;
        if (x + y * this.width >= ((Tile[]) this.tilesLayers[layer]).length) return;

        ((Tile[]) this.tilesLayers[layer])[x + y * this.width] = tile;
    }

    public Tile getTile(int layer, int x, int y) {
        if (layer < 0 || layer > this.tilesLayers.length) return null;
        if (x < 0 || x >= this.width) return null;
        if (y < 0 || y >= this.height) return null;
        if (!(this.tilesLayers[layer] instanceof Tile[])) return null;
        if (x + y * this.width >= ((Tile[]) this.tilesLayers[layer]).length) return null;

        return ((Tile[]) this.tilesLayers[layer])[x + y * this.width];
    }

    public Tile getTileByID(int tileID) {
        for (TileSet tileSet : this.tileSets) {
            if (tileID >= tileSet.getTiles().length) continue;
            return tileSet.getTileByID(tileID);
        }
        return null;
    }

//    public AABB getTileBoundingBox(int layer, int x, int y) {
//        if (layer < 0 || layer > this.tilesLayers.length) return null;
//        if (x < 0 || x >= this.width) return null;
//        if (y < 0 || y >= this.height) return null;
//
//        return this.boundingBoxes[layer][x + y * this.width];
//    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getScale() {
        return this.scale;
    }

    public Matrix4f getWorldMatrix() {
        return this.world;
    }

    public int getLayers() {
        return this.tilesLayers.length;
    }

    public String getLevelName() {
        return this.levelName;
    }

    public void switchWorld(String world, Collision collision, AABB boundingBox) {
        this.switchWorld(world, collision, boundingBox, this.levelName);
    }

    public void switchWorld(String world, Collision collision, AABB boundingBox, String origin) {
        this.teleportTo = world;
        this.transitionCollision = collision;
        this.transitionOrigin = origin;
        this.transitionBoundingBox = boundingBox;
        this.transitionTime = System.currentTimeMillis() + 600;
    }

    public void setTransitionTime(long transitionTime) {
        this.transitionTime = transitionTime;
    }

    public long getTransitionTime() {
        return this.transitionTime;
    }

    public String getFromName() {
        return this.fromName;
    }
}
