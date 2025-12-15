/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biblioteca.Controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
/**
 *
 * @author achil
 */
public class ControllerLogout {

    @FXML
    private Button buttonConferma;

    @FXML
    private void conferma() {
        try {
            // recupera la finestra dello stage
            Stage popupStage = (Stage) buttonConferma.getScene().getWindow();
            // recupera la schermata sotto il popup
            Stage stage = (Stage) popupStage.getOwner();
            
            popupStage.close();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/homepage.fxml"));
            Parent root = loader.load();
            
            
            stage.setMinWidth(900);  // non si puo stringere la schermata oltre questi valori
            stage.setMinHeight(600);

            stage.setScene(new Scene(root));

            stage.setTitle("Homepage");
            stage.centerOnScreen();

            stage.show();

        } catch (IOException e) {
            System.err.println("Errore durante il logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void annulla() {
        Stage stage = (Stage) buttonConferma.getScene().getWindow();
        stage.close();
    }
}
