package scratch.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import scratch.model.Constants;
import scratch.model.VariableAssignmentAction;

public class VariableAssignmentActionEditor extends ActionEditor{
    private final VariableAssignmentAction variableAssignmentAction;
    private final TextField variableNameField = new TextField();
    private final ValueField valueField = new ValueField();
    private final Label errorLabel = new Label("Entrez un nom de variable valide et/ou une valeur valide.");


    public VariableAssignmentActionEditor(VariableAssignmentAction action) {
        variableAssignmentAction = action;

        setupEditor();

        setupBindings();
        setupListeners();
    }

    @Override
    public void setupEditor() {
        variableNameField.setPrefWidth(Constants.EDITOR_CONTROLS_PREFERRED_WIDTH);
        variableNameField.setAlignment(Pos.CENTER);

        variableNameField.setText(variableAssignmentAction.getVariableName());
        valueField.setText(variableAssignmentAction.getVariableValue());

        hBox.getChildren().addAll(new Label("Assignation de la variable "), variableNameField, new Label(" à "), valueField);

        errorLabel.setTextFill(Color.RED);
        getChildren().add(errorLabel);
    }

    private void setupBindings() {
        errorLabel.visibleProperty().bind(variableAssignmentAction.isValidProperty().not());
        errorLabel.managedProperty().bind(errorLabel.visibleProperty());
    }

    private void setupListeners() {
        variableNameField.textProperty().addListener((_, _, newText ) ->
                variableAssignmentAction.setVariableName(newText));

        valueField.textProperty().addListener((_, _, newText ) ->
                variableAssignmentAction.setVariableValue(newText));
    }
}
