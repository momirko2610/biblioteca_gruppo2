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
        new JFXPanel(); 
    }


    @BeforeEach
    public void setUp() throws Exception {
        controller = new ControllerInfo();

        mockTableView = new TableView<>();

        injectField(controller, "tableViewPrestito", mockTableView);
        
        injectField(controller, "ISBN", new javafx.scene.control.TableColumn<>());
        injectField(controller, "DataPrestito", new javafx.scene.control.TableColumn<>());

        mockDatabase = Mockito.mockStatic(Database.class);
    }

    @AfterEach
    public void tearDown() {

        if (mockDatabase != null) {
            mockDatabase.close();
        }
    }

    @Test
    public void testInitialize() throws Exception {
        mockDatabase.when(Database::creaDatabase).thenAnswer(i -> null);

        runOnFxThread(() -> {
            try {
                controller.initialize();
            } catch (Exception e) {
                e.printStackTrace();
                fail("Initialize failed: " + e.getMessage());
            }
        });
    }


    @Test
    public void testChiudi() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            try {
                Stage stage = new Stage();
                Scene scene = new Scene(mockTableView);
                stage.setScene(scene);
                stage.show(); 

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

    @Test
    public void testSetStudente() throws Exception {
        Studente studenteTarget = mock(Studente.class);
        when(studenteTarget.getMatricola()).thenReturn("MAT123");

        List<Prestito> tuttiIPrestiti = new ArrayList<>();

        Prestito p1 = mock(Prestito.class);
        when(p1.getMatricola()).thenReturn("MAT123");
        when(p1.getIsbn()).thenReturn(11111L);
        tuttiIPrestiti.add(p1);

        Prestito p2 = mock(Prestito.class);
        when(p2.getMatricola()).thenReturn("MAT999");
        when(p2.getIsbn()).thenReturn(22222L);
        tuttiIPrestiti.add(p2);

        mockDatabase.when(Database::leggiDatabasePrestiti).thenReturn(tuttiIPrestiti);

        controller.setStudente(studenteTarget);

        ObservableList<Prestito> items = mockTableView.getItems();
        assertNotNull(items);
        assertEquals(1, items.size(), "Dovrebbe esserci solo 1 prestito per MAT123");
        assertEquals(p1, items.get(0));
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