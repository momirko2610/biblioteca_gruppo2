package softeng.progetto.gruppo2.Controller;

import softeng.progetto.gruppo2.Model.Libro;
import softeng.progetto.gruppo2.Model.Studente;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * @file ControllerDelete.java
 * @brief Controller per la gestione del popup di conferma in caso di eliminazione dei libri o studenti dal database.
 * @author Mirko Montella
 * @author Achille Romano
 * @author Sabrina Soriano
 * @author Ciro Senese
 */
public class ControllerDelete {

    /** @brief L'oggetto (Libro o Studente) selezionato per la cancellazione. */
    private Object oggettoDaEliminare; 
    
    /** @brief Label per la visualizzazione di eventuali errori. */
    @FXML
    private Label errore; 

    /**
     * @brief Imposta l'oggetto che deve essere rimosso dal database.
     * @param obj Libro o Studente da eliminare.
     */
    public void setOggettoDaEliminare(Object obj) {
        this.oggettoDaEliminare = obj;
    }

    /** @brief Pulsante per confermare la cancellazione. */
    @FXML
    private Button buttonConferma; 
    
    /**
     * @brief Esegue la cancellazione definitiva dei dati nel database.
     * Gestisce i diversi esiti dell'operazione (successo, libro in prestito, errore database)
     * aggiornando l'interfaccia di conseguenza.
     * @param event L'evento di click del mouse sul pulsante conferma.
     */
    @FXML
    public void conferma(MouseEvent event) { 
        try {
            int esito;
            if (this.oggettoDaEliminare instanceof Libro) {
                Libro l = (Libro) this.oggettoDaEliminare;
                esito = l.cancellazioneDatiLibro();

                if (esito == -1) {
                    errore.setText("Non puoi eliminare il libro, è in prestito"); 
                } else if (esito == -2){
                    errore.setText("Libro non risulta nel nostro database");
                } else if (esito == -3){
                    errore.setText("ERROR, database not found");
                } else if (esito == 0) {
                    annulla();
                    apriPopupSuccesso();
                }
            } 
            else if (this.oggettoDaEliminare instanceof Studente) {
                Studente s = (Studente) this.oggettoDaEliminare;
                esito = s.cancellazioneDatiStudente();
                 
                if (esito == -2) {
                    errore.setText("Studente non risulta nel nostro database"); 
                } else if (esito == -3){
                    errore.setText("ERROR, database not found");
                } else if (esito == 0) {
                    annulla();
                    apriPopupSuccesso();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * @brief Chiude il popup
     */
    @FXML
    public void annulla() {
        Stage stage = (Stage) buttonConferma.getScene().getWindow();
        stage.close();
    }
    
    /**
     * @brief Apre una finestra di notifica per confermare l'avvenuta eliminazione.
     * Carica il file successo.fxml e personalizza il messaggio in base al tipo di oggetto eliminato.
     */
    @FXML
    private void apriPopupSuccesso() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/successo.fxml"));
            Parent root = loader.load();
            
            ControllerSuccesso controller = loader.getController();
            
            if (this.oggettoDaEliminare instanceof Libro) {
                Libro l = (Libro) this.oggettoDaEliminare;
                controller.setMessaggio(String.format("Libro: %s eliminato correttamente!", l.getTitolo()));
            }
            else if (this.oggettoDaEliminare instanceof Studente) {
                Studente s = (Studente) this.oggettoDaEliminare;
                controller.setMessaggio(String.format("Studente: %s %s eliminato correttamente!", s.getNome(), s.getCognome()));
            }

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