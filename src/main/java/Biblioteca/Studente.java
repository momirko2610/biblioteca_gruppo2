package Biblioteca;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @brief Classe che gestisce il database degli studenti
 * @author Mirko Montella
 * @author Ciro Senese
 * @author Achille Romano
 * @author Sabrina Soriano
 */
public class Studente {
    private String nome; /*!<Nome dello studente*/
    private String cognome; /*!<Cognome dello studente*/
    private int matricola; /*!<matricola dello studente*/
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
    public Studente(String nome, String cognome, int matricola, String e_mail) {
        this.nome = nome;
        this.cognome = cognome;
        this.matricola = matricola;
        this.e_mail = e_mail;
    }
    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public int getmatricola() { return matricola; }
    public String gete_mail() { return e_mail; }

    /**
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
        
        //Aggiungo nuovo studente
        JsonObject newBook = new JsonObject();
        newBook.addProperty("nome", this.nome);
        newBook.addProperty("cognome", this.cognome);
        newBook.addProperty("matricola", this.matricola);    
        newBook.addProperty("e_mail", this.e_mail);
        studentArray.add(newBook);
        
        //Inserisco tutti gli studenti in una lista di studenti
        List<JsonObject> studentList = new ArrayList<>();
        for (JsonElement element : studentArray) {
            studentList.add(element.getAsJsonObject());
        }
        
        //Ordino la lista in base al titolo
        studentList.sort((a, b) -> a.get("titolo").getAsString().compareToIgnoreCase(b.get("titolo").getAsString()));

        //Inserisco gli studenti in un Array ordinato
        JsonArray sortedArray = new JsonArray();
        for (JsonObject book : studentList) {
            sortedArray.add(book);
        }
          
        //Aggiorno l'Array
        label.add("studenti", sortedArray);

        //Salvo
        try (FileWriter writer = new FileWriter(file)) {
            database.toJson(label, writer);
        }
        
    };

    /**
     * @brief Aggiorna il database degli studenti modificando un elemento
     * @pre Il Bibliotecariə deve essere autenticatə
     * @post Il database contenente l’elenco degli studenti è aggiornato.
     */
    public void modificaDatiStudente(int newMatricola) throws IOException{
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
        
        int i = ricercaStudente();
        
        if ( i != -1) {
            JsonObject obj = studentArray.get(i).getAsJsonObject();
            obj.addProperty("matricola", newMatricola);
            
            try (FileWriter writer = new FileWriter(file)) {
                database.toJson(label, writer);
            }
            System.out.println("Studente modificato");
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
        
        int i = ricercaStudente();
        
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
     * @brief Mostra gli elementi presenti nel database degli studentui
     * @pre Il Bibliotecariə deve essere autenticatə
     * @post Bibliotecariə visualizza la lista completa degli studenti in ordine alfabetico
     */
    public void visualizzazioneElencoStudenti() throws IOException {
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
        }
        
        for (int i = 0; i < studentArray.size(); i++) {
            JsonObject obj = studentArray.get(i).getAsJsonObject();
            System.out.println(obj.toString());
        }
    };

    /**
     * @brief Mostra l'elemento cercato dal database degli studenti
     * @pre lo studente è presente nel database
     * @post il bibliotecariə visualizza le informazioni dello studente cercato
     */
    public void stampaStudente() throws IOException{
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
        
        int i = ricercaStudente();
        
        if ( i != -1) {
            JsonObject obj = studentArray.get(i).getAsJsonObject();
            System.out.println(obj.toString());
        }
        else System.out.println("Studente non risulta nel nostro database");
    };

    /**
     * @brief Permette allo studente di prenotare un libro da ritirare in biblioteca
     * @pre N/A
     * @post Prenota un libro da ritirare in biblioteca
     */
    public void prenotazioneLibro (){};

    /**
     * @brief Cerca un elemento dal database degli studenti
     * @pre N/A
     * @post Bibliotecariə visualizza a schermo i dati dello studente selezionato
     * @return posizione del libro nel database o -1 in caso di libro non presente
     */
    private int ricercaStudente() throws IOException{
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
            if (obj.get("matricola").getAsBigInteger().equals(this.matricola)) {
                return i;
            }
        }
        
        return -1;
    };
}
