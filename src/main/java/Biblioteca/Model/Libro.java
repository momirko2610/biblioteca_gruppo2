package Biblioteca.Model;

import com.google.gson.*;
//import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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
    private long ISBN; /*!<Codice identificativo unico del libro*/
    private int numCopie = 1; /*!<Numero di copie disponibili fisicamente nella biblioteca (non prestati))*/
    
    private static final String NAME = "database.json"; /*!<Nome del database contenente i libri*/
    private static final File FILE = new File(NAME); //File del database
    private static final Gson database = new GsonBuilder().setPrettyPrinting().create(); /*!<Oggetto della funzione GSON per la creazione dei file JSON*/
    // transient serve per non far salvare nel sile json l'hbox se no da errore
    private transient HBox azioni;

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
        
        creaBottoni();

    }
    
    private void creaBottoni(){
        // creo i bottoni che popoleranno la colonna azioni della tabella dei libri
        Button Modifica = new Button();
        Button Elimina = new Button();
        
        ImageView viewModifica = new ImageView(new Image(getClass().getResourceAsStream("/Biblioteca/icons/pencil-fiiled.png")));
        ImageView viewElimina = new ImageView(new Image(getClass().getResourceAsStream("/Biblioteca/icons/trash-filled.png")));
        
        viewModifica.setFitHeight(15);
        viewModifica.setFitWidth(15);
            
        viewElimina.setFitHeight(15);
        viewElimina.setFitWidth(15);

        Modifica.setGraphic(viewModifica);
        Elimina.setGraphic(viewElimina);
        
        Modifica.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        Elimina.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        this.azioni = new HBox(10, Modifica, Elimina);
        this.azioni.setAlignment(Pos.CENTER);
    }
    
    public HBox getAzioni() {
        // azioni è sempre nulla quando carico dal Database JSON
        if (azioni == null) {
            creaBottoni();
        }
        return azioni;
    }

    public String getTitolo() { return titolo; }
    public String getAutore() { return autore; }
    public int getAnnoPubblicazione() { return annoPubblicazione; }
    public long getIsbn() { return ISBN; }
    public int getNumCopie() { return numCopie; }
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
        JsonObject label = Database.leggiDatabase(FILE);
        
        JsonArray bookArray = Libro.getArrayLibri(label);
        
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
        
            Database.ordinaDatabaseLibro(bookArray, FILE, label);
            
            return;
        }
        Database.salva(FILE, label);
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
        JsonObject label = Database.leggiDatabase(FILE);
        
        JsonArray bookArray = Libro.getArrayLibri(label);
        
        if (bookArray == null) return;
        
        int i = Libro.ricercaLibroISBN(this.ISBN);
        
        if ( i != -1) {
            JsonObject obj = bookArray.get(i).getAsJsonObject();
            if (!(newTitle.isEmpty())) {
                obj.addProperty("titolo", newTitle);
                Database.ordinaDatabaseLibro(bookArray, FILE, label);             
            }
            if (!(newAuthor.isEmpty())) obj.addProperty("autore", newAuthor);
            if (!(newAnnoPubblicazione.isEmpty())) obj.addProperty("annoPubblicazione", newAnnoPubblicazione);
            if (!(newISBN.isEmpty())) {
                obj.addProperty("ISBN", newISBN);
                this.ISBN = Long.valueOf(newISBN);
            }
            if (!(newNumCopie.isEmpty())) obj.addProperty("numCopie", newNumCopie);
                        
            Database.salva(FILE, label);
            
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
        if (Prestito.ricercaPrestitoISBN(this.ISBN) != -1) {
            System.out.println("Non puoi eliminare il libro, è in prestito");
            return;
        }
        JsonObject label = Database.leggiDatabase(FILE);
        
        JsonArray bookArray = Libro.getArrayLibri(label);
        
        if (bookArray == null) {
            System.out.println("ERROR, database not found");
            return;
        }
        
        int i = Libro.ricercaLibroISBN(this.ISBN);
        
        if ( i != -1) {
            bookArray.remove(i);
            Database.salva(FILE, label);
            System.out.println("Libro eliminato");
        }
        else System.out.println("Libro non risulta nel nostro database");
    };

    //DA ELIMINARE
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
        
        JsonObject label = Database.leggiDatabase(FILE);
        
        JsonArray bookArray = Libro.getArrayLibri(label);
        
        if (bookArray == null) return -2;
        
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
        
        JsonObject label = Database.leggiDatabase(FILE);
        
        JsonArray bookArray = Libro.getArrayLibri(label);
        
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
        
        JsonObject label = Database.leggiDatabase(FILE);
        
        JsonArray bookArray = Libro.getArrayLibri(label);
        
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
    
     /**
     * @throws java.io.IOException
     * @brief salva in un JsonArray i libri contenuti nel database
     * @pre deve esistere un JsonObject contente i libri salvati nel database
     * @post Ottengo l'array dei libri
     */
    
    private static JsonArray getArrayLibri(JsonObject label) {
        //Ottengo l'array dei libri
        JsonArray bookArray = label.getAsJsonArray("libri");
        if (bookArray == null) {
            System.out.println("ERROR, database not found");
            return null;
        }
        return bookArray;
    }
    
    
    
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
