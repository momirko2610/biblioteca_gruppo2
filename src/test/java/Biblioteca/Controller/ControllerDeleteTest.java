package Biblioteca.Controller;

import Biblioteca.Model.Libro;
import Biblioteca.Model.Studente;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.embed.swing.JFXPanel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ControllerDeleteTest {

    private ControllerDelete controller;
    private Label mockLabelErrore;
    private Button mockButtonConferma;
    
    // Oggetti Mock (Finti)
    private Libro mockLibro;
    private Studente mockStudente;

    @BeforeAll
    public static void setUpClass() {
        // Inizializza il toolkit JavaFX una volta per tutti i test
        new JFXPanel(); 
    }


    @BeforeEach
    public void setUp() throws Exception {
        controller = new ControllerDelete();
        
        // Creiamo i componenti JavaFX veri
        mockLabelErrore = new Label();
        mockButtonConferma = new Button();

        // Iniettiamo i componenti nel controller
        injectField(controller, "errore", mockLabelErrore);
        injectField(controller, "buttonConferma", mockButtonConferma);

        // Creiamo i MOCK dei modelli
        mockLibro = Mockito.mock(Libro.class);
        mockStudente = Mockito.mock(Studente.class);
    }
    
    @AfterEach
    public void tearDown() {
        controller = null;
    }

    // ==========================================
    // TEST ELIMINAZIONE LIBRO
    // ==========================================

    @Test
    public void testEliminaLibro_InPrestito() throws Exception {
        controller.setOggettoDaEliminare(mockLibro);
        when(mockLibro.cancellazioneDatiLibro()).thenReturn(-1);

        runOnFxThread(() -> controller.conferma(null));

        verify(mockLibro, times(1)).cancellazioneDatiLibro();
        assertEquals("Non puoi eliminare il libro, è in prestito", mockLabelErrore.getText());
    }

    @Test
    public void testEliminaLibro_NonTrovato() throws Exception {
        controller.setOggettoDaEliminare(mockLibro);
        when(mockLibro.cancellazioneDatiLibro()).thenReturn(-2);

        runOnFxThread(() -> controller.conferma(null));

        assertEquals("Libro non risulta nel nostro database", mockLabelErrore.getText());
    }
    
    @Test
    public void testEliminaLibro_ErroreDatabase() throws Exception {
        controller.setOggettoDaEliminare(mockLibro);
        when(mockLibro.cancellazioneDatiLibro()).thenReturn(-3);

        runOnFxThread(() -> controller.conferma(null));

        assertEquals("ERROR, database not found", mockLabelErrore.getText());
    }

    // ==========================================
    // TEST ELIMINAZIONE STUDENTE (Aggiornati)
    // ==========================================

    @Test
    public void testEliminaStudente_NonTrovato() throws Exception {
        controller.setOggettoDaEliminare(mockStudente);
        
        // 1. Simuliamo che il Model restituisca -2 (Codice corretto)
        when(mockStudente.cancellazioneDatiStudente()).thenReturn(-2);

        // 2. Eseguiamo
        runOnFxThread(() -> controller.conferma(null));

        // 3. Verifichiamo che il Controller abbia settato il testo giusto
        assertEquals("Studente non risulta nel nostro database", mockLabelErrore.getText());
    }

    @Test
    public void testEliminaStudente_ErroreDatabase() throws Exception {
        controller.setOggettoDaEliminare(mockStudente);
        
        // 1. Simuliamo che il Model restituisca -3 (Codice corretto)
        when(mockStudente.cancellazioneDatiStudente()).thenReturn(-3);

        // 2. Eseguiamo
        runOnFxThread(() -> controller.conferma(null));

        // 3. Verifichiamo
        assertEquals("ERROR, database not found", mockLabelErrore.getText());
    }

    // ==========================================
    // TEST SUCCESSO
    // ==========================================

    @Test
    public void testEliminaLibro_Successo() throws Exception {
        controller.setOggettoDaEliminare(mockLibro);
        when(mockLibro.cancellazioneDatiLibro()).thenReturn(0);
        
        // Prepariamo uno Stage finto per evitare NullPointerException su annulla()
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            Stage stage = new Stage();
            Scene scene = new Scene(mockButtonConferma); 
            stage.setScene(scene);
            stage.show();
            
            try {
                controller.conferma(null);
            } catch (Exception e) {
                // Ignora errori caricamento FXML
            }
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);

        verify(mockLibro, times(1)).cancellazioneDatiLibro();
        // Se non ci sono errori, il test passa
        assertEquals("", mockLabelErrore.getText());
    }

    // ==========================================
    // UTILITIES
    // ==========================================

    private void runOnFxThread(Runnable action) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                latch.countDown();
            }
        });
        latch.await(5, TimeUnit.SECONDS);
    }

    private void injectField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}