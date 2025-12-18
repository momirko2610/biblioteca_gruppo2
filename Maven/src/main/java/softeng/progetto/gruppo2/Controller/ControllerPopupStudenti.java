package softeng.progetto.gruppo2.Controller;

import softeng.progetto.gruppo2.Model.Studente;
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
 * @file ControllerPopupStudenti.java
 * @brief Controller che gestisce il popup di inserimento e modifica dei dati studente.
 * @author Mirko Montella
 * @author Achille Romano
 * @author Sabrina Soriano
 * @author Ciro Senese
 */

public class ControllerPopupStudenti {
    /** @brief Titolo dinamico della finestra popup. */
    @FXML 
    private Text label;
    /** @brief Campo di testo per l'inserimento del nome dello studente. */
    @FXML 
    private TextField TextFieldnome;
    /** @brief Campo di testo per l'inserimento del cognome dello studente. */
    @FXML 
    private TextField TextFieldcognome;
    /** @brief Campo di testo per l'inserimento della matricola univoca. */
    @FXML 
    private TextField TextFieldmatricola;
    /** @brief Campo di testo per l'inserimento dell'email istituzionale. */
    @FXML 
    private TextField TextFieldemail;
    /** @brief Pulsante per la conferma dell'operazione. */
    @FXML 
    private Button conferma;
    /** @brief Etichetta per la visualizzazione dei messaggi di errore. */
    @FXML
    private Label errore; 

    /** @brief Istanza dello studente correntemente in fase di modifica. */
    private Studente studenteCorrente; 

    /**
     * @brief Configura il popup popolando i campi se si tratta di una modifica.
     * @param studente L'oggetto Studente da modificare, null se nuovo inserimento.
     */
    public void setStudenteDaModificare(Studente studente) {
        this.studenteCorrente = studente;

        if (studente != null) {

            label.setText("Modifica studente");
            TextFieldnome.setText(studente.getNome());
            TextFieldcognome.setText(studente.getCognome());
            TextFieldmatricola.setText(studente.getMatricola());
            TextFieldemail.setText(studente.getE_mail());

        } else {

            label.setText("Nuovo studente");
        }
    }
    
    /**
     * @brief Inizializza il controller.
     */
    @FXML
    public void initialize() {
        // abilita bottone conferma
      /* conferma.disableProperty().bind(
            titolo.textProperty().isEmpty()
            .or(autori.textProperty().isEmpty())
            .or(isbn.textProperty().isEmpty())
            .or(nCopie.textProperty().isEmpty())
            .or(data.valueProperty().isNull())
        );*/
      ;
    }

    /**
     * @brief Salva i dati dello studente nel sistema.
     * Verifica l'univocità della matricola e la presenza del database.
     */
    @FXML
    private void salva() {
        System.out.println("1");
        try {
           int esito;
            String nome = TextFieldnome.getText();
            String cognome = TextFieldcognome.getText();
            String matricola = TextFieldmatricola.getText();
            String email = TextFieldemail.getText();
            System.out.println("test");
            
            if (studenteCorrente == null) {
                Studente s=new Studente(nome, cognome, matricola, email);
                esito = s.inserisciDatiStudente();
                
                if (esito == -1) {
                    errore.setText("Matricola già inserita nel database");
                } else if (esito == 0) {
                    chiudi();
                    apriPopupSuccesso(s, "inserito");
                }
                
            } else {
                esito = studenteCorrente.modificaDatiStudente(nome, cognome, matricola, email);
                
                if (esito == -2){
                    errore.setText("Studente non risulta nel nostro database");
                } else if (esito == -3){
                    errore.setText("ERROR, database not found");
                } else if (esito == 0) {
                    chiudi();
                    apriPopupSuccesso(studenteCorrente, "modificato");
                }
            }

                }catch (NumberFormatException e) {
            errore.setText("Devi compilare tutti i campi in modo corretto: la matricola è numerica");
                }catch (Exception e) {
            mostraErrore("Errore durante il salvataggio: " + e.getMessage());
        }
    }

    /**
     * @brief Mostra una finestra di alert in caso di errore.
     * @param msg Il messaggio di errore da visualizzare.
     */
    private void mostraErrore(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.show();
    }
    
    /**
     * @brief Chiude la finestra del popup.
     */
    @FXML
    public void chiudi() {
        Stage stage = (Stage) label.getScene().getWindow();
        stage.close();
    }
    
    /**
     * @brief Apre il popup di successo dopo il salvataggio dei dati.
     * @param studenteSalvato Lo studente salvato.
     * @param azione Descrizione dell'azione effettuata.
     */
    private void apriPopupSuccesso(Studente studenteSalvato, String azione) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/successo.fxml"));
            Parent root = loader.load();
            
            ControllerSuccesso controller = loader.getController();
            controller.setMessaggio(String.format("Studente: %s %s %s inserito correttamente!", studenteSalvato.getNome(), studenteSalvato.getCognome(), azione));

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