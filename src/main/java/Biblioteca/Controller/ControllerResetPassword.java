package Biblioteca.Controller;

import Biblioteca.Model.Bibliotecario;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class ControllerResetPassword {

    @FXML 
    private TextField txtNuovaPass;
    
    @FXML 
    private Label errore;

    private String emailUtenteCorrente;

    public void setDatiUtente(String email) {
        this.emailUtenteCorrente = email;
    }

    @FXML
    private void confermaCambio() {
        String nuovaPass = txtNuovaPass.getText();

        if (nuovaPass == null || nuovaPass.isEmpty()) {
            errore.setText("Inserisci una password valida");
            return;
        }

        try {
            Bibliotecario b = new Bibliotecario(emailUtenteCorrente, "");
            
            int esito = b.cambiaPassword(nuovaPass);

            if (esito == 1) {
                System.out.println("Password cambiata con successo per: " + emailUtenteCorrente);
                chiudi();
            } else {
                errore.setText("Errore: Impossibile trovare l'utente o salvare.");
            }

        } catch (IOException e) {
            errore.setText("Errore di scrittura nel database.");
            e.printStackTrace();
        }
    }

    @FXML
    private void chiudi() {
        Stage stage = (Stage) txtNuovaPass.getScene().getWindow();
        stage.close();
    }
}