package scratch.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

public class Actions {
    private static final ObservableList<Action> actionsList = FXCollections.observableArrayList(
            new MoveAction(),
            new RotateAction(ActionType.TURN_LEFT),
            new RotateAction(ActionType.TURN_RIGHT),
            new PenAction(ActionType.PEN_UP),
            new PenAction(ActionType.PEN_DOWN),
            new ColorAction(),
            new VariableDeclarationAction(),
            new VariableAssignmentAction(),
            new VariableIncrementationAction(),
            new RepeatAction(),
            new EndRepeatAction()
    );

    public static ObservableList<Action> getActionsList() {
        return actionsList;
    }

    public static Action builder(ActionType type, String param, String param2) {
        return switch (type) {
            case MOVE_FORWARD -> {
                MoveAction moveAction = new MoveAction();
                if (param != null) {
                    moveAction.setDistance(param);
                }
                yield moveAction; // permet de return l'action + modif value (évite de le faire en 2 temps)
            }
            case TURN_LEFT, TURN_RIGHT -> {
                RotateAction rotateAction = new RotateAction(type);
                if (param != null) {
                    rotateAction.setAngle(param);
                }
                yield rotateAction;
            }
            case PEN_UP, PEN_DOWN -> new PenAction(type);
            case COLOR -> {
                ColorAction colorAction = new ColorAction();
                if (param != null) {
                    colorAction.setColor(Color.valueOf(param));
                }
                yield colorAction;
            }
            case VAR_DECLARATION -> {
                VariableDeclarationAction variableDeclarationAction = new VariableDeclarationAction();
                if (param != null) {
                    variableDeclarationAction.setVariableName(param);
                }
                yield variableDeclarationAction;
            }
            case VAR_ASSIGNMENT -> {
                VariableAssignmentAction variableAssignmentAction = new VariableAssignmentAction();
                if (param != null) {
                    variableAssignmentAction.setVariableName(param);
                }
                if (param2 != null) {
                    variableAssignmentAction.setVariableValue(param2);
                }
                yield variableAssignmentAction;
            }
            case INCREMENT_VARIABLE -> {
                VariableIncrementationAction variableIncrementationAction = new VariableIncrementationAction();
                if (param != null) {
                    variableIncrementationAction.setVariableName(param);
                }
                if (param2 != null) {
                    variableIncrementationAction.setStep(param2);
                }
                yield variableIncrementationAction;
            }
            case REPEAT -> {
                RepeatAction repeatAction = new RepeatAction();
                if (param != null) {
                    repeatAction.setCount(param);
                }
                yield repeatAction;
            }
            case END_REPEAT -> new EndRepeatAction();
        };
    }
}
