package Biblioteca.Controller;

import Biblioteca.Model.App;
import Biblioteca.Model.Database;
import Biblioteca.Model.Prestito;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ControllerPrestiti {
    
   
    private App model; 


    @FXML
    private TableView<Prestito> tablePrestiti;
    
    @FXML
    private TableColumn<Prestito, String> ISBN; 
    
    @FXML
    private TableColumn<Prestito, String> Matricola;
    
    @FXML
    private TableColumn<Prestito, LocalDate> Data;

    @FXML
    private TableColumn<Prestito, Void> azioni;

    private ObservableList<Prestito> listaPrestiti = FXCollections.observableArrayList(); 

    public ControllerPrestiti() {
    }

    public void setModel(App model) {
        this.model = model;
    }

    @FXML
    public void initialize() {
        configuraTabella();
        caricaDatiAllAvvio();
        aggiungiBottoniAzioni();
    }

    private void configuraTabella() {
        
        ISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        Matricola.setCellValueFactory(new PropertyValueFactory<>("matricola"));
        Data.setCellValueFactory(new PropertyValueFactory<>("dataFinePrevista")); // O "dataFine" a seconda del tuo Model
        
        tablePrestiti.setEditable(true);
    }

    void caricaDatiAllAvvio() {
        try {
           
            tablePrestiti.getItems().clear();

           
            List<Prestito> prestitiSalvati = Database.leggiDatabasePrestiti(); 
            System.out.println(prestitiSalvati);
           
            if (prestitiSalvati != null && !prestitiSalvati.isEmpty()) {
                
               
                listaPrestiti = FXCollections.observableArrayList(prestitiSalvati);
                
              
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

          
            Stage stage = (Stage) tablePrestiti.getScene().getWindow();
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
    
    @FXML
    private void goToStudenti() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/studenti.fxml"));
            Parent root = loader.load();

  
            Stage stage = (Stage) tablePrestiti.getScene().getWindow();
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



    @FXML
    private void openPopupNuovoPrestito() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/insertPrestito.fxml"));
            Parent root = loader.load();

           
           

            Stage stage = new Stage();
            stage.setTitle("Nuovo Prestito");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.showAndWait();

     
            caricaDatiAllAvvio(); 

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void aggiungiBottoniAzioni() {
       
        Callback<TableColumn<Prestito, Void>, TableCell<Prestito, Void>> cellFactory = new Callback<TableColumn<Prestito, Void>, TableCell<Prestito, Void>>() {
            @Override
            public TableCell<Prestito, Void> call(final TableColumn<Prestito, Void> param) {
                return new TableCell<Prestito, Void>() {

                   
                    private final Button btnRestituisci = new Button("Restituito"); 
                    
                    private final HBox pane = new HBox(10, btnRestituisci);

                    {
                        pane.setAlignment(Pos.CENTER);

                       
                            Image imgCheck = new Image(getClass().getResourceAsStream("/Biblioteca/icons/check.png"));
                            ImageView viewCheck = new ImageView(imgCheck);
                            viewCheck.setFitHeight(18);
                            viewCheck.setFitWidth(18);
                            btnRestituisci.setGraphic(viewCheck);
                            btnRestituisci.setTooltip(new javafx.scene.control.Tooltip("Termina Prestito (Restituisci)"));

                      
                        String style = "-fx-background-color: #DE1616; -fx-cursor: hand; -fx-text-color: white;";
                        btnRestituisci.setStyle(style);
                        
                       
                        btnRestituisci.setOnAction(event -> {
                            Prestito prestito = getTableView().getItems().get(getIndex());
                            terminaPrestito(prestito);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(pane);
                        }
                    }
                };
            }
        };

        azioni.setCellFactory(cellFactory);
    }
    
    private void terminaPrestito(Prestito prestito) {

       
        ;
    }
}