package Biblioteca.Model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import static java.time.LocalDate.now;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

/**
 * @file Prestito.java
 * @brief Classe del modello che gestisce la logica dei prestiti e delle restituzioni.
 * @author Mirko Montella
 * @author Ciro Senese
 * @author Achille Romano
 * @author Sabrina Soriano
 */
public class Prestito {
    
    /** @brief Nome del file database JSON. */
    private static final String NAME = "database.json"; 
    /** @brief Oggetto GSON per la gestione dei file JSON. */
    private static final Gson database = new GsonBuilder().setPrettyPrinting().create(); 
    /** @brief File del database. */
    private static final File FILE = new File(NAME); 
    
    /** @brief Matricola dello studente associato al prestito. */
    private final String matricola;
    /** @brief Codice ISBN del libro concesso in prestito. */
    private final long ISBN; 
    /** @brief Data di inizio del prestito. */
    private final LocalDate dataInizio;
    /** @brief Data entro la quale è prevista la restituzione. */
    private final LocalDate dataFinePrevista;
    
    /** @brief Riferimento transiente all'oggetto Libro (non salvato su JSON). */
    private transient Libro libro;     
    /** @brief Riferimento transiente all'oggetto Studente (non salvato su JSON). */
    private transient Studente studente;
    
    /** @brief Contenitore grafico per le azioni nella tabella (non salvato su JSON). */
    private transient HBox azioni;

    /**
     * @brief Costruttore della classe Prestito.
     * @param studente Lo studente che richiede il prestito.
     * @param libro Il libro richiesto.
     * @param dataInizio Data odierna di inizio.
     * @param dataFinePrevista Data di scadenza del prestito.
     */
    public Prestito(Studente studente, Libro libro, LocalDate dataInizio, LocalDate dataFinePrevista) {
        this.studente = studente;
        this.libro = libro;
        this.matricola = studente.getMatricola();
        this.ISBN = libro.getIsbn(); 
        this.dataInizio = dataInizio;
        this.dataFinePrevista = dataFinePrevista;
        
        creaBottoni();
    }

    /** @brief Restituisce l'HBox delle azioni per la TableView. */
    public HBox getAzioni() {
        if (azioni == null) {
            creaBottoni();
        }
        return azioni;
    }

    public String getMatricola() { return matricola; }
    public long getIsbn() { return ISBN; } 
    public LocalDate getDataInizio() { return dataInizio; }
    public LocalDate getDataFinePrevista() { return dataFinePrevista; }

   /**
     * @brief Registra un nuovo prestito nel sistema eseguendo tutti i controlli.
     * @param matricola Matricola dello studente.
     * @param ISBN Codice ISBN del libro.
     * @return 0 successo, -1 studente non trovato, -2 limite prestiti raggiunto, 
     * -3 ritardi presenti, -4 copie esaurite, -5 libro non trovato.
     * @throws java.io.IOException In caso di errori di I/O.
     */
    public int registrazionePrestito(String matricola, long ISBN) throws IOException {
        File file = new File(NAME);
        JsonObject label;

        if (!file.exists()) return -10; 

        try (FileReader reader = new FileReader(file)) {
            try {
                label = database.fromJson(reader, JsonObject.class);
            } catch (JsonSyntaxException | JsonIOException e) {
                label = new JsonObject();
            }
        }
        
        if (label == null) label = new JsonObject();

        JsonArray bookArray = label.getAsJsonArray("libri");
        if (bookArray == null) {
            bookArray = new JsonArray();
            label.add("libri", bookArray);
        }

        Type listType = new TypeToken<ArrayList<Libro>>(){}.getType();
        List<Libro> bookList = database.fromJson(bookArray, listType);
        if (bookList == null) bookList = new ArrayList<>();

        boolean flag = false;
        Libro libroTrovato = null;
        int indiceLibro = -1;

        for (int i = 0; i < bookList.size(); i++) {
            Libro l = bookList.get(i);
            if (l.getIsbn() == ISBN) {
                libroTrovato = l;
                indiceLibro = i;
                flag = true;
                break;
            }
        }

        if (!flag || libroTrovato == null) return -5; 

        if (checkAccountStudente() == -1) return -1; 
        if (checkPrestitiAttiviStudente(matricola) == -1) return -2; 
        if (checkRitardoRestituzionePrestito() == -1) return -3; 
        if (checkCopieDisponibili(libroTrovato) == -1) return -4;

        // Decremento copie nel database
        JsonObject libroJson = bookArray.get(indiceLibro).getAsJsonObject();
        int nuoveCopie = libroTrovato.getNumCopie() - 1;
        libroJson.addProperty("numCopie", nuoveCopie); 
        
        JsonArray prestitiArray = label.getAsJsonArray("prestiti");
        if (prestitiArray == null) {
            prestitiArray = new JsonArray();
            label.add("prestiti", prestitiArray);
        }

        JsonObject newPrestito = new JsonObject();
        newPrestito.addProperty("matricola", this.matricola);
        newPrestito.addProperty("titolo", libroTrovato.getTitolo()); 
        newPrestito.addProperty("autore", libroTrovato.getAutore());
        newPrestito.addProperty("annoPubblicazione", libroTrovato.getAnnoPubblicazione());
        newPrestito.addProperty("ISBN", libroTrovato.getIsbn()); 
        newPrestito.addProperty("dataInizio", this.dataInizio.toString());
        newPrestito.addProperty("dataFinePrevista", this.dataFinePrevista.toString());
        newPrestito.addProperty("dataRestituzioneEffettiva", ""); 

        prestitiArray.add(newPrestito);
        Database.ordinaDatabasePrestito(prestitiArray, file, label);
        
        System.out.println("Prestito registrato con successo!");
        return 0;
    }

    /**
     * @brief Registra la restituzione di un libro e aggiorna l'inventario.
     * @param matricola Matricola dello studente.
     * @param ISBN Codice ISBN del libro restituito.
     * @throws java.io.IOException In caso di errori di scrittura.
     * @pre Il bibliotecario deve essere autenticato.
     * @post Il prestito viene eliminato e l'inventario aggiornato.
     */
    public static void registrazioneRestituzione(String matricola, long ISBN) throws IOException {
        JsonObject label = Database.leggiDatabase(FILE);
        JsonArray prestitiArray = label.getAsJsonArray("prestiti");
        
        if (prestitiArray == null) {
            System.out.println("Nessun prestito attivo nel database.");
            return;
        }
        
        int indexDaRimuovere = ricercaPrestito(matricola, ISBN);
        if(indexDaRimuovere == -1) return;
        
        JsonArray bookArray = Libro.getArrayLibri(label);
        int indiceLibro = Libro.ricercaLibroISBN(ISBN);
        if(indiceLibro == -1) return;

        JsonObject bookObj = bookArray.get(indiceLibro).getAsJsonObject();  
        int copieAttuali = bookObj.get("numCopie").getAsInt();
        bookObj.addProperty("numCopie", copieAttuali + 1);

        prestitiArray.remove(indexDaRimuovere);
        Database.salva(FILE, label);
    }
    
    /**
     * @brief Cerca un prestito specifico tramite matricola e ISBN.
     * @return Indice dell'array JSON o -1 se non trovato.
     */
    public static int ricercaPrestito(String matricola, long ISBN) throws IOException {
        JsonObject label = Database.leggiDatabase(FILE);
        JsonArray loanArray = label.getAsJsonArray("prestiti");
        if (loanArray == null) return -2;
        
        for (int i = 0; i < loanArray.size(); i++) {
            JsonObject obj = loanArray.get(i).getAsJsonObject();
            if (obj.get("ISBN").getAsLong() == (ISBN) && obj.get("matricola").getAsString().equals(matricola)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * @brief Verifica l'esistenza di prestiti attivi per un determinato ISBN.
     * Utilizzato per impedire la cancellazione di libri attualmente in prestito.
     */
    public static int ricercaPrestitoISBN(long ISBN) throws IOException {
        JsonObject label = Database.leggiDatabase(FILE);
        JsonArray loanArray = label.getAsJsonArray("prestiti");
        if (loanArray == null) return -2;
        
        for (int i = 0; i < loanArray.size(); i++) {
            JsonObject obj = loanArray.get(i).getAsJsonObject();
            if (obj.get("ISBN").getAsLong() == (ISBN)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @brief Verifica se lo studente è registrato nel database.
     * @return 0 se presente, -1 altrimenti.
     */
    private int checkAccountStudente() throws IOException {
        if(Studente.ricercaStudenteMatricola(matricola) >= 0){return 0;}
        return -1;
    }

    /**
     * @brief Verifica che lo studente non abbia superato il limite di 3 prestiti attivi.
     * @return 0 se entro il limite, -1 se superato.
     */
    private int checkPrestitiAttiviStudente(String matricola) throws IOException {
        File file = new File(NAME);
        JsonObject label;
        try (FileReader reader = new FileReader(file)) {
            label = database.fromJson(reader, JsonObject.class);
        }
        JsonArray prestitiArray = label.getAsJsonArray("prestiti");
        if (prestitiArray == null) return 0; 
        
        int conta = 0;
        for (int i = 0; i < prestitiArray.size(); i++) {                
            JsonObject obj = prestitiArray.get(i).getAsJsonObject();
            if (obj.get("matricola").getAsString().equals(matricola)){
                conta++;
            }   
        }
        if(conta >= 3) return -1;            
        return 0;
    }

    /**
     * @brief Verifica la presenza di ritardi nelle restituzioni.
     * @return 0 se in regola, -1 se in ritardo.
     */
   private int checkRitardoRestituzionePrestito() {
        if(dataFinePrevista.isAfter(now())){
            return 0;
        }
        return -1; 
    }

    /**
     * @brief Verifica la disponibilità fisica di copie del libro.
     * @return 0 se disponibile, -1 se esaurito.
     */
    private int checkCopieDisponibili(Libro libro) throws IOException{
        File file = new File(NAME);
        JsonObject label;
        try (FileReader reader = new FileReader(file)) {
            label = database.fromJson(reader, JsonObject.class);
        }
        
        JsonArray bookArray = label.getAsJsonArray("libri");
        if (bookArray == null) return -1;
        
        int idx = Libro.ricercaLibroISBN(libro.getIsbn());
        if (idx >= 0) {
             JsonObject libroJson = bookArray.get(idx).getAsJsonObject();
             int copie = libroJson.get("numCopie").getAsInt();
             if (copie > 0) return 0;
             else return -1;
        }
        return -1;
    }

    /**
     * @brief Inizializza il pulsante "Restituito" per la gestione UI dei prestiti.
     */
    private void creaBottoni(){
        Button Ritorno = new Button("Restituito");
        Ritorno.setStyle("-fx-background-color: #2264E5; -fx-cursor: hand; -fx-text-fill: white;");
        this.azioni = new HBox(10, Ritorno);
        this.azioni.setAlignment(Pos.CENTER);
    }
}