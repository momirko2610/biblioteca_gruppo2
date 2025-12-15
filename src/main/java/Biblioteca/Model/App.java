package Biblioteca.Model;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Mirko Montella
 * @author Ciro Senese
 * @author Sabrina Soriano
 * @author Achille Romano
 */
public class App extends Application {
    
public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/homepage.fxml"));
        Parent root = loader.load();
        
        stage.setMinWidth(900);  // non si puo stringere la schermata oltre questi valori
        stage.setMinHeight(600);
        
        stage.setScene(new Scene(root));
        
        stage.setTitle("GestoreBiblioteca");
        stage.centerOnScreen();
        
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
