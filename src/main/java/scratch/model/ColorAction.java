package scratch.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

public class ColorAction extends Action {
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>();

    public ColorAction() {
        this(Color.BLACK);
    }

    public ColorAction(Color color) {
        super(ActionType.COLOR);
        this.color.set(color);

        setupBinding();
    }

    public Color getColor() {
        return color.get();
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    private void setupBinding() {
        displayTextProperty().bind(Bindings.concat(toString()));
    }

    @Override
    public String save() {
        return super.save() + color.get();
    }

    @Override
    public void execute(ActionContext actionContext) {
        actionContext.getCursor().setColor(color.get());
    }

    @Override
    public Action copy() {
        return new ColorAction(color.get());
    }

    @Override
    public String toString() {
        return "Colorier";
    }
}
