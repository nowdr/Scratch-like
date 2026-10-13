package scratch.view;

import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import scratch.model.ColorAction;
import scratch.model.Constants;

public class ColorActionEditor extends ActionEditor {
    private final ColorAction colorAction;
    private final ColorPicker colorPicker = new ColorPicker();

    public ColorActionEditor(ColorAction action) {
        colorAction = action;

        setupEditor();

        setupBinding();
    }

    @Override
    public void setupEditor() {
        colorPicker.setPrefWidth(Constants.EDITOR_CONTROLS_PREFERRED_WIDTH);

        hBox.getChildren().addAll(new Label("Colorier en"), colorPicker);
    }

    private void setupBinding() {
        colorPicker.valueProperty().bindBidirectional(colorAction.colorProperty());
    }
}