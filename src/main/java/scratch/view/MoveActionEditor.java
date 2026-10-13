package scratch.view;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import scratch.model.MoveAction;

public class MoveActionEditor extends ActionEditor {
    private final MoveAction moveAction;
    private final ValueField valueField = new ValueField();
    private final Label errorLabel = new Label("Entrez une distance entre 1 et 100 ou un nom de variable valide.");

    public MoveActionEditor(MoveAction action) {
        moveAction = action;

        setupEditor();

        setupBindings();
        setupListener();
    }

    @Override
    public void setupEditor() {
        valueField.setText(moveAction.getDistance());

        hBox.getChildren().addAll(new Label("Avancer de"), valueField, new Label("pixels"));

        errorLabel.setTextFill(Color.RED);
        getChildren().add(errorLabel);
    }

    private void setupBindings() {
        errorLabel.visibleProperty().bind(moveAction.isValidProperty().not());
        errorLabel.managedProperty().bind(errorLabel.visibleProperty());
    }

    private void setupListener() {
        valueField.textProperty().addListener((_, _, newText) ->
                moveAction.setDistance(newText));
    }
}