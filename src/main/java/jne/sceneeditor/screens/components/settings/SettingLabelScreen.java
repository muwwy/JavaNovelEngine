package jne.sceneeditor.screens.components.settings;

import jne.engine.errors.ErrorManager;
import jne.engine.screens.components.Area;
import jne.engine.screens.components.Component;
import jne.engine.screens.components.ComponentBuilderHelper;
import jne.engine.screens.components.MethodConstructor;
import jne.engine.screens.widgets.CheckBox;
import jne.engine.screens.widgets.Label;
import jne.engine.screens.widgets.TextBox;
import jne.engine.utils.Util;
import jne.sceneeditor.screens.components.SettingComponentScreen;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class SettingLabelScreen extends SettingComponentScreen {

    public Label<? extends Label<?>> label;

    public SettingLabelScreen(Area area) {
        super(new ComponentBuilderHelper(GRAPHICS.label().self()), area);
    }

    @Override
    public void collect() {
        errored = false;

        Label.Builder<? extends Label.Builder<?, ?>, ? extends Label<?>> builder = GRAPHICS.label().self();
        Class<? extends Label.Builder> clazz = builder.getClass();

        boolean build = build(clazz, builder);

        if (build) {
            init = true;

            if (this.label != null) {
                remove(this.label);
            }

            this.label = builder.build();
            Area center = this.area.getCenter();
            this.label.setArea(new Area(center.x - 100, center.y - 100, Z_LEVEL, 200, 200));
            add(this.label);
        }
    }

}