package Biblioteca.Controller;

import Biblioteca.Model.Database;
import Biblioteca.Model.Prestito;
import Biblioteca.Model.Studente;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.embed.swing.JFXPanel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ControllerInfoTest {

    private ControllerInfo controller;
    private TableView<Prestito> mockTableView;
    private MockedStatic<Database> mockDatabase;

    @BeforeAll
    public static void setUpClass() {
        // Inizializza il toolkit JavaFX una volta per tutti i test
        new JFXPanel(); 
    }


    @BeforeEach
    public void setUp() throws Exception {
        controller = new ControllerInfo();
        
        // Creiamo la TableView (JavaFX component)
        mockTableView = new TableView<>();

        // Inietto la TableView nel controller
        injectField(controller, "tableViewPrestito", mockTableView);
        
        // Inietto le colonne (FONDAMENTALE per testInitialize)
        // Uso i nomi esatti dei campi nel ControllerInfo.java
        injectField(controller, "ISBN", new javafx.scene.control.TableColumn<>());
        injectField(controller, "DataPrestito", new javafx.scene.control.TableColumn<>());

        // Attivo il Mock Statico del Database
        mockDatabase = Mockito.mockStatic(Database.class);
    }

    @AfterEach
    public void tearDown() {
        // Chiudiamo il mock statico per non rovinare gli altri test
        if (mockDatabase != null) {
            mockDatabase.close();
        }
    }

    // ==========================================
    // TEST 1: Initialize
    // ==========================================
    @Test
    public void testInitialize() throws Exception {
        // Diciamo al Database di non fare nulla quando viene creato
        mockDatabase.when(Database::creaDatabase).thenAnswer(i -> null);

        // Eseguiamo initialize. Poiché abbiamo iniettato le colonne, non darà NullPointer.
        // Lo eseguiamo nel thread FX per sicurezza.
        runOnFxThread(() -> {
            try {
                controller.initialize();
            } catch (Exception e) {
                e.printStackTrace();
                fail("Initialize failed: " + e.getMessage());
            }
        });
    }

    // ==========================================
    // TEST 2: Chiudi Finestra
    // ==========================================
    @Test
    public void testChiudi() throws Exception {
        // Dobbiamo simulare una Finestra (Stage) reale
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            try {
                Stage stage = new Stage();
                Scene scene = new Scene(mockTableView); // Mettiamo la tabella nella scena
                stage.setScene(scene);
                stage.show(); // Mostriamo la finestra (invisibile nei test headless)
                
                // Ora chiudi() funzionerà perché getWindow() non è null
                controller.chiudi();
                
            } catch (Exception e) {
                e.printStackTrace();
                fail("Chiudi failed: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        
        latch.await(5, TimeUnit.SECONDS);
    }

    // ==========================================
    // TEST 3: Filtro Studente (Il test critico)
    // ==========================================
    @Test
    public void testSetStudente() throws Exception {
        // 1. Creiamo lo studente Target
        Studente studenteTarget = mock(Studente.class);
        when(studenteTarget.getMatricola()).thenReturn("MAT123");

        // 2. Creiamo i prestiti finti
        List<Prestito> tuttiIPrestiti = new ArrayList<>();

        // P1: Del nostro studente
        Prestito p1 = mock(Prestito.class);
        when(p1.getMatricola()).thenReturn("MAT123");
        when(p1.getIsbn()).thenReturn(11111L);
        tuttiIPrestiti.add(p1);

        // P2: Di un altro
        Prestito p2 = mock(Prestito.class);
        when(p2.getMatricola()).thenReturn("MAT999");
        when(p2.getIsbn()).thenReturn(22222L);
        tuttiIPrestiti.add(p2);

        // 3. Configuriamo il Mock del Database
        mockDatabase.when(Database::leggiDatabasePrestiti).thenReturn(tuttiIPrestiti);

        // 4. Eseguiamo il metodo sul thread principale (Main Thread)
        // NOTA: setStudente chiama tableViewPrestito.setItems(). 
        // Se la TableView non è in una scena, si può fare dal main thread.
        controller.setStudente(studenteTarget);

        // 5. Verifiche
        ObservableList<Prestito> items = mockTableView.getItems();
        assertNotNull(items);
        assertEquals(1, items.size(), "Dovrebbe esserci solo 1 prestito per MAT123");
        assertEquals(p1, items.get(0));
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