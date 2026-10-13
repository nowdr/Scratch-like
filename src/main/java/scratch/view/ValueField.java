package scratch.view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import scratch.model.Constants;

public class ValueField extends HBox {
    private final Button minusButton = new Button("-");
    private final TextField valueField = new TextField();
    private final Button plusButton = new Button("+");
    private final BooleanProperty isInteger = new SimpleBooleanProperty();

    public ValueField() {
        setupLayout();

        setupBindings();
        setupHandlers();
        setupListener();
    }

    public void setText(String string) {
        valueField.setText(string);
    }

    public StringProperty textProperty() {
        return valueField.textProperty();
    }

    private void setupLayout() {
        valueField.setPrefWidth(Constants.EDITOR_CONTROLS_PREFERRED_WIDTH);
        valueField.setAlignment(Pos.CENTER);

        setSpacing(5);
        setAlignment(Pos.CENTER_LEFT);

        getChildren().addAll(minusButton, valueField, plusButton);
    }

    private void setupBindings() {
        minusButton.visibleProperty().bind(isInteger);
        minusButton.managedProperty().bind(minusButton.visibleProperty());

        plusButton.visibleProperty().bind(isInteger);
        plusButton.managedProperty().bind(plusButton.visibleProperty());
    }

    private void setupHandlers() {
        minusButton.setOnAction(_ -> {
            int value = Integer.parseInt(valueField.getText());
            valueField.setText(String.valueOf(--value));
        });

        plusButton.setOnAction(_ -> {
            int value = Integer.parseInt(valueField.getText());
            valueField.setText(String.valueOf(++value));
        });
    }

    private void setupListener() {
        valueField.textProperty().addListener((_, _, newText) ->
                isInteger.set(newText.matches(Constants.INTEGER_VALUE_REGEX)));
    }
}
