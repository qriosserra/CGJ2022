package fr.lost_in_dark.game.objects;

import fr.lost_in_dark.game.Window;
import fr.lost_in_dark.game.collision.AABB;
import fr.lost_in_dark.game.collision.Collision;
import fr.lost_in_dark.game.entity.Transform;
import fr.lost_in_dark.game.render.MatrixStack;
import fr.lost_in_dark.game.render.camera.Camera;
import fr.lost_in_dark.game.world.Tile;
import fr.lost_in_dark.game.world.World;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class GameObject {

    protected AABB boundingBox;
    protected Transform transform;

    private boolean movable = false;

    protected float boundingBoxCenterX;
    protected float boundingBoxCenterY;
    protected float boundingBoxWidth;
    protected float boundingBoxHeight;

    private int zIndex = 0;

    protected boolean onGround = true;
    protected boolean collideTop = false;
    protected boolean collideRight = false;
    protected boolean collideLeft = false;

    protected boolean doubleJump = false;
    protected long lastGroundTime = 0L;
    protected long jumpTime = 0L;
    protected final long jumpDuration = 300L;
    protected boolean canJump = true;
    protected float jumpDirection = 0.0F;
    protected boolean canWallJump = false;

    protected boolean visible = true;

    protected boolean noClip = false;

    protected static boolean DEBUG = false;

    public GameObject(float boundingBoxCenterX, float boundingBoxCenterY, float boundingBoxWidth, float boundingBoxHeight) {
        this.transform = new Transform();
        this.boundingBoxCenterX = boundingBoxCenterX;
        this.boundingBoxCenterY = boundingBoxCenterY;
        this.boundingBoxWidth = boundingBoxWidth;
        this.boundingBoxHeight = boundingBoxHeight;

        this.boundingBox = new AABB(new Vector2f(this.transform.pos.x + boundingBoxCenterX, this.transform.pos.y + boundingBoxCenterY), new Vector2f(boundingBoxWidth, boundingBoxHeight));
    }

    public void onCollideObject(GameObject object, Collision collision) {

    }

    public void handleDialogEvent(String event) {

    }

    public void collideWithTiles(World world) {
        if (this.isNoClip()) return;

        boolean onGround = false;
        boolean collideTop = false;
        boolean collideLeft = false;
        boolean collideRight = false;

        int w = (int) (this.transform.scale.x * 2 + 1);
        int h = (int) (this.transform.scale.y * 2 + 1);
        List<AABB> boxes = new ArrayList<>();
        for (int layer = 0; layer < world.getLayers(); layer++) {
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    int posX = Math.round(this.transform.pos.x - this.transform.scale.x) + x;
                    int posY = Math.round(this.transform.pos.y - this.transform.scale.y) + y;
                    Tile tile = world.getTile(layer, posX, posY);
                    if (tile == null) continue;

                    for (AABB aabb : tile.getCollisions()) {
                        boxes.add(new AABB(aabb.getCenter().add(posX, posY, new Vector2f()), aabb.getHalf_extent()));
                    }
                }
            }
        }

        for (AABB box : boxes) {
            if (box == null) continue;

            Vector2f length = box.getCenter().sub(this.transform.pos.x + this.boundingBoxCenterX, this.transform.pos.y + this.boundingBoxCenterY, new Vector2f());
            float max_distance = box.getHalf_extent().add(this.boundingBox.getHalf_extent(), new Vector2f()).lengthSquared();

            if (length.lengthSquared() <= max_distance) {
                Collision collision = this.boundingBox.getCollision(box);

                if (collision.intersecting) {
                    float[] collide = this.boundingBox.correctPosition(box, collision);
                    if (collide[0] < 0) collideLeft = true;
                    if (collide[0] > 0) collideRight = true;
                    if (collide[1] > 0) onGround = true;
                    if (collide[1] < 0) collideTop = true;

                    this.transform.pos.set(this.boundingBox.getCenter().sub(this.boundingBoxCenterX, this.boundingBoxCenterY, new Vector2f()), 0);
                }
            }
        }

        if (this.isOnGround() || this.isJumping()) {
            this.lastGroundTime = System.currentTimeMillis();
        }

        this.onGround = onGround;
        this.collideTop = collideTop;
        this.collideLeft = collideLeft;
        this.collideRight = collideRight;
    }

    public void collideWithObject(GameObject object) {
        Collision collision = this.boundingBox.getCollision(object.boundingBox);

        if (!collision.intersecting) return;

        this.onCollideObject(object, new Collision(collision.distance, true));
        object.onCollideObject(this, new Collision(collision.distance, true));

        collision.distance.div(2);

        if (this.isMovable()) {
            this.boundingBox.correctPosition(object.boundingBox, collision);
            this.transform.pos.set(this.boundingBox.getCenter().sub(this.boundingBoxCenterX, this.boundingBoxCenterY, new Vector2f()), 0);
        }

        if (object.isMovable()) {
            object.boundingBox.correctPosition(this.boundingBox, collision);
            object.transform.pos.set(object.boundingBox.getCenter().sub(object.boundingBoxCenterX, object.boundingBoxCenterY, new Vector2f()), 0);
        }
    }

    public abstract void update(float delta, Window window, Camera camera, World world);

    public abstract void render(MatrixStack stack, Camera camera, World world);

    public void updateGravity(Vector2f movement, float speed) {
        float progress = (System.currentTimeMillis() - this.lastGroundTime) / (float) this.jumpDuration;
        if (this.lastGroundTime <= 0) progress = 0.1F;

        movement.add(0, (speed * 2.0F) * Math.max(0, Math.min(progress, 2)));
    }


    public void move(Vector2f direction) {
        this.transform.pos.add(new Vector3f(direction, 0));
        this.updateBoundingBoxPos();
    }

    public void setPos(int x, int y) {
        this.transform.pos.set(x, y, 0);
        this.updateBoundingBoxPos();
    }

    public void updateBoundingBoxPos() {
        this.boundingBox.getCenter().set(this.transform.pos.x + this.boundingBoxCenterX, this.transform.pos.y + this.boundingBoxCenterY);
    }

    public GameObject setMovable(boolean movable) {
        this.movable = movable;
        return this;
    }

    public boolean isMovable() {
        return this.movable;
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public int getZIndex() {
        return this.zIndex;
    }

    public void parseJSON(JSONObject object) {
        if (object.has("properties")) {
            JSONArray properties = object.getJSONArray("properties");

            for (int j = 0; j < properties.length(); j++) {
                JSONObject property = properties.getJSONObject(j);
                String name = property.getString("name");

                if (name.equals("z-index")) {
                    this.setZIndex(property.getInt("value"));
                }
            }
        }
    }

    public Transform getTransform() {
        return this.transform;
    }

    public AABB getBoundingBox() {
        return this.boundingBox;
    }

    public float getBoundingBoxCenterX() {
        return this.boundingBoxCenterX;
    }

    public float getBoundingBoxCenterY() {
        return this.boundingBoxCenterY;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public boolean isJumping() {
        return this.jumpTime + this.jumpDuration > System.currentTimeMillis();
    }

    public void jump() {
        if (!this.canJump) return;

        if (this.isOnGround()) {
            this.jumpTime = System.currentTimeMillis();
            this.doubleJump = false;
            this.canJump = false;
            this.jumpDirection = 0.0F;
            this.canWallJump = true;
        } else if (!this.doubleJump || (this.canWallJump && (this.collideLeft || this.collideRight))) {
            this.jumpTime = System.currentTimeMillis();
            this.doubleJump = true;
            this.canJump = false;
            this.canWallJump = true;

            this.jumpDirection = 0.0F;
            if (this.collideRight) {
                this.jumpDirection = -1.0F;
            } else if (this.collideLeft) {
                this.jumpDirection = 1.0F;
            }
        }
    }

    public void setCanJump(boolean canJump) {
        this.canJump = canJump;
    }

    public void setNoClip(boolean noClip) {
        this.noClip = noClip;
    }

    public boolean isNoClip() {
        return this.noClip;
    }
}
