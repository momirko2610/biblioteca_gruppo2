/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biblioteca;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author ssabr
 */
public class Libro {
    private String titolo;
    private String autore;
    private int annoPubblicazione;
    private int ISBN;
    private int numCopie = 1;
    
    private static final String NAME = "database.json";
    
    private static final Gson database = new GsonBuilder().setPrettyPrinting().create();
    
    /**
     * @brief 
     */
    public Libro(String titolo, String autore, int annoPubblicazione,int ISBN) {
        this.titolo = titolo;
        this.autore = autore;
        this.annoPubblicazione = annoPubblicazione;
        this.ISBN = ISBN;
    }
    
    /**
     * @brief 
     */
    public void inserisciLibro() throws IOException {

        File file = new File(NAME);
        
        //Leggo il database
        JsonObject label;
        try (FileReader reader = new FileReader(file)) {
            label = database.fromJson(reader, JsonObject.class);
        }
        
        //Ottengo l'array dei libri
        JsonArray bookArray = label.getAsJsonArray("libri");
        if (bookArray == null) bookArray = new JsonArray();
        
        int i = ricercaLibro();
        
        if ( i != -1) {
            JsonObject obj = bookArray.get(i).getAsJsonObject();
            int copie = obj.get("numCopie").getAsInt();
            obj.addProperty("numCopie", copie + 1);

            // aggiorna array
            bookArray.set(i, obj);
        }
        else {
            //Aggiungo nuovo libro
            JsonObject newBook = new JsonObject();
            newBook.addProperty("titolo", this.titolo);
            newBook.addProperty("autore", this.autore);
            newBook.addProperty("annoPubblicazione", this.annoPubblicazione);
            newBook.addProperty("ISBN", this.ISBN);
            newBook.addProperty("numCopie", this.numCopie);
            bookArray.add(newBook);
        
            //Inserisco tutti i libri in una lista di libri
            List<JsonObject> bookList = new ArrayList<>();
            for (JsonElement element : bookArray) {
                bookList.add(element.getAsJsonObject());
            }
        
            //Ordino la lista in base al titolo
            bookList.sort((a, b) -> a.get("titolo").getAsString().compareToIgnoreCase(b.get("titolo").getAsString()));

            //Inserisco i libri in un Array ordinato
            JsonArray sortedArray = new JsonArray();
            for (JsonObject book : bookList) {
                sortedArray.add(book);
            }
          
            //Aggiorno l'Array
            label.add("libri", sortedArray);

            //Salvo
            try (FileWriter writer = new FileWriter(file)) {
                database.toJson(label, writer);
            }
            return;
        }
        try (FileWriter writer = new FileWriter(file)) {
        database.toJson(label, writer);
        }
    }

    
    /**
     * @brief 
     */
    public void modificaDatiLibro(){};
    
    /**
     * @brief 
     */
    public void cancellazioneDatiLibro() throws IOException {
        File file = new File(NAME);
        
        //Leggo il database
        JsonObject label;
        try (FileReader reader = new FileReader(file)) {
            label = database.fromJson(reader, JsonObject.class);
        }
        
        //Ottengo l'array dei libri
        JsonArray bookArray = label.getAsJsonArray("libri");
        if (bookArray == null) {
            System.out.println("ERROR, database not found");
            return;
        }
        
        int i = ricercaLibro();
        
        if ( i != -1) {
            bookArray.remove(i);
            try (FileWriter writer = new FileWriter(file)) {
                database.toJson(label, writer);
            }
            System.out.println("Libro eliminato");
        }
        else System.out.println("Libro non risulta nel nostro database");
    };
    
    /**
     * @brief 
     */
    public static void visualizzazioneListaLibri() throws IOException {
        File file = new File(NAME);
        
        //Leggo il database
        JsonObject label;
        try (FileReader reader = new FileReader(file)) {
            label = database.fromJson(reader, JsonObject.class);
        }
        
        //Ottengo l'array dei libri
        JsonArray bookArray = label.getAsJsonArray("libri");
        if (bookArray == null) {
            System.out.println("ERROR, database not found");
        }
        
        for (int i = 0; i < bookArray.size(); i++) {
            JsonObject obj = bookArray.get(i).getAsJsonObject();
            System.out.println(obj.toString());
        }
    };
    
    /**
     * @brief 
     */
    public void stampaLibro()throws IOException{
        File file = new File(NAME);
        
        //Leggo il database
        JsonObject label;
        try (FileReader reader = new FileReader(file)) {
            label = database.fromJson(reader, JsonObject.class);
        }
        
        //Ottengo l'array dei libri
        JsonArray bookArray = label.getAsJsonArray("libri");
        if (bookArray == null) {
            System.out.println("ERROR, database not found");
            return;
        }
        
        int i = ricercaLibro();
        
        if ( i != -1) {
            JsonObject obj = bookArray.get(i).getAsJsonObject();
            System.out.println(obj.toString());
        }
        else System.out.println("Libro non risulta nel nostro database");
    };
    
    /**
     * @brief 
     */
    private int ricercaLibro() throws IOException {
        File file = new File(NAME);
        //Leggo il database
        JsonObject label;
        try (FileReader reader = new FileReader(file)) {
            label = database.fromJson(reader, JsonObject.class);
        }
        
        //Ottengo l'array dei libri
        JsonArray bookArray = label.getAsJsonArray("libri");
        if (bookArray == null) return -1;
        
        for (int i = 0; i < bookArray.size(); i++) {
            JsonObject obj = bookArray.get(i).getAsJsonObject();
            if (obj.get("titolo").getAsString().equalsIgnoreCase(this.titolo)) {
                return i;
            }
        }
        
        return -1;
    }
}
