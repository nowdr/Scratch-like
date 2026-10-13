package scratch.viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import scratch.model.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ListsViewModel {
    private final Executor executor;
    private final ObjectProperty<Action> selectedPaletteAction = new SimpleObjectProperty<>();
    private final IntegerProperty selectedProgramIndex = new SimpleIntegerProperty(-1);
    private final ObjectProperty<Action> actionForEditor = new SimpleObjectProperty<>();

    public ListsViewModel(Executor executor) {
        this.executor = executor;
    }

    public StringProperty executionErrorProperty() {
        return executor.executionErrorLabelProperty();
    }

    public ObjectProperty<Action> selectedPaletteActionProperty() {
        return selectedPaletteAction;
    }

    public int getSelectedProgramIndex() {
        return selectedProgramIndex.get();
    }

    public void setSelectedProgramIndex(int selectedProgramIndex) {
        this.selectedProgramIndex.set(selectedProgramIndex);
    }

    public IntegerProperty selectedProgramIndexProperty() {
        return selectedProgramIndex;
    }

    public void setActionForEditor(Action action) {
        actionForEditor.set(action);
    }

    public ObjectProperty<Action> actionForEditorProperty() {
        return actionForEditor;
    }

    public IntegerProperty executionIndexProperty() {
        return executor.executionIndexProperty();
    }

    public ObservableList<Action> getPaletteList() {
        return Actions.getActionsList();
    }

    public ObservableList<Action> getProgramList() {
        return executor.getProgramList();
    }

    public void addToProgram() {
        int index = getSelectedProgramIndex();
        Action action = selectedPaletteAction.get().copy();

        if (index >= 0) {
            executor.addToProgram(index + 1, action);
            setSelectedProgramIndex(index + 1);
        } else {
            executor.addToProgram(action);
            setSelectedProgramIndex(executor.programSize() - 1);
        }
    }

    public void moveUp() {
        int index = getSelectedProgramIndex();

        if (index > 0 && index < executor.programSize()) {
            executor.moveUpInProgram(index);
            setSelectedProgramIndex(index - 1);
        }
    }

    public void moveDown() {
        int index = getSelectedProgramIndex();

        if (index >= 0 && index < executor.programSize() - 1) {
            executor.moveDownInProgram(index);
            setSelectedProgramIndex(index + 1);
        }
    }

    public void duplicate() {
        int index = getSelectedProgramIndex();

        if (index >= 0 && index < executor.programSize()) {
            Action action = executor.getFromProgram(index);
            executor.duplicateInProgram(action);
            setSelectedProgramIndex(executor.programSize() - 1);
        }
    }

    public void remove() {
        int index = getSelectedProgramIndex();

        if (index >= 0) {
            executor.removeFromProgram(index);
            setSelectedProgramIndex(index < executor.programSize() ? index : index - 1);
        }
    }

    public void clear() {
        executor.clearProgram();
        setSelectedProgramIndex(-1);
    }

    public BooleanBinding canAdd() {
        return Bindings.isNotNull(selectedPaletteAction);
    }

    public BooleanBinding canMoveUp() {
        return selectedProgramIndex.greaterThan(0);
    }

    public BooleanBinding canMoveDown() {
        return Bindings.createBooleanBinding(()
                -> getSelectedProgramIndex() >= 0 && getSelectedProgramIndex() < executor.programSize() - 1,
                selectedProgramIndex,
                executor.getProgramList());
    }

    public BooleanBinding canRemove() {
        return selectedProgramIndex.greaterThanOrEqualTo(0);
    }

    public BooleanBinding canClear() {
        return Bindings.isNotEmpty(executor.getProgramList());
    }

    public void saveProgram(File file) throws IOException {
        ProgramIO.save(executor.getProgramList(), file);
    }

    public void loadProgram(File file) throws IOException {
        List<Action> actionsList = ProgramIO.load(file);

        clear();

        for (Action action : actionsList) {
            executor.addToProgram(action);
        }
    }
}