package jne.engine.screens.widgets;

import jne.engine.constants.MouseClickType;
import jne.engine.screens.components.constructor.ComponentConstructor;
import jne.engine.texture.TextureContainer;
import org.json.JSONObject;

import static jne.engine.constants.Colors.*;

public class CheckBox<SELF extends CheckBox<SELF>> extends Component<SELF> {

    public boolean flag = false;

    @Override
    public void onRender(float partialTicks) {
        RENDER.color(toolColor, () -> {
            RENDER.drawQuad(area.x, area.y, area.z, area.x2, area.y2);
        });
        if (!area.isEmpty()) {
            RENDER.color(flag ? trueColor : falseColor, () -> {
                RENDER.drawTexturedQuad(area.x, area.y, area.z, area.x2, area.y2, flag ? TextureContainer.get("true") : TextureContainer.get("false"));
            });
        }
    }

    @Override
    public void onClicked(int mouseX, int mouseY, int button, MouseClickType type) {
        if (type == MouseClickType.RELEASED) return;
        this.flag = !this.flag;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put("flag", flag);
        return json;
    }

    @Override
    public void fromJson(JSONObject json) {
        super.fromJson(json);
        this.flag = json.getBoolean("flag");
    }

    public SELF self() {
        return (SELF) this;
    }

    public static class Builder<SELF extends Builder<SELF, T>, T extends CheckBox<T>> extends Component.Builder<SELF, T> {

        @Override
        public T create() {
            return (T) new CheckBox();
        }


        @ComponentConstructor(text = "Check Box")
        public SELF flag(boolean flag) {
            instance().flag = flag;
            return self();
        }

    }


}
