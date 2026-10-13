package scratch.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MoveAction extends Action {
    private final StringProperty distance = new SimpleStringProperty("");

    public MoveAction() {
        this("30");
    }

    public MoveAction(String distance) {
        super(ActionType.MOVE_FORWARD);
        this.distance.set(distance);

        setupBindings();
    }

    public String getDistance() {
        return distance.get();
    }

    public void setDistance(String distance) {
        this.distance.set(distance);
    }

    private void setupBindings() {
        isValidProperty().bind(Bindings.createBooleanBinding(() -> {
            try {
                int value = Integer.parseInt(distance.get());
                return value >= 1 && value <= 100;
            } catch (NumberFormatException e) {
                return distance.get().matches(Constants.VARIABLE_NAME_REGEX);
            }
        }, distance));

        displayTextProperty().bind(Bindings.concat(toString(), distance));
    }

    @Override
    public String save() {
        return super.save() + distance.get();
    }

    @Override
    public void execute(ActionContext actionContext) {
        int resolvedDistance = resolveValue(distance.get(), actionContext);
        if (!(resolvedDistance >= 1 && resolvedDistance <= 100)) {
            throw new IllegalArgumentException ("On ne peut pas avancer de " + resolvedDistance + " pixels");
        } else {
            actionContext.getCursor().moveForward(resolvedDistance);

        }
    }

    @Override
    public Action copy() {
        return new MoveAction(distance.get());
    }

    @Override
    public String toString() {
        return "Avancer de ";
    }
}
