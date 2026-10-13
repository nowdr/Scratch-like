package scratch.view;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import scratch.model.Action;
import scratch.model.ActionType;
import scratch.viewmodel.ListsViewModel;

public class ListsView extends GridPane {
    private final ListsViewModel listsViewModel;
    private final ListView<Action> paletteList = new ListView<>();
    private final ListView<Action> programList = new ListView<>();
    private final Button addButton = new Button("Ajouter au programme");
    private final Button moveUpButton = new Button("Monter");
    private final Button moveDownButton = new Button("Descendre");
    private final Button duplicateButton = new Button("Dupliquer");
    private final Button removeButton = new Button("Supprimer");
    private final Button clearButton = new Button("Vider tout");
    private final VBox editorContainer = new VBox();
    private final TitledPane editorTiltedPane = new TitledPane("Détail de l'action", editorContainer);
    private final Label executionErrorLabel = new Label();

    public ListsView(ListsViewModel listsViewModel) {
        this.listsViewModel = listsViewModel;

        initializeLists();
        setupLayout();

        setupBindings();
        setupHandlers();
        setupListeners();
    }

    private void initializeLists() {
        paletteList.setItems(listsViewModel.getPaletteList());
        paletteList.setCellFactory(_ -> new ActionFormatCell(false));

        programList.setItems(listsViewModel.getProgramList());
        programList.setCellFactory(_ -> new ActionFormatCell(true));
    }

    private void setupLayout() {
        setHgap(30);
        setVgap(10);

        HBox buttons = new HBox(10);
        buttons.setPadding(new Insets(10));
        buttons.getChildren().addAll(moveUpButton, moveDownButton, duplicateButton, removeButton, clearButton);

        editorTiltedPane.setExpanded(false);

        editorContainer.setPadding(new Insets(10));

        executionErrorLabel.setTextFill(Color.RED);

        VBox controls = new VBox(10);
        controls.setAlignment(Pos.CENTER);
        controls.getChildren().addAll(buttons, editorTiltedPane, executionErrorLabel);

        add(new Label("Palette d'actions"), 0, 0);
        add(paletteList, 0, 1);
        add(addButton, 0, 2);

        add(new Label("Programme"), 1, 0);
        add(programList, 1, 1);
        add(controls, 1, 2);

        GridPane.setVgrow(paletteList, Priority.ALWAYS);
        GridPane.setHgrow(paletteList, Priority.ALWAYS);

        GridPane.setVgrow(programList, Priority.ALWAYS);
        GridPane.setHgrow(programList, Priority.ALWAYS);

        GridPane.setValignment(addButton, VPos.TOP);
    }

    private void setupBindings() {
        // lie l'action sélectionnée dans la palette avec l'action correspondante
        listsViewModel.selectedPaletteActionProperty().bind(paletteList.getSelectionModel().selectedItemProperty());

        // désactive les différents boutons quand ils doivent l'être
        addButton.disableProperty().bind(listsViewModel.canAdd().not());
        moveUpButton.disableProperty().bind(listsViewModel.canMoveUp().not());
        moveDownButton.disableProperty().bind(listsViewModel.canMoveDown().not());
        duplicateButton.disableProperty().bind(programList.getSelectionModel().selectedItemProperty().isNull());
        removeButton.disableProperty().bind(listsViewModel.canRemove().not());
        clearButton.disableProperty().bind(listsViewModel.canClear().not());

        editorTiltedPane.disableProperty().bind(listsViewModel.actionForEditorProperty().isNull());

        executionErrorLabel.textProperty().bind(listsViewModel.executionErrorProperty());
        executionErrorLabel.visibleProperty().bind(executionErrorLabel.textProperty().isNotEmpty());
        executionErrorLabel.managedProperty().bind(executionErrorLabel.visibleProperty());
    }

    private void setupHandlers() {
//      permet d'ajouter une action au programme avec le double-click
        paletteList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                listsViewModel.addToProgram();
            }
        });

//      connexion entre les boutons et leurs actions respectives
        addButton.setOnAction(_ -> listsViewModel.addToProgram());
        moveUpButton.setOnAction(_ -> listsViewModel.moveUp());
        moveDownButton.setOnAction(_ -> listsViewModel.moveDown());
        duplicateButton.setOnAction(_ -> listsViewModel.duplicate());
        removeButton.setOnAction(_ -> listsViewModel.remove());
        clearButton.setOnAction(_ -> listsViewModel.clear());
    }

    private void setupListeners() {
//      désactive et vide l'éditeur quand on interagit avec la palette
        paletteList.focusedProperty().addListener(
                (_, _, newValue) -> {
                    if (newValue) {
                        listsViewModel.setActionForEditor(null);
                    }
                }
        );

//      active l'éditeur et y charge l'action actuellement sélectionnée quand on interagit avec le programme
        programList.focusedProperty().addListener(
                (_, _, newValue) -> {
                    if (newValue) {
                        Action action = programList.getSelectionModel().getSelectedItem();

                        if (action != null) {
                            listsViewModel.setActionForEditor(action);
                        }
                    }
                }
        );

//      supprime l'ancien éditeur et en génère un nouveau correspondant à la nouvelle action ciblée
        listsViewModel.actionForEditorProperty().addListener(
                (_, _, newAction) -> {
                    editorContainer.getChildren().clear();

                    if (newAction != null) {
                        Node editor = ActionEditorFactory.createEditor(newAction);
                        editorContainer.getChildren().add(editor);
                    }
                }
        );

//      synchronise la sélection visuelle de la liste avec l'avancement du programme pendant son exécution
        listsViewModel.executionIndexProperty().addListener(
                (_, _, newValue) ->
                        programList.getSelectionModel().select(newValue.intValue())
        );

//      synchronisation de l'index de sélection de la vue avec celui de la vue-modèle
        programList.getSelectionModel().selectedIndexProperty().addListener(
                (_, _, newValue) -> {
                    if (newValue.intValue() != listsViewModel.getSelectedProgramIndex()) {
                        listsViewModel.setSelectedProgramIndex(newValue.intValue());
                    }
                }
        );

//      synchronisation de l'index de sélection de la vue-modèle avec celui de la vue
        listsViewModel.selectedProgramIndexProperty().addListener(
                (_, _, newValue) -> {
                    if (newValue.intValue() != programList.getSelectionModel().getSelectedIndex()) {
                        programList.getSelectionModel().select(newValue.intValue());
                    }
                }
        );

//      transmission de l'action sélectionnée à l'éditeur
        programList.getSelectionModel().selectedItemProperty().addListener(
                (_, _, newAction) -> {
                    listsViewModel.setActionForEditor(newAction);
                }
        );

//      synchronisation entre la désactivation et la fermeture de l'éditeur
        editorTiltedPane.disableProperty().addListener((_, _, newValue) ->
                editorTiltedPane.setExpanded(!newValue));
    }

    private static class ActionFormatCell extends ListCell<Action> {
        private final Circle circle = new Circle(4);
        private final boolean isEditable;

        private ActionFormatCell(boolean isEditable) {
            this.isEditable = isEditable;
            setGraphicTextGap(8);
        }

        @Override
        protected void updateItem(Action item, boolean empty) {
            super.updateItem(item, empty);

            textProperty().unbind();

            if (empty || item == null) {
                setGraphic(null);
                setText(null);
                setPadding(Insets.EMPTY);
            } else {
                switch (item.getType()) {
                    case MOVE_FORWARD -> {
                        setTextFill(Color.BLUE);
                        circle.setFill(Color.BLUE);
                    }
                    case TURN_LEFT, TURN_RIGHT -> {
                        setTextFill(Color.RED);
                        circle.setFill(Color.RED);
                    }
                    case PEN_UP, PEN_DOWN -> {
                        setTextFill(Color.GREEN);
                        circle.setFill(Color.GREEN);
                    }
                    case VAR_DECLARATION, VAR_ASSIGNMENT, INCREMENT_VARIABLE -> {
                        setTextFill(Color.DARKCYAN);
                        circle.setFill(Color.DARKCYAN);
                    }
                    case REPEAT, END_REPEAT -> {
                        setTextFill(Color.PURPLE);
                        circle.setFill(Color.PURPLE);
                    }
                    default -> {
                        setTextFill(Color.BLACK);
                        circle.setFill(Color.BLACK);
                    }
                }

                setGraphic(circle);

                if (isEditable) {
                    textProperty().bind(item.displayTextProperty());
                } else {
                    setText(item.toString());
                }

                int indentLevel = computeIndentLevel(getIndex(), getListView().getItems());

                if (item.getType() == ActionType.END_REPEAT) {
                    if (indentLevel != 0)
                        --indentLevel;
                }

                setPadding(new Insets(0, 0, 0, indentLevel * 15));
            }
        }

        private int computeIndentLevel(int index, ObservableList<Action> items) {
            int indentLevel = 0;
            int max = Math.min(index, items.size());

            for (int i = 0; i < max; ++i) {
                Action action = items.get(i);

                switch (action.getType()) {
                    case REPEAT -> ++indentLevel;
                    case END_REPEAT -> indentLevel = Math.max(0, indentLevel - 1);
                }
            }
            return indentLevel;
        }
    }
}