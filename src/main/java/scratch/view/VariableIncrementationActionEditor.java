package scratch.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import scratch.model.Constants;
import scratch.model.VariableIncrementationAction;

public class VariableIncrementationActionEditor extends ActionEditor {
    private final VariableIncrementationAction variableIncrementationAction;
    private final TextField variableNameField = new TextField();
    private final ValueField valueField = new ValueField();
    private final Label errorLabel = new Label("Entrez un nom de variable valide et/ou une valeur valide.");

    public VariableIncrementationActionEditor(VariableIncrementationAction action) {
        variableIncrementationAction = action;

        setupEditor();

        setupBindings();
        setupListeners();
    }

    @Override
    public void setupEditor() {
        variableNameField.setPrefWidth(Constants.EDITOR_CONTROLS_PREFERRED_WIDTH);
        variableNameField.setAlignment(Pos.CENTER);

        variableNameField.setText(variableIncrementationAction.getVariableName());
        valueField.setText(variableIncrementationAction.getStep());

        hBox.getChildren().addAll(new Label("Incrémenter "), variableNameField, new Label(" de "), valueField);

        errorLabel.setTextFill(Color.RED);
        getChildren().add(errorLabel);
    }

    private void setupBindings() {
        errorLabel.visibleProperty().bind(variableIncrementationAction.isValidProperty().not());
        errorLabel.managedProperty().bind(errorLabel.visibleProperty());
    }

    private void setupListeners() {
        variableNameField.textProperty().addListener((_, _, newText ) ->
                variableIncrementationAction.setVariableName(newText));

        valueField.textProperty().addListener((_, _, newText ) ->
                variableIncrementationAction.setStep(newText));
    }
}
