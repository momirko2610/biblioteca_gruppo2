package softeng.progetto.gruppo2.Model;

import com.google.gson.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * @file Libro.java
 * @brief Classe del modello che gestisce le informazioni e le operazioni sui libri,
 * implementando la logica per la conservazione e modifica di Titolo, Autori, Anno, 
 * ISBN e Numero di copie.
 * @author Sabrina Soriano
 * @author Mirko Montella
 * @author Ciro Senese
 * @author Achille Romano
 */
public class Libro {
    /** @brief Titolo del libro. */
    private final String titolo; 
    /** @brief Autore/i del libro. */
    private final String autore; 
    /** @brief Anno di pubblicazione del libro. */
    private final int annoPubblicazione; 
    /** @brief Codice identificativo univoco (ISBN) del libro. */
    private long ISBN; 
    /** @brief Numero di copie disponibili fisicamente (non prestate). */
    private int numCopie; 
    
    /** @brief Nome del database JSON[cite: 17]. */
    private static final String NAME = "database.json"; 
    /** @brief File del database. */
    private static final File FILE = new File(NAME); 
    /** @brief Oggetto GSON per la gestione dei file JSON. */
    private static final Gson database = new GsonBuilder().setPrettyPrinting().create(); 
    
    /** @brief Contenitore grafico per i bottoni di azione nella TableView (non salvato su JSON). */
    private transient HBox azioni;

    /**
     * @brief Costruttore della classe Libro.
     * @param titolo Titolo del libro.
     * @param autore Autore/i.
     * @param annoPubblicazione Anno di pubblicazione.
     * @param ISBN Codice ISBN.
     * @param numCopie Numero di copie iniziali.
     */
    public Libro(String titolo, String autore, int annoPubblicazione, long ISBN, int numCopie) {
        this.titolo = titolo;
        this.autore = autore;
        this.annoPubblicazione = annoPubblicazione;
        this.ISBN = ISBN;
        this.numCopie = numCopie;
        
        creaBottoni();
    }
    
    /**
     * @brief Inizializza i componenti grafici (Modifica/Elimina) per la colonna Azioni.
     */
    private void creaBottoni(){
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
    
    /** @brief Restituisce l'HBox delle azioni, inizializzandolo se nullo (dopo caricamento JSON). */
    public HBox getAzioni() {
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
        return String.format("Titolo: %s| Autore: %s | Anno: %d | ISBN: %d | Copie: %d",
            titolo, autore, annoPubblicazione, ISBN, numCopie);
    }
    
    /**
     * @brief Inserisce un nuovo libro o aggiorna le copie di uno esistente.
     * @throws java.io.IOException Se si verificano errori di scrittura.
     * @pre Il bibliotecario deve essere autenticato[cite: 196, 197].
     * @post Il catalogo dei libri è aggiornato su file[cite: 25, 198].
     * @return 0 successo, -1 ISBN non valido (13 cifre), -2 Anno non valido (4 cifre).
     */
    public int inserisciLibro() throws IOException {
        JsonObject label = Database.leggiDatabase(FILE);
        JsonArray bookArray = Libro.getArrayLibri(label);
        
        if (bookArray == null) bookArray = new JsonArray();
        
        int i = Libro.ricercaLibroISBN(this.ISBN);
        
        if (i >= 0) {
            JsonObject obj = bookArray.get(i).getAsJsonObject();
            obj.addProperty("numCopie", this.numCopie);
            bookArray.set(i, obj);
        }
        else {
            JsonObject newBook = new JsonObject();
            newBook.addProperty("titolo", this.titolo);
            newBook.addProperty("autore", this.autore);
            
            if (String.valueOf(this.ISBN).matches("\\d{13}")) {
                newBook.addProperty("ISBN", this.ISBN);
            } else { return -1; }
            
            if (String.valueOf(this.annoPubblicazione).matches("\\d{4,}")) {
                newBook.addProperty("annoPubblicazione", this.annoPubblicazione);
            } else { return -2; }

            newBook.addProperty("numCopie", this.numCopie);
            bookArray.add(newBook);
            Database.ordinaDatabaseLibro(bookArray, FILE, label);
        }
        Database.salva(FILE, label);
        return 0;
    }

    /**
     * @brief Modifica i dati di un libro esistente.
     * @param newTitle Nuovo titolo.
     * @param newAuthor Nuovo autore.
     * @param newAnnoPubblicazione Nuovo anno.
     * @param newISBN Nuovo ISBN.
     * @param newNumCopie Nuovo numero di copie.
     * @throws java.io.IOException Se si verificano errori di scrittura.
     * @pre Il bibliotecario deve essere autenticato.
     * @post Il catalogo aggiornato viene salvato su file.
     * @return 0 successo, -2 libro non trovato, -3 database non trovato.
     */
    public int modificaDatiLibro(String newTitle, String newAuthor, String newAnnoPubblicazione, String newISBN, String newNumCopie) throws IOException {
        JsonObject label = Database.leggiDatabase(FILE);
        JsonArray bookArray = Libro.getArrayLibri(label);
        
        if (bookArray == null) return -3;
        
        int i = Libro.ricercaLibroISBN(this.ISBN);
        
        if (i >= 0) {
            JsonObject obj = bookArray.get(i).getAsJsonObject();
            boolean titleChanged = false;
            
            if (!(newTitle.isEmpty())) {
                obj.addProperty("titolo", newTitle);
                titleChanged = true;             
            }
            if (!(newAuthor.isEmpty())) obj.addProperty("autore", newAuthor);
            if (!(newAnnoPubblicazione.isEmpty())) obj.addProperty("annoPubblicazione", newAnnoPubblicazione);
            if (!(newISBN.isEmpty())) {
                obj.addProperty("ISBN", newISBN);
                this.ISBN = Long.valueOf(newISBN);
            }
            if (!(newNumCopie.isEmpty())) obj.addProperty("numCopie", newNumCopie);
            
            if (titleChanged) {
                 Database.ordinaDatabaseLibro(bookArray, FILE, label);
            }
            Database.salva(FILE, label);
            return 0;
        }
        return -2;
    }

    /**
     * @brief Rimuove un libro dal catalogo.
     * @throws java.io.IOException Se si verificano errori di scrittura.
     * @pre Il libro non deve essere attualmente in prestito.
     * @post Il libro viene eliminato dal database
     * @return 0 successo, -1 libro in prestito, -2 libro non trovato, -3 database mancante.
     */
    public int cancellazioneDatiLibro() throws IOException {
        if (Prestito.ricercaPrestitoISBN(this.ISBN) >= 0) {
            return -1;
        }
        JsonObject label = Database.leggiDatabase(FILE);
        JsonArray bookArray = Libro.getArrayLibri(label);
        
        if (bookArray == null) return -3;
        
        int i = Libro.ricercaLibroISBN(this.ISBN);
        
        if (i >= 0) {
            bookArray.remove(i);
            Database.salva(FILE, label);
            return 0;
        }
        return -2;
    }

    /**
     * @brief Ricerca un libro tramite codice ISBN.
     * @param ISBN Codice ISBN da cercare.
     * @throws java.io.IOException Se si verificano errori di lettura.
     * @return La posizione nell'array JSON o -1 se non presente.
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
     * @brief Mostra i libri corrispondenti a un titolo.
     * @param titolo Query di ricerca per titolo.
     * @return Una lista di oggetti Libro corrispondenti.
     * @throws java.io.IOException Se si verificano errori di lettura.
     */
    public static List<Libro> ricercaLibroTitolo(String titolo) throws IOException {
        JsonObject label = Database.leggiDatabase(FILE);
        JsonArray bookArray = Libro.getArrayLibri(label);
        
        if (bookArray == null) return null;
        
        List<Libro> libri = new ArrayList<>();
        for (int i = 0; i < bookArray.size(); i++) {
            JsonObject obj = bookArray.get(i).getAsJsonObject();
            if (obj.get("titolo").getAsString().compareTo(titolo) > 0) break;
            else if (obj.get("titolo").getAsString().equalsIgnoreCase(titolo)) {
                Libro libro = database.fromJson(obj, Libro.class);
                libri.add(libro);
            }
        }
        return libri;
    }

    /**
     * @brief Mostra i libri corrispondenti a un autore.
     * @param autore Query di ricerca per autore.
     * @return Lista di libri corrispondenti.
     * @throws java.io.IOException Se si verificano errori di lettura.
     */
    public static List<Libro> ricercaLibroAutore(String autore) throws IOException {
        JsonObject label = Database.leggiDatabase(FILE);
        JsonArray bookArray = Libro.getArrayLibri(label);
        
        if (bookArray == null) return null;
        
        List<Libro> libri = new ArrayList<>();
        for (int i = 0; i < bookArray.size(); i++) {
            JsonObject obj = bookArray.get(i).getAsJsonObject();
            if (obj.get("autore").getAsString().equalsIgnoreCase(autore)) {
                Libro libro = database.fromJson(obj, Libro.class);
                libri.add(libro);
            }
        }
        return libri;
    }

    /**
     * @brief Estrae l'array dei libri dal database principale.
     * @param label JsonObject del database.
     * @return JsonArray "libri" o null se mancante.
     */
    public static JsonArray getArrayLibri(JsonObject label) {
        JsonArray bookArray = label.getAsJsonArray("libri");
        if (bookArray == null) {
            return null;
        }
        return bookArray;
    }
}