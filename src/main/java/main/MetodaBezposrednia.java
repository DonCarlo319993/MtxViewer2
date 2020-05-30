package main;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class MetodaBezposrednia {

    public String sciezkapliku;

    final static Logger logger = Logger.getLogger (MainWindow.class.getName ());


    public void bazaM1 () throws IOException {
        logger.info ("Wejście do ciała metody o nazwie bazaM1...");
        org.apache.commons.io.FileUtils.cleanDirectory(new File("C:/MtxViewer/tymczasowaBazaGrafowa"));
        logger.info ("Kasowanie nieaktualnej tymczasowej bazy danych...");
        File macierz = new File(sciezkapliku);
        Scanner odczyt = new Scanner(macierz);
        String tekst = odczyt.nextLine();
        String[] aktualnaLinia;
        int liczbaWierzch;
        int liczbaRel;
        int iterator = 0;
        int rozmiar = 0;
        int indeks;
        float temp;
        int liczbaElementow;
        long startCzas;
        long stopCzas;
        long rozmiarBazy;
        logger.info ("Pomyślnie załadowano zmienne.");


        logger.info ("Wczytywanie macierzy...");
        logger.info ("Rozpoczęcie pomiaru czasu wykonania metody.");
        startCzas=System.currentTimeMillis ();
        while (tekst.startsWith("%")) {
            tekst = odczyt.nextLine();
            System.out.println(tekst);
        }
        logger.info ("Pominięto początkowy opis macierzy");


        List<List<Float>> wierzcholek = new ArrayList<List<Float>>();
        aktualnaLinia = tekst.split(" ");
        liczbaWierzch = Integer.parseInt(aktualnaLinia[0]);
        liczbaRel = Integer.parseInt(aktualnaLinia[2]);
        for (int i = 0; i < liczbaWierzch; i++) {
            wierzcholek.add(new LinkedList<Float>());
        }

        logger.info ("zostanie utworzonych : "+liczbaWierzch+" wierzchołków oraz "+liczbaRel+" relacji.");

        System.out.println("\nOd tego momentu zaczyna się macierz: \n");
        logger.info ("Zbieranie informacji o macierzy...");

        while (odczyt.hasNextLine()) {  // Jeśli jest następna linia w dokumencie to kręć
            tekst = odczyt.nextLine();      //przypisuje następną linię do zmiennej typu String
            System.out.println(tekst);      //wypisuje tę linię w terminalu
            aktualnaLinia = tekst.split(" ");    //linia ta dzielona jest na odzielne elementy i każdy z nich tafia do tablicy typu String
            List<Float> aktualnaLiniaInt = new ArrayList<>(); // powstaje lista typu float

            if (aktualnaLinia.length < 3) {  // może się zdarzyć, że w pliku Matrix Market nie ma podanych wartości współrzędnych, wtedy tablica Stringów będzie trzymała tylko 2 elementy, nr wiersza i nr kolumny
                aktualnaLiniaInt.add(Float.parseFloat(aktualnaLinia[aktualnaLinia.length - 1]));//dodaj do listy float sparsowany numer kolumny
                aktualnaLiniaInt.add((float) 1); //a jaką drugą wartość dodaj do niej wartość 1
            } else {                                                                             //jeśli l.elementów >2 to...
                aktualnaLiniaInt.add(Float.parseFloat(aktualnaLinia[aktualnaLinia.length - 2])); //dodaj nr kolumny do listy float
                aktualnaLiniaInt.add(Float.parseFloat(aktualnaLinia[aktualnaLinia.length - 1])); //dodaj wartość współrzędnych do listy float
            }

            iterator = Integer.parseInt(aktualnaLinia[0]);  //nr wiersza przetwarzanej linijki pliku MM zostaje przypisany do zmiennej
            if (wierzcholek.get(iterator - 1).isEmpty()) {  //jeśli numer elementu listy list odpowiadający numerowi wiersza we właśnie przetwarzanej linijce pliku MM jest pusty
                wierzcholek.set(iterator - 1, aktualnaLiniaInt);  /// to przypisz do niego kolumnę i wartość
                rozmiar++;  //zwiększ o 1 rozmiar
            } else {                                        //jeśli element pod tym numerem ma już jakąś przypisaną wartość to...
                List<Float> scalona = new ArrayList<>(wierzcholek.get(iterator - 1)); //stwórz tymaczasowo listę przechowującą tę wartość
                scalona.addAll(aktualnaLiniaInt);       //na koniec tymczasowej listy zostaje dodana lista zawierająca nr kolumny i wartości
                wierzcholek.set(iterator - 1, scalona); //nadpisz numer elementu dodając nową kolumnę i wartość
            }
        }

        boolean verbose = true;

        //tak oto została skonstruowana lista ,,wierzchołek''  zawierająca kompletną informację o macierzy
        //każdy element tej listy odpowiada odpowiedniemu wierszowi
        //każdy taki element zawiera numer powiązanej relacją kolumny i przyporządkowaną wertość relacji

        //Nie pozostaje już nic innego jak tylko użyć sterownika Neo4j i zacząć konstruować prawdziwe wierzchołki i relacje.

        System.out.println("\n\n");


        MainWindowController mainWindowController = new MainWindowController();
        for (int i = 0; i < wierzcholek.size(); i++) {
            if (verbose) {
                System.out.println("Wierzchołek " + (i + 1) + ": " + wierzcholek.get(i));
               /* mainWindowController.logger = wierzcholek.get(i).toString();
                System.out.println(mainWindowController.logger);
                mainWindowController.wypiszLog();*/
            }
        }

        logger.info ("Informacje zebrane pomyślnie.");

        //Tutaj będę tworzył bazę grafową

        logger.info ("Rozpoczęcie budowania bazy grafowej na podstawie zebranych informacji...");

        List<Node> nodes = new ArrayList<>();
        List<Relationship> relacje = new ArrayList<>();
        Relationship testowa;
        logger.info ("Utworzono rodzaj relacji: testowa.");

        GraphDatabaseService graf = new GraphDatabaseFactory().newEmbeddedDatabase(new File("C://MtxViewer//tymczasowaBazaGrafowa"));
        //org.apache.commons.io.FileUtils.
        logger.info ("Utworzono pustą bazę grafową.");
        IndexDefinition indexDefinition;
        try (Transaction tx = graf.beginTx()) {
            Schema schema = graf.schema();
            indexDefinition = schema.indexFor(Label.label("Wierzcholek")).on("value").create();

            tx.success();
            logger.info ("Utworzono etykietę : Wierzchołek. ");
        }

        try (Transaction tx = graf.beginTx()) {

            Label label = Label.label("Wierzcholek");
            for (int i = 0; i < liczbaWierzch; i++) {
                nodes.add(i, graf.createNode(label));
            }
            for (int i = 0; i < liczbaWierzch; i++) {
                nodes.get(i).setProperty("value", i + 1);
            }

            logger.info ("Utworzono pomyślnie "+liczbaWierzch+" wierzchołków.");

            System.out.println(nodes.get(6).getProperty("value"));
            System.out.println(Math.round(wierzcholek.get(2).get(0)));

            testowa = nodes.get(2).createRelationshipTo(nodes.get(3), RelTypes.RELACJA);
            relacje.add(testowa);
            testowa = nodes.get(5).createRelationshipTo(nodes.get(7), RelTypes.RELACJA);
            relacje.add(testowa);

            logger.info ("Tworzenie relacji między wierzchołkami...");


            for (int i = 0; i < liczbaWierzch; i++) {
                liczbaElementow = wierzcholek.get(i).size();
                for (int j = 0; j < liczbaElementow; j = j + 2) {
                    System.out.println(wierzcholek.get(i).get(j));
                    temp = wierzcholek.get(i).get(j);
                    indeks = (int) temp;
                    if (nodes.get(i) != nodes.get(indeks - 1)) {
                        testowa = nodes.get(i).createRelationshipTo(nodes.get(indeks - 1), RelTypes.RELACJA);
                        testowa.setProperty("value", wierzcholek.get(i).get(j + 1));
                    }
                }
            }
            tx.success();
            stopCzas = System.currentTimeMillis ();
            logger.info ("Tworzenie relacji zakończono pomyślnie.");
            logger.info ("Baza grafowa jest gotowa!");
            logger.info ("Baza zbudowana w "+(stopCzas-startCzas)+" milisekund");
            File folder = new File("C://MtxViewer//tymczasowaBazaGrafowa");
            rozmiarBazy = FileUtils.sizeOfDirectory (folder);
            logger.info ("Rozmiar bazy: "+rozmiarBazy+" bajtów");
        }

        graf.shutdown();

    }
}