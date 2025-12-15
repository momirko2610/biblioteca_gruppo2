package Biblioteca.Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @brief Classe che gestisce il login dei bibliotecari
 * @author Sabrina Soriano
 * @author Mirko Montella
 * @author Ciro Senese
 * @author Achille Romano
 */
public class Bibliotecario {
    private final String e_mail; /*!<Email dell'account letta da una textbox che verrà confrontata con i dati presenti nel database dal metodo loginBibliotecario*/
    private final String password; /*!<password dell'account letta da una textbox che verrà confrontata con i dati presenti nel database dal metodo loginBibliotecario*/

    private static final String NAME = "database.json"; /*!<Nome del database da cui verranno confrontati i dati*/
    private static final File FILE = new File(NAME); //File del database
    private static final Gson database = new GsonBuilder().setPrettyPrinting().create();
    /**
     * @brief Costruttore di base
     * @param e_mail L'email del bibliotecariə
     * @param password La password del bibliotecariə
     */
    public Bibliotecario(String e_mail, String password) {
        this.e_mail = e_mail;
        this.password = password;
    }
    public String getEmail() { return this.e_mail;}
    /**
     * @return 
     * @throws java.io.FileNotFoundException
     * @brief Funzione che verifica gli attrubuti dell'oggetto Bibliotecario con i dati presenti nel database
     * @brief Login da parte dei bibliotecari per accedere alle funzionalità ristrette.
     * @post Il bibliotecariə ha accesso a tutte le funzionalità
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
     * @param password
     * @return 
     * @throws java.io.FileNotFoundException
     * @brief Il bibliotecario più cambiare la password per effettuare il login
     * @pre N/A
     * @post Nuova password
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
    
    public String resetPassword() throws FileNotFoundException, IOException{
        JsonObject label = Database.leggiDatabase(FILE);
        
        JsonArray librarianArray = Bibliotecario.getArrayBibliotecari(label);
        
        if (librarianArray == null) System.out.println("ERROR, database not found");
        
        for (int i = 0; i < librarianArray.size(); i++) {
            JsonObject obj = librarianArray.get(i).getAsJsonObject();
            if (obj.get("e_mail").getAsString().equalsIgnoreCase(this.e_mail)) {
                Random random = new Random();
                StringBuilder builder = new StringBuilder(8);

                for (int j = 0; i < 8; i++) {
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
     * @throws java.io.IOException
     * @brief salva in un JsonArray i dati dei bibliotecari contenuti nel database
     * @pre deve esistere un JsonObject contente i dati dei bibliotecari salvati nel database
     * @post Ottengo l'array con i dati dei bibliotecari
     */
    
    private static JsonArray getArrayBibliotecari(JsonObject label) {
        //Ottengo l'array dei bibliotecari
        JsonArray bookArray = label.getAsJsonArray("bibliotecari");
        if (bookArray == null) {
            System.out.println("ERROR, database not found");
            return null;
        }
        return bookArray;
    }
}
