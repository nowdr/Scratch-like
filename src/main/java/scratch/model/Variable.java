package scratch.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class Variable {
    private final StringProperty name;
    private final IntegerProperty value;

    public Variable(StringProperty name, IntegerProperty value) {
        this.name = name;
        this.value = value;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public int getValue() {
        return value.get();
    }

    public void setValue(int value) {
        this.value.set(value);
    }

    public IntegerProperty valueProperty() {
        return value;
    }
}
