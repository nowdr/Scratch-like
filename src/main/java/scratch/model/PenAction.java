package scratch.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class PenAction extends Action {
    private final BooleanProperty isDown = new SimpleBooleanProperty();

    public PenAction(ActionType type) {
        super(type);
        isDown.set(type == ActionType.PEN_DOWN);

        setupBinding();
    }

    public boolean getIsDown() {
        return isDown.get();
    }

    private void setupBinding() {
        displayTextProperty().bind(Bindings.concat(toString()));
    }

    @Override
    public void execute(ActionContext actionContext) {
        actionContext.getCursor().setPenDown(isDown.get());
    }

    @Override
    public Action copy() {
        return new PenAction(type);
    }

    @Override
    public String toString() {
        return (isDown.get() ? "Abaisser " : "Lever ") + "stylo";
    }
}