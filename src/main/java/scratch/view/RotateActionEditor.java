package scratch.view;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import scratch.model.RotateAction;

public class RotateActionEditor extends ActionEditor {
    private final RotateAction rotateAction;
    private final ValueField valueField = new ValueField();
    private final Label errorLabel = new Label("Entrez un angle entre 1 et 180 ou un nom de variable valide.");

    public RotateActionEditor(RotateAction action) {
        rotateAction = action;
        setupEditor();

        setupBindings();
        setupListener();
    }

    @Override
    public void setupEditor() {
        valueField.setText(rotateAction.getAngle());

        hBox.getChildren().addAll(new Label("Tourner " + (rotateAction.isClockwise() ? "à droite " : "à gauche ") + "de"), valueField, new Label("degrés"));

        errorLabel.setTextFill(Color.RED);
        getChildren().add(errorLabel);
    }

    private void setupBindings() {
        errorLabel.visibleProperty().bind(rotateAction.isValidProperty().not());
        errorLabel.managedProperty().bind(errorLabel.visibleProperty());
    }

    private void setupListener() {
        valueField.textProperty().addListener((_, _, newText) ->
                rotateAction.setAngle(newText));
    }
}
