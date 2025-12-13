/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biblioteca.Controller;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
/**
 *
 * @author achil
 */
public class ControllerPopupPrestiti {

    @FXML
    private Text txtMessaggio;

    // Metodo per impostare il testo dinamico
    public void setDatiRestituzione(String nomeStudente, String matricola, String titoloLibro) {
        // Ottieni la data di oggi formattata (es. 13/10/2023)
        String dataOdierna = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // Costruisci la frase
        String messaggio = String.format(
            "Lo studente: %s\ncon matricola: %s\nha restituito il libro: \"%s\"\nin data: %s", 
            nomeStudente, matricola, titoloLibro, dataOdierna
        );

        txtMessaggio.setText(messaggio);
    }

    @FXML
    private void conferma() {
        System.out.println("Restituzione confermata nel DB!");
        // Qui inserisci la logica per aggiornare il Database (UPDATE prestiti SET data_rientro = NOW()...)
        chiudi();
    }

    @FXML
    private void chiudi() {
        Stage stage = (Stage) txtMessaggio.getScene().getWindow();
        stage.close();
    }
}
