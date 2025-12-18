package softeng.progetto.gruppo2.Controller;

import softeng.progetto.gruppo2.Model.Database;
import softeng.progetto.gruppo2.Model.Prestito;
import softeng.progetto.gruppo2.Model.Studente;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * @file ControllerInfo.java
 * @brief Controller per la visualizzazione dei prestiti specifici di uno studente,
 * Qmostrando lo storico o i prestiti attivi di un determinato studente.
 * @author Mirko Montella
 * @author Achille Romano
 * @author Sabrina Soriano
 * @author Ciro Senese
 */

public class ControllerInfo {

    /** @brief Tabella per la visualizzazione dei prestiti. */
    @FXML
    private TableView<Prestito> tableViewPrestito;

    /** @brief Colonna per il codice ISBN del libro in prestito. */
    @FXML
    private TableColumn<Prestito, String> ISBN; 

    /** @brief Colonna per la data di inizio del prestito. */
    @FXML
    private TableColumn<Prestito, LocalDate> DataPrestito;

    /** @brief Lista osservabile filtrata dei prestiti dello studente. */
    private ObservableList<Prestito> listaPrestito = FXCollections.observableArrayList();
    
    /** @brief Riferimento allo studente di cui si vogliono visualizzare le informazioni. */
    private Studente studenteSelezionato;

    /**
     * @brief Inizializza la tabella impostando le proprietà delle colonne.
     * Mappa le colonne agli attributi della classe Prestito.
     * @throws IOException In caso di errori nella creazione o accesso al database.
     */
    @FXML
    public void initialize() throws IOException {
        Database.creaDatabase();
        ISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        DataPrestito.setCellValueFactory(new PropertyValueFactory<>("dataInizio"));
    }

    /**
     * @brief Imposta lo studente selezionato e avvia la ricerca dei suoi prestiti.
     * @param studente L'oggetto Studente selezionato dalla lista degli studenti.
     */
    public void setStudente(Studente studente) {
        this.studenteSelezionato = studente;
        cercaPrestiti();
    }

    /**
     * @brief Filtra il database dei prestiti in base alla matricola dello studente selezionato,
     * popolando la lista con solo i prestit associati alla matricola.
     */
    private void cercaPrestiti() {
        try {
            List<Prestito> tuttiIPrestiti = Database.leggiDatabasePrestiti();

            if (tuttiIPrestiti != null) {
                for (Prestito p : tuttiIPrestiti) {
                    
                    String matricolaPrestito = p.getMatricola();
                    String matricolaStudente = studenteSelezionato.getMatricola();
                    if (matricolaPrestito.equals(matricolaStudente)) {
                        listaPrestito.add(p);
                    }
                }
            }

            tableViewPrestito.setItems(listaPrestito);

        } catch (IOException e) {
            System.out.println("Errore caricamento dati");
        }
    }

    /**
     * @brief Chiude la finestra corrente dei dettagli studente.
     */
    public void chiudi() {
        Stage stage = (Stage) tableViewPrestito.getScene().getWindow();
        stage.close();
    }
}