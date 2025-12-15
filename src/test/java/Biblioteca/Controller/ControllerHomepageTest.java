package Biblioteca.Controller;

import Biblioteca.Model.Libro;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.embed.swing.JFXPanel;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerHomepageTest {

    private ControllerHomepage controller;
    private TableView<Libro> mockTableView;
    private TextField mockSearchField;
    private ObservableList<Libro> mockListaLibri;

    // 1. Inizializziamo il Toolkit JavaFX una volta sola per tutti i test
    @BeforeAll
    public static void setUpClass() throws InterruptedException {
        new JFXPanel();
    }

    @BeforeEach
    public void setUp() throws Exception {
        controller = new ControllerHomepage();
        
        // 2. Creiamo i componenti finti
        mockTableView = new TableView<>();
        mockSearchField = new TextField();
        mockListaLibri = FXCollections.observableArrayList();

        // 3. Usiamo la REFLECTION per inserire i componenti nel controller privato
        injectField(controller, "tableViewBook", mockTableView);
        injectField(controller, "searchBookTextField", mockSearchField);
        injectField(controller, "listaLibri", mockListaLibri);
        
        // Iniettiamo anche le colonne per evitare NullPointerException se chiamiamo initialize
        injectField(controller, "ISBN", new TableColumn<>());
        injectField(controller, "Titolo", new TableColumn<>());
        injectField(controller, "Autore", new TableColumn<>());
        injectField(controller, "Anno", new TableColumn<>());
        injectField(controller, "Copie_Disp", new TableColumn<>());
    }

    @AfterEach
    public void tearDown() {
        controller = null;
    }

    /**
     * Testiamo la logica di ricerca (onSearchBook).
     * Questo è il test più importante perché verifica la logica del filtro.
     */
    @Test
    public void testRicercaLibro() throws Exception {
        System.out.println("Test Ricerca Libro");

        // A. Prepariamo dei dati di prova
        Long isbn1= new Long("1234567890");
        Long isbn2= new Long("9876543210");
        
        Libro libro1 = new Libro("Il Signore degli Anelli", "Tolkien", 1954, isbn1, 5);
        Libro libro2 = new Libro("Harry Potter", "Rowling", 1997, isbn2, 3);
        
        mockListaLibri.addAll(libro1, libro2);
        
        // Simuliamo che la tabella abbia inizialmente tutti i libri
        mockTableView.setItems(mockListaLibri);

        // B. Caso 1: Cerchiamo "Potter" (dovrebbe trovare 1 libro)
        mockSearchField.setText("Potter");
        invokePrivateMethod(controller, "onSearchBook");
        
        assertEquals(1, mockTableView.getItems().size(), "Dovrebbe trovare 1 libro");
        assertEquals("Harry Potter", mockTableView.getItems().get(0).getTitolo());

        // C. Caso 2: Cerchiamo "Tolkien" (autore)
        mockSearchField.setText("tolkien"); // testiamo anche il case-insensitive
        invokePrivateMethod(controller, "onSearchBook");
        
        assertEquals(1, mockTableView.getItems().size(), "Dovrebbe trovare 1 libro per autore");
        assertEquals("Il Signore degli Anelli", mockTableView.getItems().get(0).getTitolo());

        // D. Caso 3: Stringa vuota (dovrebbe resettare la lista)
        mockSearchField.setText("");
        invokePrivateMethod(controller, "onSearchBook");
        
        assertEquals(2, mockTableView.getItems().size(), "Dovrebbe mostrare tutti i libri se la ricerca è vuota");
    }

    /**
     * Testiamo che initialize non lanci eccezioni.
     * Nota: Questo creerà realmente il file JSON se non esiste, a causa di Database.creaDatabase().
     */
    @Test
    public void testInitialize() {
        System.out.println("Test Initialize");
        assertDoesNotThrow(() -> controller.initialize());
    }

    // --- Metodi di utilità per la Reflection (per accedere a campi/metodi privati) ---

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