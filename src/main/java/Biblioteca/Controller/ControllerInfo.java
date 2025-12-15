/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biblioteca.Controller;

import Biblioteca.Model.Database;
import Biblioteca.Model.Prestito;
import Biblioteca.Model.Studente;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 *
 *  @author Mirko Montella
 *  @author Achille Romano
 *  @author Sabrina Soriano
 *  @author Ciro Senese
 */

public class ControllerInfo {

    @FXML
    private TableView<Prestito> tableViewPrestito;

    @FXML
    private TableColumn<Prestito, String> ISBN; 

    @FXML
    private TableColumn<Prestito, LocalDate> DataPrestito;

    private ObservableList<Prestito> listaPrestito = FXCollections.observableArrayList();
    
    private Studente studenteSelezionato;

    @FXML
    public void initialize() throws IOException {
        Database.creaDatabase();
        ISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        
        DataPrestito.setCellValueFactory(new PropertyValueFactory<>("dataInizio"));
    }

    public void setStudente(Studente studente) {
        this.studenteSelezionato = studente;
        
        cercaPrestiti();
    }

    private void cercaPrestiti() {
        try {
            List<Prestito> tuttiIPrestiti = Database.leggiDatabasePrestiti();

            if (tuttiIPrestiti != null) {
                
                for (Prestito p : tuttiIPrestiti) {
                    
                    String matricolaPrestito = p.getMatricola();
                    String matricolaStudente = studenteSelezionato.getMatricola();

                    if (matricolaPrestito.equals(matricolaStudente)) {
                        listaPrestito.add(p);
                    }
                }
            }

            tableViewPrestito.setItems(listaPrestito);

        } catch (IOException e) {
            System.out.println("Errore caricamento dati");
        }
    }
    public void chiudi() {
        Stage stage = (Stage) tableViewPrestito.getScene().getWindow();
        stage.close();
    }
}