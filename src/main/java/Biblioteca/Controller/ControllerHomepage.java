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

public class ControllerHomepage {

    @FXML
    private TableView<Libro> tableViewBook;
    
   
    @FXML
    private TableColumn<Libro, Long> ISBN; 
    
    @FXML
    private TableColumn<Libro, String> Titolo;
    
    @FXML
    private TableColumn<Libro, String> Autore;
    
    @FXML
    private TableColumn<Libro, Integer> Anno;
    
    @FXML
    private TableColumn<Libro, Integer> Copie_Disp;
    
    @FXML
    private TextField searchBookTextField;
    
    private ObservableList<Libro> listaLibri = FXCollections.observableArrayList(); 
    
    public ControllerHomepage() {}
   
    @FXML
    public void initialize() throws IOException {
        Database.creaDatabase();
        configuraTabella();
        caricaDatiAllAvvio();
        
        searchBookTextField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                onSearchBook(); // Esegui la ricerca
            }
        });
    }

    private void configuraTabella() {
        // Libro.java: public long getIsbn() -> "isbn"
        ISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        
        // Libro.java: public String getTitolo() -> "titolo"
        Titolo.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        
        // Libro.java: public String getAutore() -> "autore"
        Autore.setCellValueFactory(new PropertyValueFactory<>("autore"));
        
        // Libro.java: public int getAnnoPubblicazione() -> "annoPubblicazione"
        Anno.setCellValueFactory(new PropertyValueFactory<>("annoPubblicazione"));
        
        // Libro.java: public int getnumCopie() -> "numCopie"
        Copie_Disp.setCellValueFactory(new PropertyValueFactory<>("numCopie"));
        
        tableViewBook.setEditable(true);
    }

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
    
    @FXML
    private void goToLogin() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/login.fxml"));
            Parent root = loader.load();

            // recupera lo stage precedente, (in questo caso lo fa attraverso la tableviw, ma potrebbe farlo da qualsiasi altra cosa)
            Stage stage = (Stage) tableViewBook.getScene().getWindow();

            stage.setMinWidth(450);  // non si puo stringere la schermata oltre questi valori
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