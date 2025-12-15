/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biblioteca.Controller;

import Biblioteca.Model.Studente;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author achil
 */

public class ControllerPopupStudenti {
    @FXML 
    private Text label;
    @FXML 
    private TextField TextFieldnome;
    @FXML 
    private TextField TextFieldcognome;
    @FXML 
    private TextField TextFieldmatricola;
    @FXML 
    private TextField TextFieldemail;
    @FXML 
    private Button conferma;

    private Studente studenteCorrente; 

    public void setStudenteDaModificare(Studente studente) {
        this.studenteCorrente = studente;

        if (studente != null) {

            label.setText("Modifica studente");
            TextFieldnome.setText(studente.getNome());
            TextFieldcognome.setText(studente.getCognome());
            TextFieldmatricola.setText(studente.getMatricola());
            TextFieldemail.setText(studente.getE_mail());

        } else {

            label.setText("Nuovo studente");
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
        System.out.println("1");
        try {
           
            String nome = TextFieldnome.getText();
            String cognome = TextFieldcognome.getText();
            String matricola = TextFieldmatricola.getText();
            String email = TextFieldemail.getText();
            System.out.println("test");
            
            if (studenteCorrente == null) {
                Studente s=new Studente(nome, cognome, matricola, email);
                s.inserisciDatiStudente();
                
            } else {
                
                studenteCorrente.modificaDatiStudente(nome, cognome, matricola, email);
            }

            chiudi();
                }catch (NumberFormatException e) {
            mostraErrore("Devi compilare tutti i campi");
                }catch (Exception e) {
            mostraErrore("Errore durante il salvataggio: " + e.getMessage());
        }
    }

    private void mostraErrore(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.show();
    }
    
    @FXML
    public void chiudi() {
        Stage stage = (Stage) label.getScene().getWindow();
        stage.close();
    }
    
}
