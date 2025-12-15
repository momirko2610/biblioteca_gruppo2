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

    // Dati finti per i test
    private Libro libroA;
    private Libro libroB;
    private Libro libroC;

    @BeforeAll
    public static void setUpClass() {
        // Inizializza il toolkit JavaFX una volta per tutti i test
        new JFXPanel(); 
    }

    @BeforeEach
    public void setUp() throws Exception {
        controller = new ControllerHomepage();
        
        // 1. Creiamo i componenti UI finti
        // Li creiamo dentro il Thread JavaFX per poterli associare a una Scena/Stage
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            mockTableView = new TableView<>();
            mockSearchField = new TextField();
            
            // --- FIX PER IL TEST GO TO LOGIN ---
            // Creiamo una Scena e uno Stage finti per evitare NullPointerException
            // quando il codice chiama tableViewBook.getScene().getWindow()
            Scene scene = new Scene(mockTableView); 
            Stage stage = new Stage();
            stage.setScene(scene);
            // -----------------------------------
            
            latch.countDown();
        });
        
        // Aspettiamo che il Thread JavaFX abbia finito di creare lo Stage
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new RuntimeException("Timeout waiting for JavaFX setup");
        }

        mockListaLibri = FXCollections.observableArrayList();

        // 2. Creiamo dei libri di prova
        try {
            libroA = new Libro("Il Signore degli Anelli", "J.R.R. Tolkien", 1954, 97888046L, 5);
            libroB = new Libro("Harry Potter", "J.K. Rowling", 1997, 97888691L, 10);
            libroC = new Libro("Clean Code", "Robert C. Martin", 2008, 12345678L, 2);
            
            mockListaLibri.addAll(libroA, libroB, libroC);
        } catch (Exception e) {
            System.err.println("Info: Immagini non caricate (normale nei test): " + e.getMessage());
        }

        // 3. Iniettiamo i componenti nel controller (Reflection)
        injectField(controller, "tableViewBook", mockTableView);
        injectField(controller, "searchBookTextField", mockSearchField);
        injectField(controller, "listaLibri", mockListaLibri);
        
        // Iniettiamo le colonne
        injectField(controller, "ISBN", new TableColumn<>());
        injectField(controller, "Titolo", new TableColumn<>());
        injectField(controller, "Autore", new TableColumn<>());
        injectField(controller, "Anno", new TableColumn<>());
        injectField(controller, "Copie_Disp", new TableColumn<>());
        
        // Impostiamo i dati iniziali nella tabella (va fatto nel thread FX per sicurezza, ma spesso funziona anche fuori)
        Platform.runLater(() -> mockTableView.setItems(mockListaLibri));
    }
    
    @AfterEach
    public void tearDown() {
        controller = null;
        mockListaLibri.clear();
    }

    // ==========================================
    // SEZIONE 1: TEST RICERCA E FILTRI (Core Logic)
    // ==========================================

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
        // Cerchiamo parte dell'ISBN di Clean Code (12345678L)
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
        // Prima filtriamo
        mockSearchField.setText("Potter");
        invokePrivateMethod(controller, "onSearchBook");
        assertEquals(1, mockTableView.getItems().size());

        // Poi cancelliamo il testo
        mockSearchField.setText("");
        invokePrivateMethod(controller, "onSearchBook");
        
        // Deve tornare tutto
        assertEquals(3, mockTableView.getItems().size(), "Con ricerca vuota devono tornare tutti i libri");
    }

    @Test
    @DisplayName("Ricerca: Spazi Vuoti (Trim)")
    public void testRicercaSpazi() throws Exception {
        // Stringa con spazi ma vuota
        mockSearchField.setText("   ");
        invokePrivateMethod(controller, "onSearchBook");
        
        assertEquals(3, mockTableView.getItems().size(), "Spazi vuoti dovrebbero essere ignorati");
    }
    
    // ==========================================
    // SEZIONE 2: TEST GESTIONE DATI (Edge Cases)
    // ==========================================

    @Test
    @DisplayName("Gestione: Lista Libri Vuota")
    public void testListaVuota() throws Exception {
        // Svuotiamo la lista sorgente
        mockListaLibri.clear();
        mockTableView.setItems(mockListaLibri);
        
        // Cerchiamo qualcosa
        mockSearchField.setText("Qualcosa");
        invokePrivateMethod(controller, "onSearchBook");
        
        assertTrue(mockTableView.getItems().isEmpty());
    }

    @Test
    @DisplayName("Gestione: Null Safety su Titoli/Autori")
    public void testLibroConDatiMancanti() throws Exception {
        // Creiamo un libro con campi nulli (per evitare NullPointerException nel filtro)
        // Usiamo la reflection per forzare null perché il costruttore potrebbe non permetterlo
        Libro libroRotto = new Libro("LibroBug", "AutoreOk", 2020, 111L, 1);
        Field titoloField = Libro.class.getDeclaredField("titolo");
        titoloField.setAccessible(true);
        titoloField.set(libroRotto, null); // Forziamo titolo a null

        mockListaLibri.add(libroRotto);
        
        mockSearchField.setText("AutoreOk");
        assertDoesNotThrow(() -> invokePrivateMethod(controller, "onSearchBook"), 
            "Il metodo di ricerca non deve crashare se un libro ha titolo null");
    }

    // ==========================================
    // SEZIONE 3: TEST NAVIGAZIONE
    // ==========================================
    
    @Test
    @DisplayName("Navigazione: Gestione Errore FXML")
    public void testGoToLoginException() throws InterruptedException {
        // 1. Prepariamo un "semaforo" per aspettare il thread grafico
        CountDownLatch latch = new CountDownLatch(1);
        
        // 2. Usiamo un array per catturare eventuali eccezioni lanciate dentro il thread grafico
        Throwable[] exceptionHolder = new Throwable[1];

        // 3. Eseguiamo il metodo sul Thread JavaFX
        Platform.runLater(() -> {
            try {
                // Chiamiamo il metodo privato che fa cambio scena
                invokePrivateMethod(controller, "goToLogin");
            } catch (Exception e) {
                // Se succede qualcosa di grave (es. errore di reflection), lo salviamo
                exceptionHolder[0] = e;
            } finally {
                // Segnaliamo che abbiamo finito
                latch.countDown();
            }
        });

        // 4. Il test aspetta qui finché il thread grafico non finisce (max 5 secondi)
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Timeout: Il metodo goToLogin ha impiegato troppo tempo.");
        }

        // 5. Se abbiamo catturato un'eccezione imprevista, facciamo fallire il test
        if (exceptionHolder[0] != null) {
            exceptionHolder[0].printStackTrace();
            fail("Eccezione lanciata durante goToLogin: " + exceptionHolder[0].getMessage());
        }
        
        // Se arriviamo qui, goToLogin è stato eseguito, ha (probabilmente) fallito 
        // il caricamento FXML (perché il file non c'è nel test environment), 
        // ma ha gestito l'errore col suo try-catch interno senza crashare. Test superato.
    }

    // ==========================================
    // UTILITIES (REFLECTION)
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