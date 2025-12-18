package Biblioteca.Controller;

import Biblioteca.Model.Prestito;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @file ControllerRitorno.java
 * @brief Controller che gestisce la conferma di restituzione di un libro.
 * @author Mirko Montella
 * @author Achille Romano
 * @author Sabrina Soriano
 * @author Ciro Senese
 */
public class ControllerRitorno {
    /** @brief Testo visualizzato nel popup contenente i dettagli della restituzione. */
    @FXML
    private Text messaggio;

    /** @brief Matricola dello studente che effettua la restituzione. */
    private String matricolaCorrente;
    
    /** @brief Codice ISBN del libro restituito. */
    private Long isbnCorrente;

    /**
     * @brief Inizializza i dati della restituzione e imposta il messaggio con le informazioni relative alla restituzione.
     * @param matricola La matricola dello studente.
     * @param isbn Il codice ISBN del libro.
     */
    public void setDatiRestituzione(String matricola, Long isbn) {
        this.matricolaCorrente = matricola;
        this.isbnCorrente = isbn;

        String dataOdierna = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        String txtmessaggio = String.format(
            "Lo studente con la matricola: %s\nHa restituito il libro: %s\nIn data: %s", 
            matricola, isbn, dataOdierna
        );

        messaggio.setText(txtmessaggio);
    }

    /**
     * @brief Conferma l'avvenuta restituzione del libro.
     * In caso di successo, chiude la finestra di popup.
     */
    @FXML
    private void conferma() {
        try {
            Prestito.registrazioneRestituzione(matricolaCorrente, isbnCorrente);
            
            System.out.println("Restituzione completata.");
            chiudi();
            
        } catch (IOException e) {
            System.err.println("Errore durante la restituzione: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @brief Chiude la finestra di dialogo corrente.
     */
    @FXML
    public void chiudi() {
        Stage stage = (Stage) messaggio.getScene().getWindow();
        stage.close();
    }
}