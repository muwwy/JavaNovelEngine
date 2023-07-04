package jne.engine.screens.listeners;

import jne.engine.events.types.ScreenEvent;
import jne.engine.screens.components.Component;
import jne.engine.utils.IComponentsListener;
import jne.engine.utils.IWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class ComponentsListener implements IComponentsListener, IWrapper {

    public int width;
    public int height;

    public void init() {

    }

    public void close() {

    }

    public void update() {

    }

    @Override
    final public void render(float partialTicks) {
        getComponents().forEach(it -> it.render(partialTicks));
    }

    @Override
    final public void mouseMove(int mouseX, int mouseY) {
        getComponents().forEach(it -> it.mouseMove(mouseX, mouseY));
    }

    @Override
    final public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        getComponents().forEach(it -> it.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    final public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        getComponents().forEach(it -> it.mouseReleased(mouseX, mouseY, mouseButton));
    }

    @Override
    final public void mouseClickMove(int mouseX, int mouseY, int mouseButton, long timeSinceLastClick) {
        getComponents().forEach(it -> it.mouseClickMove(mouseX, mouseY, mouseButton, timeSinceLastClick));
    }

    @Override
    final public void keyTyped(char typedChar, int keyCode) {
        getComponents().forEach(it -> it.keyTyped(typedChar, keyCode));
    }

    @Override
    final public void tick() {
        getComponents().forEach(Component::tick);
    }

    final public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        recreate();
    }

    final public void recreate() {
        components.clear();
        init();
        update();
    }

    final public void openSubscreen(ComponentsListener subscreen) {
        WINDOW.screenManager.addSubscreen(subscreen);
    }

    final public void sendUpdateInfo() {
        new ScreenEvent.UpdateInfo(this).post();
    }

    final public void killAll() {
        WINDOW.screenManager.clearSubscreens();
    }

    final public void kill() {
        WINDOW.screenManager.removeSubscreen(this);
    }

    /* Components management */

    private final List<Component> components = new ArrayList<Component>();

    final public <T extends Component> void add(T component) {
        components.add(component);
    }

    final public <T extends Component> void remove(T component) {
        components.remove(component);
    }

    final public List<Component> getComponents() {
        return new ArrayList<>(components);
    }

    final public <T extends Component> List<T> getComponentsByType(Class<T> componentType) {
        return components.stream()
                .filter(componentType::isInstance)
                .map(c -> (T) c)
                .collect(Collectors.toList());
    }

    final public <T extends Component> List<T> getComponentsByID(int id, Class<T> componentType) {
        return components.stream()
                .filter(componentType::isInstance)
                .filter(it -> it.id == id)
                .map(c -> (T) c)
                .collect(Collectors.toList());
    }

    final public <T extends Component> List<T> getComponentsByID(int id) {
        return components.stream()
                .filter(it -> it.id == id)
                .map(c -> (T) c)
                .collect(Collectors.toList());
    }

}
