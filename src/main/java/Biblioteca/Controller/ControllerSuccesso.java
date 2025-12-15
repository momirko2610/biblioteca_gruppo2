package Biblioteca.Controller;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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