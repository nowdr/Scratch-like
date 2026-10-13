package scratch.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RepeatAction extends CompositeAction {
    private final StringProperty count = new SimpleStringProperty("");

    public RepeatAction() {
        this("4");
    }

    public RepeatAction(String count) {
        super(ActionType.REPEAT);
        this.count.set(count);

        setupBindings();
    }

    public String getCount() {
        return count.get();
    }

    public void setCount(String count) {
        this.count.set(count);
    }

    private void setupBindings() {
        isValidProperty().bind(Bindings.createBooleanBinding(() -> {
            try {
                int value = Integer.parseInt(count.get());
                return value > 0;
            } catch (NumberFormatException e) {
                return count.get().matches(Constants.VARIABLE_NAME_REGEX);
            }
        }, count));

        displayTextProperty().bind(Bindings.concat(toString(), count, " fois"));
    }

    @Override
    public String save() {
        return super.save() + count.get();
    }

    @Override
    public void execute(ActionContext actionContext) {
    }

    @Override
    public Action copy() {
        return new RepeatAction(count.get());
    }

    @Override
    public String toString() {
        return "Répéter ";
    }
}