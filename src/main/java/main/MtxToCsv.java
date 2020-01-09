package main;

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

    public void bazaM2() throws IOException {

        org.apache.commons.io.FileUtils.cleanDirectory(new File("C:/MtxViewer/tymczasowaBazaGrafowa"));

        File macierz = new File(sciezkapliku);
        PrintWriter zapis = new PrintWriter("C:/MtxViewer/tymczasowyPlikCsv/"+nazwaPliku);
        Scanner odczyt = new Scanner(macierz);
        String tekst = odczyt.nextLine();
        String[] aktualnaLinia;
        String dopisz;
        int rozmiarM;

        while (tekst.startsWith("%")){
            tekst = odczyt.nextLine();
            System.out.println(tekst);
        }
        aktualnaLinia = tekst.split(" ");
        rozmiarM = Integer.parseInt(aktualnaLinia[0]);

        while (odczyt.hasNextLine()){
            tekst = odczyt.nextLine();
            aktualnaLinia = tekst.split(" ");
            dopisz = aktualnaLinia[0]+","+aktualnaLinia[1]+","+aktualnaLinia[2];
            zapis.println(dopisz);
        }
        zapis.close();

        GraphDatabaseService db = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder(new File("C://MtxViewer//tymczasowaBazaGrafowa"))
                .setConfig(GraphDatabaseSettings.allow_file_urls, "true").newGraphDatabase();


        try ( Transaction tx = db.beginTx())
        {
            String query;

            for (int i = 1; i<=rozmiarM; i++) {
                query = "CREATE (:Pierwszy {PierwszyId:"+i+" } )";
                Result zapytanko = db.execute(query);
            }

            Result importanteDeLaNoche =
                    db.execute("LOAD CSV FROM 'file:///C:/MtxViewer/tymczasowyPlikCsv/"+nazwaPliku+"' " +
                            "AS row" +
                            " MATCH (a:Pierwszy {PierwszyId: toInteger(row[0])}), (b:Pierwszy {PierwszyId: toInteger(row[1])}) " +
                            " CREATE (a)-[rel:zawiera {value:row[2]}]->(b)" +
                            " RETURN a, b");
            tx.success();
        }

        db.shutdown();

    }
}
