package com.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {
    private static final String WINDOW_TITLE = "Segway-Ninebot JSON-track to GPX-track Converter";
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 800;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Setup controller
        MainWindowController controller = new MainWindowController(primaryStage);

        // Setup window fxml
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/mainWindow.fxml"));
        fxmlLoader.setController(controller);
        Parent root = fxmlLoader.load();

        // Setup window
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle(WINDOW_TITLE);
        primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        primaryStage.show();
    }
}
