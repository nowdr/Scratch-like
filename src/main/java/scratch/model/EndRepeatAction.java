package scratch.model;

import javafx.beans.binding.Bindings;

public class EndRepeatAction extends Action {

    public EndRepeatAction() {
        super(ActionType.END_REPEAT);

        displayTextProperty().bind(Bindings.concat(toString()));
    }

    @Override
    public void execute(ActionContext actionContext) {
    }

    @Override
    public Action copy() {
        return new EndRepeatAction();
    }

    @Override
    public String toString() {
        return "Fin répéter";
    }
}