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
    
    private String matricola;
    private long ISBN; 
    private LocalDate dataInizio;
    private LocalDate dataFinePrevista;
    

    private transient Libro libro;    
    private transient Studente studente;


    public Prestito(Studente studente, Libro libro, LocalDate dataInizio, LocalDate dataFinePrevista) {
        this.studente = studente;
        this.libro = libro;
        this.matricola = studente.getMatricola();
        this.ISBN = libro.getIsbn(); 
        this.dataInizio = dataInizio;
        this.dataFinePrevista = dataFinePrevista;
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

        File file = new File(NAME);
        JsonObject label;

        // 1. Lettura sicura del Database
        if (!file.exists()) {
            System.out.println("Errore: Database non trovato.");
            return;
        }

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

        if (!flag || libroTrovato == null) {
            System.out.println("Libro con ISBN " + ISBN + " non trovato!");
            return;
        }


        if (libroTrovato.getNumCopie() <= 0) {
             System.out.println("Copie terminate per questo libro!");
             return;
        }

        if (checkAccountStudente() == -1) {
            System.out.println("Studente non trovato!\n");
            return;
        }

        if (checkPrestitiAttiviStudente(matricola) == -1) {
            System.out.println("Ha troppi prestiti attivi!\n");
            return;
        }

        if (checkRitardoRestituzionePrestito() == -1) {
            System.out.println("Ha un prestito in ritardo!\n");
            return;
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

      
        try (FileWriter writer = new FileWriter(file)) {
            database.toJson(label, writer);
        }
        
        System.out.println("Prestito registrato con successo!");
    }
    
    
    //FORSE INUTILE
    /**
     * @brief Mostra gli elementi presenti nel database dei libri
     * @pre Il Bibliotecariə deve essere autenticatə
     * @post Bibliotecariə visualizza la lista completa dei libri in prestito in ordine alfabetico
     */
    public void visualizzazioneElencoPrestiti() throws IOException{
    
        File file = new File(NAME);
        
        //Leggo il database
        JsonObject label;
        try (FileReader reader = new FileReader(file)) {
            label = database.fromJson(reader, JsonObject.class);
        }
        
        //Ottengo l'array dei prestiti
        JsonArray prestitiArray = label.getAsJsonArray("prestiti");
        if (prestitiArray == null) {
            System.out.println("ERROR, database not found");  //da implementare come interfaccia grafica
        }
        
        for (int i = 0; i < prestitiArray.size(); i++) {
            JsonObject obj = prestitiArray.get(i).getAsJsonObject();
            System.out.println(obj.toString());
        }
    
    }

    /**
     * @param matricola
     * @param ISBN
     * @throws java.io.IOException
     * @brief Aggiorna il database dei prestiti eliminando un elemento
     * @pre Il Bibliotecariə deve essere autenticatə
     * @post N/A
     */
    public void registrazioneRestituzione(String matricola, long ISBN) throws IOException{
   
        File file = new File(NAME);
        
        //Leggo il database
        JsonObject label;
        try (FileReader reader = new FileReader(file)) {
            label = database.fromJson(reader, JsonObject.class);
        }
        
        //Ottengo l'array dei prestiti
        JsonArray prestitiArray = label.getAsJsonArray("prestiti");
        if (prestitiArray == null) {
            System.out.println("ERROR, database not found");  //da implementare come interfaccia grafica
            return;
        }

        
        int i;
        boolean flag = false;       //flag necessaria per capire se il prestito è presente
        //Trova il prestito da rimuovere, se presente
        for (i = 0; i < prestitiArray.size(); i++) {              
            JsonObject obj = prestitiArray.get(i).getAsJsonObject();
            if (obj.get("matricola").getAsString().equals(matricola) && obj.get("ISBN").getAsString().equals((Long.toString(ISBN)))){
                flag = true;
                break;
            }            
        }
        
        if (flag) {
            //Ottengo l'array dei libri
            JsonArray bookArray = label.getAsJsonArray("libri");
            if (bookArray == null) {bookArray = new JsonArray();}

            //Metto le copie a -1
            int j = Libro.ricercaLibroISBN(ISBN);   //indice del libro da restituire

            JsonObject obj = bookArray.get(j).getAsJsonObject();

            int copie = obj.get("numCopie").getAsInt() + 1;

            libro.modificaDatiLibro("", "", "", "", String.valueOf(copie));     //aumento 

            //Elimino il prestito dal database
            prestitiArray.remove(i);
            try (FileWriter writer = new FileWriter(file)) {
                database.toJson(label, writer);
            }
            System.out.println("Prestito eliminato");
        }
        else System.out.println("Prestito non risulta nel nostro database");
        
    }
    
    /**
     * @param ISBN
     * @throws java.io.IOException
     * @brief Cerca un elemento dal database dei libri
     * @pre N/A
     * @post L’utente (sia bibliotecariə che studente) visualizza il libro selezionato
     * @return posizione del libro nel database o -1 in caso di libro non presente
     */
    public static int ricercaPrestitoISBN(Long ISBN) throws IOException {
        File file = new File(NAME);
        //Leggo il database
        JsonObject label;
        try (FileReader reader = new FileReader(file)) {
            label = database.fromJson(reader, JsonObject.class);
        }
        
        //Ottengo l'array dei libri
        JsonArray loanArray = label.getAsJsonArray("prestiti");
        if (loanArray == null) return -2;
        
        for (int i = 0; i < loanArray.size(); i++) {
            JsonObject obj = loanArray.get(i).getAsJsonObject();
            if (obj.get("ISBN").getAsLong() == ISBN) {
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
        
        if(Studente.ricercaStudenteMatricola(matricola) != -1){return 0;}
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
        
        //Leggo il database
        JsonObject label;
        try (FileReader reader = new FileReader(file)) {
            label = database.fromJson(reader, JsonObject.class);
        }
        
        //Ottengo l'array dei libri
        JsonArray bookArray = label.getAsJsonArray("libri");
        if (bookArray == null) bookArray = new JsonArray();
        
        int i = Libro.ricercaLibroISBN(libro.getIsbn());
        
        if ( i != -1) {                   
            return 0;
        }
        
        System.out.println("Libro non trovato!\n"); //Diventerà una label nell'interfaccia grafica

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
        
        //Leggo il database
        JsonObject label;
        try (FileReader reader = new FileReader(file)) {
            label = database.fromJson(reader, JsonObject.class);
        }
        
        //Ottengo l'array dei prestiti
        JsonArray prestitiArray = label.getAsJsonArray("prestiti");
        if (prestitiArray == null) {
            System.out.println("ERROR, database not found");  //da implementare come interfaccia grafica
            return -1;
        }
        
        else{           
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
}




