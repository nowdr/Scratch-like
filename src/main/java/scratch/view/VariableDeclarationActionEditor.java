package scratch.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import scratch.model.Constants;
import scratch.model.VariableDeclarationAction;

public class VariableDeclarationActionEditor extends ActionEditor {
    private final VariableDeclarationAction variableDeclarationAction;
    private final TextField variableNameField = new TextField();
    private final Label errorLabel = new Label("Entrez un nom de variable valide.");

    public VariableDeclarationActionEditor(VariableDeclarationAction action) {
        variableDeclarationAction = action;

        setupEditor();

        setupBindings();
        setupListener();
    }

    @Override
    public void setupEditor() {
        variableNameField.setPrefWidth(Constants.EDITOR_CONTROLS_PREFERRED_WIDTH);
        variableNameField.setAlignment(Pos.CENTER);

        variableNameField.setText(variableDeclarationAction.getVariableName());

        hBox.getChildren().addAll(new Label("Déclaration de la variable "), variableNameField);

        errorLabel.setTextFill(Color.RED);
        getChildren().add(errorLabel);
    }

    private void setupBindings() {
        errorLabel.visibleProperty().bind(variableDeclarationAction.isValidProperty().not());
        errorLabel.managedProperty().bind(errorLabel.visibleProperty());
    }

    private void setupListener() {
        variableNameField.textProperty().addListener((_, _, newText) ->
                variableDeclarationAction.setVariableName(newText));
    }
}
