package scratch;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scratch.model.*;
import scratch.view.MainView;
import scratch.viewmodel.CanvasViewModel;
import scratch.viewmodel.ListsViewModel;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        ChosenActions chosenActions = new ChosenActions();
        Cursor cursor = new Cursor();
        VariableManager variableManager = new VariableManager();

        Executor executor = new Executor(chosenActions,cursor, variableManager);

        ListsViewModel listsViewModel = new ListsViewModel(executor);
        CanvasViewModel canvasViewModel = new CanvasViewModel(executor);

        MainView mainView = new MainView(listsViewModel, canvasViewModel);

        Scene scene = new Scene(mainView);
        primaryStage.setTitle("Scratch");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}