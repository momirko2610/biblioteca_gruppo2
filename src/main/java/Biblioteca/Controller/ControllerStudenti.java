/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biblioteca.Controller;

import Biblioteca.Model.App;
import Biblioteca.Model.Database;
import Biblioteca.Model.Libro;
import Biblioteca.Model.Studente;
import java.io.IOException;
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

/**
 *
 * @author achil
 */

public class ControllerStudenti {
    private App model; 

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
    
    @FXML
    private TableColumn<Studente, Void> Azioni;
    
    @FXML
    private TextField searchStudentTextField;
    
   
    

    private ObservableList<Studente> listaStudente= FXCollections.observableArrayList(); 

   
    public ControllerStudenti() {
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
        
        // Libro.java: public long getIsbn() -> "isbn"
        Nome.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        
        // Libro.java: public String getTitolo() -> "titolo"
        Cognome.setCellValueFactory(new PropertyValueFactory<>("Cognome"));
        
        // Libro.java: public String getAutore() -> "autore"
        Matricola.setCellValueFactory(new PropertyValueFactory<>("Matricola"));
        
        // Libro.java: public int getAnnoPubblicazione() -> "annoPubblicazione"
        Email.setCellValueFactory(new PropertyValueFactory<>("E_mail"));
        
        tableViewStudenti.setEditable(true);
    }

    void caricaDatiAllAvvio() {
        try {
           
            Database database = new Database();
            
          
            List<Studente> studenteSalvati = database.leggiDatabaseStudenti();
            
           
            if (studenteSalvati != null && !studenteSalvati.isEmpty()) {
              
                listaStudente= FXCollections.observableArrayList(studenteSalvati);
                
                
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

            
            Stage stage = (Stage) searchStudentTextField.getScene().getWindow();
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
    private void goToLibri() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/libri.fxml"));
            Parent root = loader.load();

      
            Stage stage = (Stage) searchStudentTextField.getScene().getWindow();
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
    
    
    private void aggiungiBottoniAzioni() {
         TableColumn<Studente, Void> colBtn = new TableColumn<>("Azioni");

        Callback<TableColumn<Studente, Void>, TableCell<Studente, Void>> cellFactory = new Callback<TableColumn<Studente, Void>, TableCell<Studente, Void>>() {
            @Override
            public TableCell<Studente, Void> call(final TableColumn<Studente, Void> param) {
                return new TableCell<Studente, Void>() {

                    private final Button btnModifica = new Button(""); 
                    private final Button btnElimina = new Button("");
                    private final Button btnInfo = new Button("");
                    private final HBox pane = new HBox(9, btnModifica, btnElimina, btnInfo);

                    {
                        pane.setAlignment(Pos.CENTER);

                       
                        try {
                            
                            Image imgEdit = new Image(getClass().getResourceAsStream("/Biblioteca/icons/pencil-fiiled.png"));
                            ImageView viewEdit = new ImageView(imgEdit);
                            viewEdit.setFitHeight(15);
                            viewEdit.setFitWidth(15);
                            btnModifica.setGraphic(viewEdit);

                           
                            Image imgDel = new Image(getClass().getResourceAsStream("/Biblioteca/icons/trash-filled.png"));
                            ImageView viewDel = new ImageView(imgDel);
                            viewDel.setFitHeight(15);
                            viewDel.setFitWidth(15);
                            btnElimina.setGraphic(viewDel);
                            
                           
                            Image imgInfo = new Image(getClass().getResourceAsStream("/Biblioteca/icons/info.png"));
                            ImageView viewInfo = new ImageView(imgInfo);
                            viewInfo.setFitHeight(15);
                            viewInfo.setFitWidth(15);
                            btnInfo.setGraphic(viewInfo);

                        } catch (Exception e) {
                            
                            btnModifica.setText("Mod");
                            btnElimina.setText("Eli");
                        }

                       
                        btnModifica.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                        btnElimina.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                        btnInfo.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                        
                      
                        btnModifica.setOnAction(event -> {
                            Studente studenteCorrente = getTableView().getItems().get(getIndex());
                            if (studenteCorrente != null) {
                                apriPopupModifica(studenteCorrente); 
                            }
                        });

                        // AZIONE ELIMINA
                        btnElimina.setOnAction(event -> {
                            Studente studenteCorrente = getTableView().getItems().get(getIndex());
                            if (studenteCorrente != null) {
                                apriPopupElimina(studenteCorrente); 
                            }
                        });
                        
                        btnInfo.setOnAction(event -> {
                            Studente studenteCorrente = getTableView().getItems().get(getIndex());
                            if (studenteCorrente != null) {
                                apriPopupInfo(studenteCorrente); 
                            }
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

        colBtn.setCellFactory(cellFactory);


        tableViewStudenti.getColumns().add(colBtn);
    }
    
    @FXML
    private void openPopupDelete() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/libri.fxml"));
            Parent root = loader.load();


            Stage stage = (Stage) searchStudentTextField.getScene().getWindow();
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
    private void openPopupStudente() {
        try {
       
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/insertStudente.fxml"));
        Parent root = loader.load();

       
        Stage stage = new Stage();
        stage.setTitle("Aggiungi Studente");
        stage.setScene(new Scene(root));
        
       
        stage.initModality(Modality.APPLICATION_MODAL); 
        stage.showAndWait();

      
        caricaDatiAllAvvio(); 

    } catch (IOException e) {
        e.printStackTrace();
    }
    }
    
 
    private void apriPopupModifica(Studente studente) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/insertStudente.fxml"));
            Parent root = loader.load();

            
            ControllerPopupStudenti controller = loader.getController();
            
           
            controller.setStudenteDaModificare(studente);

            Stage stage = new Stage();
            stage.setTitle("Modifica studente");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.showAndWait(); 

           
            caricaDatiAllAvvio();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

private void apriPopupElimina(Studente studente) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/delete.fxml"));
        Parent root = loader.load();

        ControllerDelete controller = loader.getController();
        

        controller.setOggettoDaEliminare(studente);

        Stage stage = new Stage();
        stage.setTitle("Elimina studente");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        
      
        stage.showAndWait();

       
        caricaDatiAllAvvio(); 

    } catch (IOException e) {
        System.err.println("Errore caricamento popup delete: " + e.getMessage());
        e.printStackTrace();
    }
    }

private void apriPopupInfo(Studente studente) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/infoStudenti.fxml"));
        Parent root = loader.load();

        ControllerInfo controller = loader.getController();
        
      

        Stage stage = new Stage();
        stage.setTitle("Info studente");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        
     
        stage.showAndWait();

        caricaDatiAllAvvio(); 

    } catch (IOException e) {
        System.err.println("Errore caricamento popup info: " + e.getMessage());
        e.printStackTrace();
    }
    }
}