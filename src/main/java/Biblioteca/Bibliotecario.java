/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biblioteca;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @brief Classe che gestisce il login dei bibliotecari
 * @author Mirko Montella
 * @author Ciro Senese
 * @author Achille Romano
 * @author Sabrina Soriano
 */
public class Bibliotecario {
    private String e_mail; /*!<Email dell'account letta da una textbox che verrà confrontata con i dati presenti nel database dal metodo loginBibliotecario*/
    private String password; /*!<password dell'account letta da una textbox che verrà confrontata con i dati presenti nel database dal metodo loginBibliotecario*/
    
    private static final String NAME = "database.json"; /*!<Nome del database da cui verranno confrontati i dati*/
    
    private static final Gson database = new GsonBuilder().setPrettyPrinting().create();
    /**
     * @brief Costruttore di base
     * @param e_mail L'email del bibliotecario
     * @param password La password del bibliotecario
     */
    public Bibliotecario(String e_mail, String password) {
        this.e_mail = e_mail;
        this.password = password;
    }
    /**
     * @brief Funzione che verifica gli attrubuti dell'oggetto Bibliotecario con i dati presenti nel database
     */
    public void loginBibliotecario() {};
        
  
}
