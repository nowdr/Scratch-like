package scratch.model;

import java.util.ArrayList;
import java.util.List;

public abstract class CompositeAction extends Action {
    protected final List<Action> children = new ArrayList<>();

    public CompositeAction(ActionType type) {
        super(type);
    }

    public List<Action> getChildren() {
        return children;
    }

    public void add(Action action) {
        children.add(action);
    }

    public void remove(Action action) {
        children.remove(action);
    }
}
