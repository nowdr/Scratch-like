package scratch.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RotateAction extends Action {
    private final StringProperty angle = new SimpleStringProperty("");
    private final boolean isClockwise;

    public RotateAction(ActionType type) {
        this(type, "90");
    }

    public RotateAction(ActionType type, String angle) {
        super(type);
        this.angle.set(angle);
        isClockwise = (type == ActionType.TURN_RIGHT);

        setupBindings();
    }

    public String getAngle() {
        return angle.get();
    }

    public void setAngle(String angle) {
        this.angle.set(angle);
    }

    public boolean isClockwise() {
        return isClockwise;
    }

    private void setupBindings() {
        isValidProperty().bind(Bindings.createBooleanBinding(() -> {
            try {
                int value = Integer.parseInt(angle.get());
                return value >= 1 && value <= 180;
            } catch (NumberFormatException e) {
                return angle.get().matches(Constants.VARIABLE_NAME_REGEX);
            }
        }, angle));

        displayTextProperty().bind(Bindings.concat(toString(), angle));
    }

    @Override
    public String save() {
        return super.save() + angle.get();
    }

    @Override
    public void execute(ActionContext actionContext) {
        int resolvedAngle = resolveValue(angle.get(), actionContext);
        if (!(resolvedAngle >= 1 && resolvedAngle <= 180)) {
            throw new IllegalArgumentException("On ne peut pas tourner de " + resolvedAngle + " degrés");
        } else {
            actionContext.getCursor().rotate(isClockwise ? resolvedAngle : -resolvedAngle);
        }
    }

    @Override
    public Action copy() {
        return new RotateAction(this.type, getAngle());
    }

    @Override
    public String toString() {
        return "Tourner à " + (isClockwise ? "droite " : "gauche ") + "de ";
    }
}
