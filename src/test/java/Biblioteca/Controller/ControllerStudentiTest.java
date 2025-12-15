package Biblioteca.Controller;

import Biblioteca.Model.Bibliotecario;
import Biblioteca.Model.Database;
import Biblioteca.Model.Studente;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ControllerStudentiTest {

    private ControllerStudenti controller;
    
    private TableView<Studente> mockTableView;
    private TextField mockSearchField;

    private MockedStatic<Database> mockDatabase;

    @BeforeAll
    public static void setUpClass() {
        new JFXPanel();
    }

    @BeforeEach
    public void setUp() throws Exception {
        controller = new ControllerStudenti();
        
        mockTableView = new TableView<>();
        mockSearchField = new TextField();

        injectField(controller, "tableViewStudenti", mockTableView);
        injectField(controller, "searchStudentTextField", mockSearchField);
        
        injectField(controller, "Nome", new TableColumn<>());
        injectField(controller, "Cognome", new TableColumn<>());
        injectField(controller, "Matricola", new TableColumn<>());
        injectField(controller, "Email", new TableColumn<>());
        injectField(controller, "Azioni", new TableColumn<>());

        mockDatabase = Mockito.mockStatic(Database.class);
    }

    @AfterEach
    public void tearDown() {
        if (mockDatabase != null) {
            mockDatabase.close();
        }
        controller = null;
    }

    @Test
    public void testCaricamentoDati() throws Exception {
        List<Studente> studenti = new ArrayList<>();
        
        Studente s1 = mock(Studente.class);
        when(s1.getNome()).thenReturn("Mario");
        when(s1.getCognome()).thenReturn("Rossi");
        when(s1.getMatricola()).thenReturn("0512101111");
        when(s1.getAzioni()).thenReturn(creaHBoxFinto());
        studenti.add(s1);

        Studente s2 = mock(Studente.class);
        when(s2.getNome()).thenReturn("Luigi");
        when(s2.getCognome()).thenReturn("Verdi");
        when(s2.getMatricola()).thenReturn("0512102222");
        when(s2.getAzioni()).thenReturn(creaHBoxFinto());
        studenti.add(s2);
        
        mockDatabase.when(Database::leggiDatabaseStudenti).thenReturn(studenti);
        mockDatabase.when(Database::creaDatabase).thenAnswer(i -> null);

        controller.initialize();

        assertEquals(2, mockTableView.getItems().size());
        assertEquals(s1, mockTableView.getItems().get(0));
        assertEquals(s2, mockTableView.getItems().get(1));
    }

    @Test
    public void testRicercaStudente() throws Exception {
        List<Studente> studenti = new ArrayList<>();
        
        Studente s1 = mock(Studente.class);
        when(s1.getNome()).thenReturn("Mario");
        when(s1.getCognome()).thenReturn("Rossi");
        when(s1.getMatricola()).thenReturn("0512101111");
        when(s1.getAzioni()).thenReturn(creaHBoxFinto());
        studenti.add(s1);

        Studente s2 = mock(Studente.class);
        when(s2.getNome()).thenReturn("Luigi");
        when(s2.getCognome()).thenReturn("Bianchi");
        when(s2.getMatricola()).thenReturn("0512109999");
        when(s2.getAzioni()).thenReturn(creaHBoxFinto());
        studenti.add(s2);

        javafx.collections.ObservableList<Studente> obsStudenti = javafx.collections.FXCollections.observableArrayList(studenti);
        injectField(controller, "listaStudente", obsStudenti);

        mockSearchField.setText("9999");
        invokePrivateMethod(controller, "onSearchStudent");

        assertEquals(1, mockTableView.getItems().size());
        assertEquals(s2, mockTableView.getItems().get(0));
        
        mockSearchField.setText("Mario");
        invokePrivateMethod(controller, "onSearchStudent");
        
        assertEquals(1, mockTableView.getItems().size());
        assertEquals(s1, mockTableView.getItems().get(0));
    }

    @Test
    public void testApriResetPassword() throws Exception {
        Bibliotecario mockBiblio = mock(Bibliotecario.class);
        when(mockBiblio.getEmail()).thenReturn("admin@test.it");
        
        controller.setBibliotecario(mockBiblio);
        
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                invokePrivateMethod(controller, "apriResetPassword");
            } catch (Exception e) {
                // Ignora errori FXML
            } finally {
                latch.countDown();
            }
        });
        latch.await(5, TimeUnit.SECONDS);
        
        verify(mockBiblio, atLeastOnce()).getEmail();
    }


    private HBox creaHBoxFinto() {
        HBox box = new HBox();
        box.getChildren().add(new Button("Modifica"));
        box.getChildren().add(new Button("Elimina"));
        box.getChildren().add(new Button("Info"));
        return box;
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