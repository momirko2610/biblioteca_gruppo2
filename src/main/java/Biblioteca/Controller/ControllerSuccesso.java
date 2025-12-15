/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biblioteca.Controller;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.Stage;
/**
 *
 *  @author Mirko Montella
 *  @author Achille Romano
 *  @author Sabrina Soriano
 *  @author Ciro Senese
 */
public class ControllerSuccesso {

    @FXML
    private Text messaggioTesto;

    public void setMessaggio(String testo) {
        if (messaggioTesto != null) {
            messaggioTesto.setText(testo);
        }
    }

    @FXML
    public void chiudi() {
        if (messaggioTesto != null && messaggioTesto.getScene() != null) {
            Stage stage = (Stage) messaggioTesto.getScene().getWindow();
            stage.close();
        }
    }
}