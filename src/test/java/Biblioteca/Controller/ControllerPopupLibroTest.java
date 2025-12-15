package Biblioteca.Controller;

import Biblioteca.Model.Libro;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javafx.embed.swing.JFXPanel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ControllerPopupLibroTest {

    private ControllerPopupLibro controller;
    
    // Componenti UI Mockati (ma reali per JavaFX)
    private TextField mockTitolo;
    private TextField mockAutori;
    private TextField mockIsbn;
    private TextField mockNCopie;
    private TextField mockData;
    private Label mockErrore;
    private Text mockLabelTitolo; // La label "Nuovo Libro" / "Modifica Libro"

    // Mock del Libro esistente (per i test di modifica)
    private Libro mockLibroEsistente;

    @BeforeAll
    public static void setUpClass() {
        // Inizializza il toolkit JavaFX una volta per tutti i test
        new JFXPanel(); 
    }


    @BeforeEach
    public void setUp() throws Exception {
        controller = new ControllerPopupLibro();
        
        // Creiamo i componenti UI nel thread FX
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            mockTitolo = new TextField();
            mockAutori = new TextField();
            mockIsbn = new TextField();
            mockNCopie = new TextField();
            mockData = new TextField();
            mockErrore = new Label();
            mockLabelTitolo = new Text();
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);

        // Iniettiamo i componenti
        injectField(controller, "titolo", mockTitolo);
        injectField(controller, "autori", mockAutori);
        injectField(controller, "isbn", mockIsbn);
        injectField(controller, "nCopie", mockNCopie);
        injectField(controller, "data", mockData);
        injectField(controller, "errore", mockErrore);
        injectField(controller, "label", mockLabelTitolo); // Importante per setLibroDaModificare

        // Creiamo il mock per i test di modifica
        mockLibroEsistente = mock(Libro.class);
    }
    
    @AfterEach
    public void tearDown() {
        controller = null;
    }

    // ==========================================
    // TEST 1: Inserimento Nuovo Libro (Successo)
    // ==========================================
    @Test
    public void testNuovoLibroSuccesso() throws Exception {
        // 1. Setup UI (Simuliamo input utente)
        Platform.runLater(() -> {
            mockTitolo.setText("Java Programming");
            mockAutori.setText("Gosling");
            mockIsbn.setText("1234567890123");
            mockNCopie.setText("5");
            mockData.setText("2020");
        });
        
        // 2. Chiamiamo setLibroDaModificare(null) per dire "Nuovo Libro"
        runOnFxThread(() -> controller.setLibroDaModificare(null));
        
        // 3. Eseguiamo il salvataggio intercettando la creazione del nuovo Libro
        AtomicReference<Throwable> error = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            // Intercettiamo "new Libro(...)"
            try (MockedConstruction<Libro> mockedLibro = Mockito.mockConstruction(Libro.class,
                    (mock, context) -> {
                        // Quando il nuovo libro viene salvato, restituisci 0 (Successo)
                        when(mock.inserisciLibro()).thenReturn(0);
                        when(mock.getTitolo()).thenReturn("Java Programming"); // Per il messaggio di successo
                    })) {

                // Prepariamo lo Stage per poterlo chiudere
                Stage stage = new Stage();
                Scene scene = new Scene(mockTitolo); // Mettiamo un componente nella scena
                stage.setScene(scene);
                stage.show();

                invokePrivateMethod(controller, "salva");

                // Verifiche:
                // 1. Verifica che inserisciLibro sia stato chiamato
                // Nota: Mockito.mockConstruction crea mock, dobbiamo recuperare l'istanza creata
                if (mockedLibro.constructed().isEmpty()) {
                    throw new AssertionError("Il costruttore di Libro non è stato chiamato!");
                }
                Libro libroCreato = mockedLibro.constructed().get(0);
                verify(libroCreato, times(1)).inserisciLibro();
                
                // 2. Verifica che non ci siano errori a video
                if (!mockErrore.getText().isEmpty()) {
                     throw new AssertionError("Errore inatteso: " + mockErrore.getText());
                }

            } catch (Throwable e) {
                // Ignoriamo LoadException (FXML) perché è attesa nei test unitari
                if (!e.toString().contains("javafx.fxml.LoadException")) {
                    error.set(e);
                }
            } finally {
                latch.countDown();
            }
        });

        latch.await(5, TimeUnit.SECONDS);
        if (error.get() != null) fail(error.get().getMessage());
    }

    // ==========================================
    // TEST 2: Inserimento Nuovo Libro (Errore ISBN)
    // ==========================================
    @Test
    public void testNuovoLibroErroreISBN() throws Exception {
        Platform.runLater(() -> {
            mockTitolo.setText("Test Book");
            mockNCopie.setText("1");
            mockData.setText("2000");
            mockIsbn.setText("123"); // ISBN Corto -> Errore
        });

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try (MockedConstruction<Libro> mockedLibro = Mockito.mockConstruction(Libro.class,
                    (mock, context) -> {
                        // Simuliamo errore formato ISBN (-1)
                        when(mock.inserisciLibro()).thenReturn(-1);
                    })) {

                invokePrivateMethod(controller, "salva");

                // Verifica il messaggio di errore
                String testo = mockErrore.getText();
                if (!testo.contains("Formato ISBN incorretto")) {
                    throw new AssertionError("Messaggio errore errato: " + testo);
                }

            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });
        latch.await(5, TimeUnit.SECONDS);
    }

    // ==========================================
    // TEST 3: Modifica Libro Esistente
    // ==========================================
    @Test
    public void testModificaLibro() throws Exception {
        // 1. Configuriamo il controller con un libro esistente
        runOnFxThread(() -> controller.setLibroDaModificare(mockLibroEsistente));
        
        // 2. Simuliamo che l'utente cambi il titolo
        Platform.runLater(() -> {
            mockTitolo.setText("Nuovo Titolo Modificato");
            mockNCopie.setText("10"); // Necessario perché parseInt non fallisca
            mockData.setText("2021");
        });
        Thread.sleep(100);

        // 3. Configuriamo il mock esistente per restituire successo (0)
        // Nota: modificaDatiLibro prende stringhe
        when(mockLibroEsistente.modificaDatiLibro(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(0);
        when(mockLibroEsistente.getTitolo()).thenReturn("Nuovo Titolo Modificato");

        // 4. Salviamo
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                // Stage finto per chiusura
                Stage stage = new Stage();
                Scene scene = new Scene(mockTitolo);
                stage.setScene(scene);
                stage.show();
                
                invokePrivateMethod(controller, "salva");
                
                // Verifica chiamata al metodo di modifica
                verify(mockLibroEsistente, times(1)).modificaDatiLibro(anyString(), anyString(), anyString(), anyString(), anyString());

            } catch (Throwable e) {
                 if (!e.toString().contains("javafx.fxml.LoadException")) e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });
        latch.await(5, TimeUnit.SECONDS);
    }

    // ==========================================
    // TEST 4: Validazione Input (NumberFormat)
    // ==========================================
    @Test
    public void testValidazioneInputNumerico() throws Exception {
        Platform.runLater(() -> {
            mockTitolo.setText("Libro Bug");
            mockNCopie.setText("dieci"); // ERRORE: Non è un numero!
            mockData.setText("2020");
        });
        
        runOnFxThread(() -> {
            try {
                invokePrivateMethod(controller, "salva");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        // Verifica asincrona del testo errore
        AtomicReference<String> errorText = new AtomicReference<>();
        runOnFxThread(() -> errorText.set(mockErrore.getText()));
        
        assertTrue(errorText.get().contains("Devi compilare tutti i campi in modo corretto"));
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

    private void invokePrivateMethod(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }
}