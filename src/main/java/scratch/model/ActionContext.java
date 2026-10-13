package scratch.model;

public class ActionContext {
    private final Cursor cursor;
    private final VariableManager variableManager;

    public ActionContext(Cursor cursor, VariableManager variableManager) {
        this.cursor = cursor;
        this.variableManager = variableManager;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public VariableManager getVariableManager() {
        return variableManager;
    }
}
