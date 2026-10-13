package scratch.view;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import scratch.model.RepeatAction;

public class RepeatActionEditor extends ActionEditor {
    private final RepeatAction repeatAction;
    private final ValueField valueField = new ValueField();
    private final Label errorLabel = new Label("Entrez une valeur supérieure à 0.");

    public RepeatActionEditor(RepeatAction repeatAction) {
        this.repeatAction = repeatAction;

        setupEditor();

        setupBindings();
        setupListener();
    }


    @Override
    public void setupEditor() {
        valueField.setText(repeatAction.getCount());

        hBox.getChildren().addAll(new Label("Répéter"), valueField, new Label("fois"));

        errorLabel.setTextFill(Color.RED);
        getChildren().add(errorLabel);
    }

    private void setupBindings() {
        errorLabel.visibleProperty().bind(repeatAction.isValidProperty().not());
        errorLabel.managedProperty().bind(errorLabel.visibleProperty());
    }

    private void setupListener() {
        valueField.textProperty().addListener((_, _, newText) ->
                repeatAction.setCount(newText));
    }
}
