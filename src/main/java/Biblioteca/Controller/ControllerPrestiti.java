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

public class ControllerPrestiti {

    @FXML
    private TableView<Prestito> tablePrestiti;
    
    @FXML
    private TableColumn<Prestito, String> ISBN; 
    
    @FXML
    private TableColumn<Prestito, String> Matricola;
    
    @FXML
    private TableColumn<Prestito, LocalDate> Data;

    @FXML private TableColumn<Prestito, HBox> Azioni; 

    private ObservableList<Prestito> listaPrestiti = FXCollections.observableArrayList(); 
    
    private Bibliotecario bibliotecarioLoggato;
    
    public void setBibliotecario(Bibliotecario bibliotecario) {
        this.bibliotecarioLoggato = bibliotecario;
    }
    
    public ControllerPrestiti() {
    }

    @FXML
    public void initialize() throws IOException {
        Database.creaDatabase();
        configuraTabella();
        caricaDatiAllAvvio();
    }

    private void configuraTabella() {
        
        ISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        Matricola.setCellValueFactory(new PropertyValueFactory<>("matricola"));
        Data.setCellValueFactory(new PropertyValueFactory<>("dataFinePrevista")); 
        
        Azioni.setCellValueFactory(new PropertyValueFactory<>("azioni"));
        
        tablePrestiti.setEditable(true);
    }

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
   
    
    @FXML
    private void goToLibri() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/libri.fxml"));
            Parent root = loader.load();

            // recupera lo stage precedente, (in questo caso lo fa attraverso la tableview, ma potrebbe farlo da qualsiasi altra cosa)
            Stage stage = (Stage) tablePrestiti.getScene().getWindow();

            stage.setMinWidth(900);  // non si puo stringere la schermata oltre questi valori
            stage.setMinHeight(600);

            stage.setScene(new Scene(root));

            stage.setTitle("Prestiti Attivi");
            stage.centerOnScreen();

            stage.show();

        } catch (IOException e) {
            System.err.println("Errore nel caricamento della schermata libri: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void goToStudenti() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/studenti.fxml"));
            Parent root = loader.load();

            // recupera lo stage precedente, (in questo caso lo fa attraverso la tableview, ma potrebbe farlo da qualsiasi altra cosa)
            Stage stage = (Stage) tablePrestiti.getScene().getWindow();

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
    
    private void terminaPrestito(Prestito prestito) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/ritorno.fxml"));
            Parent root = loader.load();
            
            ControllerRitorno controller = loader.getController();
            
            controller.setDatiRestituzione(prestito.getMatricola(), prestito.getIsbn());
            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            stage.setTitle("Logout");
            stage.centerOnScreen();
            stage.setResizable(false);
            
            stage.initModality(Modality.APPLICATION_MODAL); // Blocca la finestra sotto          
            stage.showAndWait();

            caricaDatiAllAvvio(); 
            
        } catch (IOException e) {
            System.err.println("Errore nel caricamento del popup di logout: " + e.getMessage());
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
    
    @FXML
        private void apriResetPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/resetPassword.fxml"));
            Parent root = loader.load();

            ControllerResetPassword controller = loader.getController();
            controller.setDatiUtente(this.bibliotecarioLoggato.getEmail()); 

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            
            stage.setTitle("Logout");
            stage.centerOnScreen();
            stage.setResizable(false);
                
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}