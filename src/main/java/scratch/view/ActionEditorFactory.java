package scratch.view;

import javafx.scene.Node;
import javafx.scene.control.Label;
import scratch.model.*;

public class ActionEditorFactory {
    public static Node createEditor(Action action) {
         switch (action.getType()) {
            case MOVE_FORWARD -> {
                return new MoveActionEditor((MoveAction) action);
            }
            case TURN_LEFT, TURN_RIGHT -> {
                return new RotateActionEditor((RotateAction) action);
            }
            case COLOR -> {
                return new ColorActionEditor((ColorAction) action);
            }
            case VAR_DECLARATION -> {
                return new VariableDeclarationActionEditor((VariableDeclarationAction) action);
            }
            case VAR_ASSIGNMENT -> {
                return new VariableAssignmentActionEditor((VariableAssignmentAction) action);
            }
            case INCREMENT_VARIABLE -> {
                return new VariableIncrementationActionEditor((VariableIncrementationAction) action);
            }
             case REPEAT -> {
                return new RepeatActionEditor((RepeatAction) action);
             }
            default -> {
                return new Label("Aucun paramètre configurable");
            }
         }
    }
}