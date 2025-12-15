package Biblioteca.Controller;

import Biblioteca.Model.Database;
import Biblioteca.Model.Prestito;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ControllerPrestitiTest {

    private ControllerPrestiti controller;
    private TableView<Prestito> mockTableView;
    private MockedStatic<Database> mockDatabase;

    @BeforeAll
    public static void setUpClass() {
        new JFXPanel(); // Init Toolkit
    }

    @BeforeEach
    public void setUp() throws Exception {
        controller = new ControllerPrestiti();
        mockTableView = new TableView<>();

        // Iniettiamo la tabella
        injectField(controller, "tablePrestiti", mockTableView);
        
        // Iniettiamo le colonne per evitare NullPointer in initialize/configuraTabella
        injectField(controller, "ISBN", new TableColumn<>());
        injectField(controller, "Matricola", new TableColumn<>());
        injectField(controller, "Data", new TableColumn<>());
        injectField(controller, "Azioni", new TableColumn<>());

        // Attiviamo Mock Statico per il Database
        mockDatabase = Mockito.mockStatic(Database.class);
    }

    @AfterEach
    public void tearDown() {
        if (mockDatabase != null) {
            mockDatabase.close();
        }
        controller = null;
    }

    // ==========================================
    // TEST 1: Caricamento Dati all'Avvio
    // ==========================================
    @Test
    public void testCaricaDatiAllAvvio() throws Exception {
        // --- 1. Dati Finti ---
        List<Prestito> prestitiFinti = new ArrayList<>();
        
        // Prestito 1
        Prestito p1 = mock(Prestito.class);
        when(p1.getIsbn()).thenReturn(111L);
        when(p1.getMatricola()).thenReturn("MAT01");
        when(p1.getAzioni()).thenReturn(creaHBoxFinto()); // IMPORTANTE: evita crash su immagini
        prestitiFinti.add(p1);

        // Prestito 2
        Prestito p2 = mock(Prestito.class);
        when(p2.getIsbn()).thenReturn(222L);
        when(p2.getMatricola()).thenReturn("MAT02");
        when(p2.getAzioni()).thenReturn(creaHBoxFinto());
        prestitiFinti.add(p2);

        // --- 2. Configura Mock ---
        mockDatabase.when(Database::leggiDatabasePrestiti).thenReturn(prestitiFinti);
        mockDatabase.when(Database::creaDatabase).thenAnswer(i -> null);

        // --- 3. Esegui initialize ---
        // Poiché i componenti sono detached (non nella scena), possiamo eseguire sul main thread
        // MA il mock statico richiede coerenza. Se il controller usa Platform.runLater internamente, serve cautela.
        // ControllerPrestiti.initialize() è sincrono, quindi:
        
        controller.initialize();

        // --- 4. Verifiche ---
        assertEquals(2, mockTableView.getItems().size(), "La tabella deve contenere 2 prestiti");
        assertEquals(p1, mockTableView.getItems().get(0));
        assertEquals(p2, mockTableView.getItems().get(1));
    }

    // ==========================================
    // TEST 2: Database Vuoto o Nullo
    // ==========================================
    @Test
    public void testCaricaDatiVuoti() throws Exception {
        mockDatabase.when(Database::leggiDatabasePrestiti).thenReturn(new ArrayList<>());
        mockDatabase.when(Database::creaDatabase).thenAnswer(i -> null);

        controller.initialize();

        assertTrue(mockTableView.getItems().isEmpty(), "La tabella deve essere vuota");
    }

    // ==========================================
    // UTILITIES
    // ==========================================

    private HBox creaHBoxFinto() {
        HBox box = new HBox();
        box.getChildren().add(new Button("Restituisci")); // Bottone finto senza icona
        return box;
    }

    private void injectField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}