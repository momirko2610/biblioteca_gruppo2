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
public class Studente {
    private String nome;
    private String cognome;
    private int matricola;
    private String e_mail;
    
    private static final String NAME = "database.json";
    
    private static final Gson database = new GsonBuilder().setPrettyPrinting().create();
    /**
     * @brief 
     */
    public Studente(String nome, String cognome, int matricola, String e_mail) {
        this.nome = nome;
        this.cognome = cognome;
        this.matricola = matricola;
        this.e_mail = e_mail;
    }
    
    /**
     * @brief 
     */
    public void inserisciDatiStudente() {};
    
    /**
     * @brief 
     */
    public void modificaDatiStudente() {};
    
    /**
     * @brief 
     */
    public void cancellazioneDatiStudente () {};
    
    /**
     * @brief 
     */
    public void visualizzazioneElencoStudenti() {};
    
    /**
     * @brief 
     */
    public void stampaStudente(){};
    
    /**
     * @brief 
     */
    public void prenotazioneLibro (){};
    
    /**
     * @brief 
     */
    private int ricercaStudente() {
        return 0;
    };
}
