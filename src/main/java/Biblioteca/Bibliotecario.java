/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biblioteca;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author ssabr
 */
public class Bibliotecario {
    private String e_mail;
    private String password;
    
    private static final String NAME = "database.json";
    
    private static final Gson database = new GsonBuilder().setPrettyPrinting().create();
    /**
     * @brief 
     */
    public Bibliotecario(String e_mail, String password) {
        this.e_mail = e_mail;
        this.password = password;
    }
    /**
     * @brief 
     */
    public void loginBibliotecario() {};
        
  
}
