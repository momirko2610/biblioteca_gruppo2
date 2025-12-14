package Biblioteca.Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @brief Classe che gestisce il database degli studenti
 * @author Sabrina Soriano
 * @author Mirko Montella
 * @author Ciro Senese
 * @author Achille Romano
 */
public class Studente {
    private String nome; /*!<Nome dello studente*/
    private String cognome; /*!<Cognome dello studente*/
    private String matricola; /*!<matricola dello studente*/
    private String e_mail; /*!<e-mail dello studente*/
    /**< Nome del database che verrà creato */
    private static final String NAME = "database.json";
    /**< Oggetto della funzione per la creazione dei file JSON */
    private static final Gson database = new GsonBuilder().setPrettyPrinting().create();

    /**
     * @brief Costruttore di base
     * @param nome nome dello studente
     * @param cognome nome dello studente
     * @param matricola matricola nome dello studente
     * @param e_mail e-mail nome dello studente
     */
    public Studente(String nome, String cognome, String matricola, String e_mail) {
        this.nome = nome;
        this.cognome = cognome;
        this.matricola = matricola;
        this.e_mail = e_mail;
    }
    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public String getMatricola() { return matricola; }
    public String getE_mail() { return e_mail; }
    
    @Override
    public String toString() {
        return String.format(
            "Nome: %s| Cognome: %s | Matricola: %s | e_mail: %s",
            nome, cognome, matricola, e_mail
        );
    }

    /**
     * @throws java.io.IOException
     * @brief Aggiorna il database degli studenti creando un nuovo elemento
     * @pre Il Bibliotecariə deve essere autenticatə
     * @post Il database contenente l’elenco degli studenti è aggiornato.
     */
    public void inserisciDatiStudente() throws IOException {
        
        File file = new File(NAME);
        
        //Leggo il database
        JsonObject label;
        try (FileReader reader = new FileReader(file)) {
            label = database.fromJson(reader, JsonObject.class);
        }
        
        //Ottengo l'array degli studenti
        JsonArray studentArray = label.getAsJsonArray("studenti");
        if (studentArray == null) studentArray = new JsonArray();
        
        int i = Studente.ricercaStudenteMatricola(this.matricola);
        
        if ( i != -1) {
            System.out.println("Matricola già inserita nel database");
            return;
        }
        
        else {
            //Aggiungo nuovo studente
            JsonObject newStudent = new JsonObject();
            newStudent.addProperty("nome", this.nome);
            newStudent.addProperty("cognome", this.cognome);
            newStudent.addProperty("matricola", this.matricola);    
            newStudent.addProperty("e_mail", this.e_mail);
            studentArray.add(newStudent);
        
            Database.ordinaDatabaseStudente(studentArray, file, label);
        }
    };

    /**
     * @param newNome
     * @param newCognome
     * @param newMatricola
     * @param newE_mail
     * @throws java.io.IOException
     * @brief Aggiorna il database degli studenti modificando un elemento
     * @pre Il Bibliotecariə deve essere autenticatə
     * @post Il database contenente l’elenco degli studenti è aggiornato.
     */
    public void modificaDatiStudente(String newNome, String newCognome, String newMatricola, String newE_mail) throws IOException{
        File file = new File(NAME);
        
        //Leggo il database
        JsonObject label;
        try (FileReader reader = new FileReader(file)) {
            label = database.fromJson(reader, JsonObject.class);
        }
        
        //Ottengo l'array dei libri
        JsonArray studentArray = label.getAsJsonArray("studenti");
        if (studentArray == null) {
            System.out.println("ERROR, database not found");
            return;
        }
        
        int i = Studente.ricercaStudenteMatricola(this.matricola);
        
        if ( i != -1) {
            JsonObject obj = studentArray.get(i).getAsJsonObject();
            if (!(newNome.isEmpty())) obj.addProperty("nome", newNome);
            if (!(newCognome.isEmpty())) {
                obj.addProperty("cognome", newCognome);
                Database.ordinaDatabaseStudente(studentArray, file, label);
            }
            if (!(newMatricola.isEmpty())) {
                obj.addProperty("matricola", newMatricola);
                this.matricola = newMatricola;
            }
            if (!(newE_mail.isEmpty())) obj.addProperty("e_mail", newE_mail);
            
            try (FileWriter writer = new FileWriter(file)) {
                database.toJson(label, writer);
            }
        System.out.println("Studente modificato:");
        System.out.println(obj.toString());
        }
        else System.out.println("Studente non risulta nel nostro database");
    };

    /**
     * @throws java.io.IOException
     * @brief Aggiorna il database degli studenti eliminando un elemento
     * @pre Il Bibliotecariə deve essere autenticatə
     * @post Il database contenente l’elenco degli studenti è aggiornato.
     */
    public void cancellazioneDatiStudente () throws IOException {
        File file = new File(NAME);
        
        //Leggo il database
        JsonObject label;
        try (FileReader reader = new FileReader(file)) {
            label = database.fromJson(reader, JsonObject.class);
        }
        
        //Ottengo l'array degli student
        JsonArray studentArray = label.getAsJsonArray("studenti");
        if (studentArray == null) {
            System.out.println("ERROR, database not found");
            return;
        }
        
        int i = Studente.ricercaStudenteMatricola(this.matricola);
        
        if ( i != -1) {
            studentArray.remove(i);
            try (FileWriter writer = new FileWriter(file)) {
                database.toJson(label, writer);
            }
            System.out.println("Studente eliminato dal database");
        }
        else System.out.println("Studente non risulta nel nostro database");
    };

    /**
     * @throws java.io.IOException
     * @brief Mostra gli elementi presenti nel database degli studentui
     * @pre Il Bibliotecariə deve essere autenticatə
     * @post Bibliotecariə visualizza la lista completa degli studenti in ordine alfabetico
     */
    public static void visualizzazioneElencoStudenti() throws IOException {
        List<Studente> studenti = Database.leggiDatabaseStudenti();
        
        studenti.forEach(l -> {
            System.out.println(l);
        });
    };

    /**
     * @brief Permette allo studente di prenotare un libro da ritirare in biblioteca
     * @pre N/A
     * @post Prenota un libro da ritirare in biblioteca
     */
    public void prenotazioneLibro (){};

    /**
     * @param matricola
     * @throws java.io.IOException
     * @brief Cerca un elemento dal database degli studenti
     * @pre N/A
     * @post Bibliotecariə visualizza a schermo i dati dello studente selezionato
     * @return posizione del libro nel database o -1 in caso di libro non presente
     */
    public static int ricercaStudenteMatricola(String matricola) throws IOException{
        File file = new File(NAME);
        //Leggo il database
        JsonObject label;
        try (FileReader reader = new FileReader(file)) {
            label = database.fromJson(reader, JsonObject.class);
        }
        
        //Ottengo l'array degli studenti
        JsonArray studentArray = label.getAsJsonArray("studenti");
        if (studentArray == null) return -1;
        
        for (int i = 0; i < studentArray.size(); i++) {
            JsonObject obj = studentArray.get(i).getAsJsonObject();
            if (obj.get("matricola").getAsString().equalsIgnoreCase(matricola)) {
                return i;
            }
        }
        
        return -1;
    };
    
    /**
     * @param cognome
     * @return 
     * @throws java.io.IOException 
     * @brief Mostra l'elemento cercato (per cognome) dal database degli studenti
     * @pre lo studente è presente nel database
     * @post il bibliotecariə visualizza le informazioni dello studente cercato
     */
    public static List<Studente> ricercaStudenteCognome(String cognome) throws IOException{
        File file = new File(NAME);
        
        //Leggo il database
        JsonObject label;
        try (FileReader reader = new FileReader(file)) {
            label = database.fromJson(reader, JsonObject.class);
        }
        
        //Ottengo l'array degli studenti
        JsonArray studentArray = label.getAsJsonArray("studenti");
        if (studentArray == null) {
            System.out.println("ERROR, database not found");
            return null;
        }
        
        //Creo una lista di tipo List<Studente>
        List<Studente> studenti = new ArrayList<>();
        
        for (int i = 0; i < studentArray.size(); i++) {
            JsonObject obj = studentArray.get(i).getAsJsonObject();
            if (obj.get("cognome").getAsString().compareTo(cognome) > 0) break;
            else if (obj.get("cognome").getAsString().equalsIgnoreCase(cognome)) {
                Studente studente = database.fromJson(obj, Studente.class);
                studenti.add(studente);
                
            }
           
        }
        
        return(studenti);
        
    };
    
    // DA ELIMINARE
    /**
     * @throws java.io.IOException
     * @brief Mostra l'elemento cercato (per cognome) dal database degli studenti
     * @pre lo studente è presente nel database
     * @post il bibliotecariə visualizza le informazioni dello studente cercato
     */
    /*
    public static void cercaStudenteCognome(String cognome) throws IOException{
        List<Studente> studenti = Studente.ricercaStudenteCognome(cognome);
        
        for (Studente s : studenti) {
            System.out.println(s);
        }

    };
    */
    
    //DA ELIMINARE
    /**
     * @throws java.io.IOException
     * @brief Mostra l'elemento cercato (per matricola) dal database degli studenti
     * @pre lo studente è presente nel database
     * @post il bibliotecariə visualizza le informazioni dello studente cercato
     */
    /*
    public static void cercaStudenteMatricola(String matricola) throws IOException{
        File file = new File(NAME);
        
        //Leggo il database
        JsonObject label;
        try (FileReader reader = new FileReader(file)) {
            label = database.fromJson(reader, JsonObject.class);
        }
        
        //Ottengo l'array degli studenti
        JsonArray studentArray = label.getAsJsonArray("studenti");
        if (studentArray == null) {
            System.out.println("ERROR, database not found");
            return;
        }
        
        int i = ricercaStudenteMatricola(matricola);
        
        if ( i != -1) {
            JsonObject obj = studentArray.get(i).getAsJsonObject();
            System.out.println(obj.toString());
        }
        else System.out.println("Studente non risulta nel nostro database");
    };
    */
}


