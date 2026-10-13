package scratch.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class VariableAssignmentAction extends Action {
    private final StringProperty variableName = new SimpleStringProperty();
    private final StringProperty variableValue = new SimpleStringProperty();

    public VariableAssignmentAction() {
        this("", "");
    }

    public VariableAssignmentAction(String variableName, String variableValue) {
        super(ActionType.VAR_ASSIGNMENT);
        this.variableName.set(variableName);
        this.variableValue.set(variableValue);

        setupBindings();
    }

    public String getVariableName() {
        return variableName.get();
    }

    public void setVariableName(String variableName) {
        this.variableName.set(variableName);
    }

    public String getVariableValue() {
        return variableValue.get();
    }

    public void setVariableValue(String variableValue) {
        this.variableValue.set(variableValue);
    }

    private void setupBindings() {
        isValidProperty().bind(Bindings.createBooleanBinding(() -> variableName.get().matches(Constants.VARIABLE_NAME_REGEX)
                        && (variableValue.get().matches(Constants.INTEGER_VALUE_REGEX) || variableValue.get().matches(Constants.VARIABLE_NAME_REGEX))
                , variableName, variableValue));

        displayTextProperty().bind(Bindings.concat(toString(),
                Bindings.when(variableName.isEmpty()).then("<variable>").otherwise(variableName),
                " la valeur ", Bindings.when(variableValue.isEmpty()).then("<valeur>").otherwise(variableValue)));
    }

    @Override
    public String save() {
        return super.save() + variableName.get() + ";" + variableValue.get();
    }

    @Override
    public void execute(ActionContext actionContext) {
        int resolvedValue = resolveValue(variableValue.get(), actionContext);
        actionContext.getVariableManager().set(variableName.get(), resolvedValue);
    }

    @Override
    public Action copy() {
        return new VariableAssignmentAction(variableName.get(), variableValue.get());
    }

    @Override
    public String toString() { return "Assigner à la variable ";}
}
