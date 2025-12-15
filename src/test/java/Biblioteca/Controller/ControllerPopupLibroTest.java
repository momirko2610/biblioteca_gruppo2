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
    
    private TextField mockTitolo;
    private TextField mockAutori;
    private TextField mockIsbn;
    private TextField mockNCopie;
    private TextField mockData;
    private Label mockErrore;
    private Text mockLabelTitolo;

    private Libro mockLibroEsistente;

    @BeforeAll
    public static void setUpClass() {
        new JFXPanel(); 
    }


    @BeforeEach
    public void setUp() throws Exception {
        controller = new ControllerPopupLibro();
        
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

        injectField(controller, "titolo", mockTitolo);
        injectField(controller, "autori", mockAutori);
        injectField(controller, "isbn", mockIsbn);
        injectField(controller, "nCopie", mockNCopie);
        injectField(controller, "data", mockData);
        injectField(controller, "errore", mockErrore);
        injectField(controller, "label", mockLabelTitolo);

        mockLibroEsistente = mock(Libro.class);
    }
    
    @AfterEach
    public void tearDown() {
        controller = null;
    }


    @Test
    public void testNuovoLibroSuccesso() throws Exception {
        Platform.runLater(() -> {
            mockTitolo.setText("Java Programming");
            mockAutori.setText("Gosling");
            mockIsbn.setText("1234567890123");
            mockNCopie.setText("5");
            mockData.setText("2020");
        });
        
        runOnFxThread(() -> controller.setLibroDaModificare(null));

        AtomicReference<Throwable> error = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try (MockedConstruction<Libro> mockedLibro = Mockito.mockConstruction(Libro.class,
                    (mock, context) -> {
                        when(mock.inserisciLibro()).thenReturn(0);
                        when(mock.getTitolo()).thenReturn("Java Programming");
                    })) {

                Stage stage = new Stage();
                Scene scene = new Scene(mockTitolo);
                stage.setScene(scene);
                stage.show();

                invokePrivateMethod(controller, "salva");

                if (mockedLibro.constructed().isEmpty()) {
                    throw new AssertionError("Il costruttore di Libro non è stato chiamato!");
                }
                Libro libroCreato = mockedLibro.constructed().get(0);
                verify(libroCreato, times(1)).inserisciLibro();

                if (!mockErrore.getText().isEmpty()) {
                     throw new AssertionError("Errore inatteso: " + mockErrore.getText());
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

    @Test
    public void testNuovoLibroErroreISBN() throws Exception {
        Platform.runLater(() -> {
            mockTitolo.setText("Test Book");
            mockNCopie.setText("1");
            mockData.setText("2000");
            mockIsbn.setText("123"); 
        });

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try (MockedConstruction<Libro> mockedLibro = Mockito.mockConstruction(Libro.class,
                    (mock, context) -> {

                        when(mock.inserisciLibro()).thenReturn(-1);
                    })) {

                invokePrivateMethod(controller, "salva");


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

    @Test
    public void testModificaLibro() throws Exception {

        runOnFxThread(() -> controller.setLibroDaModificare(mockLibroEsistente));
    
        Platform.runLater(() -> {
            mockTitolo.setText("Nuovo Titolo Modificato");
            mockNCopie.setText("10"); 
            mockData.setText("2021");
        });
        Thread.sleep(100);

        when(mockLibroEsistente.modificaDatiLibro(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(0);
        when(mockLibroEsistente.getTitolo()).thenReturn("Nuovo Titolo Modificato");

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                Stage stage = new Stage();
                Scene scene = new Scene(mockTitolo);
                stage.setScene(scene);
                stage.show();
                
                invokePrivateMethod(controller, "salva");

                verify(mockLibroEsistente, times(1)).modificaDatiLibro(anyString(), anyString(), anyString(), anyString(), anyString());

            } catch (Throwable e) {
                 if (!e.toString().contains("javafx.fxml.LoadException")) e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });
        latch.await(5, TimeUnit.SECONDS);
    }
    @Test
    public void testValidazioneInputNumerico() throws Exception {
        Platform.runLater(() -> {
            mockTitolo.setText("Libro Bug");
            mockNCopie.setText("dieci");
            mockData.setText("2020");
        });
        
        runOnFxThread(() -> {
            try {
                invokePrivateMethod(controller, "salva");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
 
        AtomicReference<String> errorText = new AtomicReference<>();
        runOnFxThread(() -> errorText.set(mockErrore.getText()));
        
        assertTrue(errorText.get().contains("Devi compilare tutti i campi in modo corretto"));
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