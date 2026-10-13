package scratch.view;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import scratch.model.Constants;
import scratch.model.Variable;
import scratch.viewmodel.CanvasViewModel;

import java.util.Arrays;

public class CanvasView extends VBox {
    private final CanvasViewModel canvasViewModel;
    private final Canvas canvas = new Canvas(Constants.CANVAS_SIZE, Constants.CANVAS_SIZE);
    private final GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
    private final Group cursor = new Group();
    private final StackPane stackPane = new StackPane();
    private final Label cursorState = new Label();
    private final RadioButton automaticExecutionButton = new RadioButton("Exécution automatique");
    private final RadioButton manualExecutionButton = new RadioButton("Exécution manuelle");
    private final Button loadResetButton = new Button();
    private final Button executeNextButton = new Button();
    private final Button stopButton = new Button("Arrêter");
    private final Slider slider = new Slider(0.01, 5, 0.5);

    public CanvasView(CanvasViewModel canvasViewModel) {
        this.canvasViewModel = canvasViewModel;

        drawBorder(graphicsContext);
        drawGrid(graphicsContext);
        drawCursor();

        setupLayout();

        setupBindings();
        setupHandlers();
        setupListeners();
    }

    private void drawBorder(GraphicsContext graphicsContext) {
        final double canvasWidth = graphicsContext.getCanvas().getWidth();
        final double canvasHeight = graphicsContext.getCanvas().getHeight();

        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.setLineWidth(2);
        graphicsContext.strokeRect(0, 0, canvasWidth, canvasHeight);
    }

    private void drawGrid(GraphicsContext graphicsContext) {
        graphicsContext.setStroke(Color.LIGHTBLUE);
        graphicsContext.setLineWidth(1);

        for (double x = 0; x <= Constants.CANVAS_SIZE; x += Constants.CANVAS_SIZE / Constants.GRID_DIVISIONS) {
            graphicsContext.strokeLine(x, 0, x, Constants.CANVAS_SIZE);
        }

        for (double y = 0; y <= Constants.CANVAS_SIZE; y += Constants.CANVAS_SIZE / Constants.GRID_DIVISIONS) {
            graphicsContext.strokeLine(0, y, Constants.CANVAS_SIZE, y);
        }
    }

    private void drawCursor() {
        Polygon turtleBody = new Polygon(-6, -6, 6, 0, -6, 6);
        turtleBody.setFill(Color.DEEPSKYBLUE);
        turtleBody.setStroke(Color.BLACK);

        Circle turtleHead = new Circle(2);
        turtleHead.setCenterX(6);
        turtleHead.setCenterY(0);
        turtleHead.setFill(Color.GREEN);
        turtleHead.setStroke(Color.BLACK);

        cursor.getChildren().addAll(turtleBody, turtleHead);
    }

    private void setupLayout() {
        stackPane.setAlignment(Pos.CENTER);
        stackPane.setPrefSize(Constants.CANVAS_SIZE, Constants.CANVAS_SIZE);
        stackPane.getChildren().addAll(canvas, cursor);

        TableView<Variable> variablesTableView = new TableView<>(canvasViewModel.getVariableObservableList());
        variablesTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        variablesTableView.setMaxHeight(100);
        TableColumn<Variable, String> variableNameColumn = new TableColumn<>("Nom");
        variableNameColumn.setCellValueFactory(cellData ->
                cellData.getValue().nameProperty());
        TableColumn<Variable, Number> variableValueColumn = new TableColumn<>("Valeur");
        variableValueColumn.setCellValueFactory(cellData ->
                cellData.getValue().valueProperty());
        variablesTableView.getColumns().addAll(Arrays.asList(variableNameColumn, variableValueColumn));

        VBox informations = new VBox(10);
        informations.setAlignment(Pos.CENTER);
        informations.setPadding(new Insets(5));
        informations.setBorder(new Border(new BorderStroke(
                Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        informations.getChildren().addAll(cursorState, variablesTableView);

        ToggleGroup toggleGroup = new ToggleGroup();
        automaticExecutionButton.setToggleGroup(toggleGroup);
        manualExecutionButton.setToggleGroup(toggleGroup);
        manualExecutionButton.setSelected(true);

        HBox radioButtons = new HBox(10);
        radioButtons.setAlignment(Pos.CENTER);
        radioButtons.getChildren().addAll(automaticExecutionButton, manualExecutionButton);

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(loadResetButton, executeNextButton, stopButton);

        slider.setMajorTickUnit(1);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);

        VBox controls = new VBox(10);
        controls.getChildren().addAll(radioButtons, buttons, slider);

        setSpacing(10);
        getChildren().addAll(new Label("Scène"), stackPane, informations, controls);
    }

    private void setupBindings() {
        cursor.translateXProperty().bind(Bindings.createDoubleBinding(() ->
                canvasViewModel.getCursorPosition().getX() - Constants.INITIAL_X, canvasViewModel.cursorPositionProperty()));
        cursor.translateYProperty().bind(Bindings.createDoubleBinding(() ->
                canvasViewModel.getCursorPosition().getY() - Constants.INITIAL_Y, canvasViewModel.cursorPositionProperty()));
        cursor.rotateProperty().bind(canvasViewModel.cursorAngleProperty());

        cursorState.textProperty().bind(
                Bindings.createStringBinding(
                () -> {
                    double x = canvasViewModel.getCursorPosition().getX() - Constants.INITIAL_X;
                    double y = (canvasViewModel.getCursorPosition().getY() - Constants.INITIAL_Y) * -1 + 0;
                    double angle = ((canvasViewModel.getCursorAngle() - Constants.INITIAL_ANGLE) * -1 + 360) % 360;

                    return "Position: (" + x + ", " + y + ") | Direction: " + angle + "°";
                }, canvasViewModel.cursorPositionProperty(), canvasViewModel.cursorAngleProperty()));

        canvasViewModel.isAutomaticProperty().bind(automaticExecutionButton.selectedProperty());

        loadResetButton.disableProperty().bind(canvasViewModel.canUseLoadResetButton().not());
        executeNextButton.disableProperty().bind(canvasViewModel.canUseExecuteNextButton().not());
        stopButton.disableProperty().bind(canvasViewModel.canUseStopButton().not());

        loadResetButton.textProperty().bind(canvasViewModel.loadResetButtonTextBinding());
        executeNextButton.textProperty().bind(canvasViewModel.executeNextButtonTextBinding());

        stopButton.visibleProperty().bind(automaticExecutionButton.selectedProperty());
        stopButton.managedProperty().bind(stopButton.visibleProperty());

        slider.visibleProperty().bind(automaticExecutionButton.selectedProperty());
        slider.managedProperty().bind(slider.visibleProperty());
        slider.valueProperty().bindBidirectional(canvasViewModel.executionSpeedProperty());
    }

    private void setupHandlers() {
        loadResetButton.setOnAction(_ -> {
                    canvasViewModel.reset();

                    graphicsContext.clearRect(0, 0, Constants.CANVAS_SIZE, Constants.CANVAS_SIZE);

                    drawBorder(graphicsContext);
                    drawGrid(graphicsContext);
                    drawCursor();

                    if (!canvasViewModel.getIsLoaded()) {
                        canvasViewModel.setIsLoaded(true);
                    } else {
                        canvasViewModel.stopAutomaticExecution();
                        canvasViewModel.setIsRunning(false);
                    }
                }
        );

        executeNextButton.setOnAction(_ -> {
                    if(automaticExecutionButton.isSelected()) {
                        canvasViewModel.startAutomaticExecution();
                        canvasViewModel.setIsRunning(true);
                    } else {
                        canvasViewModel.step();
                    }
                }
        );

        stopButton.setOnAction(_ -> {
                    canvasViewModel.stopAutomaticExecution();
                    canvasViewModel.setIsRunning(false);
                }
        );

        manualExecutionButton.setOnAction(_ -> {
            canvasViewModel.stopAutomaticExecution();
        });
    }

    private void setupListeners() {
        canvasViewModel.cursorPositionProperty().addListener((_, oldPoint, newPoint) -> {
            if (canvasViewModel.getExecutionIndex() >= 0 && canvasViewModel.getCursorIsPenDown()) {
                graphicsContext.setStroke(canvasViewModel.getCursorColor());
                graphicsContext.setLineWidth(2);
                graphicsContext.strokeLine(oldPoint.getX(), oldPoint.getY(), newPoint.getX(), newPoint.getY());
            }
        });
    }
}