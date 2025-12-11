package Biblioteca;
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

/**
 * @brief Classe che gestisce il login dei bibliotecari
 * @author Mirko Montella
 * @author Ciro Senese
 * @author Achille Romano
 * @author Sabrina Soriano
 */
public class Bibliotecario {
    private final String e_mail; /*!<Email dell'account letta da una textbox che verrà confrontata con i dati presenti nel database dal metodo loginBibliotecario*/
    private final String password; /*!<password dell'account letta da una textbox che verrà confrontata con i dati presenti nel database dal metodo loginBibliotecario*/

    private static final String NAME = "database.json"; /*!<Nome del database da cui verranno confrontati i dati*/

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
    /**
     * @return 
     * @throws java.io.FileNotFoundException
     * @brief Funzione che verifica gli attrubuti dell'oggetto Bibliotecario con i dati presenti nel database
     * @brief Login da parte dei bibliotecari per accedere alle funzionalità ristrette.
     * @post Il bibliotecariə ha accesso a tutte le funzionalità
     */
    public int loginBibliotecario() throws FileNotFoundException, IOException {
        File file = new File(NAME);
        //Leggo il database
        JsonObject label;
        try (FileReader reader = new FileReader(file)) {
            label = database.fromJson(reader, JsonObject.class);
        }
        
        //Ottengo l'array dei libri
        JsonArray librarianArray = label.getAsJsonArray("bibliotecari");
        if (librarianArray == null) {
            System.out.println("ERROR, database not found");
            return -1;
        }
        
        for (int i = 0; i < librarianArray.size(); i++) {
            JsonObject obj = librarianArray.get(i).getAsJsonObject();
            if (obj.get("e_mail").getAsString().equalsIgnoreCase(this.e_mail) && obj.get("password").getAsString().equalsIgnoreCase(this.password) ) {
                System.out.println("Credenziali valide");
                return 1;
            }
            else {
                System.out.println("Credenziali non valide, riprova");
                return 0;
            }
        }
        return -1;
    };
    
    /*
    public static void inserisciDatiBibliotecario() throws IOException {
        
        File file = new File(NAME);
        
        //Leggo il database
        JsonObject label;
        try (FileReader reader = new FileReader(file)) {
            label = database.fromJson(reader, JsonObject.class);
        }
        
        //Ottengo l'array degli studenti
        JsonArray librarianArray = label.getAsJsonArray("bibliotecari");
        if (librarianArray == null) librarianArray = new JsonArray();
        
        //Aggiungo nuovo studente
        JsonObject newLibrarian = new JsonObject();
        newLibrarian.addProperty("e_mail", "bibliotecario@unisa.it");
        newLibrarian.addProperty("password", "123456789");
        librarianArray.add(newLibrarian);
       
        //Salvo
        try (FileWriter writer = new FileWriter(file)) {
            database.toJson(label, writer);
        }
        
    };
    */

}
