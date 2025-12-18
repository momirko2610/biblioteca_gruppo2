package Biblioteca.Model;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @file App.java
 * @brief Classe principale per l'avvio dell'applicazione Gestione Biblioteca Universitaria.
 * Questa classe estende Application e si occupa di inizializzare lo stage principale.
 * @author Mirko Montella
 * @author Ciro Senese
 * @author Sabrina Soriano
 * @author Achille Romano
 */
public class App extends Application {
    
    /**
     * @brief Inizializza e visualizza la finestra principale dell'applicazione.
     * * Il metodo carica il file FXML della homepage, imposta le dimensioni minime della finestra
     * per garantire l'usabilità dell'interfaccia grafica e centra lo stage sullo schermo.
     * @param stage Lo stage primario per questa applicazione.
     * @throws Exception Se il caricamento del file FXML fallisce.
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Caricamento della homepage pubblica [cite: 145, 146]
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Biblioteca/fxml/homepage.fxml"));
        Parent root = loader.load();
        
        // Impostazione dei vincoli di dimensione per la GUI intuitiva [cite: 16, 190]
        stage.setMinWidth(900);  // non si puo stringere la schermata oltre questi valori
        stage.setMinHeight(600);
        
        stage.setScene(new Scene(root));
        
        stage.setTitle("GestoreBiblioteca");
        stage.centerOnScreen();
        
        stage.show();
    }

    /**
     * @brief Metodo main dell'applicazione.
     * * Lancia l'applicazione JavaFX richiamando il metodo launch.
     * * @param args Argomenti passati da riga di comando.
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}