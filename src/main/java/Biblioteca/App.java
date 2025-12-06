package Biblioteca;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import com.google.gson.*;
import java.io.FileWriter;
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
        Label instruction = new Label("Inserisci TITOLO e AUTORE e premi Conferma:");
        
        Label instruction1 = new Label("TITOLO:");
        Label instruction2 = new Label("AUTORE:");
        
        TextField tf1 = new TextField();
        tf1.setPromptText("Scrivi titolo...");
        
        TextField tf2 = new TextField();
        tf2.setPromptText("Scrivi autore...");

        Button confirm = new Button("Conferma");

        confirm.setOnAction(e -> {
            String titolo = tf1.getText();
            String autore = tf2.getText();

            // Stampa a console
            System.out.println("Titolo: " + titolo);
            System.out.println("Autore: " + autore);

            // Crea l'oggetto libro
            Libro libro = new Libro(titolo, autore);

            // Salva su file JSON
            Gson gson = new Gson();
            try (FileWriter writer = new FileWriter("database.json")) {
                gson.toJson(libro, writer);
                System.out.println("Salvato su database.json");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        VBox root = new VBox(10, instruction, instruction1, tf1, instruction2, tf2, confirm);
        Scene scene = new Scene(root, 320, 300);

        stage.setScene(scene);
        stage.setTitle("Esempio JavaFX");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
