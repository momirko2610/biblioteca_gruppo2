package Biblioteca.Controller;

import Biblioteca.Model.Bibliotecario;
import Biblioteca.Model.Database;
import Biblioteca.Model.Studente;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @file ControllerStudenti.java
 * @brief Controller che gestisce gli studenti da parte del bibliotecariə.
 * @author Mirko Montella
 * @author Achille Romano
 * @author Sabrina Soriano
 * @author Ciro Senese
 */

public class ControllerStudenti {

    /** @brief Tabella per la visualizzazione dell'elenco studenti. */
    @FXML
    private TableView<Studente> tableViewStudenti;
   
    /** @brief Colonna per il nome dello studente. */
    @FXML
    private TableColumn<Studente, String> Nome; 
    
    /** @brief Colonna per il cognome dello studente. */
    @FXML
    private TableColumn<Studente, String> Cognome;
    
    /** @brief Colonna per la matricola univoca dello studente. */
    @FXML
    private TableColumn<Studente, String> Matricola;
    
    /** @brief Colonna per l'e-mail istituzionale dello studente. */
    @FXML
    private TableColumn<Studente, String> Email;
    
    /** @brief Colonna per i pulsanti di azione (Modifica, Elimina, Info). */
    @FXML private TableColumn<Studente, HBox> Azioni; 
    
    /** @brief Campo di testo per la ricerca degli studenti. */
    @FXML
    private TextField searchStudentTextField;

    /** @brief Lista osservabile degli studenti caricati dal database. */
    private ObservableList<Studente> listaStudente= FXCollections.observableArrayList(); 
    
    /** @brief Riferimento al bibliotecario attualmente loggato. */
    private Bibliotecario bibliotecarioLoggato;
    
    /**
     * @brief Imposta il bibliotecario loggato.
     * @param bibliotecario Bibliotecario autenticato.
     */
    public void setBibliotecario(Bibliotecario bibliotecario) {
        this.bibliotecarioLoggato = bibliotecario;
    }
   
    /** @brief Costruttore vuoto della classe. */
    public ControllerStudenti() {
    }

    /**
     * @brief Inizializza il controller e popola la tabella
     * @throws IOException In caso di errori nell'accesso al database.
     */
    @FXML
    public void initialize() throws IOException {
        Database.creaDatabase();
        configuraTabella();
        caricaDatiAllAvvio();
        searchStudentTextField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                onSearchStudent(); // Esegui la ricerca
            }
        });
    }

    /**
     * @brief Mappa delle colonne della tabella con la classe Studente.
     */
    private void configuraTabella() {
        Nome.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        Cognome.setCellValueFactory(new PropertyValueFactory<>("Cognome"));
        Matricola.setCellValueFactory(new PropertyValueFactory<>("Matricola"));
        Email.setCellValueFactory(new PropertyValueFactory<>("E_mail"));
        Azioni.setCellValueFactory(new PropertyValueFactory<>("azioni"));
        tableViewStudenti.setEditable(true);
    }

    /**
     * @brief Carica gli studenti dal database e inizializza i pulsanti di azione.
     */
    void caricaDatiAllAvvio() {
        try {
            List<Studente> studenteSalvati = Database.leggiDatabaseStudenti();
            
            if (studenteSalvati != null && !studenteSalvati.isEmpty()) {
                listaStudente= FXCollections.observableArrayList(studenteSalvati);
                
                for (Studente studente : listaStudente) {
                    HBox box = studente.getAzioni(); 
                        
                    Button Modifica = (Button) box.getChildren().get(0);
                    Button Elimina = (Button) box.getChildren().get(1);
                    Button Info = (Button) box.getChildren().get(2);

                    Modifica.setOnAction(event -> { apriPopupModifica(studente); });
                    Elimina.setOnAction(event -> { apriPopupElimina(studente); });
                    Info.setOnAction(event -> { apriPopupInfo(studente); });
                }
                
                tableViewStudenti.setItems(listaStudente);
                System.out.println("Tabella aggiornata con successo. Studenti caricati: " + listaStudente.size());
            } else {
                System.out.println("Nessun studente trovato nel database JSON.");
            }

        } catch (IOException e) {
            System.err.println("Errore critico nel caricamento del database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * @brief Gestisce la ricerca degli studenti per cognome o matricola.
     */
    @FXML
    private void onSearchStudent() {
        String searchText = searchStudentTextField.getText();
        
        if (searchText == null || searchText.trim().isEmpty()) {
            tableViewStudenti.setItems(listaStudente);
            return;
        }
        
        String lowerCaseFilter = searchText.toLowerCase();
        ObservableList<Studente> risultati = FXCollections.observableArrayList();
        
        if (listaStudente != null) {
            for (Studente studente : listaStudente) {
                // Bug fix: corretto l'uso dei getter per cognome e matricola
                String cognome = (studente.getCognome() != null) ? studente.getCognome().toLowerCase() : "";
                String matricola = (studente.getMatricola() != null) ? studente.getMatricola().toLowerCase() : "";

                if (cognome.contains(lowerCaseFilter) || matricola.contains(lowerCaseFilter)) {
                    risultati.add(studente);
                }
            }
        }
        tableViewStudenti.setItems(risultati);
    }

    /**
     * @brief Naviga verso la schermata di gestione dei prestiti.
     */
    @FXML
    private void goToPrestiti() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/prestiti.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tableViewStudenti.getScene().getWindow();
            ControllerPrestiti controller = loader.getController();
            controller.setBibliotecario(this.bibliotecarioLoggato);
            stage.setMinWidth(900);
            stage.setMinHeight(600);
            stage.setScene(new Scene(root));
            stage.setTitle("Prestiti Attivi");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            System.err.println("Errore nel caricamento della schermata prestiti: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * @brief Naviga verso la schermata di gestione del catalogo libri.
     */
    @FXML
    private void goToLibri() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/libri.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tableViewStudenti.getScene().getWindow();
            ControllerLibri controller = loader.getController();
            controller.setBibliotecario(this.bibliotecarioLoggato);
            stage.setMinWidth(900);
            stage.setMinHeight(600);
            stage.setScene(new Scene(root));
            stage.setTitle("Catalogo libri");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            System.err.println("Errore nel caricamento della schermata libri: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * @brief Apre il popup per l'inserimento di un nuovo studente.
     */
    @FXML
    private void openPopupStudente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/insertStudente.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Aggiungi Studente");
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.showAndWait();
            caricaDatiAllAvvio(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * @brief Apre il popup per la modifica dei dati di uno studente esistente.
     * @param studente Lo studente da modificare.
     */
    @FXML
    private void apriPopupModifica(Studente studente) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/insertStudente.fxml"));
            Parent root = loader.load();
            ControllerPopupStudenti controller = loader.getController();
            controller.setStudenteDaModificare(studente);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifica studente");
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.showAndWait(); 
            caricaDatiAllAvvio();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @brief Apre il popup per la cancellazione di uno studente.
     * @param studente Lo studente da eliminare.
     */
    @FXML
    private void apriPopupElimina(Studente studente) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/delete.fxml"));
            Parent root = loader.load();
            ControllerDelete controller = loader.getController();
            controller.setOggettoDaEliminare(studente);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Elimina studente");
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            caricaDatiAllAvvio(); 
        } catch (IOException e) {
            System.err.println("Errore caricamento popup delete: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @brief Apre il popup informativo con i dettagli e i prestiti dello studente.
     * @param studente Lo studente di cui visualizzare le info.
     */
    @FXML
    private void apriPopupInfo(Studente studente) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/infoStudenti.fxml"));
            Parent root = loader.load();
            ControllerInfo controller = loader.getController();
            controller.setStudente(studente);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Info studente");
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            caricaDatiAllAvvio(); 
        } catch (IOException e) {
            System.err.println("Errore caricamento popup info: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @brief Apre il popup per confermare il logout.
     */
    @FXML
    private void openPopupLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/logout.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Logout");
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL); 
            if (tableViewStudenti != null && tableViewStudenti.getScene() != null) {
                stage.initOwner(tableViewStudenti.getScene().getWindow());
            }
            stage.showAndWait();
        } catch (IOException e) {
            System.err.println("Errore nel caricamento del popup di logout: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * @brief Apre la schermata per il reset della password del bibliotecariə.
     */
    @FXML
    private void apriResetPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/resetPassword.fxml"));
            Parent root = loader.load();
            ControllerResetPassword controller = loader.getController();
            controller.setDatiUtente(this.bibliotecarioLoggato.getEmail()); 
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Reset Password");
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}