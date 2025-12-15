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
    
    private Libro mockLibro;
    private Studente mockStudente;

    @BeforeAll
    public static void setUpClass() {
        new JFXPanel(); 
    }


    @BeforeEach
    public void setUp() throws Exception {
        controller = new ControllerDelete();
        
        mockLabelErrore = new Label();
        mockButtonConferma = new Button();

        injectField(controller, "errore", mockLabelErrore);
        injectField(controller, "buttonConferma", mockButtonConferma);

        mockLibro = Mockito.mock(Libro.class);
        mockStudente = Mockito.mock(Studente.class);
    }
    
    @AfterEach
    public void tearDown() {
        controller = null;
    }

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

    @Test
    public void testEliminaStudente_NonTrovato() throws Exception {
        controller.setOggettoDaEliminare(mockStudente);
        
        when(mockStudente.cancellazioneDatiStudente()).thenReturn(-2);

        runOnFxThread(() -> controller.conferma(null));

        assertEquals("Studente non risulta nel nostro database", mockLabelErrore.getText());
    }

    @Test
    public void testEliminaStudente_ErroreDatabase() throws Exception {
        controller.setOggettoDaEliminare(mockStudente);
        
        when(mockStudente.cancellazioneDatiStudente()).thenReturn(-3);

        runOnFxThread(() -> controller.conferma(null));

        assertEquals("ERROR, database not found", mockLabelErrore.getText());
    }

    @Test
    public void testEliminaLibro_Successo() throws Exception {
        controller.setOggettoDaEliminare(mockLibro);
        when(mockLibro.cancellazioneDatiLibro()).thenReturn(0);
        
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            Stage stage = new Stage();
            Scene scene = new Scene(mockButtonConferma); 
            stage.setScene(scene);
            stage.show();
            
            try {
                controller.conferma(null);
            } catch (Exception e) {
            }
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);

        verify(mockLibro, times(1)).cancellazioneDatiLibro();
        assertEquals("", mockLabelErrore.getText());
    }

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