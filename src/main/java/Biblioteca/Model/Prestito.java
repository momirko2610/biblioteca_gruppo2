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

public class Prestito {
        
    private String matricola;
    private long ISBN; 
    private LocalDate dataInizio;
    private LocalDate dataFinePrevista;
    
    private transient Libro libro;     
    private transient Studente studente;

    private static final String NAME = "database.json"; 
    private static final Gson database = new GsonBuilder().setPrettyPrinting().create();
    private transient HBox azioni;

    public Prestito(Studente studente, Libro libro, LocalDate dataInizio, LocalDate dataFinePrevista) {
        this.studente = studente;
        this.libro = libro;
        this.matricola = studente.getMatricola();
        this.ISBN = libro.getIsbn(); 
        this.dataInizio = dataInizio;
        this.dataFinePrevista = dataFinePrevista;
        creaBottoni();
    }
    
    // Getter e Setter...
    public String getMatricola() { return matricola; }
    public long getIsbn() { return ISBN; } 
    public LocalDate getDataInizio() { return dataInizio; }
    public LocalDate getDataFinePrevista() { return dataFinePrevista; }
    public HBox getAzioni() { if (azioni == null) creaBottoni(); return azioni; }    
    
    /**
     * @brief Registra un nuovo prestito nel database
     * @return int: 0 successo, codici negativi errore
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

        if (!flag || libroTrovato == null) return -5; // Libro non trovato

        // --- CONTROLLI ---

        // 1. Controllo Account Studente
        if (checkAccountStudente() == -1) {
            return -1; 
        }

        // 2. Controllo Limite Prestiti (max 3)
        if (checkPrestitiAttiviStudente(matricola) == -1) {
            return -2; 
        }

        // 3. Controllo Ritardi
        if (checkRitardoRestituzionePrestito() == -1) {
            return -3; 
        }
        
        // 4. NUOVO CONTROLLO RICHIESTO: Copie Disponibili
        // Nota: passo 'libroTrovato' perché è l'oggetto recuperato dal DB
        if (checkCopieDisponibili(libroTrovato) == -1) {
            return -4; // Codice per "Copie non disponibili"
        }

        // --- SALVATAGGIO ---

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
        return 0; // SUCCESSO
    }

    // ... METODI STATIC (registrazioneRestituzione, ricercaPrestitoISBN) ...
    // (Mantieni quelli che avevi già nel codice precedente)

    public static void registrazioneRestituzione(String matricola, long ISBN) throws IOException {
         // ... (Copia il tuo metodo originale qui) ...
         // Per brevità ometto il corpo se è identico a prima
         // Ricordati di incollare il codice di restituzione che funzionava
    }

    public static int ricercaPrestitoISBN(Long ISBN) throws IOException {
         // ... (Mantieni il tuo codice originale) ...
         return -1; // Placeholder
    }

    // --- METODI PRIVATI DI CHECK ---

    private int checkAccountStudente() throws IOException {
        if(Studente.ricercaStudenteMatricola(matricola) >= 0){return 0;}
        return -1;
    }

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

    private int checkRitardoRestituzionePrestito() {
        if(dataFinePrevista.isAfter(now())){
            return 0;
        }
        return -1; 
    }
    
    // NUOVO CHECK AGGIUNTO O AGGIORNATO
    private int checkCopieDisponibili(Libro libro) throws IOException{
        File file = new File(NAME);
        JsonObject label;
        try (FileReader reader = new FileReader(file)) {
            label = database.fromJson(reader, JsonObject.class);
        }
        
        JsonArray bookArray = label.getAsJsonArray("libri");
        if (bookArray == null) return -1;
        
        // Uso ricercaLibroISBN per trovare l'indice corretto
        int idx = Libro.ricercaLibroISBN(libro.getIsbn());
        
        if (idx >= 0) {
             JsonObject libroJson = bookArray.get(idx).getAsJsonObject();
             int copie = libroJson.get("numCopie").getAsInt();
             
             if (copie > 0) return 0; // Copie disponibili
             else return -1; // Copie esaurite
        }
        
        return -1; // Libro non trovato
    }
    
    private void creaBottoni(){
        Button Ritorno = new Button("Restituito");
        Ritorno.setStyle("-fx-background-color: #2264E5; -fx-cursor: hand; -fx-text-fill: white;");
        this.azioni = new HBox(10, Ritorno);
        this.azioni.setAlignment(Pos.CENTER);
    }
}