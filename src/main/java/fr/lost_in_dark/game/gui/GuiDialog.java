package fr.lost_in_dark.game.gui;

import com.salwyrr.dialog.Dialog;
import fr.lost_in_dark.game.Game;
import fr.lost_in_dark.game.assets.Assets;
import fr.lost_in_dark.game.gui.dialog.DialogEndListener;
import fr.lost_in_dark.game.render.MatrixStack;
import fr.lost_in_dark.game.utils.Timer;

import java.util.ArrayList;
import java.util.List;

public class GuiDialog {

    private boolean visible = false;
    private boolean waiting = false;
    private String message = "";
    private Dialog dialog;
    private List<String> choices = new ArrayList<>();

    private DialogEndListener listener = null;

    public void render(MatrixStack stack) {
        if (!this.visible) return;

        if (!this.dialog.isRunning() && this.choices.size() == 0) {
            this.dialog = null;
            this.visible = false;
            this.message = "";
            if (this.listener != null) {
                this.listener.onDialogEnd();
                this.listener = null;
            }
            return;
        }

        if (this.dialog != null && this.dialog.isRunning()) {
            this.dialog.update();
        }

        int x = 10;
        int width = Game.getInstance().getWindow().getWidthFramebuffer() - x * 2;
        int height = 100;
        int y = Game.getInstance().getWindow().getHeightFramebuffer() - height - x;

        stack.push();
        stack.translate(x, y, 0);
        stack.scale(width, height, 1);
        stack.applyMatrix();
        stack.unbindImage();
        stack.color(0, 0, 0, 0.5F);

        Assets.getModel().render();
        stack.pop();

        stack.push();
        stack.translate(x + 10, y + 10, 0);
        stack.scale(2.0F, 2.0F, 2.0F);
        Gui.getInstance().getFont().render(stack, this.message, 0, 0);

        for (int i = 0; i < this.choices.size(); i++) {
            Gui.getInstance().getFont().render(stack, (i + 1) + ". " + this.getChoices().get(i), 0, height - this.choices.size() * 17 + i * 17 - 10);
        }
        stack.pop();

        if (this.waiting) {
            if ((int) (Timer.getTimeInSeconds() * 20) % 10 < 5) Gui.getInstance().getFont().render(stack, ">", x + width - 26, y + height - 26);
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        this.setMessage("");
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setMessage(String message) {
        this.message = message;
        this.choices = new ArrayList<>();
    }

    public String getMessage() {
        return message;
    }

    public void setWaiting(boolean waiting) {
        this.waiting = waiting;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public void setListener(DialogEndListener listener) {
        this.listener = listener;
    }

    public Dialog getDialog() {
        return this.dialog;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    public List<String> getChoices() {
        return this.choices;
    }
}
