/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biblioteca.Controller;

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
 *
 * @author achil
 */

public class ControllerStudenti {

    @FXML
    private TableView<Studente> tableViewStudenti;
   
    @FXML
    private TableColumn<Studente, String> Nome; 
    
    @FXML
    private TableColumn<Studente, String> Cognome;
    
    @FXML
    private TableColumn<Studente, String> Matricola;
    
    @FXML
    private TableColumn<Studente, String> Email;
    
    @FXML private TableColumn<Studente, HBox> Azioni; 
    
    @FXML
    private TextField searchStudentTextField;
    
   
    

    private ObservableList<Studente> listaStudente= FXCollections.observableArrayList(); 

   
    public ControllerStudenti() {
    }

 
    @FXML
    public void initialize() {
        configuraTabella();
        caricaDatiAllAvvio();
    }

    private void configuraTabella() {
        
        // Libro.java: public long getIsbn() -> "isbn"
        Nome.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        
        // Libro.java: public String getTitolo() -> "titolo"
        Cognome.setCellValueFactory(new PropertyValueFactory<>("Cognome"));
        
        // Libro.java: public String getAutore() -> "autore"
        Matricola.setCellValueFactory(new PropertyValueFactory<>("Matricola"));
        
        // Libro.java: public int getAnnoPubblicazione() -> "annoPubblicazione"
        Email.setCellValueFactory(new PropertyValueFactory<>("E_mail"));
        
        Azioni.setCellValueFactory(new PropertyValueFactory<>("azioni"));
        
        tableViewStudenti.setEditable(true);
    }

    void caricaDatiAllAvvio() {
        try {
           
            Database database = new Database();
            
          
            List<Studente> studenteSalvati = database.leggiDatabaseStudenti();
            
           
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
                
                System.out.println("Tabella aggiornata con successo. Libri caricati: " + listaStudente.size());
            } else {
                System.out.println("Nessun libro trovato nel database JSON.");
            }

        } catch (IOException e) {
            System.err.println("Errore critico nel caricamento del database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
     @FXML
    private void onSearchStudent() {
        
       String searchText = searchStudentTextField.getText();
         System.out.println("test");
       
        if (searchText == null || searchText.trim().isEmpty()) {
            tableViewStudenti.setItems(listaStudente);
            return;
        }
        
        String lowerCaseFilter = searchText.toLowerCase();
        
       
        ObservableList<Studente> risultati = FXCollections.observableArrayList();
        
        if (listaStudente != null) {
          
            for (Studente studente : listaStudente) {
               String cognome = (studente.getCognome() != null) ? studente.getNome().toLowerCase() : "";
                String matricola = (studente.getMatricola() != null) ? studente.getMatricola().toLowerCase() : "";

                boolean matchCognome = cognome.contains(lowerCaseFilter);
                boolean matchMatricola = matricola.contains(lowerCaseFilter);

                if (matchCognome || matchMatricola) {
                    risultati.add(studente);
                }
            }
            }
        
        
      
        tableViewStudenti.setItems(risultati);
    }
    @FXML
    private void goToPrestiti() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/prestiti.fxml"));
            Parent root = loader.load();

            // recupera lo stage precedente, (in questo caso lo fa attraverso la table view, ma potrebbe farlo da qualsiasi altra cosa)
            Stage stage = (Stage) tableViewStudenti.getScene().getWindow();

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
    private void goToLibri() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/libri.fxml"));
            Parent root = loader.load();

            // recupera lo stage precedente, (in questo caso lo fa attraverso la table view, ma potrebbe farlo da qualsiasi altra cosa)
            Stage stage = (Stage) tableViewStudenti.getScene().getWindow();

            stage.setMinWidth(900);  // non si puo stringere la schermata oltre questi valori
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
            stage.showAndWait();


        } catch (IOException e) {
            System.err.println("Errore nel caricamento del popup di logout: " + e.getMessage());
            e.printStackTrace();
        }
    }
}