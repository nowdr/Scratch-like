package scratch.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;

public class VariableIncrementationAction extends Action {

    private final StringProperty variableName = new SimpleStringProperty();
    private final StringProperty step = new SimpleStringProperty();

    public VariableIncrementationAction() {
        this("", "");
    }

    public VariableIncrementationAction(String variableName, String step) {
        super(ActionType.INCREMENT_VARIABLE);
        this.variableName.set(variableName);
        this.step.set(step);

        setupBindings();
    }

    public String getVariableName() {
        return variableName.get();
    }

    public void setVariableName(String variableName) {
        this.variableName.set(variableName);
    }

    public String getStep() {
        return step.get();
    }

    public void setStep(String step) {
        this.step.set(step);
    }

    private void setupBindings() {
        isValidProperty().bind(Bindings.createBooleanBinding(() -> variableName.get().matches(Constants.VARIABLE_NAME_REGEX)
                        && (step.get().matches(Constants.INTEGER_VALUE_REGEX) || step.get().matches(Constants.VARIABLE_NAME_REGEX))
                , variableName, step));

        displayTextProperty().bind(Bindings.concat(toString(),
                Bindings.when(variableName.isEmpty()).then("<variable>").otherwise(variableName),
                " de ", Bindings.when(step.isEmpty()).then("<valeur>").otherwise(step)));
    }

    @Override
    public String save() {
        return super.save() + variableName.get() + ";" + step.get();
    }

    @Override
    public void execute(ActionContext actionContext) {
        int resolvedStep = resolveValue(this.step.get(), actionContext);
        actionContext.getVariableManager().update(variableName.get(), resolvedStep);
    }

    @Override
    public Action copy() {
        return new VariableIncrementationAction(variableName.get(), step.get());
    }

    @Override
    public String toString() {
        return "Incrémenter la variable ";
    }
}
