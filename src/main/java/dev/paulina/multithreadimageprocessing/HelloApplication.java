package dev.paulina.multithreadimageprocessing;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        AppLogger.logInfo("Aplikacja została otwarta");

        FXMLLoader fxmlLoader = new FXMLLoader(HelloController.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Multithread Image Processing App");
        stage.setScene(scene);

        stage.setOnCloseRequest(event -> {
            AppLogger.logInfo("Aplikacja została zamknięta");
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
