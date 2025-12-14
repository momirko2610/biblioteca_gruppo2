/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biblioteca.Controller;

import Biblioteca.Model.Libro;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author achil
 */

public class ControllerPopupLibro {
    @FXML 
    private Text label;
    @FXML 
    private TextField titolo;
    @FXML 
    private TextField autori;
    @FXML 
    private TextField isbn;
    @FXML 
    private TextField nCopie;
    @FXML 
    private TextField data;
    @FXML 
    private Button conferma;

    private Libro libroCorrente; 


    public void setLibroDaModificare(Libro libro) {
        this.libroCorrente = libro;

        if (libro != null) {
         
            label.setText("Modifica Libro");
            titolo.setText(libro.getTitolo());
            autori.setText(libro.getAutore());
            isbn.setText(Long.toString(libro.getIsbn()));
            nCopie.setText(String.valueOf(libro.getNumCopie()));
            data.setText(String.valueOf(libro.getAnnoPubblicazione()));

        } else {
            //nuovo libro
            label.setText("Nuovo Libro");
        }
    }
    
    @FXML
    public void initialize() {
        // abilita bottone conferma
      /*  conferma.disableProperty().bind(
            titolo.textProperty().isEmpty()
            .or(autori.textProperty().isEmpty())
            .or(isbn.textProperty().isEmpty())
            .or(nCopie.textProperty().isEmpty())
            .or(data.valueProperty().isNull())
        );*/
      ;
    }

    @FXML
    private void salva() {
        try {
           
            String nuovoTitolo = titolo.getText();
            String nuoviAutori = autori.getText();
            String nuovoIsbn = isbn.getText();
            int copie = Integer.parseInt(nCopie.getText());
            int dataPubb = Integer.parseInt(data.getText());

            if (libroCorrente == null) {
                Libro l=new Libro(nuovoTitolo, nuoviAutori, dataPubb, new Long(nuovoIsbn));
                l.inserisciLibro();
              
            } else {
               
                libroCorrente.modificaDatiLibro(nuovoTitolo, nuoviAutori, data.getText(), nuovoIsbn, nCopie.getText());
            }

            chiudi();

        } catch (NumberFormatException e) {
            mostraErrore("Devi compilare tutti i campi");
        } catch (Exception e) {
            mostraErrore("Errore durante il salvataggio: " + e.getMessage());
        }
    }

    @FXML
    private void chiudi() {
        Stage stage = (Stage) titolo.getScene().getWindow();
        stage.close();
    }

    private void mostraErrore(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.show();
    }
    
}
