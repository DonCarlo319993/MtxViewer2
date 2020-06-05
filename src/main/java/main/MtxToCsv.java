package main;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class MtxToCsv {

   public String sciezkapliku;
   public String nazwaPliku;
   public boolean LogPstryk = false;

    final static Logger logger = Logger.getLogger (MainWindow.class.getName ());

    public void bazaM2() throws IOException {

        org.apache.commons.io.FileUtils.cleanDirectory(new File("C:/MtxViewer/tymczasowaBazaGrafowa"));

        File macierz = new File(sciezkapliku);
        PrintWriter zapis = new PrintWriter("C:/MtxViewer/tymczasowyPlikCsv/"+nazwaPliku);
        Scanner odczyt = new Scanner(macierz);
        String tekst = odczyt.nextLine();
        String[] aktualnaLinia;
        String dopisz;
        int rozmiarM, lKrawedzi;
        long startCzas, stopCzas, rozmiarBazy, pustaBCz, tworzenieWCz, laczenieRCz, tworzenieCSVCz;

        while (tekst.startsWith("%")){              //omijanie informacyjnych wierszy
            tekst = odczyt.nextLine();
            System.out.println(tekst);
        }

        aktualnaLinia = tekst.split(" ");
        rozmiarM = Integer.parseInt(aktualnaLinia[0]);              //zczytanie rozmiaru macierzy
        lKrawedzi = Integer.parseInt (aktualnaLinia[2]);

        startCzas=System.currentTimeMillis ();
        while (odczyt.hasNextLine()){                           //tworzenie pliku CSV
            tekst = odczyt.nextLine();
            aktualnaLinia = tekst.split(" ");                  //rozdziel tekst, przypisz wartości do komórek tablicy
            if (aktualnaLinia.length == 3) {
                dopisz = aktualnaLinia[0] + "," + aktualnaLinia[1] + "," + aktualnaLinia[2];     //utwórz łańcuch znaków w formacie csv, czyli z wartościami po przecinku
                zapis.println (dopisz);                                                  //zapisz w nowym pliku
            }else {
                dopisz = aktualnaLinia[0] + "," + aktualnaLinia[1] + "," +1;     //utwórz łańcuch znaków w formacie csv, czyli z wartościami po przecinku
                zapis.println (dopisz);
            }
        }
        zapis.close();
        odczyt.close ();
        stopCzas=System.currentTimeMillis ();
        tworzenieCSVCz = stopCzas - startCzas;


        startCzas =System.currentTimeMillis ();
        GraphDatabaseService db = new GraphDatabaseFactory()                                                //utwórz pustą bazę danych
                .newEmbeddedDatabaseBuilder(new File("C://MtxViewer//tymczasowaBazaGrafowa"))
                .setConfig(GraphDatabaseSettings.allow_file_urls, "true").newGraphDatabase();
        stopCzas = System.currentTimeMillis ();
        pustaBCz =stopCzas - startCzas;


        try ( Transaction tx = db.beginTx())
        {
            String query;

            startCzas = System.currentTimeMillis ();
            for (int i = 1; i<=rozmiarM; i++) {                         //tworzenie wierzchołków
                query = "CREATE (:Pierwszy {PierwszyId:"+i+" } )";
                Result zapytanko = db.execute(query);
            }
            stopCzas = System.currentTimeMillis ();
            tworzenieWCz = stopCzas - startCzas;

            startCzas = System.currentTimeMillis ();
            Result importCSV =                                                                                              //import utworzonego wcześniej pliku CSV i utworzenie relacji
                    db.execute("LOAD CSV FROM 'file:///C:/MtxViewer/tymczasowyPlikCsv/"+nazwaPliku+"' " +
                            "AS row" +
                            " MATCH (a:Pierwszy {PierwszyId: toInteger(row[0])}), (b:Pierwszy {PierwszyId: toInteger(row[1])}) " +
                            " CREATE (a)-[rel:zawiera {value:row[2]}]->(b)" +
                            " RETURN a, b");
            tx.success();
            stopCzas = System.currentTimeMillis ();
            laczenieRCz = stopCzas - startCzas;
        }
        logger.info ("Graf został pomyślnie odwzorowany!");
        logger.info ("Liczba wierzchołków w grafie : "+rozmiarM);
        logger.info ("Liczba krawedzi w grafie: "+lKrawedzi);
        logger.info ("Plik .csv utworzony w: "+tworzenieCSVCz+" milisekund");
        logger.info ("Pusta baza utworzona w: "+pustaBCz+" milisekund");
        logger.info ("Wierzchołki utworzone w: "+tworzenieWCz+" milisekund");
        logger.info ("Import pliku .csv + połączenie relacjami wierzchołków wykonano w: "+laczenieRCz+" milisekund");

        File folder = new File("C://MtxViewer//tymczasowaBazaGrafowa");
        rozmiarBazy = FileUtils.sizeOfDirectory (folder);
        logger.info ("Rozmiar bazy: "+rozmiarBazy+" bajtów");

        db.shutdown();
    }
}
