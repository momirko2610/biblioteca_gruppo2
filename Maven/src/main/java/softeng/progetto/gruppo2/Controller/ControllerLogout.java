package softeng.progetto.gruppo2.Controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * @file ControllerLogout.java
 * @brief Controller per la gestione del logout dei bibliotecariə.
 * Gestisce il popup di conferma reindirizzando alla schermata di login
 * @author Mirko Montella
 * @author Achille Romano
 * @author Sabrina Soriano
 * @author Ciro Senese
 */
public class ControllerLogout {

    /** @brief Pulsante per confermare il logout. */
    @FXML
    private Button buttonConferma;

    /**
     * @brief Esegue la procedura di logout e reindirizza alla schermata di login
     * sostituendo la scena principale con il form di login.
     */
    @FXML
    private void conferma() {
        try {
            //Recupera la finestra dello stage
            Stage popupStage = (Stage) buttonConferma.getScene().getWindow();
            
            //Recupera la schermata sotto il popup
            Stage stage = (Stage) popupStage.getOwner();
            popupStage.close();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/login.fxml"));
            Parent root = loader.load();
            
            // Imposta le dimensioni minime e la configurazione della finestra principale
            stage.setMinWidth(900); //Non si può stringere la schermata sotto questi valori
            stage.setMinHeight(600);
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.centerOnScreen();

            stage.show();

        } catch (IOException e) {
            System.err.println("Errore durante il logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @brief Annulla l'operazione di logout.
     * Chiude semplicemente il popup di conferma.
     */
    @FXML
    private void annulla() {
        Stage stage = (Stage) buttonConferma.getScene().getWindow();
        stage.close();
    }
}