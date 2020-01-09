package main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BrowserController implements Initializable {

    Stage stage;

    @FXML
    WebView webView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        WebEngine engine = webView.getEngine();
        engine.load("http://localhost:7474/");
    }

    public void otworzBrowser() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/browser.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage = new Stage();
        stage.setTitle("Browser");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOnCloseRequest(event -> {
            try {
                Runtime.getRuntime().exec("taskkill /IM cmd.exe");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        stage.setScene(scene);
        stage.show();

    }
}
