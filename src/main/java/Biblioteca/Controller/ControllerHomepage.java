package Biblioteca.Controller;

import Biblioteca.Model.Database;
import Biblioteca.Model.Libro;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @file ControllerHomepage.java
 * @brief Controller per la gestione della schermata principale della biblioteca.
 * Classe gestisce l'interfaccia visualizzata all'avvio dell'applicazione
 * in cui è presente il catalogo e la ricerca dei libri e la possibilita di loggarsi
 * da parte del bibliotecariə
 * @author Mirko Montella
 * @author Achille Romano
 * @author Sabrina Soriano
 * @author Ciro Senese
 
 */

public class ControllerHomepage {

    /** @brief Tabella per la visualizzazione dei libri. */
    @FXML
    private TableView<Libro> tableViewBook;
    
    /** @brief Colonna per il codice ISBN del libro. */
    @FXML
    private TableColumn<Libro, Long> ISBN; 
    
    /** @brief Colonna per il titolo del libro. */
    @FXML
    private TableColumn<Libro, String> Titolo;
    
    /** @brief Colonna per l'autore del libro. */
    @FXML
    private TableColumn<Libro, String> Autore;
    
    /** @brief Colonna per l'anno di pubblicazione. */
    @FXML
    private TableColumn<Libro, Integer> Anno;
    
    /** @brief Colonna per il numero di copie disponibili. */
    @FXML
    private TableColumn<Libro, Integer> Copie_Disp;
    
    /** @brief Campo di testo per l'inserimento della query di ricerca. */
    @FXML
    private TextField searchBookTextField;
    
    /** @brief Lista osservabile contenente tutti i libri caricati dal database. */
    private ObservableList<Libro> listaLibri = FXCollections.observableArrayList(); 
    
    /** @brief Costruttore vuoto della classe. */
    public ControllerHomepage() {}
   
    /**
     * @brief Inizializzazione per leggere il database e popolare la tabella 
     * * @throws IOException In caso di errori nell'accesso al database.
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
     * @brief Mappa le colonne della TableView con gli attributi della classe Libro.
     */
    private void configuraTabella() {
        ISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        Titolo.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        Autore.setCellValueFactory(new PropertyValueFactory<>("autore"));
        Anno.setCellValueFactory(new PropertyValueFactory<>("annoPubblicazione"));
        Copie_Disp.setCellValueFactory(new PropertyValueFactory<>("numCopie"));
        
        tableViewBook.setEditable(true);
    }

    /**
     * @brief Legge la lista dei libri dal database per aggiornare la TableView.
     */
    public void caricaDatiAllAvvio() {
        try {
            List<Libro> libriSalvati = Database.leggiDatabaseLibri();
            
            if (libriSalvati != null && !libriSalvati.isEmpty()) {    
                listaLibri = FXCollections.observableArrayList(libriSalvati);
                tableViewBook.setItems(listaLibri);
                System.out.println("Tabella aggiornata con successo. Libri caricati: " + listaLibri.size());
            } else {
                System.out.println("Nessun libro trovato nel database JSON.");
            }
        } catch (IOException e) {
            System.err.println("Errore critico nel caricamento del database: " + e.getMessage());
        }
    }
    
    /**
     * @brief Gestisce la ricerca dei libri nel catalogo.
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

                boolean matchTitolo = titolo.contains(lowerCaseFilter);
                boolean matchAutore = autore.contains(lowerCaseFilter);
                boolean matchISBN = isbn.contains(lowerCaseFilter);

                if (matchTitolo || matchAutore || matchISBN) {
                    risultati.add(libro);
                }
            }
        } 
        tableViewBook.setItems(risultati);
    }
    
    /**
     * @brief Carica l'xml per passare alla schermata di login
     */
    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/login.fxml"));
            Parent root = loader.load();
            //Recupera lo stage precedente, (in questo caso lo fa attraverso la tableview, ma potrebbe farlo da qualsiasi altra cosa)
            Stage stage = (Stage) tableViewBook.getScene().getWindow();
            stage.setMinWidth(450); //non si può stringere la schermata oltre questi valori
            stage.setMinHeight(550);

            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            System.err.println("Errore nel caricamento della schermata login: " + e.getMessage());
        }
    }
}