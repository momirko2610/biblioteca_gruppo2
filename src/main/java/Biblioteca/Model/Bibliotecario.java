package Biblioteca.Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

/**
 * @file Bibliotecario.java
 * @brief Classe del modello che gestisce l'autenticazione e i dati dei bibliotecari.
 * * @author Sabrina Soriano
 * @author Mirko Montella
 * @author Ciro Senese
 * @author Achille Romano
 */
public class Bibliotecario {
    /** @brief Email dell'account letta da una textbox per il confronto con il database. [cite: 97] */
    private final String e_mail; 
    /** @brief Password dell'account letta da una textbox per il confronto con il database. [cite: 97] */
    private final String password; 

    /** @brief Nome del file di persistenza dell'intero archivio. [cite: 17, 120] */
    private static final String NAME = "database.json"; 
    /** @brief Oggetto File associato al database. */
    private static final File FILE = new File(NAME); 
    /** @brief Istanza Gson configurata per la formattazione leggibile dei dati JSON. */
    private static final Gson database = new GsonBuilder().setPrettyPrinting().create();

    /**
     * @brief Costruttore dell'oggetto Bibliotecario.
     * @param e_mail L'indirizzo e-mail istituzionale fornito in input. 
     * @param password La password inserita dall'utente. [cite: 97]
     */
    public Bibliotecario(String e_mail, String password) {
        this.e_mail = e_mail;
        this.password = password;
    }

    /**
     * @brief Restituisce l'email dell'istanza corrente.
     * @return Stringa contenente l'email.
     */
    public String getEmail() { return this.e_mail;}

    /**
     * @brief Verifica le credenziali dell'oggetto Bibliotecario confrontandole con il database.
     * @return 1 se le credenziali sono valide, -1 se errate, -2 se il database non è trovato.
     * @throws java.io.IOException In caso di errori di lettura dal file.
     * @post In caso di esito positivo (1), il bibliotecario ha accesso a tutte le funzionalità. [cite: 98]
     */
    public int loginBibliotecario() throws FileNotFoundException, IOException {
        JsonObject label = Database.leggiDatabase(FILE);
        
        JsonArray librarianArray = Bibliotecario.getArrayBibliotecari(label);
        
        if (librarianArray == null) {
            System.out.println("ERROR, database not found");
            return -2;
        }
        
        for (int i = 0; i < librarianArray.size(); i++) {
            JsonObject obj = librarianArray.get(i).getAsJsonObject();
            if (obj.get("e_mail").getAsString().equalsIgnoreCase(this.e_mail) && obj.get("password").getAsString().equalsIgnoreCase(this.password) ) {
                System.out.println("Credenziali valide");
                return 1;
            }
        }
        return -1;
    };
    
    /**
     * @brief Consente al bibliotecario di modificare la propria password.
     * @param password La nuova stringa alfanumerica da impostare come password.
     * @return 1 in caso di successo, -1 se l'utente non è trovato, -2 in caso di errore database.
     * @throws java.io.IOException In caso di errori di scrittura sul file.
     * @post La nuova password viene persistita nel database JSON. 
     */
    public int cambiaPassword(String password) throws FileNotFoundException, IOException {
        JsonObject label = Database.leggiDatabase(FILE);
        
        JsonArray librarianArray = Bibliotecario.getArrayBibliotecari(label);
        
        if (librarianArray == null) {
            System.out.println("ERROR, database not found");
            return -2;
        }
        
        for (int i = 0; i < librarianArray.size(); i++) {
            JsonObject obj = librarianArray.get(i).getAsJsonObject();
            if (obj.get("e_mail").getAsString().equalsIgnoreCase(this.e_mail)) {
                obj.addProperty("password", password);
                Database.salva(FILE, label);
                return 1;
            }
        }
        return -1;
    };
    
    /**
     * @brief Genera una nuova password casuale in caso di reset forzato dopo 3 tentativi falliti.
     * @return La nuova password generata (stringa di 8 cifre) o null se l'utente non esiste.
     * @throws java.io.IOException In caso di errori di aggiornamento del database.
     */
    public String resetPassword() throws FileNotFoundException, IOException{
        JsonObject label = Database.leggiDatabase(FILE);
        
        JsonArray librarianArray = Bibliotecario.getArrayBibliotecari(label);
        
        if (librarianArray == null) System.out.println("ERROR, database not found");
        
        for (int i = 0; i < librarianArray.size(); i++) {
            JsonObject obj = librarianArray.get(i).getAsJsonObject();
            if (obj.get("e_mail").getAsString().equalsIgnoreCase(this.e_mail)) {
                Random random = new Random();
                StringBuilder builder = new StringBuilder(8);

                for (int j = 0; j < 8; j++) {
                    builder.append(random.nextInt(10));
                }
                String password = builder.toString();
                obj.addProperty("password", password);
                
                Database.salva(FILE, label);
                
                return password;
            }
        }
        return null;
    }
    
    /**
     * @brief Estrae l'array dei bibliotecari dal file JSON caricato in memoria.
     * @param label Il JsonObject principale che rappresenta l'intero database.
     * @return JsonArray contenente i dati dei bibliotecari o null se la sezione manca.
     * @pre Deve esistere un JsonObject caricato correttamente tramite Database.leggiDatabase.
     */
    private static JsonArray getArrayBibliotecari(JsonObject label) {
        //Ottengo l'array dei bibliotecari dalla sezione specifica del database
        JsonArray bookArray = label.getAsJsonArray("bibliotecari");
        if (bookArray == null) {
            System.out.println("ERROR, database not found");
            return null;
        }
        return bookArray;
    }
}