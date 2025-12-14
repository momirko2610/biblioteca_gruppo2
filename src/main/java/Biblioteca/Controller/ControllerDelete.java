package Biblioteca.Controller;

import Biblioteca.Model.Libro;
import Biblioteca.Model.Studente;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ControllerDelete {

    private Object oggettoDaEliminare; 


    public void setOggettoDaEliminare(Object obj) {
        this.oggettoDaEliminare = obj;
    }

    @FXML
    private Button buttonConferma; 
    
    @FXML
    public void conferma(MouseEvent event) { 
        try {
            if (this.oggettoDaEliminare instanceof Libro) {
                Libro l = (Libro) this.oggettoDaEliminare;
                l.cancellazioneDatiLibro();
                System.out.println("Libro eliminato: " + l.getTitolo());
            } 
            else if (this.oggettoDaEliminare instanceof Studente) {
                 Studente s = (Studente) this.oggettoDaEliminare;
                 s.cancellazioneDatiStudente();
            }
            
            
            chiudi();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void chiudi() {
        
         if (buttonConferma != null) {
             Stage stage = (Stage) buttonConferma.getScene().getWindow();
             stage.close();
         } else {
             
             System.out.println("Finestra chiusa (logica da implementare con fx:id)");
             Button b = new Button(); 
         }
    }
    
    
    @FXML
    public void chiudiFinestra(MouseEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}