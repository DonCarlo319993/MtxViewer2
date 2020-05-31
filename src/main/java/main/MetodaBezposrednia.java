package main;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

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
        odczyt.close ();


        boolean verbose = true;

        //tak oto została skonstruowana lista ,,wierzchołek''  zawierająca kompletną informację o macierzy
        //każdy element tej listy odpowiada odpowiedniemu wierszowi
        //każdy taki element zawiera numer powiązanej relacją kolumny i przyporządkowaną wertość relacji

        //Nie pozostaje już nic innego jak tylko użyć sterownika Neo4j i zacząć konstruować prawdziwe wierzchołki i relacje.

        System.out.println("\n\n");


        MainWindowController mainWindowController = new MainWindowController();
       /* for (int i = 0; i < wierzcholek.size(); i++) {
            if (verbose) {
                System.out.println("Wierzchołek " + (i + 1) + ": " + wierzcholek.get(i));
               *//* mainWindowController.logger = wierzcholek.get(i).toString();
                System.out.println(mainWindowController.logger);
                mainWindowController.wypiszLog();*//*
            }
        }*/

        logger.info ("Informacje zebrane pomyślnie.");

        //Tutaj będę tworzył bazę grafową

        logger.info ("Rozpoczęcie budowania bazy grafowej na podstawie zebranych informacji...");

        List<Node> nodes = new ArrayList<>(); //powstaje lista obiektów typu Neo4j'owy 'wierzchołek'
        List<Relationship> relacje = new ArrayList<>(); //powstaje lista obiektów typu Neo4j'owa 'relacja'
        Relationship testowa;   //zmienna typu 'Relationship'

        //-------------------------------- tutaj kod eksperymentalny ----------------------------------
        int size;
        int wiersz, kolumna, wartosc;
        odczyt = new Scanner(macierz);
        String line = odczyt.nextLine ();

        GraphDatabaseService graf = new GraphDatabaseFactory ()
                .newEmbeddedDatabase(new File("C://MtxViewer//tymczasowaBazaGrafowa")); //utworzenie pustej bazy danych
        Label label = Label.label("Wierzcholek");


        while (odczyt.nextLine ().startsWith ("%")){
            line = odczyt.nextLine ();
        }
        size = Integer.parseInt (line.split (" ")[0]);

        //aktualnaLinia = odczyt.nextLine ().split (" ");
        //size = Integer.parseInt (aktualnaLinia[0]);

        try (Transaction tx = graf.beginTx()){
        for (int i = 0; i<size; i++){
            nodes.add (i, graf.createNode (label));
            nodes.get (i).setProperty ("value", i+1);
        }
            tx.success ();
        }
        try (Transaction tx = graf.beginTx()){
            while (odczyt.hasNextLine ()){
                aktualnaLinia = odczyt.nextLine ().split (" ");
                wiersz = Integer.parseInt (aktualnaLinia[0]);
                System.out.println ("wiersz= "+wiersz);
                kolumna = Integer.parseInt (aktualnaLinia[1]);
                System.out.println ("kolumna= "+kolumna);

                if (aktualnaLinia.length == 3){
                    System.out.println ("aktualnaLinia.lenght= "+aktualnaLinia.length);
                    wartosc = Integer.parseInt (aktualnaLinia[2]);
                    System.out.println ("wartosc= "+wartosc);
                    testowa = nodes.get (wiersz-1).createRelationshipTo (nodes.get (kolumna-1), RelTypes.RELACJA);
                    System.out.println ("wiersz + kolumna="+wiersz+" + "+kolumna);
                    testowa.setProperty ("value", wartosc);
                }else {
                    testowa = nodes.get (wiersz-1).createRelationshipTo (nodes.get (kolumna-1), RelTypes.RELACJA);
                    testowa.setProperty ("value", 1);
                }
            }
            tx.success ();
        }
















        //-------------------------------- tutaj kod eksperymentalny ----------------------------------

       /* logger.info ("Utworzono rodzaj relacji: testowa.");

        GraphDatabaseService graf = new GraphDatabaseFactory()
                .newEmbeddedDatabase(new File("C://MtxViewer//tymczasowaBazaGrafowa")); //utworzenie pustej bazy danych
        //org.apache.commons.io.FileUtils.
        logger.info ("Utworzono pustą bazę grafową.");
        IndexDefinition indexDefinition; //utworzono zmienną typu 'indeks'
        try (Transaction tx = graf.beginTx()) {
            Schema schema = graf.schema();   //tworzenie struktury schematu dla bazy danych
            indexDefinition = schema.indexFor(Label.label("Wierzcholek")).on("value").create(); //zdefiniowanie ideksu na obiektach, które
                                                                                    //mają label 'Wierzcholek' i właściwość 'value'
            tx.success();
            logger.info ("Utworzono indeksy na : Wierzcholek + value. ");
        }

        try (Transaction tx = graf.beginTx()) {

            Label label = Label.label("Wierzcholek");               //utworzono label 'Wierzcholek'
            for (int i = 0; i < liczbaWierzch; i++) {               //zostaje uruchomiona pętla o liczbie powtórzeń ustalonej przez rozmiar macierzy
                nodes.add(i, graf.createNode(label));               //dodaj do listy wierzchołków nowy wierzchołek mieniący się labelem 'Wierzcholek'
            }
            for (int i = 0; i < liczbaWierzch; i++) {               //
                nodes.get(i).setProperty("value", i + 1);           //dla nowo dodanego wierzchołka właściwość 'value' przyjmie wartość odpowiadającą liczbie
            }                                                       //wiersza/kolumny który reprezentuje

            logger.info ("Utworzono pomyślnie "+liczbaWierzch+" wierzchołków.");

            logger.info ("Tworzenie relacji między wierzchołkami...");


            for (int i = 0; i < liczbaWierzch; i++) {  //ilość powtórzeń głównej pętli równa się rozmiarowi macierzy
                liczbaElementow = wierzcholek.get(i).size(); //zmienna 'liczbaElementow' to liczba elementów każdego kolejnego indeksu w liście 'wierzcholek', docelowo są to 2 elementy: kolumna i wartość
                for (int j = 0; j < liczbaElementow; j = j + 2) { //
                    System.out.println(wierzcholek.get(i).get(j));
                    temp = wierzcholek.get(i).get(j);       //tymczasowo trzymaj numer kolumny dla i-tego wiersza
                    indeks = (int) temp;                    //sparsowanie numeru kolumny do int'a i przypisanie do zmiennej 'indeks'
                    if (nodes.get(i) != nodes.get(indeks - 1)) { //jeżeli nie istnieje jeszcze taki wierzchołek to... w tym warunku chodzi o to, żeby nie tworzyć dwa razy tej samej relacji. Numer wierzchołka odnosi się tak samo do numeru wiersza macierzy jak i kolumny.
                        testowa = nodes.get(i).createRelationshipTo(nodes.get(indeks - 1), RelTypes.RELACJA); //zostaje ustanowiona relacja między dwoma wierzchołkami
                        testowa.setProperty("value", wierzcholek.get(i).get(j + 1)); //oraz ustawiona odpowiednia właściwość relacji
                    }
                }
            }
            tx.success();  //i to wszystko.
            stopCzas = System.currentTimeMillis ();
            logger.info ("Tworzenie relacji zakończono pomyślnie.");
            logger.info ("Baza grafowa jest gotowa!");
            logger.info ("Baza zbudowana w "+(stopCzas-startCzas)+" milisekund");
            File folder = new File("C://MtxViewer//tymczasowaBazaGrafowa");
            rozmiarBazy = FileUtils.sizeOfDirectory (folder);
            logger.info ("Rozmiar bazy: "+rozmiarBazy+" bajtów");
        }

        graf.shutdown();*/

    }
}