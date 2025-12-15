package Biblioteca.Controller;

import Biblioteca.Model.Database;
import Biblioteca.Model.Libro;
import Biblioteca.Model.Prestito;
import Biblioteca.Model.Studente;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ControllerPopupPrestiti {

    @FXML
    private Text label;
    
    @FXML
    private ComboBox<String> comboLibri; 
    
    @FXML
    private ComboBox<String> comboStudenti; 
    
    @FXML
    private DatePicker datePickerRestituzione;
    
    @FXML
    private Button conferma;

    private List<Libro> listaLibriCompleta;
    private List<Studente> listaStudentiCompleta;

    @FXML
    public void initialize() throws IOException {
        Database.creaDatabase();
        label.setText("Nuovo Prestito");
        datePickerRestituzione.setValue(LocalDate.now().plusDays(30));
        
        caricaDatiNelleComboBox();
    }

    private void caricaDatiNelleComboBox() {
        try {
           
            System.out.println("Tentativo caricamento libri...");
            listaLibriCompleta = Database.leggiDatabaseLibri(); 
            
            ObservableList<String> opzioniLibri = FXCollections.observableArrayList();
            
            if (listaLibriCompleta != null && !listaLibriCompleta.isEmpty()) {
                System.out.println("Libri trovati: " + listaLibriCompleta.size());
                for (Libro l : listaLibriCompleta) {
                   
                    if (l != null) {
                        opzioniLibri.add(l.getIsbn() + " - " + l.getTitolo());
                    }
                }
            } else {
                System.err.println("ATTENZIONE: Lista libri è NULL o VUOTA.");
            }
            comboLibri.setItems(opzioniLibri);

           
            System.out.println("Tentativo caricamento studenti...");
            listaStudentiCompleta = Database.leggiDatabaseStudenti(); 
            
            ObservableList<String> opzioniStudenti = FXCollections.observableArrayList();
            
            if (listaStudentiCompleta != null && !listaStudentiCompleta.isEmpty()) {
                System.out.println("Studenti trovati: " + listaStudentiCompleta.size());
                for (Studente s : listaStudentiCompleta) {
                    if (s != null) {
                        opzioniStudenti.add(s.getMatricola() + " - " + s.getCognome());
                    }
                }
            } else {
                System.err.println("ATTENZIONE: Lista studenti è NULL o VUOTA.");
            }
            comboStudenti.setItems(opzioniStudenti);

        } catch (IOException e) {
            mostraErrore("Errore I/O nel caricamento dati: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostraErrore("Errore generico: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void salva() {
        try {
            String selezioneLibro = comboLibri.getValue();
            String selezioneStudente = comboStudenti.getValue();
            LocalDate dataFine = datePickerRestituzione.getValue();

            if (selezioneLibro == null || selezioneStudente == null) {
                mostraErrore("Devi selezionare un Libro e uno Studente!");
                return;
            }
            
            if (dataFine == null || dataFine.isBefore(LocalDate.now())) {
                mostraErrore("Data di restituzione non valida!");
                return;
            }

    
            long isbnSelezionato = Long.parseLong(selezioneLibro.split(" - ")[0].trim());
            String matricolaSelezionata = selezioneStudente.split(" - ")[0].trim();

            Libro libroScelto = listaLibriCompleta.stream()
                    .filter(l -> l.getIsbn() == isbnSelezionato)
                    .findFirst().orElse(null);

            Studente studenteScelto = listaStudentiCompleta.stream()
                    .filter(s -> s.getMatricola().equalsIgnoreCase(matricolaSelezionata))
                    .findFirst().orElse(null);
            
            if (libroScelto == null || studenteScelto == null) {
                mostraErrore("Errore: oggetto non trovato nella lista.");
                return;
            }

            Prestito nuovoPrestito = new Prestito(studenteScelto, libroScelto, LocalDate.now(), dataFine);
            
            nuovoPrestito.registrazionePrestito(matricolaSelezionata, isbnSelezionato);

            chiudi();

        } catch (Exception e) {
            mostraErrore("Errore durante il salvataggio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void chiudi() {
        Stage stage = (Stage) label.getScene().getWindow();
        stage.close();
    }

    private void mostraErrore(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.show();
    }
}