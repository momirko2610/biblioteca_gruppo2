package Biblioteca.Controller;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @file ControllerSuccesso.java
 * @brief Controller che gestisce la conferma di operazione riuscita.
 * @author Mirko Montella
 * @author Achille Romano
 * @author Sabrina Soriano
 * @author Ciro Senese
 */
public class ControllerSuccesso {

    /** @brief Serve per mostarre il messaggio */
    @FXML
    private Text messaggioTesto;

    /**
     * @brief Imposta il testo del messaggio da visualizzare nel popup.
     * @param testo Stringa contenente il messaggio di conferma dell'operazione.
     */
    public void setMessaggio(String testo) {
        if (messaggioTesto != null) {
            messaggioTesto.setText(testo);
        }
    }

    /**
     * @brief Chiude la finestra di dialogo di successo.
     * Verifica la presenza della scena prima di procedere alla chiusura.
     */
    @FXML
    public void chiudi() {
        if (messaggioTesto != null && messaggioTesto.getScene() != null) {
            Stage stage = (Stage) messaggioTesto.getScene().getWindow();
            stage.close();
        }
    }
}