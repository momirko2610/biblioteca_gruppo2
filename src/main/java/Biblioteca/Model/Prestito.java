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
    
    private void creaBottoni(){
        // creo i bottoni che popoleranno la colonna azioni della tabella dei libri
        Button Ritorno = new Button("Restituito");
        
        Ritorno.setStyle("-fx-background-color: #2264E5; -fx-cursor: hand; -fx-text-fill: white;");

        this.azioni = new HBox(10, Ritorno);
        this.azioni.setAlignment(Pos.CENTER);
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
    public void registrazionePrestito(String matricola, long ISBN) throws IOException {
        
        //Legge il database
        JsonObject label = Database.leggiDatabase(FILE);
        //Ottiene l'Array dei libri
        JsonArray bookArray = Libro.getArrayLibri(label);

        Type listType = new TypeToken<ArrayList<Libro>>(){}.getType();
        List<Libro> bookList = database.fromJson(bookArray, listType);

        if (bookList == null) bookList = new ArrayList<>();
        
        boolean flag = false;
        Libro libroTrovato = null;
        int indiceLibro = -1;
        //Cerco il libro da prestare nell'Array
        for (int i = 0; i < bookList.size(); i++) {
            Libro l = bookList.get(i);

            if (l.getIsbn() == ISBN) {
                libroTrovato = l;   //Libro trovato!
                indiceLibro = i;
                flag = true;
                break;
            }
        }

        if (!flag || libroTrovato == null) {
            System.out.println("Libro con ISBN " + ISBN + " non trovato!");
            return;
        }
        
        //Vari check per proseguire con il prestito
        if (checkAccountStudente(matricola) == -1) {
            System.out.println("Studente non trovato!\n");
            return;
        }

        if (checkPrestitiAttiviStudente(matricola) == -1) {
            System.out.println("Ha troppi prestiti attivi!\n");
            return;
        }

        if (checkRitardoRestituzionePrestito(matricola, ISBN) == -1) {
            System.out.println("Ha un prestito in ritardo!\n");
            return;
        }
        
        if (checkCopieDisponibili(ISBN) == -1) {
            System.out.println("Non ci sono copie del libro!\n");
            return;
        }
        
        //Sottrae una copia del libro 
        JsonObject libroJson = bookArray.get(indiceLibro).getAsJsonObject();
        int nuoveCopie = libroTrovato.getNumCopie() - 1;
        libroJson.addProperty("numCopie", nuoveCopie); 
       
        //Ottiene, o genera se inesistente, un array di prestiti
        JsonArray prestitiArray = label.getAsJsonArray("prestiti");
        if (prestitiArray == null) {
            prestitiArray = new JsonArray();
            label.add("prestiti", prestitiArray);
        }
        //Creo il nuovo prestito da registrare
        JsonObject newPrestito = new JsonObject();
        newPrestito.addProperty("matricola", this.matricola);
        newPrestito.addProperty("titolo", libroTrovato.getTitolo()); 
        newPrestito.addProperty("autore", libroTrovato.getAutore());
        newPrestito.addProperty("annoPubblicazione", libroTrovato.getAnnoPubblicazione());
        newPrestito.addProperty("ISBN", libroTrovato.getIsbn()); 
        newPrestito.addProperty("dataInizio", this.dataInizio.toString());
        newPrestito.addProperty("dataFinePrevista", this.dataFinePrevista.toString());
        newPrestito.addProperty("dataRestituzioneEffettiva", ""); 

        //Aggiungo il nuovo prestito all'Array
        prestitiArray.add(newPrestito);
        
        //Salvo l'Array dei prestiti in maniera ordinata per dataFinePrevista
        Database.ordinaDatabasePrestito(prestitiArray, FILE, label);
        
        System.out.println("Prestito registrato con successo!");
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
      
      /*  for (int i = 0; i < prestitiArray.size(); i++) {              
            JsonObject obj = prestitiArray.get(i).getAsJsonObject();
            
            // Confronto Matricola (String) e ISBN (Long)
            String matDB = obj.get("matricola").getAsString();
            long isbnDB = obj.get("ISBN").getAsLong();

            if (matDB.equals(matricola) && isbnDB == ISBN) {
                indexDaRimuovere = i;
                trovato = true;
                break;
            }            
        }*/
  
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

      /*  for (int k = 0; k < bookArray.size(); k++) {
            JsonObject bookObj = bookArray.get(k).getAsJsonObject();
            if (bookObj.get("ISBN").getAsLong() == ISBN) {
                // Abbiamo trovato il libro: incrementiamo le copie
                int copieAttuali = bookObj.get("numCopie").getAsInt();
                bookObj.addProperty("numCopie", copieAttuali + 1);
                System.out.println("Copie libro aggiornate: " + (copieAttuali + 1));
                break;
            }
        }*/


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
    private int checkAccountStudente(String matricola) throws IOException {
        
        if(Studente.ricercaStudenteMatricola(matricola) != -1){return 0;}
        return -1;
    }

    /**
     * @brief Verifica se è presente almeno una copia del libro nel database dei libri
     * @pre Accesso database libri e prestiti
     * @post N/A
     * @return numero di copie disponibili/boolean
     */
    private int checkCopieDisponibili(long ISBN) throws IOException{  
        
        //Leggo il database
        JsonObject label = Database.leggiDatabase(FILE);
        
        //Ottengo l'Array dei libri
        JsonArray bookArray = Libro.getArrayLibri(label);

        // Cerchiamo il libro nell'array dei libri
        int indiceLibro = Libro.ricercaLibroISBN(ISBN);
        
        if(indiceLibro == -1){
            System.out.println("Libro non trovato!\n");
            return -1;
        } 
        //Libro trovato!
        JsonObject bookObj = bookArray.get(indiceLibro).getAsJsonObject();  

        // Abbiamo trovato il libro: incrementiamo le copie
        int copieAttuali = bookObj.get("numCopie").getAsInt();
        
        if(copieAttuali > 0){return 0;}
        
        

        return -1;
    }

    /**
     * @brief Verifica quanti prestiti ha attivo lo studente
     * @pre Accesso database studenti e prestiti
     * @post N/A
     * @return numero di prestiti attivi
     */
    private int checkPrestitiAttiviStudente(String matricola) throws IOException {
           
        //Leggo il database
        JsonObject label = Database.leggiDatabase(FILE);
               
        //Ottengo l'array dei prestiti
        JsonArray prestitiArray = label.getAsJsonArray("prestiti");
          
            int i;
            int flag = 0;
            for (i = 0; i < prestitiArray.size(); i++) {               
                JsonObject obj = prestitiArray.get(i).getAsJsonObject();
                if (obj.get("matricola").getAsString().equals(matricola)){
                    flag++;
                }   
            }
            if(flag >= 3){
                return -1;
            }           
            return 0;
        
        
    }

    /**
     * @brief Verifica se lo studente ha un ritardo nella restituzione di un prestito
     * @pre Accesso database studenti e prestiti
     * @post N/A
     * @return boolean
     */
    private int checkRitardoRestituzionePrestito(String matricola, long ISBN) throws IOException {
        
        //Leggo il database
        JsonObject label = Database.leggiDatabase(FILE);
               
        //Ottengo l'array dei prestiti
        JsonArray prestitiArray = label.getAsJsonArray("prestiti");
        
        int i = ricercaPrestito(matricola, ISBN);
        
        if(i != -1 && i != -2){
            //Ottengo il prestito
            JsonObject obj = prestitiArray.get(i).getAsJsonObject();
            //Ottengo la data di fine prevista del prestito
            LocalDate dataFine = LocalDate.parse(obj.get("dataFinePrevista").getAsString());
            //Controllo che il prestito non sia scaduto
            if(dataFine.isAfter(now())){
                return 0;
            }
        }
       
        return -1;
    }
}




