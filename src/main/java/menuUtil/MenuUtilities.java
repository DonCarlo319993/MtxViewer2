package menuUtil;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import main.MainWindowController;

import java.io.IOException;

public class MenuUtilities {

    public static MenuBar prepareMenu(){
        MenuBar menuBar = new MenuBar();

        Menu plikMenu = new Menu("Plik");
        MenuItem plikZamknij = new MenuItem("Zamknij");
        plikZamknij.setGraphic(new ImageView("file:src/main/logout.png"));
        plikZamknij.setOnAction(event -> {
            try {
                MainWindowController.zamknijProgram();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        plikMenu.getItems().add(plikZamknij);
        menuBar.getMenus().add(plikMenu);


        return menuBar;
    }
}
