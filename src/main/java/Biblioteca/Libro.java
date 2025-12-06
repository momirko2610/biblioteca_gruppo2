/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biblioteca;

/**
 *
 * @author ssabr
 */
public class Libro {
    private String titolo;
    private String autore;
    /**
     * @brief 
     */
    public Libro(String titolo, String autore) {
        this.titolo = titolo;
        this.autore = autore;
    }
    
    /**
     * @brief 
     */
    public void modificaDatiLibro(){};
    
    /**
     * @brief 
     */
    public void cancellazioneDatiLibro() {};
    
    /**
     * @brief 
     */
    public void visualizzazioneListaLibri() {};
    
    /**
     * @brief 
     */
    public void ricercaLibro(){};
}
