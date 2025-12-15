package Biblioteca.Controller;

import Biblioteca.Model.Prestito;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ControllerRitornoTest {

    private ControllerRitorno controller;
    private Text mockMessaggio;

    @BeforeAll
    public static void setUpClass() {
        // Inizializza JavaFX toolkit
        new JFXPanel();
    }

    @BeforeEach
    public void setUp() throws Exception {
        controller = new ControllerRitorno();
        
        // Creiamo il componente Text
        mockMessaggio = new Text();
        
        // Lo iniettiamo nel controller
        injectField(controller, "messaggio", mockMessaggio);
    }

    @AfterEach
    public void tearDown() {
        controller = null;
    }

    // ==========================================
    // TEST 1: Impostazione Dati e Messaggio
    // ==========================================
    @Test
    public void testSetDatiRestituzione() throws Exception {
        // Setup dati
        String matricola = "MAT123";
        Long isbn = 987654321L;
        
        Platform.runLater(() -> {
            controller.setDatiRestituzione(matricola, isbn);
            
            // Verifiche
            String testoGenerato = mockMessaggio.getText();
            String dataOggi = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            
            // Verifichiamo che il messaggio contenga i dati giusti
            assertTrue(testoGenerato.contains(matricola));
            assertTrue(testoGenerato.contains(isbn.toString()));
            assertTrue(testoGenerato.contains(dataOggi));
        });
        
        Thread.sleep(500); // Breve attesa per il thread FX
    }

    // ==========================================
    // TEST 2: Conferma Restituzione (Successo)
    // ==========================================
    @Test
    public void testConferma() throws Exception {
        AtomicReference<Throwable> error = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            // Mockiamo il metodo statico dentro il thread FX
            try (MockedStatic<Prestito> mockPrestitoStatic = Mockito.mockStatic(Prestito.class)) {
                
                // 1. Setup Dati nel controller
                controller.setDatiRestituzione("MAT123", 111L);

                // 2. Setup Finestra per permettere la chiusura
                Stage stage = new Stage();
                // Importante: Text va dentro uno StackPane (Parent)
                Scene scene = new Scene(new StackPane(mockMessaggio));
                stage.setScene(scene);
                stage.show();

                // 3. Esecuzione
                invokePrivateMethod(controller, "conferma");

                // 4. Verifiche
                // Verifichiamo che sia stato chiamato il metodo statico del Model con i parametri giusti
                mockPrestitoStatic.verify(() -> 
                    Prestito.registrazioneRestituzione(eq("MAT123"), eq(111L)), 
                    times(1)
                );

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
    // UTILITIES
    // ==========================================

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