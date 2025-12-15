package Biblioteca.Controller;

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

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerSuccessoTest {

    private ControllerSuccesso controller;
    private Text mockMessaggioTesto;

    @BeforeAll
    public static void setUpClass() {
        // Inizializza il toolkit JavaFX
        new JFXPanel();
    }

    @BeforeEach
    public void setUp() throws Exception {
        controller = new ControllerSuccesso();
        
        // Creiamo il componente Text reale (detached, non nella scena)
        mockMessaggioTesto = new Text();
        
        // Lo iniettiamo nel controller
        injectField(controller, "messaggioTesto", mockMessaggioTesto);
    }

    @AfterEach
    public void tearDown() {
        controller = null;
    }

    // ==========================================
    // TEST 1: setMessaggio
    // ==========================================
    @Test
    public void testSetMessaggio() {
        // Poiché il nodo Text non è attaccato a una scena, possiamo modificarlo nel main thread
        String messaggioAtteso = "Operazione completata con successo!";
        
        controller.setMessaggio(messaggioAtteso);
        
        assertEquals(messaggioAtteso, mockMessaggioTesto.getText(), "Il testo del componente non è stato aggiornato correttamente.");
    }

    // ==========================================
    // TEST 2: chiudi (Verifica chiusura finestra)
    // ==========================================
    @Test
    public void testChiudi() throws Exception {
        AtomicReference<Throwable> error = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean isStageClosed = new AtomicBoolean(false);

        Platform.runLater(() -> {
            try {
                // 1. Creiamo una Finestra (Stage) reale
                Stage stage = new Stage();
                
                // 2. Mettiamo il Text dentro la Scena (avvolto in StackPane perché Text non è Parent)
                Scene scene = new Scene(new StackPane(mockMessaggioTesto));
                stage.setScene(scene);
                stage.show();

                // 3. Chiamiamo il metodo chiudi
                controller.chiudi();

                // 4. Verifichiamo se lo stage è stato chiuso
                // Nota: stage.isShowing() diventa false quando lo stage è chiuso/nascosto
                isStageClosed.set(!stage.isShowing());

            } catch (Throwable e) {
                error.set(e);
            } finally {
                latch.countDown();
            }
        });

        latch.await(5, TimeUnit.SECONDS);
        
        if (error.get() != null) {
            fail(error.get().getMessage());
        }
        
        assertTrue(isStageClosed.get(), "La finestra dovrebbe essere chiusa dopo aver chiamato chiudi()");
    }

    // ==========================================
    // UTILITIES
    // ==========================================

    private void injectField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}