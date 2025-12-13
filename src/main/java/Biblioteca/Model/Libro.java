package Biblioteca.Model;

import com.google.gson.*;
//import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
/**
 * @brief Classe che gestisce il database dei libri
 * @author Sabrina Soriano
 * @author Mirko Montella
 * @author Ciro Senese
 * @author Achille Romano
 */
public class Libro {
    private final String titolo; /*!<Titolo del libro*/
    private final String autore; /*!<Autore/i del libro*/
    private final int annoPubblicazione; /*!<Anno di publicazione del libro*/
    private final long ISBN; /*!<Codice identificativo unico del libro*/
    private int numCopie = 1; /*!<Numero di copie disponibili fisicamente nella biblioteca (non prestati))*/
    
    private static final String NAME = "database.json"; /*!<Nome del database contenente i libri*/
    
    private static final Gson database = new GsonBuilder().setPrettyPrinting().create(); /*!<Oggetto della funzione GSON per la creazione dei file JSON*/

    /**
     * @brief Costruttore di base
     * @param titolo Titolo del libro
     * @param autore Autore/i del libro
     * @param annoPubblicazione Anno di publicazione del libro
     * @param ISBN Codice identificativo unico del libro
     */
    public Libro(String titolo, String autore, int annoPubblicazione,long ISBN) {
        this.titolo = titolo;
        this.autore = autore;
        this.annoPubblicazione = annoPubblicazione;
        this.ISBN = ISBN;
    }
    public String getTitolo() { return titolo; }
    public String getAutore() { return autore; }
    public int getAnnoPubblicazione() { return annoPubblicazione; }
    public long getIsbn() { return ISBN; }
    
    @Override
    public String toString() {
        return String.format(
            "Titolo: %s| Autore: %s | Anno: %d | ISBN: %d | Copie: %d",
            titolo, autore, annoPubblicazione, ISBN, numCopie
        );
    }

    
    /**
     * @throws java.io.IOException
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
        
        int i = Libro.ricercaLibroISBN(this.ISBN);
        
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
     * @param newTitle
     * @param newAuthor
     * @param newAnnoPubblicazione
     * @param newISBN
     * @param newNumCopie
     * @throws java.io.IOException
     * @brief Aggiorna il database dei libri modificando un elemento
     * @pre Il Bibliotecariə deve essere autenticatə
     * @post Il database contenente il catalogo dei libri è aggiornato.
     */
    public void modificaDatiLibro( String newTitle, String newAuthor, String newAnnoPubblicazione, String newISBN, String newNumCopie) throws IOException{
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
        
        int i = Libro.ricercaLibroISBN(this.ISBN);
        
        if ( i != -1) {
            JsonObject obj = bookArray.get(i).getAsJsonObject();
            if (!(newTitle.isEmpty())) obj.addProperty("titolo", newTitle);
            if (!(newAuthor.isEmpty())) obj.addProperty("autore", newAuthor);
            if (!(newAnnoPubblicazione.isEmpty())) obj.addProperty("annoPubblicazione", newAnnoPubblicazione);
            if (!(newISBN.isEmpty())) obj.addProperty("ISBN", newISBN);
            if (!(newNumCopie.isEmpty())) obj.addProperty("numCopie", newNumCopie);
                        
            try (FileWriter writer = new FileWriter(file)) {
                database.toJson(label, writer);
            }
        System.out.println("Libro modificato:");
        System.out.println(obj.toString());
        }
        else System.out.println("Libro non risulta nel nostro database");
    };

    /**
     * @throws java.io.IOException
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
        
        int i = Libro.ricercaLibroISBN(this.ISBN);
        
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
     * @throws java.io.IOException
     * @brief Mostra gli elementi presenti nel database dei libri
     * @pre N/A
     * @post L’utente (sia bibliotecariə che studente) visualizza la lista completa dei libri (disponibili e non) in ordine alfabetico
     */
    public static void visualizzazioneListaLibri() throws IOException {
        List<Libro> libri = Database.leggiDatabaseLibri();
        
        libri.forEach(l -> {
            System.out.println(l);
        });
    }

    /**
     * @param ISBN
     * @throws java.io.IOException
     * @brief Cerca un elemento dal database dei libri
     * @pre N/A
     * @post L’utente (sia bibliotecariə che studente) visualizza il libro selezionato
     * @return posizione del libro nel database o -1 in caso di libro non presente
     */
    public static int ricercaLibroISBN(Long ISBN) throws IOException {
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
            if (obj.get("ISBN").getAsLong() == ISBN) {
                return i;
            }
        }
        
        return -1;
    }
    /**
     * @param titolo
     * @return 
     * @throws java.io.IOException
     * @brief Mostra il libro cercato per titolo
     * @pre Il libro è presente nel database
     * @post L’utente (sia bibliotecariə che studente) visualizza le informazioni del libro cercato
     */
    public static List<Libro> ricercaLibroTitolo(String titolo)throws IOException{
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
            return null;
        }
        
        //Creo una lista di tipo List<Libro>
        List<Libro> libri = new ArrayList<>();
        
        for (int i = 0; i < bookArray.size(); i++) {
            JsonObject obj = bookArray.get(i).getAsJsonObject();
            if (obj.get("titolo").getAsString().compareTo(titolo) > 0) break;
            else if (obj.get("titolo").getAsString().equalsIgnoreCase(titolo)) {
                Libro libro = database.fromJson(obj, Libro.class);
                libri.add(libro);
            }
            
           
        }
        return (libri);    
    };
    
    /**
     * @param autore
     * @return 
     * @throws java.io.IOException
     * @brief Mostra il libro cercato per titolo
     * @pre Il libro è presente nel database
     * @post L’utente (sia bibliotecariə che studente) visualizza le informazioni del libro cercato
     */
    public static List<Libro> ricercaLibroAutore(String autore)throws IOException{
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
            return null;
        }
        
        //Creo una lista di tipo List<Libro>
        List<Libro> libri = new ArrayList<>();
        
        for (int i = 0; i < bookArray.size(); i++) {
            JsonObject obj = bookArray.get(i).getAsJsonObject();
            if (obj.get("autore").getAsString().equalsIgnoreCase(autore)) {
                Libro libro = database.fromJson(obj, Libro.class);
                libri.add(libro);
            }
           
        }
        
        return(libri);
    };
    
    
    
    
    
    //DA ELIMINARE
    /**
     * @brief Mostra l'elemento cercato dal database dei libri
     * @pre Il libro è presente nel database
     * @post L’utente (sia bibliotecariə che studente) visualizza le informazioni del libro cercato
     */
    /*
    public static void cercaLibroISBN(Long ISBN)throws IOException{
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
        
        int i = Libro.ricercaLibroISBN(ISBN);
        
        if ( i != -1) {
            JsonObject obj = bookArray.get(i).getAsJsonObject();
            System.out.println(obj.toString());
        }
        else System.out.println("Libro non risulta nel nostro database");
    };
    */
    
    //DA ELIMINARE
    /**
     * @param titolo
     * @throws java.io.IOException
     * @brief Mostra il libro cercato per titolo
     * @pre Il libro è presente nel database
     * @post L’utente (sia bibliotecariə che studente) visualizza le informazioni del libro cercato
     */
    /*
    public static void cercaLibroTitolo(String titolo)throws IOException{
        List<Libro> libri = Libro.ricercaLibroTitolo(titolo);
        
        for (Libro l : libri) {
             System.out.println(l);
        }
    };
    */
    
    //DA ELIMINARE
    /**
     * @param autore
     * @throws java.io.IOException
     * @brief Mostra il libro cercato per titolo
     * @pre Il libro è presente nel database
     * @post L’utente (sia bibliotecariə che studente) visualizza le informazioni del libro cercato
     */
    /*
    public static void cercaLibroAutore(String autore)throws IOException{
        List<Libro> libri = Libro.ricercaLibroAutore(autore);
        
        if (libri.isEmpty()) {
            System.out.println("Il libro non esiste nel nostro database");
            return;
        }
        
        libri.forEach(l -> {
            System.out.println(l);
        });
    };
    */
}
