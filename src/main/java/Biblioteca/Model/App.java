package Biblioteca.Model;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.net.URL;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
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
        URL urlRisorsa = getClass().getResource("/Biblioteca/fxml/homepage.fxml");
        System.out.println("URL risorsa:"+ urlRisorsa);
        Parent root    = FXMLLoader.load(urlRisorsa);
        stage.setScene(new Scene(root, 1920, 1080));
        stage.setTitle("GestoreBiblioteca");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
