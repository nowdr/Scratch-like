package scratch.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class VariableManager {
    private final ObservableMap<String, Variable> variableObservableMap = FXCollections.observableHashMap();

    public ObservableMap<String, Variable> getVariableObservableMap() {
        return variableObservableMap;
    }

    public Variable get(String name) {
        return variableObservableMap.get(name);
    }

    public void add(String name) {
        if (!variableObservableMap.containsKey(name)) {
            Variable variable = new Variable(new SimpleStringProperty(name), new SimpleIntegerProperty(0));
            variableObservableMap.put(name, variable);
        }
    }

    public void set(String name, int value) {
        if (variableObservableMap.containsKey(name)) {
            Variable variable = variableObservableMap.get(name);
            variable.setValue(value);
        }
    }

    public void update(String name, int value) {
        if (variableObservableMap.containsKey(name)) {
            Variable variable = variableObservableMap.get(name);
            variable.setValue(variable.getValue() + value);
        }
    }

    public void reset() {
        variableObservableMap.clear();
    }
}
