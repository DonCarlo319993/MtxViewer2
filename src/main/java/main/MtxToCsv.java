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

        logger.info ("Wejście do ciała metody o nazwie bazaM2...");


        org.apache.commons.io.FileUtils.cleanDirectory(new File("C:/MtxViewer/tymczasowaBazaGrafowa"));
        logger.info ("Kasowanie nieaktualnej tymczasowej bazy danych...");


        File macierz = new File(sciezkapliku);
        PrintWriter zapis = new PrintWriter("C:/MtxViewer/tymczasowyPlikCsv/"+nazwaPliku);
        Scanner odczyt = new Scanner(macierz);
        String tekst = odczyt.nextLine();
        String[] aktualnaLinia;
        String dopisz;
        int rozmiarM;
        long startCzas;
        long stopCzas;
        long rozmiarBazy;

        logger.info ("Pomyślnie załadowano zmienne.");
        logger.info ("Wczytywanie macierzy...");
        logger.info ("Rozpoczęcie pomiaru czasu wykonania metody.");
        startCzas=System.currentTimeMillis ();
        while (tekst.startsWith("%")){
            tekst = odczyt.nextLine();
            System.out.println(tekst);
        }
        logger.info ("Pominięto początkowy opis macierzy");

        aktualnaLinia = tekst.split(" ");
        rozmiarM = Integer.parseInt(aktualnaLinia[0]);

        while (odczyt.hasNextLine()){
            tekst = odczyt.nextLine();
            aktualnaLinia = tekst.split(" ");
            dopisz = aktualnaLinia[0]+","+aktualnaLinia[1]+","+aktualnaLinia[2];
            zapis.println(dopisz);
        }
        zapis.close();
        logger.info ("zostanie utworzonych : "+aktualnaLinia[0]+" wierzchołków oraz "+aktualnaLinia[2]+" relacji.");


        GraphDatabaseService db = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder(new File("C://MtxViewer//tymczasowaBazaGrafowa"))
                .setConfig(GraphDatabaseSettings.allow_file_urls, "true").newGraphDatabase();
        logger.info ("Utworzono pustą bazę grafową.");



        try ( Transaction tx = db.beginTx())
        {
            String query;

            for (int i = 1; i<=rozmiarM; i++) {
                query = "CREATE (:Pierwszy {PierwszyId:"+i+" } )";
                Result zapytanko = db.execute(query);
            }
            logger.info ("Utworzono wierzchołki grafu.");


            logger.info ("Wczytywanie pliku CSV.");
            logger.info ("Wykonywanie zapytania w celu stworzenia połączeń relacyjnych.");


            Result importCSV =
                    db.execute("LOAD CSV FROM 'file:///C:/MtxViewer/tymczasowyPlikCsv/"+nazwaPliku+"' " +
                            "AS row" +
                            " MATCH (a:Pierwszy {PierwszyId: toInteger(row[0])}), (b:Pierwszy {PierwszyId: toInteger(row[1])}) " +
                            " CREATE (a)-[rel:zawiera {value:row[2]}]->(b)" +
                            " RETURN a, b");
            tx.success();
            stopCzas = System.currentTimeMillis ();
            boolean logPstryk = true;
            logger.info ("Baza grafowa jest gotowa!");
            logger.info ("Baza zbudowana w "+(stopCzas-startCzas)+" milisekund");
            File folder = new File("C://MtxViewer//tymczasowaBazaGrafowa");
            rozmiarBazy = FileUtils.sizeOfDirectory (folder);
            logger.info ("Rozmiar bazy: "+rozmiarBazy+" bajtów");


        }

        db.shutdown();

    }
}
