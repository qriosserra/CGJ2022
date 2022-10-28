package fr.lost_in_dark.game.gui.dialog;

import com.salwyrr.dialog.Dialog;
import com.salwyrr.dialog.exceptions.DialogInvalidIndex;
import com.salwyrr.dialog.exceptions.DialogUnknownKey;
import com.salwyrr.dialog.handler.DialogHandler;
import fr.lost_in_dark.game.entity.Entity;
import fr.lost_in_dark.game.gui.Gui;
import fr.lost_in_dark.game.listeners.KeyListener;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class GameDialogHandler implements DialogHandler, KeyListener {

    @Override
    public void display(String text, Dialog dialog) {
        Gui.getInstance().getDialog().setMessage(text);
        Gui.getInstance().getDialog().setWaiting(false);
    }

    @Override
    public void displayChoices(List<String> list, Dialog dialog) {
        Gui.getInstance().getDialog().setChoices(list);
    }

    @Override
    public void event(Object narrator, String event, Dialog dialog) {
        if (narrator instanceof Entity) ((Entity) narrator).handleDialogEvent(event);
    }

    @Override
    public void eventWithValue(Object narrator, String event, String value, Dialog dialog) {

    }

    @Override
    public void switchNarrator(Object narrator, Dialog dialog) {

    }

    @Override
    public void switchNarratorAndLookTo(Object narrator, Object character, Dialog dialog) {

    }

    @Override
    public void waitingInput(Dialog dialog) {
        Gui.getInstance().getDialog().setWaiting(true);
    }

    @Override
    public void keyPress(long window, int key, int scancode, int mods) {
        if (Gui.getInstance().getDialog().getDialog() == null) return;

        if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_SPACE) {
            Gui.getInstance().getDialog().getDialog().skip();
        }

        if (key == GLFW.GLFW_KEY_ESCAPE) {
            Gui.getInstance().getDialog().getDialog().exit(true);
            Gui.getInstance().getDialog().setDialog(null);
            Gui.getInstance().getDialog().setVisible(false);
        }

        if (key >= GLFW.GLFW_KEY_0 && key <= GLFW.GLFW_KEY_9) {
            int num = key - GLFW.GLFW_KEY_0;
            try {
                if (num > 0 && num <= Gui.getInstance().getDialog().getChoices().size()) {
                    Gui.getInstance().getDialog().getDialog().choose(num - 1);
                }
            } catch (DialogInvalidIndex | DialogUnknownKey e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void keyRelease(long window, int key, int scancode, int mods) {

    }

    @Override
    public void textInput(long window, String text) {

    }
}
