package fr.lost_in_dark.game.entity;

import com.salwyrr.dialog.Dialog;
import com.salwyrr.dialog.exceptions.DialogUnknownKey;
import fr.lost_in_dark.game.Game;
import fr.lost_in_dark.game.Window;
import fr.lost_in_dark.game.collision.Collision;
import fr.lost_in_dark.game.gui.Gui;
import fr.lost_in_dark.game.gui.dialog.DialogEndListener;
import fr.lost_in_dark.game.objects.GameObject;
import fr.lost_in_dark.game.render.camera.Camera;
import fr.lost_in_dark.game.render.texture.SpriteSheet;
import fr.lost_in_dark.game.world.World;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TutoEntity extends Entity implements DialogEndListener {

    private static List<TutoEntity> tutoList = new ArrayList<>();

    private Dialog dialog;
    private boolean talked = false;
    private Vector2f to = null;

    public TutoEntity() {
        super(1, 14 / 16.0F / 2.0F, 24 / 16.0F / 2.0F, 4.0F, 4.0F);
        this.loadAnim(new SpriteSheet("characters/wife_animation.png"),
                0,
                0,
                0,
                4,
                4,
                16,
                26);

        this.getScale().set(1.0F, 26 / 16.0F, 1.0F);

        this.setMovable(false);
        this.setNoClip(true);
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        if (this.to == null) return;

        this.transform.pos.lerp(new Vector3f(this.to, 0), delta * 1.75F);
        this.updateBoundingBoxPos();
    }

    @Override
    public void onCollideObject(GameObject object, Collision collision) {
        collision.distance.set(0, 0);

        if (!(object instanceof Player)) return;

        if (Gui.getInstance().getDialog().isVisible()) {
            if (Gui.getInstance().getDialog().getDialog() != this.dialog) {
                Gui.getInstance().getDialog().getDialog().exit(true);
            }
            return;
        }

        if (this.to != null && this.transform.pos.distance(new Vector3f(this.to, 0)) > 4) {
            return;
        }

        if (this.talked) return;
        this.talked = true;

        Gui.getInstance().getDialog().setVisible(true);
        Gui.getInstance().getDialog().setDialog(this.dialog);
        Gui.getInstance().getDialog().setListener(this);

        try {
            this.dialog.start("tuto", Game.getInstance().getGameDialogHandler());
        } catch (DialogUnknownKey e) {
            e.printStackTrace();
        }
    }

    public static void spawn(float x, float y, World world, String[] args) {
        TutoEntity tuto = new TutoEntity();

        boolean empty = true;

        for (GameObject object : world.getGameObjects()) {
            if (!(object instanceof TutoEntity)) continue;

            empty = false;
            break;
        }

        if (empty) {
            TutoEntity.tutoList.clear();
        }

        TutoEntity.tutoList.add(tuto);
        tuto.setPosCentered(x, y);
        try {
            tuto.dialog = new Dialog("#tuto_1\n" + args[0], new File(""));
        } catch (IOException e) {
            e.printStackTrace();
        }
        world.getGameObjects().add(tuto);

        TutoEntity.tutoList.sort(Comparator.comparingInt(t -> (int) (t.transform.pos.x)));
        TutoEntity.tutoList.forEach(t -> t.visible = false);
        TutoEntity.tutoList.get(0).visible = true;
    }

    @Override
    public void onDialogEnd() {
        this.visible = false;

        int i = TutoEntity.tutoList.indexOf(this);
        if (i == -1 || i >= TutoEntity.tutoList.size() - 1) return;

        TutoEntity next = TutoEntity.tutoList.get(i + 1);
        if (next != null) {
            next.visible = true;
            next.to = new Vector2f(next.transform.pos.x, next.transform.pos.y);
            next.setPos(this.transform.pos.x, this.transform.pos.y);
        }
    }
}
