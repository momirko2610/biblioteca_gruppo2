package Biblioteca.Controller;

import Biblioteca.Model.App;
import Biblioteca.Model.Database;
import static Biblioteca.Model.Database.leggiDatabaseLibri;
import Biblioteca.Model.Libro;
import static Biblioteca.Model.Libro.ricercaLibroAutore;
import static Biblioteca.Model.Libro.ricercaLibroISBN;
import static Biblioteca.Model.Libro.ricercaLibroTitolo;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ControllerHomepage {

    
    private App model; 

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


    public ControllerHomepage() {
    }

   
    public void setModel(App model) {
        this.model = model;
    }

   
    @FXML
    public void initialize() {
        configuraTabella();
        caricaDatiAllAvvio();
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
           
            Database database = new Database();
            
            
            List<Libro> libriSalvati = database.leggiDatabaseLibri();
            
           
            if (libriSalvati != null && !libriSalvati.isEmpty()) {
                
                listaLibri = FXCollections.observableArrayList(libriSalvati);
                
                
                tableViewBook.setItems(listaLibri);
                
                System.out.println("Tabella aggiornata con successo. Libri caricati: " + listaLibri.size());
            } else {
                System.out.println("Nessun libro trovato nel database JSON.");
            }

        } catch (IOException e) {
            System.err.println("Errore critico nel caricamento del database: " + e.getMessage());
            e.printStackTrace();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/libri.fxml"));
            Parent root = loader.load();

            
            Stage stage = (Stage) searchBookTextField.getScene().getWindow();
            stage.setScene(new Scene(root, 1920, 1080));

            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            
            stage.show();
            
            
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("Errore caricamento login: " + e.getMessage());
            e.printStackTrace();
        }
    }


}