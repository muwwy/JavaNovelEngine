package jne.engine.screens;

import jne.engine.events.EventListenerHelper;
import jne.engine.events.types.ScreenEvent;
import jne.engine.screens.components.Component;
import jne.engine.screens.listeners.ComponentsListener;
import jne.engine.utils.IWrapper;
import jne.engine.utils.KeyboardType;
import jne.engine.utils.MouseClickType;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static jne.engine.utils.Util.getSystemTime;

/**
 * This class is a manager for screens and sub-screens, and then makes a calculation of mouse and keyboard positions for correct display in the main application displays
 */
public class ScreenManager implements IWrapper {

    public int width, height;

    public ComponentsListener initScreen;
    public ComponentsListener currentScreen;

    public List<ComponentsListener> subScreens = new ArrayList<>();

    /**
     * Method processing the OpenGL engine renderer
     */
    public void render(float partialTick) {
        RENDER.color(Color.WHITE, () -> {
            RENDER.drawQuad(50, 50, -1, width - 50, height - 50);
        });
        new ScreenEvent.Render(partialTick).post();
    }

    /**
     * A method that handles mouse input with mouse movement
     */
    private void mouseMove(int mouseX, int mouseY) {
        new ScreenEvent.MouseMove(mouseX, mouseY).post();
    }

    /**
     * A method that handles mouse input when the mouse is clicked and moved
     */
    private void mouseClickMove(int mouseX, int mouseY, int button, long last) {
        new ScreenEvent.MouseClickMove(mouseX, mouseY, button, last).post();
    }

    /**
     * A method that handles mouse input when the key is lifted
     */
    private void mouseReleased(int mouseX, int mouseY, int button) {
        new ScreenEvent.MouseInput(MouseClickType.RELEASED, mouseX, mouseY, button).post();
    }

    /**
     * A method that handles mouse click input
     */
    private void mouseClicked(int mouseX, int mouseY, int button) {
        new ScreenEvent.MouseInput(MouseClickType.CLICKED, mouseX, mouseY, button).post();
    }

    /**
     * A method that handles keyboard input
     */
    private void keyTyped(char character, int button, KeyboardType type) {
        new ScreenEvent.Keyboard(type, character, button).post();
    }

    /**
     * A method that handles every millisecond of engine update
     */
    public void tick() {
        input();
        new ScreenEvent.Tick().post();
    }

    /**
     * It is VERY IMPORTANT to set the current screen via this method, otherwise it will not initialize
     *
     * @param screen An instance of the ScreenListener class
     */
    public void setScreen(ComponentsListener screen) {
        if (screen == null || this.currentScreen == screen) {
            return;
        }

        ComponentsListener old = this.currentScreen;

        if (old != null) {
            EventListenerHelper.unregister(old);
            old.close();
        }

        EventListenerHelper.register(screen);
        this.currentScreen = screen;
        this.currentScreen.init();
        this.resize(WINDOW.displayWidth, WINDOW.displayHeight);
    }

    public void addSubscreen(ComponentsListener subScreen) {
        if (this.subScreens.contains(subScreen)) {
            System.out.println("The additional screen has already been added");
        } else {
            EventListenerHelper.register(subScreen);
            this.subScreens.add(subScreen);
            subScreen.init();
        }
    }

    public void removeSubscreen(ComponentsListener subScreen) {
        if (this.subScreens.contains(subScreen)) {
            EventListenerHelper.unregister(subScreen);
            this.subScreens.remove(subScreen);
        } else {
            System.out.println("Additional screen not found");
        }
    }

    public void clearSubscreens() {
        Iterator<ComponentsListener> iterator = this.subScreens.iterator();
        while (iterator.hasNext()) {
            ComponentsListener next = iterator.next();
            EventListenerHelper.unregister(next);
            iterator.remove();
        }
    }

    private long lastMouseEvent;
    private int mouseEventButton;

    private char lastChar;
    private long lastKeyboardEvent;
    private int keyboardEventButton;

    private double scaleWidth, scaleHeight;

    /**
     * Called when resizing the screen, to clearly track the position of the cursor relative to the maximum resolution of the screen
     */
    public void resize(int width, int height) {
        DisplayMode displaymode = Display.getDesktopDisplayMode();
        this.width = displaymode.getWidth();
        this.height = displaymode.getHeight();

        this.scaleWidth = (float) this.width / width;
        this.scaleHeight = (float) this.height / height;
    }

    /**
     * Reads possible mouse and keyboard updates
     */
    public void input() {
        if (Mouse.isCreated()) {
            while (Mouse.next()) {
                this.mouse();
            }
        }

        if (Keyboard.isCreated()) {

            while (Keyboard.next()) {
                this.keyboard();
            }
            if (Keyboard.isKeyDown(keyboardEventButton) && getSystemTime() - lastKeyboardEvent >= 200L) {
                this.keyTyped(lastChar, keyboardEventButton, KeyboardType.PRESSED);
            }
        }
    }

    /**
     * Handles computer mouse events
     */
    private void mouse() {
        int x = (int) ((Mouse.getEventX()) * scaleWidth);
        int y = (int) (height - ((Mouse.getEventY()) * scaleHeight) - 0.5);
        int button = Mouse.getEventButton();

        boolean flag = true;

        if (Mouse.getEventButtonState()) {
            this.mouseEventButton = button;
            this.lastMouseEvent = getSystemTime();
            this.mouseClicked(x, y, this.mouseEventButton);
            flag = false;
        } else if (button != -1) {
            this.mouseEventButton = -1;
            this.mouseReleased(x, y, button);
            flag = false;
        } else if (this.mouseEventButton != -1 && this.lastMouseEvent > 0L) {
            long last = getSystemTime() - this.lastMouseEvent;
            this.mouseClickMove(x, y, this.mouseEventButton, last);
            flag = false;
        }

        if (flag) {
            this.mouseMove(x, y);
        }
    }

    /**
     * Handles keyboard events
     */
    private void keyboard() {
        char character = Keyboard.getEventCharacter();
        int eventKey = Keyboard.getEventKey();

        if (Keyboard.getEventKeyState()) {
            this.lastChar = character;
            this.keyboardEventButton = eventKey;
            this.lastKeyboardEvent = getSystemTime();
            this.keyTyped(character, eventKey, KeyboardType.START);
        } else  {
            this.keyTyped(lastChar, keyboardEventButton, KeyboardType.STOP);
        }
    }


}
