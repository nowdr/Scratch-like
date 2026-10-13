package scratch.view;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public abstract class ActionEditor extends VBox {
    protected final HBox hBox = new HBox();

    public ActionEditor() {
        hBox.setSpacing(5);
        hBox.setAlignment(Pos.CENTER_LEFT);

        setSpacing(5);
        getChildren().add(hBox);
    }

    public abstract void setupEditor();
}
