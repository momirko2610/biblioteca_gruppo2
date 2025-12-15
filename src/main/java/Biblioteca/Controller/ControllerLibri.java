/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biblioteca.Controller;

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
 *
 * @author achil
 */

public class ControllerLibri {

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
   
    @FXML private TableColumn<Libro, HBox> Azioni;  

    private ObservableList<Libro> listaLibri = FXCollections.observableArrayList(); 

    public ControllerLibri() {
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
        
        Azioni.setCellValueFactory(new PropertyValueFactory<>("azioni"));
        
        tableViewBook.setEditable(true);
    }

    void caricaDatiAllAvvio() {
        try {
          
            Database database = new Database();
    
            List<Libro> libriSalvati = database.leggiDatabaseLibri();
            

            if (libriSalvati != null && !libriSalvati.isEmpty()) {
                
                listaLibri = FXCollections.observableArrayList(libriSalvati);
                
                for (Libro libro : listaLibri) {
                    
                    HBox box = libro.getAzioni(); 
                        
                    Button Modifica = (Button) box.getChildren().get(0);
                    Button Elimina = (Button) box.getChildren().get(1);

                    Modifica.setOnAction(event -> { apriPopupModifica(libro); });

                    Elimina.setOnAction(event -> { apriPopupElimina(libro); });
                }
                
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
         System.out.println("test");
 
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
    private void goToPrestiti() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/prestiti.fxml"));
            Parent root = loader.load();

            // recupera lo stage precedente, (in questo caso lo fa attraverso la table view, ma potrebbe farlo da qualsiasi altra cosa)
            Stage stage = (Stage) tableViewBook.getScene().getWindow();

            stage.setMinWidth(900);  // non si puo stringere la schermata oltre questi valori
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
    
    @FXML
    private void goToStudenti() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/studenti.fxml"));
            Parent root = loader.load();

            // recupera lo stage precedente, (in questo caso lo fa attraverso la tableview, ma potrebbe farlo da qualsiasi altra cosa)
            Stage stage = (Stage) tableViewBook.getScene().getWindow();

            stage.setMinWidth(900);  // non si puo stringere la schermata oltre questi valori
            stage.setMinHeight(600);

            stage.setScene(new Scene(root));

            stage.setTitle("Prestiti Attivi");
            stage.centerOnScreen();

            stage.show();

        } catch (IOException e) {
            System.err.println("Errore nel caricamento della schermata studenti: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
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
            stage.showAndWait();

            caricaDatiAllAvvio(); 

        } catch (IOException e) {
            System.err.println("Errore caricamento popup delete: " + e.getMessage());
            e.printStackTrace();
        }
    }

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

                stage.initModality(Modality.APPLICATION_MODAL); // Blocca la finestra sotto     
                stage.initOwner(tableViewBook.getScene().getWindow());

                stage.showAndWait();


            } catch (IOException e) {
                System.err.println("Errore nel caricamento del popup di logout: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }