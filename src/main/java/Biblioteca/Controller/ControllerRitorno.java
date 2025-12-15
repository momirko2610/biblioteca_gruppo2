/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biblioteca.Controller;

import Biblioteca.Model.Prestito;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author achil
 */
public class ControllerRitorno {
    @FXML
    private Text messaggio;

    private String matricolaCorrente;
    private Long isbnCorrente;


    public void setDatiRestituzione(String matricola, Long isbn) {
        this.matricolaCorrente = matricola;
        this.isbnCorrente = isbn;

        String dataOdierna = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        String txtmessaggio = String.format(
            "Lo studente con la matricola: %s\nHa restituito il libro: %s\nIn data: %s", 
            matricola, isbn, dataOdierna
        );

        messaggio.setText(txtmessaggio);
    }

    @FXML
    private void conferma() {
        try {
            Prestito.registrazioneRestituzione(matricolaCorrente, isbnCorrente);
            
            System.out.println("Restituzione completata.");
            chiudi();
            
        } catch (IOException e) {
            System.err.println("Errore durante la restituzione: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void chiudi() {
        Stage stage = (Stage) messaggio.getScene().getWindow();
        stage.close();
    }
}