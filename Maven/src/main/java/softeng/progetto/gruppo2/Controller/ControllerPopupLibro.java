package softeng.progetto.gruppo2.Controller;

import softeng.progetto.gruppo2.Model.Libro;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @file ControllerPopupLibro.java
 * @brief Controller che gestisce il popup di inserimento e modifica libri.
 * @author Mirko Montella
 * @author Achille Romano
 * @author Sabrina Soriano
 * @author Ciro Senese
 */
public class ControllerPopupLibro {
    
    /** @brief Titolo del popup. */
    @FXML 
    private Text label;
    /** @brief Campo di testo per l'inserimento del titolo del libro. */
    @FXML 
    private TextField titolo;
    /** @brief Campo di testo per l'inserimento degli autori del libro. */
    @FXML 
    private TextField autori;
    /** @brief Campo di testo per l'inserimento del codice identificativo univoco ISBN. */
    @FXML 
    private TextField isbn;
    /** @brief Campo di testo per l'inserimento del numero di copie disponibili. */
    @FXML 
    private TextField nCopie;
    /** @brief Campo di testo per l'inserimento dell'anno di pubblicazione. */
    @FXML 
    private TextField data;
    /** @brief Pulsante per confermare l'operazione di salvataggio. */
    @FXML 
    private Button conferma;
    /** @brief Etichetta per la visualizzazione di messaggi di errore di validazione. */
    @FXML
    private Label errore; 

    /** @brief Libro da modificare (o null se si tratta di un nuovo libro). */
    private Libro libroCorrente; 

    /**
     * @brief Configura il popup per la modifica di un libro esistente o per un nuovo libro
     * Se l'oggetto passato è diverso da null, popola le textfield con i dati del libro.
     * @param libro Libro da modificare, oppure null per un nuovo libro.
     */
    public void setLibroDaModificare(Libro libro) {
        this.libroCorrente = libro;

        if (libro != null) {
            label.setText("Modifica Libro");
            titolo.setText(libro.getTitolo());
            autori.setText(libro.getAutore());
            isbn.setText(Long.toString(libro.getIsbn()));
            nCopie.setText(String.valueOf(libro.getNumCopie()));
            data.setText(String.valueOf(libro.getAnnoPubblicazione()));
        } else {
            //nuovo libro
            label.setText("Nuovo Libro");
        }
    }
    
    /**
     * @brief Inizializzazione del controller.
     */
    @FXML
    public void initialize() {
        
    }

    /**
     * @brief Gestisce il salvataggio dei dati del libro nel database
     * Verificando i formati di ISBN (13 cifre) e Anno (4 cifre). 
     * In caso di successo, chiude il popup e mostra una notifica di avvenuta operazione.
     */
    @FXML
    private void salva() {
        try {
            int esito;
            String nuovoTitolo = titolo.getText();
            String nuoviAutori = autori.getText();
            String nuovoIsbn = isbn.getText();
            int copie = Integer.parseInt(nCopie.getText());
            int dataPubb = Integer.parseInt(data.getText());
            if (libroCorrente == null) {
                Libro l = new Libro(nuovoTitolo, nuoviAutori, dataPubb, new Long(nuovoIsbn), copie);
                esito = l.inserisciLibro();
                
                if (esito == -1) {
                    errore.setText("Formato ISBN incorretto, deve essere un numero di 13 cifre");
                } else if (esito == -2) {
                    errore.setText("Formato Anno incorretto, deve essere un numero di 4 cifre");
                } else if (esito == 0) {
                    chiudi();
                    apriPopupSuccesso(l, "inserito");
                }
              
            } 
            else {
                esito = libroCorrente.modificaDatiLibro(nuovoTitolo, nuoviAutori, data.getText(), nuovoIsbn, nCopie.getText());

                if (esito == -2){
                    errore.setText("Libro non risulta nel nostro database");
                } else if (esito == -3){
                    errore.setText("ERROR, database not found");
                } else if (esito == 0) {
                    chiudi();
                    apriPopupSuccesso(libroCorrente, "modificato");
                }
            }

        } catch (NumberFormatException e) {
            errore.setText("Devi compilare tutti i campi in modo corretto: isbn, numero copie e anno sono dei numerici");
        } catch (Exception e) {
            mostraErrore("Errore durante il salvataggio: " + e.getMessage());
        }
    }

    /**
     * @brief Chiude la finestra del popup.
     */
    @FXML
    private void chiudi() {
        Stage stage = (Stage) titolo.getScene().getWindow();
        stage.close();
    }

    /**
     * @brief Mostra un messaggio di errore tramite un Alert.
     * @param msg Il messaggio di errore da visualizzare.
     */
    private void mostraErrore(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.show();
    }
    
    /**
     * @brief Apre il popup di successo dopo un inserimento o una modifica.
     * @param libroSalvato Il libro salvato.
     * @param azione Stringa "inserito" o "modificato".
     */
    private void apriPopupSuccesso(Libro libroSalvato, String azione) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/successo.fxml"));
            Parent root = loader.load();
            
            ControllerSuccesso controller = loader.getController();
            controller.setMessaggio(String.format("Libro: %s %s correttamente!", libroSalvato.getTitolo(), azione));

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Impossibile aprire il popup di successo.");
        }
    }
}