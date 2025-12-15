/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biblioteca.Controller;

import Biblioteca.Model.Studente;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
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
    @FXML
    private Label errore; 

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
           int esito;
            String nome = TextFieldnome.getText();
            String cognome = TextFieldcognome.getText();
            String matricola = TextFieldmatricola.getText();
            String email = TextFieldemail.getText();
            System.out.println("test");
            
            if (studenteCorrente == null) {
                Studente s=new Studente(nome, cognome, matricola, email);
                esito = s.inserisciDatiStudente();
                
                if (esito == -1) {
                    errore.setText("Matricola già inserita nel database");
                } else if (esito == 0) {
                    chiudi();
                    apriPopupSuccesso(s, "inserito");
                }
                
            } else {
                esito = studenteCorrente.modificaDatiStudente(nome, cognome, matricola, email);
                
                if (esito == -2){
                    errore.setText("Studente non risulta nel nostro database");
                } else if (esito == -3){
                    errore.setText("ERROR, database not found");
                } else if (esito == 0) {
                    chiudi();
                    apriPopupSuccesso(studenteCorrente, "modificato");
                }
            }

                }catch (NumberFormatException e) {
            errore.setText("Devi compilare tutti i campi in modo corretto: la matricola è numerica");
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
    
    private void apriPopupSuccesso(Studente studenteSalvato, String azione) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/successo.fxml"));
            Parent root = loader.load();
            
            ControllerSuccesso controller = loader.getController();
            controller.setMessaggio(String.format("Studente: %s %s %s inserito correttamente!", studenteSalvato.getNome(), studenteSalvato.getCognome(), azione));

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Impossibile aprire il popup di successo.");
        }
    }
    
}
