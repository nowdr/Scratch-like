package scratch.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import scratch.viewmodel.CanvasViewModel;
import scratch.viewmodel.ListsViewModel;
import java.io.File;

public class MainView extends VBox {
    private final ListsViewModel listsViewModel;
    private final CanvasViewModel canvasViewModel;
    private final MenuItem loadMenuItem = new MenuItem("Charger");
    private final MenuItem saveMenuItem = new MenuItem("Sauver");

    public MainView(ListsViewModel listsViewModel, CanvasViewModel canvasViewModel) {
        this.listsViewModel = listsViewModel;
        this.canvasViewModel = canvasViewModel;

        setupLayout();
        setupBindings();
    }

    private void setupLayout() {
        Menu menu = new Menu("Fichier");
        menu.getItems().addAll(loadMenuItem, saveMenuItem);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(menu);

        HBox root = new HBox();
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(10));
        root.setSpacing(30);

        root.getChildren().addAll(new ListsView(listsViewModel), new CanvasView(canvasViewModel));
        getChildren().addAll(menuBar, root);
    }

    private void setupBindings() {
        saveMenuItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Scratch (*.scr)", "*.scr"));
            File file = fileChooser.showSaveDialog(this.getScene().getWindow());

            if (file != null) {
                try {
                    listsViewModel.saveProgram(file);
                } catch (Exception exception) {
                    showError("Erreur de sauvegarde", "Impossible d'enregistrer le fichier : " + exception.getMessage());
                }
            }
        });

        loadMenuItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Scratch (*.scr)", "*.scr"));
            File file = fileChooser.showOpenDialog(this.getScene().getWindow());

            if (file != null) {
                try {
                    listsViewModel.loadProgram(file);
                } catch (Exception exception) {
                    showError("Erreur de chargement", "Le fichier est corrompu ou illisible.");
                }
            }
        });
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
