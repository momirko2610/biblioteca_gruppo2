package softeng.progetto.gruppo2.Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * @file Studente.java
 * @brief Classe del modello per la gestione delle anagrafiche studenti.
 * @author Sabrina Soriano
 * @author Mirko Montella
 * @author Ciro Senese
 * @author Achille Romano
 */
public class Studente {
    /** @brief Nome dello studente. */
    private String nome; 
    /** @brief Cognome dello studente. */
    private String cognome; 
    /** @brief Matricola univoca dello studente. */
    private String matricola; 
    /** @brief Indirizzo e-mail istituzionale dello studente. */
    private String e_mail; 
    
    /** @brief Nome del file database JSON. */
    private static final String NAME = "database.json";
    /** @brief Oggetto File associato al database fisico. */
    private static final File FILE = new File(NAME); 
    /** @brief Istanza GSON per la formattazione e gestione JSON. */
    private static final Gson database = new GsonBuilder().setPrettyPrinting().create();
    
    /** @brief Contenitore grafico per i bottoni di azione (Modifica, Elimina, Info) nella UI. */
    private transient HBox azioni;

    /**
     * @brief Costruttore della classe Studente.
     * @param nome Nome dello studente.
     * @param cognome Cognome dello studente.
     * @param matricola Matricola univoca.
     * @param e_mail E-mail istituzionale.
     */
    public Studente(String nome, String cognome, String matricola, String e_mail) {
        this.nome = nome;
        this.cognome = cognome;
        this.matricola = matricola;
        this.e_mail = e_mail;
        
        creaBottoni();
    }
    
    /**
     * @brief Inizializza i pulsanti grafici per la colonna Azioni della tabella studenti.
     */
    private void creaBottoni(){
        Button Modifica = new Button();
        Button Elimina = new Button();
        Button Info = new Button();
        
        ImageView viewModifica = new ImageView(new Image(getClass().getResourceAsStream("/Biblioteca/icons/pencil-fiiled.png")));
        ImageView viewElimina = new ImageView(new Image(getClass().getResourceAsStream("/Biblioteca/icons/trash-filled.png")));
        ImageView viewInfo = new ImageView(new Image(getClass().getResourceAsStream("/Biblioteca/icons/info.png")));
        
        viewModifica.setFitHeight(15);
        viewModifica.setFitWidth(15);
        viewElimina.setFitHeight(15);
        viewElimina.setFitWidth(15);
        viewInfo.setFitHeight(15);
        viewInfo.setFitWidth(15);
        
        Modifica.setGraphic(viewModifica);
        Elimina.setGraphic(viewElimina);
        Info.setGraphic(viewInfo);
        
        Modifica.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        Elimina.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        Info.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        this.azioni = new HBox(10, Modifica, Elimina, Info);
        this.azioni.setAlignment(Pos.CENTER);
    }
    
    /** * @brief Restituisce l'HBox delle azioni. 
     * @return Il contenitore HBox con i pulsanti attivi.
     */
    public HBox getAzioni() {
        if (azioni == null) {
            creaBottoni();
        }
        return azioni;
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
     * @brief Registra un nuovo studente nel database.
     * @return 0 in caso di successo, -1 se la matricola è già presente.
     * @throws java.io.IOException Se fallisce la scrittura su file.
     * @pre Il Bibliotecario deve essere autenticato.
     * @post Il database viene aggiornato e ordinato per cognome.
     */
    public int inserisciDatiStudente() throws IOException {
        JsonObject label = Database.leggiDatabase(FILE);
        JsonArray studentArray = Studente.getArrayStudenti(label);
        
        if (studentArray == null) studentArray = new JsonArray();
        
        int i = Studente.ricercaStudenteMatricola(this.matricola);
        
        if (i >= 0) {
            System.out.println("Matricola già inserita nel database");
            return -1;
        }
        else {
            JsonObject newStudent = new JsonObject();
            newStudent.addProperty("nome", this.nome);
            newStudent.addProperty("cognome", this.cognome);
            newStudent.addProperty("matricola", this.matricola);    
            newStudent.addProperty("e_mail", this.e_mail);
            studentArray.add(newStudent);
        
            Database.ordinaDatabaseStudente(studentArray, FILE, label);
        }
        return 0;
    }

    /**
     * @brief Modifica i dati di un profilo studente esistente.
     * @return 0 successo, -2 studente non trovato, -3 database mancante.
     * @throws java.io.IOException Se fallisce l'aggiornamento del file.
     * @pre Il Bibliotecario deve essere autenticato.
     * @post I dati vengono persistiti e il database riordinato se necessario.
     */
    public int modificaDatiStudente(String newNome, String newCognome, String newMatricola, String newE_mail) throws IOException{
        JsonObject label = Database.leggiDatabase(FILE);
        JsonArray studentArray = Studente.getArrayStudenti(label);
        if (studentArray == null) return -3;
        
        int i = Studente.ricercaStudenteMatricola(this.matricola);
        
        if (i >= 0) {
            JsonObject obj = studentArray.get(i).getAsJsonObject();
            if (!(newNome.isEmpty())) obj.addProperty("nome", newNome);
            if (!(newCognome.isEmpty())) {
                obj.addProperty("cognome", newCognome);
                Database.ordinaDatabaseStudente(studentArray, FILE, label);
            }
            if (!(newMatricola.isEmpty())) {
                obj.addProperty("matricola", newMatricola);
                this.matricola = newMatricola;
            }
            if (!(newE_mail.isEmpty())) obj.addProperty("e_mail", newE_mail);
            
            Database.salva(FILE, label);
            return 0;
        }
        return -2;
    }

    /**
     * @brief Elimina uno studente dal database.
     * @return 0 successo, -2 studente non trovato, -3 database mancante.
     * @throws java.io.IOException Se fallisce la rimozione fisica dal file.
     * @pre Il Bibliotecario deve essere autenticato.
     * @post Il record dello studente viene rimosso definitivamente.
     */
    public int cancellazioneDatiStudente () throws IOException {
        JsonObject label = Database.leggiDatabase(FILE);
        JsonArray studentArray = Studente.getArrayStudenti(label);
        
        if (studentArray == null) return -3;
        
        int i = Studente.ricercaStudenteMatricola(this.matricola);
        
        if (i >= 0) {
            studentArray.remove(i);
            Database.salva(FILE, label);
            return 0;
        }
        return -2;
    }

    /**
     * @brief Cerca la posizione di uno studente nell'array JSON tramite matricola.
     * @param matricola La matricola da ricercare.
     * @return L'indice dell'elemento nel database o -1 se non presente.
     * @throws java.io.IOException Se fallisce la lettura del database.
     */
    public static int ricercaStudenteMatricola(String matricola) throws IOException{
        JsonObject label = Database.leggiDatabase(FILE);
        JsonArray studentArray = Studente.getArrayStudenti(label);
        
        if (studentArray == null) return -2;
        
        for (int i = 0; i < studentArray.size(); i++) {
            JsonObject obj = studentArray.get(i).getAsJsonObject();
            if (obj.get("matricola").getAsString().equalsIgnoreCase(matricola)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @brief Filtra l'elenco degli studenti per cognome.
     * @param cognome Il cognome da ricercare.
     * @return Lista di oggetti Studente che corrispondono al criterio.
     * @throws java.io.IOException Se fallisce la lettura del database.
     */
    public static List<Studente> ricercaStudenteCognome(String cognome) throws IOException{
        JsonObject label = Database.leggiDatabase(FILE);
        JsonArray studentArray = Studente.getArrayStudenti(label);
        
        if (studentArray == null) return null;
        
        List<Studente> studenti = new ArrayList<>();
        for (int i = 0; i < studentArray.size(); i++) {
            JsonObject obj = studentArray.get(i).getAsJsonObject();
            if (obj.get("cognome").getAsString().compareTo(cognome) > 0) break;
            else if (obj.get("cognome").getAsString().equalsIgnoreCase(cognome)) {
                Studente studente = database.fromJson(obj, Studente.class);
                studenti.add(studente);
            }
        }
        return studenti;
    }

    /**
     * @brief Estrae l'array "studenti" dal JsonObject del database.
     * @param label Il JsonObject principale caricato in memoria.
     * @return L'array JSON degli studenti o null se la chiave non esiste.
     */
    private static JsonArray getArrayStudenti(JsonObject label) {
        JsonArray studentArray = label.getAsJsonArray("studenti");
        if (studentArray == null) {
            return null;
        }
        return studentArray;
    }
}