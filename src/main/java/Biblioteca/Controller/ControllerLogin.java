package Biblioteca.Controller;

import Biblioteca.Model.Bibliotecario; 
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * @file ControllerLogin.java
 * @brief Controller che gestisce i login dei bibliotecari,
 * gestendo la validazione delle credenziali, il limite dei tentativi e il cambio schermata
 * @author Mirko Montella
 * @author Achille Romano
 * @author Sabrina Soriano
 * @author Ciro Senese
 */

public class ControllerLogin {

    /** @brief Campo di testo per l'inserimento dell'email istituzionale. */
    @FXML
    private TextField TextFieldEmail;

    /** @brief Campo di testo oscurato per l'inserimento della password. */
    @FXML
    private PasswordField TextFieldPassword;

    /** @brief Pulsante per confermare l'invio delle credenziali. */
    @FXML
    private Button ConfirmButton;
    
    /** @brief Etichetta per mostrare messaggi di errore o avvisi di sicurezza. */
    @FXML
    private Label errore; 
    
    /** @brief Contatore interno per monitorare i tentativi di accesso consecutivi errati. */
    private int tentativiFalliti = 0;

    /**
     * @brief Inizializzazione del controller.
     */
    @FXML
    public void initialize() {}

    /**
     * @brief Gestisce l'evento di click sul pulsante di login.
     * Verifica che i campi non siano vuoti e verifica l'esito,
     * bloccando l'accesso dopo il terzo tentativo fallito.
     */
    @FXML
    private void onLoginClick() { 
        String emailInserita = TextFieldEmail.getText();
        String passwordInserita = TextFieldPassword.getText();
        
        if (emailInserita.isEmpty() || passwordInserita.isEmpty()) {
            errore.setText("Inserisci email e/o password.");
            return;
        }

        try {
            Bibliotecario bibliotecario = new Bibliotecario(emailInserita, passwordInserita);
            int esito = bibliotecario.loginBibliotecario();

            if (esito == 1) {
                errore.setText("");
                tentativiFalliti = 0;
                System.out.println("Login effettuato con successo!");
                goToLibri(bibliotecario); 

            } else {
                tentativiFalliti++;
                errore.setText(String.format("Hai %d tentativi prima del rest forzato della password!", 3-tentativiFalliti));
                if (tentativiFalliti >= 3) {
                    try {
                        String nuovaPassword = bibliotecario.resetPassword();
                        
                        if (nuovaPassword != null) {
                            errore.setText("Troppi tentativi! Password resettata: " + nuovaPassword);
                            tentativiFalliti = 0; 
                        } else {
                            errore.setText("Email non presente nel nostro database");
                        }
                    } catch (Exception e) {
                        errore.setText("Errore durante il reset della password.");
                        e.printStackTrace();
                    }
                } else {
                    errore.setText("Email o password errate! (" + tentativiFalliti + "/3)");
                }
            }

        } catch (IOException e) {
            System.err.println("Errore database: " + e.getMessage());
            errore.setText("Errore di connessione al database.");
        }
    }

    /**
     * @brief Reindirizza l'utente alla schermata di gestione libri dopo un login riuscito,
     * passando l'oggetto bibliotecario loggato al controller successivo per mantenere la sessione.
     * @param bibliotecarioLoggato Bibliotecario autenticato.
     */
    @FXML
    private void goToLibri(Bibliotecario bibliotecarioLoggato) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/libri.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) TextFieldEmail.getScene().getWindow();
            //Recupera lo stage precedente, (in questo caso lo fa attraverso il textfield, ma potrebbe farlo da qualsiasi altra cosa)
            ControllerLibri controller = loader.getController();
            controller.setBibliotecario(bibliotecarioLoggato);

            stage.setMinWidth(900); //non si può stringere la schermata oltre questi valori 
            stage.setMinHeight(600);

            stage.setScene(new Scene(root));
            stage.setTitle("Catalogo Libri");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            System.err.println("Errore nel caricamento della schermata gestione libri: " + e.getMessage());
        }
    }
    
    /**
     * @brief Permette di tornare alla Homepage dell'applicazione.
     */
    @FXML
    private void goToHomepage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/homepage.fxml"));
            Parent root = loader.load();
            //Recupera lo stage precedente, (in questo caso lo fa attraverso il textfield, ma potrebbe farlo da qualsiasi altra cosa)
            Stage stage = (Stage) TextFieldEmail.getScene().getWindow();

            stage.setMinWidth(900); //non si può stringere la schermata oltre questi valori 
            stage.setMinHeight(600);
            
            stage.setScene(new Scene(root));
            stage.setTitle("Homepage");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            System.err.println("Errore nel caricamento della schermata homepage: " + e.getMessage());
        }
    }
}