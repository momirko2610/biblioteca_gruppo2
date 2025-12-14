/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biblioteca.Controller;

import Biblioteca.Model.App;
import Biblioteca.Model.Database;
import Biblioteca.Model.Libro;
import Biblioteca.Model.Prestito;
import Biblioteca.Model.Studente;
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
import javafx.scene.control.TreeTableView;
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

public class ControllerInfo {
    private App model; 

    @FXML
    private TableView<Prestito> tableViewPrestito;
    
    @FXML
    private TableColumn<Prestito, Libro> ISBN; 
    
    @FXML
    private TableColumn<Prestito, LocalDate> DataPrestito;
    
   
    
    
    private ObservableList<Prestito> listaPrestito= FXCollections.observableArrayList(); 
    

   
    public ControllerInfo() {
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
        ISBN.setCellValueFactory(new PropertyValueFactory<>("Libro"));
        
        // Libro.java: public String getTitolo() -> "titolo"
        DataPrestito.setCellValueFactory(new PropertyValueFactory<>("DataInizio"));
    }

    void caricaDatiAllAvvio() {
        tableViewPrestito.setItems(listaPrestito);
    }
}
    
     