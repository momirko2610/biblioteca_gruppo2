package Biblioteca.Controller;

import Biblioteca.Model.Libro;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerHomepageTest {

    private ControllerHomepage controller;
    private TableView<Libro> mockTableView;
    private TextField mockSearchField;
    private ObservableList<Libro> mockListaLibri;

    private Libro libroA;
    private Libro libroB;
    private Libro libroC;

    @BeforeAll
    public static void setUpClass() {
        new JFXPanel(); 
    }

    @BeforeEach
    public void setUp() throws Exception {
        controller = new ControllerHomepage();
        
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            mockTableView = new TableView<>();
            mockSearchField = new TextField();
            
            Scene scene = new Scene(mockTableView); 
            Stage stage = new Stage();
            stage.setScene(scene);
            
            latch.countDown();
        });
        
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new RuntimeException("Timeout waiting for JavaFX setup");
        }

        mockListaLibri = FXCollections.observableArrayList();

        try {
            libroA = new Libro("Il Signore degli Anelli", "J.R.R. Tolkien", 1954, 97888046L, 5);
            libroB = new Libro("Harry Potter", "J.K. Rowling", 1997, 97888691L, 10);
            libroC = new Libro("Clean Code", "Robert C. Martin", 2008, 12345678L, 2);
            
            mockListaLibri.addAll(libroA, libroB, libroC);
        } catch (Exception e) {
            System.err.println("Info: Immagini non caricate (normale nei test): " + e.getMessage());
        }

        injectField(controller, "tableViewBook", mockTableView);
        injectField(controller, "searchBookTextField", mockSearchField);
        injectField(controller, "listaLibri", mockListaLibri);
        
        injectField(controller, "ISBN", new TableColumn<>());
        injectField(controller, "Titolo", new TableColumn<>());
        injectField(controller, "Autore", new TableColumn<>());
        injectField(controller, "Anno", new TableColumn<>());
        injectField(controller, "Copie_Disp", new TableColumn<>());
        
        Platform.runLater(() -> mockTableView.setItems(mockListaLibri));
    }
    
    @AfterEach
    public void tearDown() {
        controller = null;
        mockListaLibri.clear();
    }

    @Test
    @DisplayName("Ricerca: Titolo Esatto")
    public void testRicercaTitoloEsatto() throws Exception {
        mockSearchField.setText("Clean Code");
        invokePrivateMethod(controller, "onSearchBook");
        
        assertEquals(1, mockTableView.getItems().size());
        assertEquals("Clean Code", mockTableView.getItems().get(0).getTitolo());
    }

    @Test
    @DisplayName("Ricerca: Parte del Titolo (Case Insensitive)")
    public void testRicercaTitoloParziale() throws Exception {
        mockSearchField.setText("signore"); // minuscolo, solo una parte
        invokePrivateMethod(controller, "onSearchBook");
        
        assertEquals(1, mockTableView.getItems().size());
        assertEquals("Il Signore degli Anelli", mockTableView.getItems().get(0).getTitolo());
    }

    @Test
    @DisplayName("Ricerca: Autore")
    public void testRicercaAutore() throws Exception {
        mockSearchField.setText("Rowling");
        invokePrivateMethod(controller, "onSearchBook");
        
        assertEquals(1, mockTableView.getItems().size());
        assertEquals("Harry Potter", mockTableView.getItems().get(0).getTitolo());
    }

    @Test
    @DisplayName("Ricerca: ISBN Parziale")
    public void testRicercaISBN() throws Exception {
        mockSearchField.setText("12345");
        invokePrivateMethod(controller, "onSearchBook");
        
        assertEquals(1, mockTableView.getItems().size());
        assertEquals(12345678L, mockTableView.getItems().get(0).getIsbn());
    }

    @Test
    @DisplayName("Ricerca: Nessun Risultato")
    public void testRicercaNessunRisultato() throws Exception {
        mockSearchField.setText("LibroCheNonEsiste");
        invokePrivateMethod(controller, "onSearchBook");
        
        assertTrue(mockTableView.getItems().isEmpty(), "La tabella dovrebbe essere vuota");
    }

    @Test
    @DisplayName("Ricerca: Stringa Vuota (Reset)")
    public void testRicercaVuotaReset() throws Exception {
        mockSearchField.setText("Potter");
        invokePrivateMethod(controller, "onSearchBook");
        assertEquals(1, mockTableView.getItems().size());

        mockSearchField.setText("");
        invokePrivateMethod(controller, "onSearchBook");
        
        assertEquals(3, mockTableView.getItems().size(), "Con ricerca vuota devono tornare tutti i libri");
    }

    @Test
    @DisplayName("Ricerca: Spazi Vuoti (Trim)")
    public void testRicercaSpazi() throws Exception {
        mockSearchField.setText("   ");
        invokePrivateMethod(controller, "onSearchBook");
        
        assertEquals(3, mockTableView.getItems().size(), "Spazi vuoti dovrebbero essere ignorati");
    }

    @Test
    @DisplayName("Gestione: Lista Libri Vuota")
    public void testListaVuota() throws Exception {
        mockListaLibri.clear();
        mockTableView.setItems(mockListaLibri);
        
        mockSearchField.setText("Qualcosa");
        invokePrivateMethod(controller, "onSearchBook");
        
        assertTrue(mockTableView.getItems().isEmpty());
    }

    @Test
    @DisplayName("Gestione: Null Safety su Titoli/Autori")
    public void testLibroConDatiMancanti() throws Exception {
        Libro libroRotto = new Libro("LibroBug", "AutoreOk", 2020, 111L, 1);
        Field titoloField = Libro.class.getDeclaredField("titolo");
        titoloField.setAccessible(true);
        titoloField.set(libroRotto, null);

        mockListaLibri.add(libroRotto);
        
        mockSearchField.setText("AutoreOk");
        assertDoesNotThrow(() -> invokePrivateMethod(controller, "onSearchBook"), 
            "Il metodo di ricerca non deve crashare se un libro ha titolo null");
    }

    @Test
    @DisplayName("Navigazione: Gestione Errore FXML")
    public void testGoToLoginException() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        
        Throwable[] exceptionHolder = new Throwable[1];

        Platform.runLater(() -> {
            try {
                invokePrivateMethod(controller, "goToLogin");
            } catch (Exception e) {
                exceptionHolder[0] = e;
            } finally {
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Timeout: Il metodo goToLogin ha impiegato troppo tempo.");
        }

        if (exceptionHolder[0] != null) {
            exceptionHolder[0].printStackTrace();
            fail("Eccezione lanciata durante goToLogin: " + exceptionHolder[0].getMessage());
        }
        
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