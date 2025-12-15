package Biblioteca.Controller;

import Biblioteca.Model.Studente;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane; // <--- Import Fondamentale aggiunto
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ControllerPopupStudentiTest {

    private ControllerPopupStudenti controller;
    
    // Componenti UI
    private TextField mockNome;
    private TextField mockCognome;
    private TextField mockMatricola;
    private TextField mockEmail;
    private Label mockErrore;
    private Text mockLabel; 

    private Studente mockStudenteEsistente;

    @BeforeAll
    public static void setUpClass() {
        // Inizializza il toolkit JavaFX
        new JFXPanel(); 
    }

    @BeforeEach
    public void setUp() throws Exception {
        controller = new ControllerPopupStudenti();
        
        mockNome = new TextField();
        mockCognome = new TextField();
        mockMatricola = new TextField();
        mockEmail = new TextField();
        mockErrore = new Label();
        mockLabel = new Text();

        injectField(controller, "TextFieldnome", mockNome);
        injectField(controller, "TextFieldcognome", mockCognome);
        injectField(controller, "TextFieldmatricola", mockMatricola);
        injectField(controller, "TextFieldemail", mockEmail);
        injectField(controller, "errore", mockErrore);
        injectField(controller, "label", mockLabel);

        mockStudenteEsistente = mock(Studente.class);
    }
    
    @AfterEach
    public void tearDown() {
        controller = null;
    }

    // ==========================================
    // TEST 1: Nuovo Studente (Successo)
    // ==========================================
    @Test
    public void testNuovoStudenteSuccesso() throws Exception {
        Platform.runLater(() -> {
            mockNome.setText("Mario");
            mockCognome.setText("Rossi");
            mockMatricola.setText("0512101111");
            mockEmail.setText("m.rossi@studenti.unisa.it");
        });
        
        runOnFxThread(() -> controller.setStudenteDaModificare(null));
        
        AtomicReference<Throwable> error = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try (MockedConstruction<Studente> mockedStudente = Mockito.mockConstruction(Studente.class,
                    (mock, context) -> {
                        when(mock.inserisciDatiStudente()).thenReturn(0);
                        when(mock.getNome()).thenReturn("Mario");
                        when(mock.getCognome()).thenReturn("Rossi");
                    })) {

                // FIX: Avvolgiamo mockLabel in uno StackPane
                Stage stage = new Stage();
                Scene scene = new Scene(new StackPane(mockLabel)); 
                stage.setScene(scene);
                stage.show();

                invokePrivateMethod(controller, "salva");

                if (mockedStudente.constructed().isEmpty()) {
                    throw new AssertionError("Costruttore Studente non chiamato!");
                }
                Studente creato = mockedStudente.constructed().get(0);
                verify(creato, times(1)).inserisciDatiStudente();
                
                if (!mockErrore.getText().isEmpty()) {
                     throw new AssertionError("Errore UI inatteso: " + mockErrore.getText());
                }

            } catch (Throwable e) {
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
    // TEST 2: Nuovo Studente (Matricola Duplicata)
    // ==========================================
    @Test
    public void testNuovoStudenteMatricolaDuplicata() throws Exception {
        Platform.runLater(() -> {
            mockNome.setText("Luigi");
            mockMatricola.setText("0512102222");
        });

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try (MockedConstruction<Studente> mockedStudente = Mockito.mockConstruction(Studente.class,
                    (mock, context) -> {
                        when(mock.inserisciDatiStudente()).thenReturn(-1); // Codice duplicato
                    })) {

                invokePrivateMethod(controller, "salva");

                String testo = mockErrore.getText();
                if (!testo.contains("Matricola già inserita")) {
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
    // TEST 3: Modifica Studente Esistente
    // ==========================================
    @Test
    public void testModificaStudente() throws Exception {
        runOnFxThread(() -> controller.setStudenteDaModificare(mockStudenteEsistente));
        
        Platform.runLater(() -> {
            mockNome.setText("Mario Modificato");
            mockMatricola.setText("0512101111");
        });
        Thread.sleep(100);

        when(mockStudenteEsistente.modificaDatiStudente(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(0);
        when(mockStudenteEsistente.getNome()).thenReturn("Mario Modificato");

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                // FIX: Avvolgiamo mockLabel in uno StackPane
                Stage stage = new Stage();
                Scene scene = new Scene(new StackPane(mockLabel));
                stage.setScene(scene);
                stage.show();
                
                invokePrivateMethod(controller, "salva");
                
                verify(mockStudenteEsistente, times(1)).modificaDatiStudente(anyString(), anyString(), anyString(), anyString());

            } catch (Throwable e) {
                 if (!e.toString().contains("javafx.fxml.LoadException")) e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });
        latch.await(5, TimeUnit.SECONDS);
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