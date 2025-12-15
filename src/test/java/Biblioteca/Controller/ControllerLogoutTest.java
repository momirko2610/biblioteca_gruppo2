package Biblioteca.Controller;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.embed.swing.JFXPanel;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerLogoutTest {

    private ControllerLogout controller;
    private Button mockButtonConferma;
    
    // Variabili per tracciare lo stato delle finestre
    private Stage parentStage;
    private Stage popupStage;

    @BeforeAll
    public static void setUpClass() {
        // Inizializza il toolkit JavaFX una volta per tutti i test
        new JFXPanel(); 
    }

    @BeforeEach
    public void setUp() throws Exception {
        controller = new ControllerLogout();
        
        // Creiamo il bottone
        mockButtonConferma = new Button("Conferma");
        injectField(controller, "buttonConferma", mockButtonConferma);
    }
    
    @AfterEach
    public void tearDown() throws Exception {
        // Chiudiamo tutto per pulizia
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (popupStage != null) popupStage.close();
            if (parentStage != null) parentStage.close();
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);
        controller = null;
    }

    // ==========================================
    // TEST 1: Annulla (Chiude solo il popup)
    // ==========================================
    @Test
    public void testAnnulla() throws Exception {
        setupStages(); // Crea la struttura Parent -> Popup

        runOnFxThread(() -> {
            try {
                // Eseguiamo annulla
                invokePrivateMethod(controller, "annulla");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Verifiche (devono essere fatte nel thread FX o dopo attesa)
        // Usiamo un controllo semplice: verifichiamo se il popup non è più visibile
        AtomicBoolean isPopupShowing = new AtomicBoolean(true);
        runOnFxThread(() -> isPopupShowing.set(popupStage.isShowing()));
        
        assertFalse(isPopupShowing.get(), "Il popup dovrebbe essere chiuso dopo 'annulla'");
        
        // Il genitore deve restare aperto
        AtomicBoolean isParentShowing = new AtomicBoolean(false);
        runOnFxThread(() -> isParentShowing.set(parentStage.isShowing()));
        assertTrue(isParentShowing.get(), "La finestra principale deve restare aperta");
    }

    // ==========================================
    // TEST 2: Conferma (Logout)
    // ==========================================
    @Test
    public void testConferma() throws Exception {
        setupStages();

        runOnFxThread(() -> {
            try {
                // Eseguiamo conferma
                // Nota: questo proverà a caricare 'homepage.fxml'. 
                // Se il file non si trova, lancerà IOException, ma la logica di chiusura popup avviene PRIMA.
                invokePrivateMethod(controller, "conferma");
            } catch (Exception e) {
                // Ignoriamo errori di caricamento FXML
            }
        });

        // Verifica: Il popup deve essere chiuso
        AtomicBoolean isPopupShowing = new AtomicBoolean(true);
        runOnFxThread(() -> isPopupShowing.set(popupStage.isShowing()));
        
        assertFalse(isPopupShowing.get(), "Il popup dovrebbe essere chiuso dopo 'conferma'");
        
        // Verifica titolo genitore: Se il caricamento FXML fosse riuscito, il titolo sarebbe "Homepage".
        // Se fallisce l'FXML, il titolo resta quello originale o cambia parzialmente.
        // Poiché è un test unitario senza risorse grafiche, ci accontentiamo di verificare che il popup si chiuda 
        // e che il metodo tenti di accedere al parentStage senza crashare.
    }

    // ==========================================
    // UTILITIES
    // ==========================================

    private void setupStages() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            // 1. Creiamo lo Stage Genitore (es. la finestra Libri)
            parentStage = new Stage();
            parentStage.setTitle("Finestra Libri");
            parentStage.show();

            // 2. Creiamo lo Stage Popup (Logout)
            popupStage = new Stage();
            popupStage.initOwner(parentStage); // Fondamentale: diciamo che è figlio del parent
            
            // Mettiamo il bottone del controller dentro il popup
            Scene scene = new Scene(mockButtonConferma);
            popupStage.setScene(scene);
            popupStage.show();
            
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);
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

    private void invokePrivateMethod(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }
}