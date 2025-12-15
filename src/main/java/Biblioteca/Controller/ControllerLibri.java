/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biblioteca.Controller;

import Biblioteca.Model.App;
import Biblioteca.Model.Database;
import Biblioteca.Model.Libro;
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

public class ControllerLibri {
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
    private TableColumn<Libro, Void> Azioni;
    
    @FXML
    private TextField searchBookTextField;
    
   

    private ObservableList<Libro> listaLibri = FXCollections.observableArrayList(); 

    public ControllerLibri() {
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

    void caricaDatiAllAvvio() {
        try {
          
            Database database = new Database();
    
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


            Stage stage = (Stage) searchBookTextField.getScene().getWindow();
            stage.setScene(new Scene(root, 1920, 1080));

            //Scene scene = new Scene(root);
            //stage.setScene(scene);
            
 
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


            Stage stage = (Stage) searchBookTextField.getScene().getWindow();
            stage.setScene(new Scene(root, 1920, 1080));

            //Scene scene = new Scene(root);
            //stage.setScene(scene);
            
           
            stage.show();
            

            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("Errore caricamento login: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    private void aggiungiBottoniAzioni() {
        TableColumn<Libro, Void> colBtn = new TableColumn<>("Azioni");

        Callback<TableColumn<Libro, Void>, TableCell<Libro, Void>> cellFactory = new Callback<TableColumn<Libro, Void>, TableCell<Libro, Void>>() {
            @Override
            public TableCell<Libro, Void> call(final TableColumn<Libro, Void> param) {
                return new TableCell<Libro, Void>() {

                    private final Button btnModifica = new Button(""); 
                    private final Button btnElimina = new Button("");
                    private final HBox pane = new HBox(9, btnModifica, btnElimina);

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

                        } catch (Exception e) {
                           
                            btnModifica.setText("Mod");
                            btnElimina.setText("Eli");
                        }

                       
                        btnModifica.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                        btnElimina.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                        
                       
                        btnModifica.setOnAction(event -> {
                            Libro libroCorrente = getTableView().getItems().get(getIndex());
                            if (libroCorrente != null) {
                                apriPopupModifica(libroCorrente); 
                            }
                        });

                       
                        btnElimina.setOnAction(event -> {
                            Libro libroCorrente = getTableView().getItems().get(getIndex());
                            if (libroCorrente != null) {
                                apriPopupElimina(libroCorrente);
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


        tableViewBook.getColumns().add(colBtn);
    }
    
    @FXML
    private void openPopupDelete() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/libri.fxml"));
            Parent root = loader.load();

           
            Stage stage = (Stage) searchBookTextField.getScene().getWindow();
            stage.setScene(new Scene(root, 1920, 1080));

            //Scene scene = new Scene(root);
            //stage.setScene(scene);
            
           
            stage.show();
            

            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("Errore caricamento login: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void openPopupLibro() {
        try {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/insertLibro.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Aggiungi Libro");
        stage.setScene(new Scene(root));
        
        
       
        stage.initModality(Modality.APPLICATION_MODAL); 

        stage.showAndWait();


        caricaDatiAllAvvio(); 

    } catch (IOException e) {
        e.printStackTrace();
    }
    }
    
    
    private void apriPopupModifica(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/insertLibro.fxml"));
            Parent root = loader.load();

            
            ControllerPopupLibro controller = loader.getController();
            
            
            controller.setLibroDaModificare(libro);

            Stage stage = new Stage();
            stage.setTitle("Modifica Libro");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.showAndWait(); 


            caricaDatiAllAvvio();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

private void apriPopupElimina(Libro libro) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/delete.fxml"));
        Parent root = loader.load();

        ControllerDelete controller = loader.getController();
        
       
        controller.setOggettoDaEliminare(libro);

    

        Stage stage = new Stage();
        stage.setTitle("Elimina Libro");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        
    
        stage.showAndWait();


        caricaDatiAllAvvio(); 

    } catch (IOException e) {
        System.err.println("Errore caricamento popup delete: " + e.getMessage());
        e.printStackTrace();
    }
    }
}