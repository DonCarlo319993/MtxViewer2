package main;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MainWindowController implements Initializable {

    Stage stage;
    String tempNazwaPliku;


   @FXML
   Button przyciskWybierz, przyciskStart, przyciskPokazGraf;

   @FXML
   Label etykietaLokalizacji;

   @FXML
    CheckBox metodaMieszana, metodaBezposrednia;




    public static void zamknijProgram() throws IOException {
        Runtime.getRuntime().exec("taskkill /IM cmd.exe") ;
        org.apache.commons.io.FileUtils.cleanDirectory(new File("C:/MtxViewer/tymczasowaBazaGrafowa"));
        Platform.exit();
        System.exit(0);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    void pokazDialogWindowDirectory(){
        try {
            DirectoryChooser dc = new DirectoryChooser();
            File selectedDir = dc.showDialog(getStage());
            etykietaLokalizacji.setText(selectedDir.toString());

        }catch (Exception e){

        }
    }

    @FXML
    public void pokazDialogWindowFile(){
        try {
            FileChooser fc = new FileChooser();
            File selectedFile = fc.showOpenDialog(getStage());
            etykietaLokalizacji.setText(selectedFile.toString());
            tempNazwaPliku = selectedFile.getName();

        }catch (Exception e){

        }
    }

    @FXML
    public void zapiszBaze() throws IOException {
        String lokalizacja;
        String nazwa;
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("ZIP files (*.zip)", "*.zip");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(getStage());
        lokalizacja = file.getPath();
        nazwa = file.getName();
        System.out.println(lokalizacja);
        System.out.println(nazwa);
        String sourceFile = "C:/MtxViewer/tymczasowaBazaGrafowa";
        FileOutputStream fos = new FileOutputStream(lokalizacja+nazwa);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(sourceFile);
        zipFile(fileToZip, fileToZip.getName(), zipOut);
        zipOut.close();
        fos.close();

    }




    @FXML
    public void nowaBaza() throws IOException {
        if (metodaBezposrednia.isSelected() && !metodaMieszana.isSelected()) {
            // METODA PIERWSZA
            MetodaBezposrednia nowaBaza = new MetodaBezposrednia();
            nowaBaza.sciezkapliku = etykietaLokalizacji.getText().replace("\\\\", "/");
            nowaBaza.bazaM1();
        }else if (!metodaBezposrednia.isSelected() && metodaMieszana.isSelected()) {
            //METODA DRUGA
            MtxToCsv nowaBaza = new MtxToCsv();
            nowaBaza.sciezkapliku = etykietaLokalizacji.getText().replace("\\\\", "/");
            System.out.println("Zobaczmy jak nazywa sie plik: " + tempNazwaPliku);
            nowaBaza.nazwaPliku = tempNazwaPliku.replace(".mtx", ".csv");
            nowaBaza.bazaM2();
        }
    }



public void funkcja() throws IOException, InterruptedException {
    FileUtils.cleanDirectory(new File("C:/Program Files/Neo4jServer/neo4j-community-3.5.12/data/databases/graph.db"));
    FileUtils.copyDirectory(new File("C:/MtxViewer/tymczasowaBazaGrafowa"), new File("C:/Program Files/Neo4jServer/neo4j-community-3.5.12/data/databases/graph.db"));
    Runtime.getRuntime().exec("cmd /c start /min cmd.exe /K \"cd C:/Program Files/Neo4jServer/neo4j-community-3.5.12/bin && neo4j console\"");

    Thread.sleep(10000);
    BrowserController browserController = new BrowserController();
    browserController.otworzBrowser();
}

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }





    public void metodaBezposredniSelected(){
        metodaMieszana.setSelected(false);
    }

    public void metodaMieszanaSelected(){
        metodaBezposrednia.setSelected(false);
    }

    public Stage getStage() {return stage; }
    void setStage(Stage stage){this.stage=stage;}

}
