package Biblioteca.Controller;

import Biblioteca.Model.App;
import Biblioteca.Model.Database;
import Biblioteca.Model.Libro;
import Biblioteca.Model.Prestito;
import Biblioteca.Model.Studente;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

/**
 *
 * @author Mirko Montella
 * @author Achille Romano 
 * @author Sabrina Soriano
 * @author Ciro Senese
 */
public class Controller {

    @FXML
    private TableView<Libro> tableViewBook; //tabella libro
    private TableView<Studente> tableViewStudent; //tabella studenti
    private TableView<Prestito> tableViewloan; //tabella studenti
    
    //Colonne tabella libro
    @FXML
    private TableColumn<Libro, String> ISBN;
    @FXML
    private TableColumn<Libro, String> Titolo;
    @FXML
    private TableColumn<Libro, String> Autore;
    @FXML
    private TableColumn<Libro, String> Anno;
    @FXML
    private TableColumn<Libro, String> Copie_Disp;
    @FXML
    private TableColumn<Libro, String> Azioni;
    
    
    //Colonne tabella studente
    @FXML
    private TableColumn<Studente, String> Nome;
    @FXML
    private TableColumn<Studente, String> Cognome;
    @FXML
    private TableColumn<Studente, String> Matricola;
    @FXML
    private TableColumn<Studente, String> E_mail;

    //Colonne tabella prestiti **DA IMPLEMENTARE**
    @FXML
    private TableColumn<Prestito, String> Libro;
    @FXML
    private TableColumn<Prestito, String> Matricola_prestito; 
    

    
    
    //Textfield ricerce
    @FXML
    private TextField searchBook_TextField; //libri
    @FXML
    private TextField searchStudent_TextField; //studenti
    @FXML
    private TextField searchLoan_TextField; //prestito
    
    //lista osservabile per aggiornare tabelle 
    private ObservableList<Libro> listaLibri; //libri
    private ObservableList<Studente> listaStudente; //studente
    private ObservableList<Prestito> listaPrestito; //prestiti
    
    @FXML
    private void initialize() throws IOException {
        // Collego le colonne ai getter di libri
        ISBN.setCellValueFactory(new PropertyValueFactory<Libro, String>("ISBN"));
        Titolo.setCellValueFactory(new PropertyValueFactory<Libro, String>("Titolo"));
        Autore.setCellValueFactory(new PropertyValueFactory<Libro, String>("Autore"));
        Anno.setCellValueFactory(new PropertyValueFactory<Libro, String>("Anno"));
        Copie_Disp.setCellValueFactory(new PropertyValueFactory<Libro, String>("Copie Disp."));
        Azioni.setCellValueFactory(new PropertyValueFactory<Libro, String>("Azioni"));
        
        // Collego le colonne ai getter di studeti
        Nome.setCellValueFactory(new PropertyValueFactory<Studente, String>("Nome"));
        Cognome.setCellValueFactory(new PropertyValueFactory<Studente, String>("Cognome"));
        Matricola.setCellValueFactory(new PropertyValueFactory<Studente, String>("Matricola"));
        E_mail.setCellValueFactory(new PropertyValueFactory<Studente, String>("E-mail"));
        
        // Collego le colonne ai getter di prestiti **DA IMPLLEMENTARE**
        Libro.setCellValueFactory(new PropertyValueFactory<Prestito, String>("Libro"));
        Matricola_prestito.setCellValueFactory(new PropertyValueFactory<Prestito, String>("Cognome"));
        
        
        // Rendo le TableView editabili
        tableViewBook.setEditable(true); //libri
        tableViewStudent.setEditable(true); //studenti
        tableViewloan.setEditable(true); //prestiti
        
        
        //collego database
        Database database=new Database();
        
        //Cerco libri
        List<Libro> TempLibri;
        TempLibri = database.leggiDatabaseLibri();

        //Converto la lista standard in ObservableList per JavaFX
        listaLibri = FXCollections.observableArrayList(TempLibri);

        //Inserisco nella tabella
        tableViewBook.setItems(listaLibri);
        
        //Cerco studente
        List<Studente> TempStudent;
        TempStudent = database.leggiDatabaseStudenti();

        //Converto la lista standard in ObservableList per JavaFX
        listaStudente = FXCollections.observableArrayList(TempStudent);

        //Inserisco nella tabella
        tableViewStudent.setItems(listaStudente);

        //Cerco prestito
        List<Prestito> TempPrestiti;
        TempPrestiti = database.leggiDatabasePrestiti();

        //Converto la lista standard in ObservableList per JavaFX
        listaPrestito = FXCollections.observableArrayList(TempPrestiti);

        //Inserisco nella tabella
        tableViewloan.setItems(listaPrestito);

    }
    
    @FXML
    private void onSearchBook() throws IOException {
        String text;
        text=searchBook_TextField.getText();
        Long search = Long.valueOf(text);
        Libro l;


        if (l.ricercaLibroISBN(search) != -1) {
            //TROVATO TRAMITE ISBN

        } else if (l.ricercaLibroAutore(text) != null) {
            //TROVATO TRAMITE AUTORE
            
        } else if (l.ricercaLibroTitolo(text) != null) {
            //TROVATO TRAMITE TITOLO

        } else {
            //NESSUN RISULTATO
            System.out.println("Errore: libro non trovato"); //label da implementare
        }
            
    }
    


    @FXML
    private void onSearchStudent() {
        String text;
        text=searchStudent_TextField.getText();
        Studente s;


        if (s.ricercaStudenteMatricola(text) != -1) {
            //TROVATO TRAMITE MATRICOLA

        } else if (s.ricercaStudenteCognome(text) == null) {
            //TROVATO TRAMITE COGNOME
            
        } else {
            //NESSUN RISULTATO
            System.out.println("Errore: libro non trovato"); //label da implementare
        }
            
    }


    @FXML
    private void onSearchLoan() {
        String text;
        text=searchLoan_TextField.getText();
        Prestito p;
        //**DA IMPLEMENTARE FUNZIONI RICERCA**
    }  
    
}