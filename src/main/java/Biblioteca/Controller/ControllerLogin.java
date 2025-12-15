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

public class ControllerLogin {

    @FXML
    private TextField TextFieldEmail;

    @FXML
    private PasswordField TextFieldPassword;

    @FXML
    private Button ConfirmButton;
    
    @FXML
    private Label errore; 

    @FXML
    public void initialize() {}

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
                System.out.println("Login effettuato con successo!");
                goToLibri(); 

            } else {
                errore.setText("Email o password errate!");
            }

        } catch (IOException e) {
            System.err.println("Errore nella lettura del database: " + e.getMessage());
            errore.setText("Errore di connessione al database.");
        }
    }

   @FXML
    private void goToLibri() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/libri.fxml"));
            Parent root = loader.load();

            // recupera lo stage precedente, (in questo caso lo fa attraverso il textfield, ma potrebbe farlo da qualsiasi altra cosa)
            Stage stage = (Stage) TextFieldEmail.getScene().getWindow();

            stage.setMinWidth(900);  // non si puo stringere la schermata oltre questi valori
            stage.setMinHeight(600);

            stage.setScene(new Scene(root));

            stage.setTitle("Catalogo Libri");
            stage.centerOnScreen();

            stage.show();

        } catch (IOException e) {
            System.err.println("Errore nel caricamento della schermata homepage: " + e.getMessage());
        }
    }
    
    @FXML
    private void goToHomepage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/homepage.fxml"));
            Parent root = loader.load();

            // recupera lo stage precedente, (in questo caso lo fa attraverso il textfield, ma potrebbe farlo da qualsiasi altra cosa)
            Stage stage = (Stage) TextFieldEmail.getScene().getWindow();

            stage.setMinWidth(900);  // non si puo stringere la schermata oltre questi valori
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