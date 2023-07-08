package jne.sceneeditor.screens;

import jne.engine.constants.EventPriority;
import jne.engine.constants.MouseClickType;
import jne.engine.events.types.ScreenEvent;
import jne.engine.events.utils.SubscribeEvent;
import jne.engine.screens.components.Area;
import jne.engine.screens.components.Component;
import jne.engine.screens.listeners.ComponentsListener;
import jne.engine.screens.widgets.Button;
import jne.engine.screens.widgets.Label;
import jne.engine.screens.widgets.TexturedComponent;
import jne.sceneeditor.screens.components.settings.SettingButtonScreen;
import jne.sceneeditor.screens.components.settings.SettingLabelScreen;
import jne.sceneeditor.screens.components.settings.SettingTextureScreen;
import jne.sceneeditor.utils.EditingTypes;
import jne.sceneeditor.utils.Frame;
import jne.sceneeditor.utils.MovableComponent;

import java.util.HashSet;

public class ScaledAreaScreen extends ComponentsListener {

    protected final int Z_LEVEL = -100;

    public final SceneEditorScreen sceneEditorScreen;
    public final HashSet<Frame> frames = new HashSet<>();

    public Component lastClickedComponent = null;

    public int id = 0;
    public Frame currentFrame = new Frame(id);
    public MovableComponent currentComponent = new MovableComponent(null);

    public ScaledAreaScreen(SceneEditorScreen sceneEditorScreen) {
        this.sceneEditorScreen = sceneEditorScreen;
        this.frames.add(currentFrame);
    }

    @Override
    public void init() {
        // Just debug
        this.isInit = true;
        Area center = new Area(120, 120, width - 120, height - 120, Z_LEVEL, false).getCenter();
        Area area = new Area(center.x - 100, center.y - 100, Z_LEVEL, 200, 200);

        add(GRAPHICS.button().area(area).color(44444444).onPress((c, t) -> {
            if (t == MouseClickType.CLICKED) {
                lastClickedComponent = c;
            }
        }).texture("exit").build());
        add(GRAPHICS.button().area(area.offset(-100, -100)).color(14444444).onPress((c, t) -> {
            if (t == MouseClickType.CLICKED) {
                lastClickedComponent = c;
            }
        }).texture("zoom").build());
    }

    public void selectComponent(Component component) {
        if (component == null) {
            this.lastClickedComponent = null;
        }
        this.currentComponent = new MovableComponent(component);
    }

    public void selectFrame(Frame frame) {
        // If equals
        if (this.currentFrame.equals(frame)) return;
        // If not created
        if (!frames.contains(frame)) return;

        // Save
        this.currentFrame.setComponents(getComponents());
        this.clearComponents();
        this.lastClickedComponent = null;
        this.selectComponent(null);

        // Load
        this.currentFrame = frame;
        this.setComponents(this.currentFrame.getComponents());
    }

    public void createFrame() {
        Frame frame = new Frame(id++);
        this.frames.add(frame);
        selectFrame(frame);
    }

    public EditingTypes getEditType() {
        return sceneEditorScreen.currentEditingType;
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void render(ScreenEvent.Render event) {
        super.render(event);
        currentComponent.render(event);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void input(ScreenEvent.MouseInput event) {
        super.input(event);
        currentComponent.input(event);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void clickMove(ScreenEvent.MouseClickMove event) {
        super.clickMove(event);
        currentComponent.clickMove(event);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void move(ScreenEvent.MouseMove event) {
        super.move(event);
        currentComponent.move(event);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void tick(ScreenEvent.Tick event) {
        super.tick(event);
        currentComponent.tick(event);
        currentComponent.setType(getEditType());

        if (currentComponent.getComponent() == null && lastClickedComponent != null) {
            this.selectComponent(lastClickedComponent);
            lastClickedComponent = null;
        }

        if (!isInit) {
            init();
        }

    }

    @SubscribeEvent
    public void onCloseSubScreen(ScreenEvent.Close event) {
        if (event.getScreen().getClass().equals(SettingButtonScreen.class)) {
            SettingButtonScreen screen = (SettingButtonScreen) event.getScreen();
            if (screen.init) {
                Button<? extends Button<?>> button = screen.button;
                button.area.z = Z_LEVEL;
                button.onPress = (component, type) -> {
                    if (type == MouseClickType.CLICKED) {
                        lastClickedComponent = component;
                    }
                };
                add(button);
            }
        }
        if (event.getScreen().getClass().equals(SettingLabelScreen.class)) {
            SettingLabelScreen screen = (SettingLabelScreen) event.getScreen();
            if (screen.init) {
                Label<? extends Label<?>> label = screen.label;
                label.area.z = Z_LEVEL;
                label.onPress = (component, type) -> {
                    if (type == MouseClickType.CLICKED) {
                        lastClickedComponent = component;
                    }
                };
                add(label);
            }
        }
        if (event.getScreen().getClass().equals(SettingTextureScreen.class)) {
            SettingTextureScreen screen = (SettingTextureScreen) event.getScreen();
            if (screen.init) {
                TexturedComponent<? extends TexturedComponent<?>> texture = screen.texture;
                texture.area.z = Z_LEVEL;
                texture.onPress = (component, type) -> {
                    if (type == MouseClickType.CLICKED) {
                        lastClickedComponent = component;
                    }
                };
                add(texture);
            }
        }
    }

}
