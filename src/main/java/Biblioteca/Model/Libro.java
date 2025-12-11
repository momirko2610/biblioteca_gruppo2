package Biblioteca.Model;



import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
/**
 * @brief Classe che gestisce il database dei libri
 * @author Mirko Montella
 * @author Ciro Senese
 * @author Achille Romano
 * @author Sabrina Soriano
 */
public class Libro {
    private String titolo; /*!<Titolo del libro*/
    private String autore; /*!<Autore/i del libro*/
    private int annoPubblicazione; /*!<Anno di publicazione del libro*/
    private int ISBN; /*!<Codice identificativo unico del libro*/
    private int numCopie = 1; /*!<Numero di copie disponibili fisicamente nella biblioteca (non prestati))*/
    
    private static final String NAME = "database.json"; /*!<Nome del database contenente i libri*/
    
    //private static final Gson database = new GsonBuilder().setPrettyPrinting().create(); *!<Oggetto della funzione GSON per la creazione dei file JSON*/

    /**
     * @brief Costruttore di base
     * @param titolo Titolo del libro
     * @param autore Autore/i del libro
     * @param annoPubblicazione Anno di publicazione del libro
     * @param ISBN Codice identificativo unico del libro
     */
    public Libro(String titolo, String autore, int annoPubblicazione,int ISBN) {
        this.titolo = titolo;
        this.autore = autore;
        this.annoPubblicazione = annoPubblicazione;
        this.ISBN = ISBN;
    }
    
    /**
     * @brief Aggiorna il database dei libri creando un nuovo elemento
     * @pre Il Bibliotecariə deve essere autenticatə
     * @post Il database contenente il catalogo dei libri è aggiornato.
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
     * @brief Aggiorna il database dei libri modificando un elemento
     * @pre Il Bibliotecariə deve essere autenticatə
     * @post Il database contenente il catalogo dei libri è aggiornato.
     */
    public void modificaDatiLibro(){};

    /**
     * @brief Aggiorna il database dei libri rimuovendo un elemento
     * @pre Il Bibliotecariə deve essere autenticatə
     * @post Il database contenente il catalogo dei libri è aggiornato.
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
     * @brief Mostra gli elementi presenti nel database dei libri
     * @pre N/A
     * @post L’utente (sia bibliotecariə che studente) visualizza la lista completa dei libri (disponibili e non) in ordine alfabetico
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
     * @brief Mostra l'elemento cercato dal database dei libri
     * @pre Il libro è presente nel database
     * @post L’utente (sia bibliotecariə che studente) visualizza le informazioni del libro cercato
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
     * @brief Cerca un elemento dal database dei libri
     * @pre N/A
     * @post L’utente (sia bibliotecariə che studente) visualizza il libro selezionato
     * @return posizione del libro nel database o -1 in caso di libro non presente
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
