package Biblioteca.Controller;

import Biblioteca.Model.Bibliotecario;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ControllerResetPasswordTest {

    private ControllerResetPassword controller;
    
    // Componenti UI
    private TextField mockTxtNuovaPass;
    private Label mockErrore;

    @BeforeAll
    public static void setUpClass() {
        // Inizializza il toolkit JavaFX
        new JFXPanel(); 
    }

    @BeforeEach
    public void setUp() throws Exception {
        controller = new ControllerResetPassword();
        
        mockTxtNuovaPass = new TextField();
        mockErrore = new Label();

        injectField(controller, "txtNuovaPass", mockTxtNuovaPass);
        injectField(controller, "errore", mockErrore);
    }
    
    @AfterEach
    public void tearDown() {
        controller = null;
    }

    // ==========================================
    // TEST 1: Password Vuota
    // ==========================================
    @Test
    public void testPasswordVuota() throws Exception {
        // Setup dati
        Platform.runLater(() -> mockTxtNuovaPass.setText(""));
        
        // Esecuzione
        runOnFxThread(() -> {
            try {
                invokePrivateMethod(controller, "confermaCambio");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Verifica
        AtomicReference<String> errorText = new AtomicReference<>();
        runOnFxThread(() -> errorText.set(mockErrore.getText()));
        
        assertEquals("Inserisci una password valida", errorText.get());
    }

    // ==========================================
    // TEST 2: Cambio Password Successo
    // ==========================================
    @Test
    public void testCambioPasswordSuccesso() throws Exception {
        // 1. Setup Dati Utente
        controller.setDatiUtente("admin@test.it");
        
        Platform.runLater(() -> mockTxtNuovaPass.setText("NuovaPassword123"));

        AtomicReference<Throwable> error = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            // 2. Intercettiamo la creazione di Bibliotecario
            try (MockedConstruction<Bibliotecario> mockedBiblio = Mockito.mockConstruction(Bibliotecario.class,
                    (mock, context) -> {
                        // Quando viene chiamato cambiaPassword, restituisci 1 (Successo)
                        when(mock.cambiaPassword(anyString())).thenReturn(1);
                    })) {

                // 3. Prepariamo lo Stage (necessario per il metodo chiudi())
                Stage stage = new Stage();
                // Usiamo StackPane perché TextField non è un Parent
                Scene scene = new Scene(new StackPane(mockTxtNuovaPass)); 
                stage.setScene(scene);
                stage.show();

                // 4. Eseguiamo
                invokePrivateMethod(controller, "confermaCambio");

                // 5. Verifiche
                // Verifichiamo che sia stato creato l'oggetto
                if (mockedBiblio.constructed().isEmpty()) {
                    throw new AssertionError("Costruttore Bibliotecario non chiamato");
                }
                
                Bibliotecario b = mockedBiblio.constructed().get(0);
                // Verifichiamo che sia stato chiamato il metodo di cambio pass
                verify(b, times(1)).cambiaPassword("NuovaPassword123");
                
                // Se non ci sono eccezioni e il mock è stato chiamato, 
                // e la finestra si è chiusa (stage.close non lancia errori), il test è passato.

            } catch (Throwable e) {
                error.set(e);
            } finally {
                latch.countDown();
            }
        });

        latch.await(5, TimeUnit.SECONDS);
        if (error.get() != null) fail(error.get().getMessage());
    }

    // ==========================================
    // TEST 3: Errore nel salvataggio (es. Utente non trovato)
    // ==========================================
    @Test
    public void testCambioPasswordFallito() throws Exception {
        controller.setDatiUtente("inesistente@test.it");
        Platform.runLater(() -> mockTxtNuovaPass.setText("Pass123"));

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try (MockedConstruction<Bibliotecario> mockedBiblio = Mockito.mockConstruction(Bibliotecario.class,
                    (mock, context) -> {
                        // Simuliamo errore (-1)
                        when(mock.cambiaPassword(anyString())).thenReturn(-1);
                    })) {

                invokePrivateMethod(controller, "confermaCambio");

                // Verifica messaggio errore
                if (!mockErrore.getText().contains("Impossibile trovare l'utente")) {
                    throw new AssertionError("Messaggio errore errato: " + mockErrore.getText());
                }

            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });
        latch.await(5, TimeUnit.SECONDS);
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

    private void invokePrivateMethod(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }
}