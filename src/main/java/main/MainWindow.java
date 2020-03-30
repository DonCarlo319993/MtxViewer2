package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
public class MainWindow extends Application {




    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/sample.fxml"));
        StackPane stackPane = loader.load();
        Scene scene = new Scene(stackPane);


        primaryStage.getIcons().add(new Image("file:src/main/community-158484_1280.png"));
        primaryStage.setOnCloseRequest(event -> {
            try {
                MainWindowController.zamknijProgram();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        primaryStage.setTitle("Projekt okno");
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    public static void main(String[] args) {
        launch(args);

    }
}
