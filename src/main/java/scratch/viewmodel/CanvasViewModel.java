package scratch.viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import scratch.model.*;

public class CanvasViewModel {
    private final Executor executor;
    private final ObservableList<Variable> variableObservableList = FXCollections.observableArrayList();
    private final ObjectProperty<Point> cursorPosition = new SimpleObjectProperty<>();
    private final DoubleProperty cursorAngle = new SimpleDoubleProperty();
    private final BooleanProperty cursorIsPenDown = new SimpleBooleanProperty();
    private final ObjectProperty<Color> cursorColor = new SimpleObjectProperty<>();
    private final BooleanProperty isLoaded = new SimpleBooleanProperty(false);
    private final BooleanProperty isAutomatic = new SimpleBooleanProperty(false);
    private final BooleanProperty isRunning = new SimpleBooleanProperty(false);

    public CanvasViewModel(Executor executor) {
        this.executor = executor;

        setupBindings();
        setupListeners();
    }

    public int getExecutionIndex() {
        return executor.getExecutionIndex();
    }

    public Point getCursorPosition() {
        return cursorPosition.get();
    }

    public ObjectProperty<Point> cursorPositionProperty() {
        return cursorPosition;
    }

    public double getCursorAngle() {
        return cursorAngle.get();
    }

    public DoubleProperty cursorAngleProperty() {
        return cursorAngle;
    }

    public boolean getCursorIsPenDown() {
        return cursorIsPenDown.get();
    }

    public Color getCursorColor() {
        return cursorColor.get();
    }

    public ObservableList<Variable> getVariableObservableList() {
        return variableObservableList;
    }

    public boolean getIsLoaded() {
        return isLoaded.get();
    }

    public void setIsLoaded(boolean isLoaded) {
        this.isLoaded.set(isLoaded);
    }

    public BooleanProperty isAutomaticProperty() {
        return isAutomatic;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning.set(isRunning);
    }

    public DoubleProperty executionSpeedProperty() {
        return executor.executionSpeedProperty();
    }

    public BooleanBinding canUseLoadResetButton() {
        return Bindings.createBooleanBinding(
                () -> executor.getValidProgram() && (!(isLoaded.get() || executor.isProgramEmpty()) || isLoaded.get()),
                executor.validProgramProperty(), isLoaded, executor.getProgramList()
        );
    }

    public BooleanBinding canUseExecuteNextButton() {
        return Bindings.createBooleanBinding(
                () -> isLoaded.get() && (isAutomatic.get() ? !isRunning.get() : !executor.getExecutionFinished()),
                isLoaded, isAutomatic, isRunning, executor.executionFinishedProperty()
        );
    }

    public BooleanBinding canUseStopButton() {
        return Bindings.createBooleanBinding(
                () -> isRunning.get() && !executor.getExecutionFinished(),
                isRunning, executor.executionFinishedProperty()
        );
    }

    public StringBinding loadResetButtonTextBinding() {
        return Bindings.createStringBinding(
                () -> !isLoaded.get() ? "Charger" : "Ré-initialiser",
                isLoaded
        );
    }

    public StringBinding executeNextButtonTextBinding() {
        return Bindings.createStringBinding(
                () -> isAutomatic.get()  ? "Exécuter" : (executor.getExecutionIndex() == -1
                        || executor.getExecutionFinished() ? "Exécuter" : "Suivant"),
                isAutomatic, executor.executionIndexProperty(), executor.executionFinishedProperty()
        );
    }

    public void startAutomaticExecution() {
        executor.startAutomaticExecution();
    }

    public void stopAutomaticExecution() {
        executor.stopAutomaticExecution();
    }

    public void step() {
        executor.step();
    }

    public void reset() {
        executor.reset();
        variableObservableList.clear();
    }

    private void setupBindings() {
        cursorPosition.bind(executor.cursorPositionProperty());
        cursorAngle.bind(executor.cursorAngleProperty());
        cursorIsPenDown.bind(executor.cursorIsPenDownProperty());
        cursorColor.bind(executor.cursorColorProperty());
    }

    private void setupListeners() {
        executor.getVariableManagerMap().addListener(
                (MapChangeListener<String, Variable>) change ->
                        variableObservableList.add(change.getValueAdded()));

        executor.getProgramList().addListener(
                (ListChangeListener<Action>) _ -> {
                    isLoaded.set(false);
                    if (isRunning.get()){
                        stopAutomaticExecution();
                        isRunning.set(false);
                    }
                });
    }
}