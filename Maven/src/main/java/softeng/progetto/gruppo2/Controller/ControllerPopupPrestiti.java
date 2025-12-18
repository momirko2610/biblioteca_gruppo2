package softeng.progetto.gruppo2.Controller;

import softeng.progetto.gruppo2.Model.Database;
import softeng.progetto.gruppo2.Model.Libro;
import softeng.progetto.gruppo2.Model.Prestito;
import softeng.progetto.gruppo2.Model.Studente;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @file ControllerPopupPrestiti.java
 * @brief Controller che gestisce il popup di registrazione di un nuovo prestito,
 * controllando i requisiti definiti.
 * @author Mirko Montella
 * @author Achille Romano
 * @author Sabrina Soriano
 * @author Ciro Senese
 */
public class ControllerPopupPrestiti {

    /** @brief Titolo della finestra popup. */
    @FXML private Text label;
    
    /** @brief Menu a tendina per la selezione del libro tramite ISBN e Titolo. */
    @FXML private ComboBox<String> comboLibri; 
    
    /** @brief Menu a tendina per la selezione dello studente tramite Matricola e Cognome. */
    @FXML private ComboBox<String> comboStudenti; 
    
    /** @brief Selettore della data prevista per la restituzione del libro. */
    @FXML private DatePicker datePickerRestituzione;
    
    /** @brief Pulsante per confermare la registrazione del prestito. */
    @FXML private Button conferma;
    
    /** @brief Etichetta per la visualizzazione dinamica degli errori di validazione. */
    // Assicurati che nel file FXML ci sia <Label fx:id="errore" ... />
    @FXML private Label errore; 

    /** @brief Lista locale dei libri caricati dal database per il filtraggio. */
    private List<Libro> listaLibriCompleta;
    
    /** @brief Lista locale degli studenti caricati dal database per il filtraggio. */
    private List<Studente> listaStudentiCompleta;

    /**
     * @brief Carica i dati di libri e studenti nelle ComboBox
     * ed imposta il datapicker dekka data di restituzione a 30 giorni dalla data corrente.
     * @throws IOException In caso di problemi nell'accesso al database.
     */
    @FXML
    public void initialize() throws IOException {
        Database.creaDatabase();
        label.setText("Nuovo Prestito");
        datePickerRestituzione.setValue(LocalDate.now().plusDays(30));
        caricaDatiNelleComboBox();
    }

    /**
     * @brief Recupera i dati dal database e popola i menu a tendina.
     */
    private void caricaDatiNelleComboBox() {
        // ... (Codice di caricamento combobox identico a prima) ...
        try {
            listaLibriCompleta = Database.leggiDatabaseLibri(); 
            ObservableList<String> opzioniLibri = FXCollections.observableArrayList();
            if (listaLibriCompleta != null) {
                for (Libro l : listaLibriCompleta) {
                    if (l != null) opzioniLibri.add(l.getIsbn() + " - " + l.getTitolo());
                }
            }
            comboLibri.setItems(opzioniLibri);

            listaStudentiCompleta = Database.leggiDatabaseStudenti(); 
            ObservableList<String> opzioniStudenti = FXCollections.observableArrayList();
            if (listaStudentiCompleta != null) {
                for (Studente s : listaStudentiCompleta) {
                    if (s != null) opzioniStudenti.add(s.getMatricola() + " - " + s.getCognome());
                }
            }
            comboStudenti.setItems(opzioniStudenti);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @brief Legge i dati dall'interfaccia e tenta di registrare il prestito nel database.
     * Verifica le selezione e controlla se la data non è antecedente a quella odierna 
     * e gestisce i codici di errore in messaggi testuali per l'utente.
     */
    @FXML
    private void salva() {
        // Resetta errore precedente
        if (errore != null) errore.setText("");

        try {
            String selezioneLibro = comboLibri.getValue();
            String selezioneStudente = comboStudenti.getValue();
            LocalDate dataFine = datePickerRestituzione.getValue();

            if (selezioneLibro == null || selezioneStudente == null) {
                if (errore != null) errore.setText("Seleziona Libro e Studente!");
                return;
            }
            if (dataFine == null || dataFine.isBefore(LocalDate.now())) {
                if (errore != null) errore.setText("Data restituzione non valida!");
                return;
            }

            long isbnSelezionato = Long.parseLong(selezioneLibro.split(" - ")[0].trim());
            String matricolaSelezionata = selezioneStudente.split(" - ")[0].trim();

            Libro libroScelto = listaLibriCompleta.stream()
                    .filter(l -> l.getIsbn() == isbnSelezionato).findFirst().orElse(null);
            Studente studenteScelto = listaStudentiCompleta.stream()
                    .filter(s -> s.getMatricola().equalsIgnoreCase(matricolaSelezionata)).findFirst().orElse(null);

            if (libroScelto == null || studenteScelto == null) {
                if (errore != null) errore.setText("Dati non trovati.");
                return;
            }

            Prestito nuovoPrestito = new Prestito(studenteScelto, libroScelto, LocalDate.now(), dataFine);
            
            // --- CHIAMATA AL MODEL ---
            int esito = nuovoPrestito.registrazionePrestito(matricolaSelezionata, isbnSelezionato);

            // --- GESTIONE ERRORI TIPO CONTROLLER LIBRO ---
            if (esito == 0) {
                chiudi();
            } 
            else if (errore != null) { // Controllo null safety per la label
                if (esito == -1) {
                    errore.setText("Studente non trovato.");
                } else if (esito == -2) {
                    errore.setText("Limite prestiti (3) raggiunto.");
                } else if (esito == -3) {
                    errore.setText("Lo studente ha prestiti in ritardo.");
                } else if (esito == -4) {
                    // QUESTO È IL NUOVO ERRORE PER checkCopieDisponibili
                    errore.setText("Copie esaurite per questo libro!");
                } else if (esito == -5) {
                    errore.setText("Libro non trovato nel DB.");
                } else {
                    errore.setText("Errore generico durante il salvataggio.");
                }
            }

        } catch (Exception e) {
            if (errore != null) errore.setText("Eccezione: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @brief Chiude la finestra popup del prestito.
     */
    @FXML
    private void chiudi() {
        Stage stage = (Stage) label.getScene().getWindow();
        stage.close();
    }
}