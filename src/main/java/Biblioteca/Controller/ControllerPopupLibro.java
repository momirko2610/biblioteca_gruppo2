/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biblioteca.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author achil
 */
public class ControllerPopupLibro {
    @FXML private Text label;
    @FXML private TextField titolo;
    @FXML private TextField autori;
    @FXML private TextField isbn;
    @FXML private TextField nCopie;
    @FXML private DatePicker data;
    @FXML private Button conferma;
    @FXML private Button annulla;

    private Libro libroCorrente; 

    // --- SETUP INIZIALE ---
    public void setLibroDaModificare(Libro libro) {
        this.libroCorrente = libro;

        if (libro != null) {
            //modifica campi
            label.setText("Modifica Libro");
            titolo.setText(libro.getTitolo());
            autori.setText(libro.getAutori());
            isbn.setText(libro.getIsbn());
            nCopie.setText(String.valueOf(libro.getCopie()));
            data.setValue(libro.getDataPubblicazione());

        } else {
            //nuovo libro
            label.setText("Nuovo Libro");
        }
    }
    
    @FXML
    public void initialize() {
        // abilita bottone conferma
        conferma.disableProperty().bind(
            titolo.textProperty().isEmpty()
            .or(autori.textProperty().isEmpty())
            .or(isbn.textProperty().isEmpty())
            .or(nCopie.textProperty().isEmpty())
            .or(data.valueProperty().isNull())
        );
    }

    @FXML
    private void salva() {
        try {
            // 2. Recupero Dati
            String nuovoTitolo = titolo.getText();
            String nuoviAutori = autori.getText();
            String nuovoIsbn = isbn.getText();
            int copie = Integer.parseInt(nCopie.getText());
            LocalDate dataPubb = data.getValue();

            // 3. Logica Database
            if (libroCorrente == null) {
                // INSERT: Chiama il metodo per creare un nuovo libro nel DB
                System.out.println("INSERT INTO Libri VALUES (" + nuovoIsbn + ", ...)");
                // Esempio: Database.getInstance().inserisciLibro(new Libro(...));
            } else {
                // UPDATE: Aggiorna il libro esistente
                System.out.println("UPDATE Libri SET titolo = " + nuovoTitolo + " WHERE isbn = " + libroCorrente.getIsbn());
                // Esempio: Database.getInstance().aggiornaLibro(libroCorrente);
            }

            chiudi();

        } catch (NumberFormatException e) {
            mostraErrore("Il numero di copie deve essere un numero intero!");
        } catch (Exception e) {
            mostraErrore("Errore durante il salvataggio: " + e.getMessage());
        }
    }

    @FXML
    private void chiudi() {
        Stage stage = (Stage) titolo.getScene().getWindow();
        stage.close();
    }

    private void mostraErrore(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.show();
    }
}
