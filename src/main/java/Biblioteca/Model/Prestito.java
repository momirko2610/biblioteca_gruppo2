package Biblioteca.Model;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import static java.time.LocalDate.now;
import java.util.Comparator;
import java.util.Iterator;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;



/**
 * @brief Classe che gestisce il database dei prestiti
 * @author Mirko Montella
 * @author Ciro Senese
 * @author Achille Romano
 * @author Sabrina Soriano
 */
public class Prestito {
    
    
    private static final String NAME = "database.json"; /*!<Nome del database contenente i prestiti*/
    private static final Gson database = new GsonBuilder().setPrettyPrinting().create(); /*!<Oggetto della funzione GSON per la creazione dei file JSON*/
     private static final File FILE = new File(NAME); //File del database
    
    private final String matricola;
    private final long ISBN; 
    private final LocalDate dataInizio;
    private final LocalDate dataFinePrevista;
    

    private transient Libro libro;    
    private transient Studente studente;
    
    // transient serve per non far salvare nel sile json l'hbox se no da errore
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
    public HBox getAzioni() {
        // azioni è sempre nulla quando carico dal Database JSON
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
     * @param matricola
     * @param ISBN
     * @throws java.io.IOException
     * @brief Aggiorna il database dei prestiti creando un nuovo elemento
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

        if (checkAccountStudente() == -1) {
            return -1; 
        }

        if (checkPrestitiAttiviStudente(matricola) == -1) {
            return -2; 
        }

        if (checkRitardoRestituzionePrestito() == -1) {
            return -3; 
        }
        
        if (checkCopieDisponibili(libroTrovato) == -1) {
            return -4;
        }


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
     * @param matricola
     * @param ISBN
     * @throws java.io.IOException
     * @brief Aggiorna il database dei prestiti eliminando un elemento
     * @pre Il Bibliotecariə deve essere autenticatə
     * @post N/A
     */
    public static void registrazioneRestituzione(String matricola, long ISBN) throws IOException {
   
        //Legge il database
        JsonObject label = Database.leggiDatabase(FILE);
    
        // Ottengo l'array dei prestiti
        JsonArray prestitiArray = label.getAsJsonArray("prestiti");
        if (prestitiArray == null) {
            System.out.println("Nessun prestito attivo nel database.");
            return;
        }
        
        //Trovo il prestito da rimuovere
        
        int indexDaRimuovere = ricercaPrestito(matricola, ISBN);
        
        if(indexDaRimuovere == -1){
            System.out.println("Prestito non trovato!\n");
            return;
        }
        
        if(indexDaRimuovere == -2){
            System.out.println("Array Prestiti non trovato in database!\n\n");
            return;
        }
      
   
  
        //Aggiorno le copie del libro (Aumenta di 1)

        //Ottengo l'Array dei libri
        JsonArray bookArray = Libro.getArrayLibri(label);

        // Cerchiamo il libro nell'array dei libri
        int indiceLibro = Libro.ricercaLibroISBN(ISBN);
        if(indiceLibro == -1){
            System.out.println("Libro non trovato!\n");
            return;
        } 
        //Libro trovato!
        JsonObject bookObj = bookArray.get(indiceLibro).getAsJsonObject();  

        // Libro trovato, incremento le copie
        int copieAttuali = bookObj.get("numCopie").getAsInt();
        bookObj.addProperty("numCopie", copieAttuali + 1);
        System.out.println("Copie libro aggiornate: " + (copieAttuali + 1));


        //Rimuovo il prestito e Salvo
        prestitiArray.remove(indexDaRimuovere);

        Database.salva(FILE, label);
        System.out.println("Prestito eliminato e database salvato.");
            
        
        
    }
    
    /**
     * @param matricola
     * @param ISBN
     * @throws java.io.IOException
     * @brief Cerca un elemento dal database dei libri
     * @pre N/A
     * @post L’utente (sia bibliotecariə che studente) visualizza il libro selezionato
     * @return posizione del libro nel database o -1 in caso di libro non presente
     */
    public static int ricercaPrestito(String matricola, long ISBN) throws IOException {
        
        //Leggo il database
        JsonObject label = Database.leggiDatabase(FILE);
        
        //Ottengo l'array dei prestiti
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
    
    
    public static int ricercaPrestitoISBN(long ISBN) throws IOException {
        
        //Leggo il database
         JsonObject label = Database.leggiDatabase(FILE);
        
        //Ottengo l'array dei prestiti
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
     * @brief Verifica se lo studente esiste nel database degli studenti
     * @pre Lo studente deve essere registrato nel database
     * @post Permesso prestito libro
     * @return boolean
     */
    private int checkAccountStudente() throws IOException {
        if(Studente.ricercaStudenteMatricola(matricola) >= 0){return 0;}
        return -1;
    }
   

    /**
     * @brief Verifica quanti prestiti ha attivo lo studente
     * @pre Accesso database studenti e prestiti
     * @post N/A
     * @return numero di prestiti attivi
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
     * @brief Verifica se lo studente ha un ritardo nella restituzione di un prestito
     * @pre Accesso database studenti e prestiti
     * @post N/A
     * @return boolean
     */
   private int checkRitardoRestituzionePrestito() {
        if(dataFinePrevista.isAfter(now())){
            return 0;
        }
        return -1; 
    }

     /**
     * @brief Verifica se è presente almeno una copia del libro nel database dei libri
     * @pre Accesso database libri e prestiti
     * @post N/A
     * @return numero di copie disponibili/boolean
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
             
             if (copie > 0) return 0; // Copie disponibili
             else return -1; // Copie esaurite
        }
        
        return -1; // Libro non trovato
    }

    /**
     * @brief crea i bottini nell'interfaccia
     */
    
    private void creaBottoni(){
        // creo i bottoni che popoleranno la colonna azioni della tabella dei libri
        Button Ritorno = new Button("Restituito");
        
        Ritorno.setStyle("-fx-background-color: #2264E5; -fx-cursor: hand; -fx-text-fill: white;");

        this.azioni = new HBox(10, Ritorno);
        this.azioni.setAlignment(Pos.CENTER);
    }
}




