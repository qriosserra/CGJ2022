package fr.lost_in_dark.game.assets;

import fr.lost_in_dark.game.render.Model;
import fr.lost_in_dark.game.render.texture.Texture;
import fr.lost_in_dark.game.utils.ModelUtils;

public class Assets {
    private static Model model;
    public final static Texture bg1 = new Texture("assets/background/bg1.png");
    public final static Texture bg2 = new Texture("assets/background/bg2.png");
    public final static Texture bg3 = new Texture("assets/background/bg3.png");
    public final static Texture bg4 = new Texture("assets/background/bg4.png");
    public final static Texture bg5 = new Texture("assets/background/bg5.png");

    public final static Texture[] bgs = new Texture[] {
            bg1, bg2, bg3, bg4, bg5
    };

    public static Model getModel() {
        return Assets.model;
    }

    public static void initAssets() {
        model = ModelUtils.createQuad(0, 0, 1, 1);
    }

    public static void deleteAssets() {
        model = null;
    }

}
