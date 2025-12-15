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
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

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
    private static final File FILE = new File(NAME); //File del database
    /**< Oggetto della funzione per la creazione dei file JSON */
    private static final Gson database = new GsonBuilder().setPrettyPrinting().create();
    
    private transient HBox azioni;
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
        
        creaBottoni();
    }
    
    private void creaBottoni(){
        // creo i bottoni che popoleranno la colonna azioni della tabella dei libri
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
    
    public HBox getAzioni() {
        // azioni è sempre nulla quando carico dal Database JSON
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
     * @throws java.io.IOException
     * @brief Aggiorna il database degli studenti creando un nuovo elemento
     * @pre Il Bibliotecariə deve essere autenticatə
     * @post Il database contenente l’elenco degli studenti è aggiornato.
     */
    public void inserisciDatiStudente() throws IOException {
        JsonObject label = Database.leggiDatabase(FILE);
        
        JsonArray studentArray = Studente.getArrayStudenti(label);
        
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
        
            Database.ordinaDatabaseStudente(studentArray, FILE, label);
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
        JsonObject label = Database.leggiDatabase(FILE);
        
        JsonArray studentArray = Studente.getArrayStudenti(label);
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
                Database.ordinaDatabaseStudente(studentArray, FILE, label);
            }
            if (!(newMatricola.isEmpty())) {
                obj.addProperty("matricola", newMatricola);
                this.matricola = newMatricola;
            }
            if (!(newE_mail.isEmpty())) obj.addProperty("e_mail", newE_mail);
            
            Database.salva(FILE, label);
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
        JsonObject label = Database.leggiDatabase(FILE);
        
        JsonArray studentArray = Studente.getArrayStudenti(label);
        
        if (studentArray == null) {
            System.out.println("ERROR, database not found");
            return;
        }
        
        int i = Studente.ricercaStudenteMatricola(this.matricola);
        
        if ( i != -1) {
            studentArray.remove(i);
            Database.salva(FILE, label);
            System.out.println("Studente eliminato dal database");
        }
        else System.out.println("Studente non risulta nel nostro database");
    };

    /**
     * @param matricola
     * @throws java.io.IOException
     * @brief Cerca un elemento dal database degli studenti
     * @pre N/A
     * @post Bibliotecariə visualizza a schermo i dati dello studente selezionato
     * @return posizione del libro nel database o -1 in caso di libro non presente
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
        JsonObject label = Database.leggiDatabase(FILE);
        
        JsonArray studentArray = Studente.getArrayStudenti(label);
        
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
    
     /**
     * @throws java.io.IOException
     * @brief salva in un JsonArray gli studenti contenuti nel database
     * @pre deve esistere un JsonObject contente gli studenti salvati nel database
     * @post Ottengo l'array degli studenti
     */
    
    private static JsonArray getArrayStudenti(JsonObject label) {
        //Ottengo l'array dei studenti
        JsonArray studentArray = label.getAsJsonArray("studenti");
        if (studentArray == null) {
            System.out.println("ERROR, database not found");
            return null;
        }
        return studentArray;
    }
    
}


