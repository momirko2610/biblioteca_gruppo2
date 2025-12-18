package Biblioteca.Controller;

import Biblioteca.Model.Bibliotecario;
import Biblioteca.Model.Database;
import Biblioteca.Model.Libro;
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
 * @file ControllerLibri.java
 * @brief Controller che gestisce le funzionalità extra del catalogo libri da parte del bibliotecariə.
 * (inserimento, modifica, cancellazione e ricerca)
 * @author Mirko Montella
 * @author Achille Romano
 * @author Sabrina Soriano
 * @author Ciro Senese
 */
public class ControllerLibri {

    /** @brief Tabella per la visualizzazione dei libri gestiti. */
    @FXML
    private TableView<Libro> tableViewBook;

    /** @brief Colonna per il codice ISBN. */
    @FXML
    private TableColumn<Libro, Long> ISBN; 
    
    /** @brief Colonna per il Titolo del libro. */
    @FXML
    private TableColumn<Libro, String> Titolo;
    
    /** @brief Colonna per gli Autori. */
    @FXML
    private TableColumn<Libro, String> Autore;
    
    /** @brief Colonna per l'Anno di pubblicazione. */
    @FXML
    private TableColumn<Libro, Integer> Anno;
    
    /** @brief Colonna per il Numero di copie disponibili. */
    @FXML
    private TableColumn<Libro, Integer> Copie_Disp;
    
    /** @brief Campo di testo per la query di ricerca. */
    @FXML
    private TextField searchBookTextField;
   
    /** @brief Colonna contenente i pulsanti di azione (Modifica/Elimina) per ogni riga. */
    @FXML 
    private TableColumn<Libro, HBox> Azioni;  

    /** @brief Lista osservabile dei libri caricati nel sistema. */
    private ObservableList<Libro> listaLibri = FXCollections.observableArrayList(); 
    
    /** @brief Riferimento al bibliotecario attualmente autenticato. */
    private Bibliotecario bibliotecarioLoggato;

    /**
     * @brief Imposta l'istanza del bibliotecario loggato.
     * @param bibliotecario Istanza del bibliotecario autenticato.
     */
    public void setBibliotecario(Bibliotecario bibliotecario) {
        this.bibliotecarioLoggato = bibliotecario;
    }

    /** @brief Costruttore vuoto. */
    public ControllerLibri() {
    }

    /**
     * @brief Inizializza l'interfaccia, configurando la tabella e caricando i dati.
     * @throws IOException In caso di errori nell'accesso al database.
     */
    @FXML
    public void initialize() throws IOException {
        Database.creaDatabase();
        configuraTabella();
        caricaDatiAllAvvio();
        
        searchBookTextField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                onSearchBook();
            }
        });
    }

    /**
     * @brief Mappa le colonne della tabella e le proprietà del Libro.
     */
    private void configuraTabella() {
        ISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        Titolo.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        Autore.setCellValueFactory(new PropertyValueFactory<>("autore"));
        Anno.setCellValueFactory(new PropertyValueFactory<>("annoPubblicazione"));
        Copie_Disp.setCellValueFactory(new PropertyValueFactory<>("numCopie"));
        Azioni.setCellValueFactory(new PropertyValueFactory<>("azioni"));
        
        tableViewBook.setEditable(true);
    }

    /**
     * @brief Carica l'elenco dei libri dal database e inizializza i pulsanti di azione.
     */
    void caricaDatiAllAvvio() {
        try {    
            List<Libro> libriSalvati = Database.leggiDatabaseLibri();

            if (libriSalvati != null && !libriSalvati.isEmpty()) {
                listaLibri = FXCollections.observableArrayList(libriSalvati);
                
                for (Libro libro : listaLibri) {
                    HBox box = libro.getAzioni(); 
                        
                    Button Modifica = (Button) box.getChildren().get(0);
                    Button Elimina = (Button) box.getChildren().get(1);

                    Modifica.setOnAction(event -> { apriPopupModifica(libro); });
                    Elimina.setOnAction(event -> { apriPopupElimina(libro); });
                }
                
                System.out.println("Tabella aggiornata con successo. Libri caricati: " + listaLibri.size());
            } else {
                System.out.println("Nessun libro trovato nel database JSON.");
            }
            tableViewBook.setItems(listaLibri);
        } catch (IOException e) {
            System.err.println("Errore critico nel caricamento del database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * @brief Gestisce la ricerca dei libri per Titolo, Autore o ISBN in modalità case-insensitive. 
     */
    @FXML
    private void onSearchBook() {
       String searchText = searchBookTextField.getText();

        if (searchText == null || searchText.trim().isEmpty()) {
            tableViewBook.setItems(listaLibri);
            return;
        }
        
        String lowerCaseFilter = searchText.toLowerCase();
        ObservableList<Libro> risultati = FXCollections.observableArrayList();
        
        if (listaLibri != null) {
            for (Libro libro : listaLibri) {
                String titolo = (libro.getTitolo() != null) ? libro.getTitolo().toLowerCase() : "";
                String autore = (libro.getAutore() != null) ? libro.getAutore().toLowerCase() : "";
                String isbn = String.valueOf(libro.getIsbn());

                if (titolo.contains(lowerCaseFilter) || autore.contains(lowerCaseFilter) || isbn.contains(lowerCaseFilter)) {
                    risultati.add(libro);
                }
            }
        }
        tableViewBook.setItems(risultati);
    }

    /**
     * @brief Naviga verso la schermata di gestione dei Prestiti.
     */
    @FXML
    private void goToPrestiti() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/prestiti.fxml"));
            Parent root = loader.load();
            //Recupera lo stage precedente, (in questo caso lo fa attraverso la tableview, ma potrebbe farlo da qualsiasi altra cosa)
            Stage stage = (Stage) tableViewBook.getScene().getWindow();
            
            ControllerPrestiti controller = loader.getController();
            //Passiamo il bibliotecariə per il logout e il reset password
            controller.setBibliotecario(this.bibliotecarioLoggato);

            stage.setMinWidth(900); //non si può stringere la schermata oltre questi valori 
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
     * @brief Naviga verso la schermata di gestione degli Studenti.
     */
    @FXML
    private void goToStudenti() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/studenti.fxml"));
            Parent root = loader.load();
            //Recupera lo stage precedente, (in questo caso lo fa attraverso la tableview, ma potrebbe farlo da qualsiasi altra cosa)
            Stage stage = (Stage) tableViewBook.getScene().getWindow();
            
            ControllerStudenti controller = loader.getController();
            //Passiamo il bibliotecariə per il logout e il reset password
            controller.setBibliotecario(this.bibliotecarioLoggato);
            
            stage.setMinWidth(900); //non si può stringere la schermata oltre questi valori 
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
     * @brief Apre il popup per l'inserimento di un nuovo libro
     */
    @FXML
    private void openPopupLibro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/insertLibro.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Aggiungi Libro");
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
     * @brief Apre il popup per la modifica di un libro esistente
     * @param libro Il libro da modificare.
     */
    @FXML
    private void apriPopupModifica(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/insertLibro.fxml"));
            Parent root = loader.load();
            ControllerPopupLibro controller = loader.getController();
            controller.setLibroDaModificare(libro);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifica Libro");
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
     * @brief Apre il popup di conferma per l'eliminazione di un libro
     * @param libro il libro da rimuovere.
     */
    @FXML
    private void apriPopupElimina(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/delete.fxml"));
            Parent root = loader.load();
            ControllerDelete controller = loader.getController();       
            controller.setOggettoDaEliminare(libro);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Elimina Libro");
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            
            if (tableViewBook != null && tableViewBook.getScene() != null) {
                stage.initOwner(tableViewBook.getScene().getWindow());
            }
            stage.showAndWait();
            caricaDatiAllAvvio();

        } catch (IOException e) {
            System.err.println("Errore caricamento popup delete: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @brief Apre il popup di conferma Logout.
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
            stage.initModality(Modality.APPLICATION_MODAL); //blocca la finestra sotto  
            stage.initOwner(tableViewBook.getScene().getWindow());
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