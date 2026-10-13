package scratch.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class VariableDeclarationAction extends Action {
    private final StringProperty variableName = new SimpleStringProperty();

    public VariableDeclarationAction() {
        this("");
    }

    public VariableDeclarationAction(String variableName) {
        super(ActionType.VAR_DECLARATION);
        this.variableName.set(variableName);

        setupBindings();
    }

    public String getVariableName() {
        return variableName.get();
    }

    public void setVariableName(String variableName) {
        this.variableName.set(variableName);
    }

    private void setupBindings() {
        isValidProperty().bind(Bindings.createBooleanBinding(() ->
                variableName.get().matches(Constants.VARIABLE_NAME_REGEX), variableName));

        displayTextProperty().bind(Bindings.concat(toString(),
                Bindings.when(variableName.isEmpty()).then("<variable>").otherwise(variableName)));
    }

    @Override
    public String save() {
        return super.save() + variableName.get();
    }

    @Override
    public void execute(ActionContext actionContext) {
        actionContext.getVariableManager().add(variableName.get());
    }

    @Override
    public Action copy() {
        return new VariableDeclarationAction(variableName.get());
    }

    @Override
    public String toString() {
        return "Déclarer la variable ";
    }
}
