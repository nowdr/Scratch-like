package scratch.model;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ChosenActions {
    private final ObservableList<Action> chosenActionsList = FXCollections.observableArrayList(
            action -> new Observable[]{action.displayTextProperty()}
    );

    public ObservableList<Action> getChosenActionsList() {
        return chosenActionsList;
    }

    public Action get(int index) {
        return chosenActionsList.get(index);
    }

    public void add(Action action) {
        chosenActionsList.add(action);
    }

    public void add(int index, Action action) {
        chosenActionsList.add(index, action);
    }

    public void moveUp(int index) {
        if (index > 0 && index < chosenActionsList.size()) {
            Action action = chosenActionsList.remove(index);
            chosenActionsList.add(index - 1, action);
        }
    }

    public void moveDown(int index) {
        if (index >= 0 && index < chosenActionsList.size() - 1) {
            Action action = chosenActionsList.remove(index);
            chosenActionsList.add(index + 1, action);
        }
    }

    public void duplicate(Action action) {
        chosenActionsList.add(action.copy());
    }

    public void remove(int index) {
        if (index >= 0 && index < chosenActionsList.size()) {
            chosenActionsList.remove(index);
        }
    }

    public void clear() {
        chosenActionsList.clear();
    }

    public int size() {
        return chosenActionsList.size();
    }

    public boolean isEmpty() {
        return chosenActionsList.isEmpty();
    }
}
