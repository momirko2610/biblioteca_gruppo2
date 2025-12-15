package Biblioteca.Controller;

import Biblioteca.Model.Bibliotecario;
import Biblioteca.Model.Database;
import Biblioteca.Model.Libro;
import javafx.application.Platform;
import javafx.scene.control.Button;
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
import javafx.embed.swing.JFXPanel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ControllerLibriTest {

    private ControllerLibri controller;
   
    private TableView<Libro> mockTableView;
    private TextField mockSearchField;

    private MockedStatic<Database> mockDatabase;

    @BeforeAll
    public static void setUpClass() {
       
        new JFXPanel(); 
    }


    @BeforeEach
    public void setUp() throws Exception {
        controller = new ControllerLibri();
        
        mockTableView = new TableView<>();
        mockSearchField = new TextField();

        injectField(controller, "tableViewBook", mockTableView);
        injectField(controller, "searchBookTextField", mockSearchField);
        
        injectField(controller, "ISBN", new javafx.scene.control.TableColumn<>());
        injectField(controller, "Titolo", new javafx.scene.control.TableColumn<>());
        injectField(controller, "Autore", new javafx.scene.control.TableColumn<>());
        injectField(controller, "Anno", new javafx.scene.control.TableColumn<>());
        injectField(controller, "Copie_Disp", new javafx.scene.control.TableColumn<>());
        injectField(controller, "Azioni", new javafx.scene.control.TableColumn<>());

        mockDatabase = Mockito.mockStatic(Database.class);
    }

    @AfterEach
    public void tearDown() {

        if (mockDatabase != null) {
            mockDatabase.close();
        }
    }


    @Test
    public void testCaricamentoDati() throws Exception {

        List<Libro> libriFinti = creaLibriFinti();
        
        mockDatabase.when(Database::leggiDatabaseLibri).thenReturn(libriFinti);
        mockDatabase.when(Database::creaDatabase).thenAnswer(i -> null);

     
        controller.initialize();

        assertNotNull(mockTableView.getItems());
        assertEquals(3, mockTableView.getItems().size(), "Dovrebbe aver caricato 3 libri");
        assertEquals("Harry Potter", mockTableView.getItems().get(0).getTitolo());
    }

 
    @Test
    public void testRicercaLibro() throws Exception {
 
        List<Libro> libriFinti = creaLibriFinti();
        javafx.collections.ObservableList<Libro> obsLibri = javafx.collections.FXCollections.observableArrayList(libriFinti);
        injectField(controller, "listaLibri", obsLibri);
       
        mockSearchField.setText("Tolkien");
        
        invokePrivateMethod(controller, "onSearchBook");

        assertEquals(1, mockTableView.getItems().size());
        assertEquals("Il Signore degli Anelli", mockTableView.getItems().get(0).getTitolo());
    }
    
    @Test
    public void testRicercaNessunRisultato() throws Exception {
        List<Libro> libriFinti = creaLibriFinti();
        javafx.collections.ObservableList<Libro> obsLibri = javafx.collections.FXCollections.observableArrayList(libriFinti);
        injectField(controller, "listaLibri", obsLibri);
        
        mockSearchField.setText("Zorro"); 
        
        invokePrivateMethod(controller, "onSearchBook");

        assertTrue(mockTableView.getItems().isEmpty());
    }


    @Test
    public void testAperturaResetPassword() throws Exception {


        Bibliotecario mockBiblio = mock(Bibliotecario.class);
        when(mockBiblio.getEmail()).thenReturn("admin@test.it");
        
        controller.setBibliotecario(mockBiblio);
        
        runOnFxThread(() -> {
            try {
                invokePrivateMethod(controller, "apriResetPassword");
            } catch (Exception e) {

            }
        });
        

        verify(mockBiblio, atLeastOnce()).getEmail();
    }

    private List<Libro> creaLibriFinti() {
        List<Libro> lista = new ArrayList<>();
        
        Libro l1 = mock(Libro.class);
        when(l1.getTitolo()).thenReturn("Harry Potter");
        when(l1.getAutore()).thenReturn("Rowling");
        when(l1.getIsbn()).thenReturn(12345L);
        when(l1.getAzioni()).thenReturn(creaHBoxFinto());

        Libro l2 = mock(Libro.class);
        when(l2.getTitolo()).thenReturn("Il Signore degli Anelli");
        when(l2.getAutore()).thenReturn("Tolkien");
        when(l2.getIsbn()).thenReturn(67890L);
        when(l2.getAzioni()).thenReturn(creaHBoxFinto());

        Libro l3 = mock(Libro.class);
        when(l3.getTitolo()).thenReturn("Clean Code");
        when(l3.getAutore()).thenReturn("Martin");
        when(l3.getIsbn()).thenReturn(11111L);
        when(l3.getAzioni()).thenReturn(creaHBoxFinto());

        lista.add(l1);
        lista.add(l2);
        lista.add(l3);
        return lista;
    }
    
    private HBox creaHBoxFinto() {
        HBox box = new HBox();
        box.getChildren().add(new Button("Modifica"));
        box.getChildren().add(new Button("Elimina"));
        return box;
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