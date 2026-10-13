package scratch.model;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.*;

public class Executor {
    private final ChosenActions chosenActions;
    private final Cursor cursor;
    private final VariableManager variableManager;
    private final Deque<ExecutionContext> executionContextDeque = new ArrayDeque<>();
    private final HashMap<Action, Integer> indexHashMap = new HashMap<>();
    private final Timeline timeline = new Timeline();
    private final BooleanProperty validProgram = new SimpleBooleanProperty(false);
    private final BooleanProperty executionFinished = new SimpleBooleanProperty(false);
    private final IntegerProperty executionIndex = new SimpleIntegerProperty(-1);
    private final DoubleProperty executionSpeed = new SimpleDoubleProperty(0.5);
    private final StringProperty executionErrorLabel = new SimpleStringProperty("");

    public Executor(ChosenActions chosenActions,Cursor cursor, VariableManager variableManager) {
        this.chosenActions = chosenActions;
        this.cursor = cursor;
        this.variableManager = variableManager;

        setupListeners();
    }

    public ObservableList<Action> getProgramList() {
        return chosenActions.getChosenActionsList();
    }

    public Action getFromProgram(int index) {
        return chosenActions.get(index);
    }

    public void addToProgram(Action action) {
        chosenActions.add(action);
    }

    public void addToProgram(int index, Action action) {
        chosenActions.add(index, action);
    }

    public void moveUpInProgram(int index) {
        chosenActions.moveUp(index);
    }

    public void moveDownInProgram(int index) {
        chosenActions.moveDown(index);
    }

    public void duplicateInProgram(Action action) {
        chosenActions.duplicate(action);
    }

    public void removeFromProgram(int index) {
        chosenActions.remove(index);
    }

    public void clearProgram() {
        chosenActions.clear();
    }

    public int programSize() {
        return chosenActions.size();
    }

    public boolean isProgramEmpty() {
        return chosenActions.isEmpty();
    }

    public ObjectProperty<Point> cursorPositionProperty() {
        return cursor.positionProperty();
    }

    public DoubleProperty cursorAngleProperty() {
        return cursor.angleProperty();
    }

    public BooleanProperty cursorIsPenDownProperty() {
        return cursor.penDownProperty();
    }

    public ObjectProperty<Color> cursorColorProperty() {
        return cursor.colorProperty();
    }

    public ObservableMap<String, Variable> getVariableManagerMap() {
        return variableManager.getVariableObservableMap();
    }

    public boolean getValidProgram() {
        return validProgram.get();
    }

    public BooleanProperty validProgramProperty() {
        return validProgram;
    }

    public boolean getExecutionFinished() {
        return executionFinished.get();
    }

    public BooleanProperty executionFinishedProperty() {
        return executionFinished;
    }

    public int getExecutionIndex() {
        return executionIndex.get();
    }

    public IntegerProperty executionIndexProperty() {
        return executionIndex;
    }

    public DoubleProperty executionSpeedProperty() {
        return executionSpeed;
    }

    public StringProperty executionErrorLabelProperty() {
        return executionErrorLabel;
    }

    public void setupTimeline() {
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame keyFrame = new KeyFrame(
                Duration.seconds(executionSpeed.get()),
                _ -> {
                    if (executionIndex.get() < 0 || !executionContextDeque.isEmpty()) {
                        step();
                    }else {
                        stopAutomaticExecution();
                    }
                }
        );
        timeline.getKeyFrames().setAll(keyFrame);
    }

    public void startAutomaticExecution() {
        setupTimeline();
        timeline.play();
    }

    public void stopAutomaticExecution() {
        timeline.stop();
    }

    public void step() {
        if (executionIndex.get() < 0) {
            List<Action> program = compileProgram();
            ExecutionContext executionContext = new ExecutionContext(program, 1);
            executionContextDeque.push(executionContext);

        } else {
            ExecutionContext executionContext = executionContextDeque.peek();

            if (!executionContextDeque.isEmpty()) {
                Action action = executionContext.getAction();

                if (action.getType() == ActionType.REPEAT) {
                    RepeatAction repeatAction = (RepeatAction) action;
                    int count = repeatAction.resolveValue(repeatAction.getCount(), new ActionContext(cursor, variableManager));

                    if (count > 0 && !repeatAction.getChildren().isEmpty()) {
                        executionContextDeque.push(new ExecutionContext(repeatAction.getChildren(), count));
                    }
                } else {
                    try {
                        action.execute(new ActionContext(cursor, variableManager));
                    } catch (IllegalArgumentException e) {
                        stopAutomaticExecution();
                        executionErrorLabel.set(e.getMessage());
                        return;
                    }

                    executionContext.setIndex(executionContext.getIndex() + 1);
                }
            }

            while (!executionContextDeque.isEmpty() &&
                    executionContext.getIndex() >= executionContext.getActionList().size()) {
                int remainingCount = executionContext.getRemainingCount();

                if (remainingCount > 1) {
                    executionContext.setRemainingCount(remainingCount - 1);
                    executionContext.setIndex(0);
                } else {
                    executionContextDeque.pop();

                    if (!executionContextDeque.isEmpty()) {
                        executionContext = executionContextDeque.peek();
                        executionContext.setIndex(executionContext.getIndex() + 1);
                    }
                }
            }
        }

        if (!executionContextDeque.isEmpty()) {
            executionIndex.set(indexHashMap.get(executionContextDeque.peek().getAction()));
        } else {
            executionFinished.set(true);
        }
    }

    public void reset() {
        cursor.reset();
        variableManager.reset();
        executionContextDeque.clear();
        indexHashMap.clear();
        executionFinished.set(false);
        executionIndex.set(-1);
        executionErrorLabel.set("");
    }

    private boolean validateProgram() {
        boolean penDown = true;
        boolean inDeclarationZone = true;
        HashSet<String> variableList = new HashSet<>();
        Stack<String> lockedVariables = new Stack<>();
        int repeatDepth = 0;

        for (Action action : chosenActions.getChosenActionsList()) {
            if (!action.isValidProperty().get()) {
                return false;
            }

            if (action.getType() != ActionType.VAR_DECLARATION) {
                inDeclarationZone = false;
            }

            switch (action.getType()) {
                case MOVE_FORWARD -> {
                    MoveAction moveAction = (MoveAction) action;
                    String distance = moveAction.getDistance();

                    if (distance.matches(Constants.VARIABLE_NAME_REGEX)
                            && !variableList.contains(distance)) {
                        return false;
                    }
                }
                case TURN_LEFT, TURN_RIGHT -> {
                    RotateAction rotateAction = (RotateAction) action;
                    String angle = rotateAction.getAngle();

                    if (angle.matches(Constants.VARIABLE_NAME_REGEX)
                            && !variableList.contains(angle)) {
                        return false;
                    }
                }
                case PEN_UP, PEN_DOWN -> {
                    PenAction penAction = (PenAction) action;

                    if (penDown == penAction.getIsDown()) {
                        return false;
                    }
                    penDown = penAction.getIsDown();
                }
                case VAR_DECLARATION -> {
                    if (!inDeclarationZone) {
                        return false;
                    }
                    VariableDeclarationAction variableDeclarationAction = (VariableDeclarationAction) action;
                    String variableName = variableDeclarationAction.getVariableName();

                    if (variableList.contains(variableName)) {
                        return false;
                    }
                    variableList.add(variableName);
                }
                case VAR_ASSIGNMENT -> {
                    VariableAssignmentAction variableAssignmentAction = (VariableAssignmentAction) action;
                    String variableValue = variableAssignmentAction.getVariableValue();

                    if (lockedVariables.contains(variableAssignmentAction.getVariableName())) { // verification que la variable est utilise
                        return false;
                    }

                    if (!variableList.contains(variableAssignmentAction.getVariableName())
                            || (variableValue.matches(Constants.VARIABLE_NAME_REGEX)
                            && !variableList.contains(variableValue))) {
                        return false;
                    }
                }
                case INCREMENT_VARIABLE -> {
                    VariableIncrementationAction variableIncrementationAction = (VariableIncrementationAction) action;
                    String step = variableIncrementationAction.getStep();

                    if (lockedVariables.contains(variableIncrementationAction.getVariableName())) {
                        return false;
                    }

                    if (!variableList.contains(variableIncrementationAction.getVariableName())
                            || (step.matches(Constants.VARIABLE_NAME_REGEX)
                            && !variableList.contains(step))) {
                        return false;
                    }

                }
                case REPEAT -> {
                    RepeatAction repeatAction = (RepeatAction) action;
                    String count = repeatAction.getCount();

                    if (count.matches(Constants.VARIABLE_NAME_REGEX)) {
                        lockedVariables.push(count); // on rajoute dans la pile le nom de la variable pour la bloquer
                        if (!variableList.contains(count)) {
                            return false;
                        }
                    }else
                        lockedVariables.push(""); // sinon on rajoute un étage vide
                    ++repeatDepth;

                }
                case END_REPEAT -> {
                    if (lockedVariables.isEmpty()) {
                        return false;
                    } else {
                        lockedVariables.pop(); // a la fin de repeat on efface l'etage
                        --repeatDepth;
                    }
                }
            }
        }
        return repeatDepth == 0;
    }

    private List<Action> compileProgram() {
        Deque<List<Action>> actionListDeque = new ArrayDeque<>();
        List<Action> actionList = new ArrayList<>();

        for (int i = 0; i < chosenActions.size(); ++i) {
            Action action = chosenActions.get(i);

            if (action.getType() == ActionType.END_REPEAT) {
                if (!actionListDeque.isEmpty()) {
                    actionList = actionListDeque.pop();
                }
            } else {
                Action copy = action.copy();
                actionList.add(copy);

                indexHashMap.put(copy, i);

                if (copy.getType() == ActionType.REPEAT) {
                    RepeatAction repeatAction = (RepeatAction) copy;
                    actionListDeque.push(actionList);
                    actionList = repeatAction.getChildren();
                }
            }
        }
        return actionList;
    }

    private void setupListeners() {
        chosenActions.getChosenActionsList().addListener(
                (ListChangeListener<Action>) _ -> validProgram.set(validateProgram())
        );

        executionSpeed.addListener((_, _, _) -> {
            if( timeline.getStatus() == Animation.Status.RUNNING) {
                timeline.stop();
                setupTimeline();
                timeline.play();
            }
        });
    }

    private static class ExecutionContext {
        private final List<Action> actionList;
        private int remainingCount;
        private int index = 0;

        public ExecutionContext(List<Action> actionList, int remainingCount) {
            this.actionList = actionList;
            this.remainingCount = remainingCount;
        }

        public List<Action> getActionList() {
            return actionList;
        }

        public int getRemainingCount() {
            return remainingCount;
        }

        public void setRemainingCount(int remainingCount) {
            this.remainingCount = remainingCount;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public Action getAction() {
            return actionList.get(index);
        }
    }
}