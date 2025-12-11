package Biblioteca.Model;



//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.stage.Stage;

//import java.io.IOException;

//import com.google.gson.*;
//import java.io.FileWriter;
import java.io.IOException;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * JavaFX App
 */

public class App extends Application {

    static void setRoot(String secondary) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void start(Stage stage) {
        //Crea il database se non esiste
        try {
            Database.creaDatabase();
        } catch (IOException exception1) {
            exception1.printStackTrace();
            return; // esce se non riesce a creare il database
        }
        
        //Homepage
        VBox root = new VBox(10);
        
        //Opzione inserisci libri
        Button enterBook = new Button("Inserisci Libro");
        //Opzione cerca un libro specifico
        Button findBook = new Button("Cerca Libro");
        //Opzione visualizza l'elenco di tutti i libri presenti nel database
        Button listBook = new Button("Visualizza Lista Libri");
        //Opzione visualizza l'elenco di tutti i libri presenti nel database
        Button deleteBook = new Button("Elimina Libro");
        
        //Aggiunge all'home page solo il bottone inserisci libri
        root.getChildren().addAll(enterBook, findBook, listBook,deleteBook);
     
        //Quando premo il bottone inserisci libro
        enterBook.setOnAction(e -> {
            //Pulisco la schermata
            root.getChildren().clear();
            
            //Creo le label
            Label instruction = new Label("Inserisci TITOLO e AUTORE e premi Conferma:");
            Label instruction1 = new Label("TITOLO:");
            Label instruction2 = new Label("AUTORE:");
            Label instruction3 = new Label("ANNO DI PUBBLICAZIONE:");
            Label instruction4 = new Label("ISBN:");
            
            //Creo i campi per inserire i valori
            TextField tf1 = new TextField();
            tf1.setPromptText("Scrivi titolo...");
        
            TextField tf2 = new TextField();
            tf2.setPromptText("Scrivi autore...");
            
            TextField tf3 = new TextField();
            tf3.setPromptText("Scrivi anno di pubblicazione...");
        
            TextField tf4 = new TextField();
            tf4.setPromptText("Scrivi ISBN...");

            Button confirm = new Button("Conferma");
            
            //Quando premo conferma
            confirm.setOnAction(event -> {
                String titolo = tf1.getText();
                String autore = tf2.getText();
                int annoPubblicazione = Integer.valueOf(tf3.getText());
                int ISBN = Integer.valueOf(tf4.getText());

                Libro libro = new Libro(titolo, autore, annoPubblicazione, ISBN);

                try {
                    libro.inserisciLibro();
                    System.out.println("Libro inserito!");
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            });
            
             //Aggiungo gli elementi alla schermata
            root.getChildren().addAll(instruction, instruction1, tf1, instruction2, tf2, instruction3, tf3, instruction4, tf4, confirm);
        });
        
        //Quando premo il bottone cerca libro
        findBook.setOnAction(e -> {
            //Pulisco la schermata
            root.getChildren().clear();
            
            //Creo le label
            Label instruction = new Label("Inserisci TITOLO e AUTORE e premi Conferma:");
            Label instruction1 = new Label("TITOLO:");
            Label instruction2 = new Label("AUTORE:");            
            Label instruction3 = new Label("ANNO DI PUBBLICAZIONE:");
            Label instruction4 = new Label("ISBN:");

            
            //Creo i campi per inserire i valori
            TextField tf1 = new TextField();
            tf1.setPromptText("Scrivi titolo...");
        
            TextField tf2 = new TextField();
            tf2.setPromptText("Scrivi autore...");
            
            TextField tf3 = new TextField();
            tf3.setPromptText("Scrivi anno di pubblicazione...");
        
            TextField tf4 = new TextField();
            tf4.setPromptText("Scrivi ISBN...");

            Button confirm = new Button("Conferma");
            
            //Quando premo conferma
            confirm.setOnAction(event -> {
                String titolo = tf1.getText();
                String autore = tf2.getText();
                int annoPubblicazione = Integer.valueOf(tf3.getText());
                int ISBN = Integer.valueOf(tf4.getText());

                Libro libro = new Libro(titolo, autore, annoPubblicazione, ISBN);

                try {
                    libro.stampaLibro();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            });
            
             //Aggiungo gli elementi alla schermata
            root.getChildren().addAll(instruction, instruction1, tf1, instruction2, tf2, instruction3, tf3, instruction4, tf4, confirm);
            
        });
        
        //Quando premo il bottone visualizza lista libri
        listBook.setOnAction(e -> {

            try {
                Libro.visualizzazioneListaLibri();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
        
        //Quando premo il bottone elimina il libro
        deleteBook.setOnAction(e -> {
            //Pulisco la schermata
            root.getChildren().clear();
            
            //Creo le label
            Label instruction = new Label("Inserisci TITOLO e AUTORE e premi Conferma:");
            Label instruction1 = new Label("TITOLO:");
            Label instruction2 = new Label("AUTORE:");
            Label instruction3 = new Label("ANNO DI PUBBLICAZIONE:");
            Label instruction4 = new Label("ISBN:");
            
            //Creo i campi per inserire i valori
            TextField tf1 = new TextField();
            tf1.setPromptText("Scrivi titolo...");
        
            TextField tf2 = new TextField();
            tf2.setPromptText("Scrivi autore...");
            
            TextField tf3 = new TextField();
            tf3.setPromptText("Scrivi anno di pubblicazione...");
        
            TextField tf4 = new TextField();
            tf4.setPromptText("Scrivi ISBN...");

            Button confirm = new Button("Conferma");
            
            //Quando premo conferma
            confirm.setOnAction(event -> {
                String titolo = tf1.getText();
                String autore = tf2.getText();
                int annoPubblicazione = Integer.valueOf(tf3.getText());
                int ISBN = Integer.valueOf(tf4.getText());

                Libro libro = new Libro(titolo, autore, annoPubblicazione, ISBN);

                try {
                    libro.cancellazioneDatiLibro();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            });
            
             //Aggiungo gli elementi alla schermata
            root.getChildren().addAll(instruction, instruction1, tf1, instruction2, tf2, instruction3, tf3, instruction4, tf4, confirm);
            
        });
        
        Scene scene = new Scene(root, 320, 400);
        stage.setScene(scene);
        stage.setTitle("Esempio JavaFX");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
