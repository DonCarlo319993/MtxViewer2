package main;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MetodaBezposrednia {


    public String sciezkapliku; //pole klasy uzupełniające się po wybraniu odpowiedniego pliku Matric Market o ścieżkę tego pliku

    final static Logger logger = Logger.getLogger (MainWindow.class.getName ()); //inicjalizacja loggera4j


    public void bazaM1 () throws IOException {
        org.apache.commons.io.FileUtils.cleanDirectory(new File("C:/MtxViewer/tymczasowaBazaGrafowa")); //kasowanie nieaktualnej tymczasowej bazy danych

        File macierz = new File(sciezkapliku);        //wskaż, że pod wybraną ścieżką znajduje się plik
        String[] aktualnaLinia;                      //komórki tej zmiennej posłużą do przechowywania wierszy, kolumn i wartości zapisanych w linijkach pliku MM

        int wiersz, kolumna, size, lKrawedzi;         //zmienne związane z budową bazy grafowej
        float wartosc;                      // ta również
        long startCzas, stopCzas, rozmiarBazy, pustaBCz, tworzenieWCz, laczenieRCz, lacznyCzas;   //zmienne związane z pomiarem różnych rzeczy


        List<Node> nodes = new ArrayList<>(); //powstaje lista obiektów typu Neo4j'owy 'wierzchołek'
        Relationship testowa;   //zmienna typu 'Relationship'

        Scanner odczyt = new Scanner(macierz); //inicjalizacja zmiennej do czytania pliku MM
        String line = odczyt.nextLine ();     //przypisanie całego łańcucha znaków z linijki pliku MM

        startCzas = System.currentTimeMillis ();
        GraphDatabaseService graf = new GraphDatabaseFactory ()
                .newEmbeddedDatabase(new File("C://MtxViewer//tymczasowaBazaGrafowa")); //utworzenie pustej bazy danych
        Label label = Label.label("Wierzcholek");                                                //utworzenie etykiety rodzaju 'Wierzcholek'
        stopCzas = System.currentTimeMillis ();
        pustaBCz = stopCzas - startCzas;


        while (line.startsWith ("%")){        //pomiń informacyjną część pliku
            line = odczyt.nextLine ();                      //...
        }
        size = Integer.parseInt (line.split (" ")[0]);  //spisz rozmiar macierzy z odpowiedniej linijki i kolumny
        lKrawedzi=Integer.parseInt (line.split (" ")[2]);


        try (Transaction tx = graf.beginTx()){
            startCzas = System.currentTimeMillis ();
            for (int i = 0; i<size; i++){                       //tworzenie wierzchołków
                nodes.add (i, graf.createNode (label));
                nodes.get (i).setProperty ("value", i+1);       //przypisywanie dla właściwości 'value' wartości numeru wierzchołka
            }
            stopCzas = System.currentTimeMillis ();
            tworzenieWCz = stopCzas - startCzas;

            startCzas = System.currentTimeMillis ();
            while (odczyt.hasNextLine ()){                                  //tworzenie relacji
                aktualnaLinia = odczyt.nextLine ().split (" ");       //przewiń następną linię, poszatkuj łańcuch na części i umieść w komórkach
                wiersz = Integer.parseInt (aktualnaLinia[0]);               //przypisz numer wiersza
                kolumna = Integer.parseInt (aktualnaLinia[1]);              //przypisz numer kolumny
                if (aktualnaLinia.length == 3){                             // jeśli linia ma układ: wiersz, kolumna, wartość
                    wartosc = Float.parseFloat (aktualnaLinia[2]);
                    testowa = nodes.get (wiersz-1).createRelationshipTo (nodes.get (kolumna-1), RelTypes.RELACJA);      //utwórz relację
                    testowa.setProperty ("value", wartosc);
                }else {                                                    //jeśli tylko: wiersz, kolumna
                    testowa = nodes.get (wiersz-1).createRelationshipTo (nodes.get (kolumna-1), RelTypes.RELACJA);      //utwórz relację
                    testowa.setProperty ("value", 1);
                }
            }
            tx.success ();
            stopCzas = System.currentTimeMillis ();
        }
        laczenieRCz = stopCzas-startCzas;
        lacznyCzas = pustaBCz+tworzenieWCz+laczenieRCz;

        logger.info ("Graf został pomyślnie odwzorowany!");
        logger.info ("Liczba wierzchołków w grafie : "+size);
        logger.info ("Liczba krawedzi w grafie: "+lKrawedzi);

        logger.info ("Pusta baza utworzona w: "+timer (pustaBCz));
        logger.info ("Wierzchołki utworzone w: "+timer (tworzenieWCz));
        logger.info ("Relacje utworzono w: "+timer (laczenieRCz));
        logger.info ("Łączny czas: "+timer (lacznyCzas));

        File folder = new File("C://MtxViewer//tymczasowaBazaGrafowa");
        rozmiarBazy = FileUtils.sizeOfDirectory (folder);
        logger.info ("Rozmiar bazy: "+rozmiarBazy+" bajtów");

        graf.shutdown ();
        }

    public static String timer(long czas){
        int msek, sek, min;
        String napis="";
        if (czas/60000 >= 1){
            min = (int) (czas/60000);
        }else {
            min = 0;
        }
        if (min >= 1){
            sek = (int) ((czas - (60000*min))/1000);
        }else {
            sek = (int) (czas/1000);
        }
        if (min >=1 && sek >= 1){
            msek = (int) (czas - (60000*min + 1000*sek));
        }else if (min>=1 && sek ==0){
            msek = (int) (czas-60000*min);
        }else if (min ==0 && sek >=1){
            msek = (int) (czas - 1000*sek);
        }else {
            msek = (int) czas;
        }
        napis = min+"min "+sek+"sek "+msek+"ms";

        return napis;
    }
}