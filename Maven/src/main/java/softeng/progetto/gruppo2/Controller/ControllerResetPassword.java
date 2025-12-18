package softeng.progetto.gruppo2.Controller;

import softeng.progetto.gruppo2.Model.Bibliotecario;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * @file ControllerResetPassword.java
 * @brief Controller che gestisce il cambio password dei bibliotecari.
 * @author Mirko Montella
 * @author Achille Romano
 * @author Sabrina Soriano
 * @author Ciro Senese
 */
public class ControllerResetPassword {

    /** @brief Campo di testo per l'inserimento della nuova password. */
    @FXML 
    private TextField txtNuovaPass;
    
    /** @brief Etichetta per la visualizzazione di messaggi di errore o conferma. */
    @FXML 
    private Label errore;

    /** @brief Indirizzo email del bibliotecario che sta effettuando il reset. */
    private String emailUtenteCorrente;

    /**
     * @brief Imposta l'email dell'utente per il quale deve essere cambiata la password.
     * @param email L'indirizzo email del bibliotecariə corrente.
     */
    public void setDatiUtente(String email) {
        this.emailUtenteCorrente = email;
    }

    /**
     * @brief Gestisce l'aggiornamento della password.
     */
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

    /**
     * @brief Chiude la finestra corrente di reset password.
     */
    @FXML
    private void chiudi() {
        Stage stage = (Stage) txtNuovaPass.getScene().getWindow();
        stage.close();
    }
}