package Biblioteca.Controller;

import Biblioteca.Model.Libro;
import Biblioteca.Model.Studente;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ControllerDelete {

    private Object oggettoDaEliminare; 
    
    @FXML
    private Label errore; 


    public void setOggettoDaEliminare(Object obj) {
        this.oggettoDaEliminare = obj;
    }

    @FXML
    private Button buttonConferma; 
    
    @FXML
    public void conferma(MouseEvent event) { 
        try {
            int esito;
            if (this.oggettoDaEliminare instanceof Libro) {
                Libro l = (Libro) this.oggettoDaEliminare;
                esito = l.cancellazioneDatiLibro();

                if (esito == -1) {
                    errore.setText("Non puoi eliminare il libro, è in prestito"); 
                } else if (esito == -2){
                    errore.setText("Libro non risulta nel nostro database");
                } else if (esito == -3){
                    errore.setText("ERROR, database not found");
                } else if (esito == 0) {
                    annulla();
                    apriPopupSuccesso();
                }
            } 
            else if (this.oggettoDaEliminare instanceof Studente) {
                Studente s = (Studente) this.oggettoDaEliminare;
                esito = s.cancellazioneDatiStudente();
                 
                if (esito == -2) {
                    errore.setText("Studente non risulta nel nostro database"); 
                } else if (esito == -3){
                    errore.setText("ERROR, database not found");
                } else if (esito == 0) {
                    annulla();
                    apriPopupSuccesso();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    @FXML
    public void annulla() {
        Stage stage = (Stage) buttonConferma.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void apriPopupSuccesso() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/successo.fxml"));
            Parent root = loader.load();
            
            ControllerSuccesso controller = loader.getController();
            
            if (this.oggettoDaEliminare instanceof Libro) {
                Libro l = (Libro) this.oggettoDaEliminare;
                controller.setMessaggio(String.format("Libro: %s eliminato correttamente!", l.getTitolo()));
            }
            else if (this.oggettoDaEliminare instanceof Studente) {
                Studente s = (Studente) this.oggettoDaEliminare;
                controller.setMessaggio(String.format("Studente: %s %s eliminato correttamente!", s.getNome(), s.getCognome()));
            }

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Impossibile aprire il popup di successo.");
        }
    }
}