package Biblioteca.Controller;

import Biblioteca.Model.Bibliotecario;
import Biblioteca.Model.Database;
import Biblioteca.Model.Prestito;
import java.io.IOException;
import java.time.LocalDate;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @file ControllerPrestiti.java
 * @brief Controller che gestisce e visualizza i prestiti attivi.
 * @author Mirko Montella
 * @author Achille Romano
 * @author Sabrina Soriano
 * @author Ciro Senese
 */
public class ControllerPrestiti {

    /** @brief Tabella per la visualizzazione dei prestiti registrati nel sistema. */
    @FXML
    private TableView<Prestito> tablePrestiti;
    
    /** @brief Colonna della tabella per il codice ISBN del libro in prestito. */
    @FXML
    private TableColumn<Prestito, String> ISBN; 
    
    /** @brief Colonna della tabella per la matricola dello studente che ha richiesto il prestito. */
    @FXML
    private TableColumn<Prestito, String> Matricola;
    
    /** @brief Colonna della tabella per la data di restituzione prevista. */
    @FXML
    private TableColumn<Prestito, LocalDate> Data;

    /** @brief Colonna per ospitare i pulsanti di azione (es. Ritorno) per ogni riga. */
    @FXML private TableColumn<Prestito, HBox> Azioni; 

    /** @brief Lista osservabile dei prestiti caricati dal database. */
    private ObservableList<Prestito> listaPrestiti = FXCollections.observableArrayList(); 
    
    /** @brief Riferimento al bibliotecario attualmente loggato nel sistema. */
    private Bibliotecario bibliotecarioLoggato;
    
    /**
     * @brief Imposta il bibliotecario loggato per la sessione corrente.
     * @param bibliotecario Bibliotecario autenticato.
     */
    public void setBibliotecario(Bibliotecario bibliotecario) {
        this.bibliotecarioLoggato = bibliotecario;
    }
    
    /** @brief Costruttore vuoto della classe. */
    public ControllerPrestiti() {
    }

    /**
     * @brief Inizializza il controller configurando la tabella e caricando i dati all'avvio.
     * @throws IOException In caso di errori nell'accesso al database.
     */
    @FXML
    public void initialize() throws IOException {
        Database.creaDatabase();
        configuraTabella();
        caricaDatiAllAvvio();
    }

    /**
     * @brief Mappa le colonne della TableView alla classe Prestito.
     */
    private void configuraTabella() {
        ISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        Matricola.setCellValueFactory(new PropertyValueFactory<>("matricola"));
        Data.setCellValueFactory(new PropertyValueFactory<>("dataFinePrevista")); 
        Azioni.setCellValueFactory(new PropertyValueFactory<>("azioni"));
        tablePrestiti.setEditable(true);
    }

    /**
     * @brief Carica l'elenco dei prestiti dal database e popola la tabella.
     */
    void caricaDatiAllAvvio() {
        try {  
            tablePrestiti.getItems().clear();
            List<Prestito> prestitiSalvati = Database.leggiDatabasePrestiti(); 
            System.out.println(prestitiSalvati);
           
            if (prestitiSalvati != null && !prestitiSalvati.isEmpty()) {
                listaPrestiti = FXCollections.observableArrayList(prestitiSalvati);

                for (Prestito prestito : listaPrestiti) {
                    HBox box = prestito.getAzioni(); 
                    Button Ritorno = (Button) box.getChildren().get(0);
                    // Associa l'azione di restituzione al pulsante (BF-13)
                    Ritorno.setOnAction(event -> { terminaPrestito(prestito); });
                }
                
                tablePrestiti.setItems(listaPrestiti);
                System.out.println("Tabella prestiti aggiornata. Record caricati: " + listaPrestiti.size());
            } else {
                System.out.println("Nessun prestito trovato nel database (o file vuoto).");
            }
        } catch (IOException e) {
            System.err.println("Errore critico caricamento database prestiti: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Errore generico durante il popolamento della tabella: " + e.getMessage());
            e.printStackTrace();
        }
    }
   
    /**
     * @brief Cambia la visualizzazione passando alla gestione del catalogo libri.
     */
    @FXML
    private void goToLibri() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/libri.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tablePrestiti.getScene().getWindow();
            ControllerLibri controller = loader.getController();
            controller.setBibliotecario(this.bibliotecarioLoggato);
            stage.setMinWidth(900);
            stage.setMinHeight(600);
            stage.setScene(new Scene(root));
            stage.setTitle("Gestione Libri");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            System.err.println("Errore nel caricamento della schermata libri: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * @brief Cambia la visualizzazione passando alla gestione dell'anagrafica studenti.
     */
    @FXML
    private void goToStudenti() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/studenti.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tablePrestiti.getScene().getWindow();
            ControllerStudenti controller = loader.getController();
            controller.setBibliotecario(this.bibliotecarioLoggato);
            stage.setMinWidth(900);
            stage.setMinHeight(600);
            stage.setScene(new Scene(root));
            stage.setTitle("Gestione Studenti");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            System.err.println("Errore nel caricamento della schermata studenti: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @brief Apre il popup per la registrazione di un nuovo prestito.
     */
    @FXML
    private void openPopupNuovoPrestito() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/insertPrestito.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Aggiungi Prestito");
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
     * @brief Apre il popup per gestire la restituzione di un libro.
     * @param prestito L'oggetto Prestito da terminare.
     */
    private void terminaPrestito(Prestito prestito) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/ritorno.fxml"));
            Parent root = loader.load();
            ControllerRitorno controller = loader.getController();
            controller.setDatiRestituzione(prestito.getMatricola(), prestito.getIsbn());
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Registra Restituzione");
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);          
            stage.showAndWait();
            caricaDatiAllAvvio(); 
        } catch (IOException e) {
            System.err.println("Errore nel caricamento del popup di ritorno: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * @brief Apre il popup per confermare il logout dell'utente.
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
            if (tablePrestiti != null && tablePrestiti.getScene() != null) {
                stage.initOwner(tablePrestiti.getScene().getWindow());
            }
            stage.showAndWait();
        } catch (IOException e) {
            System.err.println("Errore nel caricamento del popup di logout: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * @brief Apre la finestra per il reset della password del bibliotecario.
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