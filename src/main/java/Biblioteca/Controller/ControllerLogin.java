package Biblioteca.Controller;

import Biblioteca.Model.Bibliotecario; 
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
    public void initialize() {
        ;
    }

    @FXML
    private void onLoginClick(ActionEvent event) {
       
        String emailInserita = TextFieldEmail.getText();
        String passwordInserita = TextFieldPassword.getText();

        try {
           
            Bibliotecario bibliotecario = new Bibliotecario(emailInserita, passwordInserita);

           
            int esito = bibliotecario.loginBibliotecario();

            if (esito == 1) {
               
                errore.setText(""); 
                System.out.println("Login effettuato con successo!");
                
                goToHomepage(event); 

            } else {
               
                errore.setText("Email o password non corrette!");
            }

        } catch (IOException e) {
            System.err.println("Errore nella lettura del database: " + e.getMessage());
            errore.setText("Errore di connessione al database.");
        }
    }

   
    private void goToHomepage(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/homepage.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }
}