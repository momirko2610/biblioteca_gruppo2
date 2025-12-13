/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biblioteca.Controller;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Button;
/**
 *
 * @author achil
 */
public class ControllerDelete {

    private Object oggettoDaEliminare; // Generico: può essere Libro, Studente o Prestito

    public void setOggettoDaEliminare(Object obj) {
        this.oggettoDaEliminare = obj;
    }

    @FXML
    private void conferma() {

        if (oggettoDaEliminare instanceof Libro) {
            Libro l = (Libro) oggettoDaEliminare;

        } 
        else if (oggettoDaEliminare instanceof Studente) {
             Studente l = (Studente) oggettoDaEliminare;
        }

        chiudi();
    }

    @FXML
    private void chiudi() {
    }
}
