package scratch.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class Action {
    protected final ActionType type;
    private final BooleanProperty isValid = new SimpleBooleanProperty(true);
    private final StringProperty displayText = new SimpleStringProperty();

    public Action(ActionType type) {
        this.type = type;
    }

    public ActionType getType() {
        return type;
    }

    public BooleanProperty isValidProperty() {
        return isValid;
    }

    public StringProperty displayTextProperty() {
        return displayText;
    }

    public String save() {
        return type + ";";
    }

    protected int resolveValue(String input, ActionContext actionContext) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return actionContext.getVariableManager().get(input).getValue();
        }
    }

    public abstract void execute(ActionContext actionContext);

    public abstract Action copy();

    public abstract String toString();
}
