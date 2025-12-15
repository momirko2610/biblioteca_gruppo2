package Biblioteca.Controller;

import Biblioteca.Model.Bibliotecario;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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

public class ControllerLoginTest {

    private ControllerLogin controller;
    private TextField mockEmailField;
    private PasswordField mockPasswordField;
    private Label mockErroreLabel;

    @BeforeAll
    public static void setUpClass() {
        // Inizializza il toolkit JavaFX una volta per tutti i test
        new JFXPanel(); 
    }


    @BeforeEach
    public void setUp() throws Exception {
        controller = new ControllerLogin();
        
        // Creiamo i componenti UI (devono essere creati nel thread FX per sicurezza)
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            mockEmailField = new TextField();
            mockPasswordField = new PasswordField();
            mockErroreLabel = new Label();
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);

        injectField(controller, "TextFieldEmail", mockEmailField);
        injectField(controller, "TextFieldPassword", mockPasswordField);
        injectField(controller, "errore", mockErroreLabel);
    }

    @AfterEach
    public void tearDown() {
        controller = null;
    }

    // ==========================================
    // TEST 1: Campi Vuoti
    // ==========================================
    @Test
    public void testCampiVuoti() throws Exception {
        // I TextField semplici si possono settare anche fuori dal thread FX se non sono in una scena
        Platform.runLater(() -> {
            mockEmailField.setText("");
            mockPasswordField.setText("");
        });
        Thread.sleep(100); // Piccolo wait per sicurezza

        runOnFxThread(() -> {
            try {
                invokePrivateMethod(controller, "onLoginClick");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Leggiamo il risultato (con Platform.runLater per thread safety)
        AtomicReference<String> testoErrore = new AtomicReference<>();
        Platform.runLater(() -> testoErrore.set(mockErroreLabel.getText()));
        Thread.sleep(100);
        
        assertEquals("Inserisci email e/o password.", testoErrore.get());
    }

    // ==========================================
    // TEST 2: Login Successo
    // ==========================================
    @Test
    public void testLoginSuccesso() throws Exception {
        Platform.runLater(() -> {
            mockEmailField.setText("admin@test.it");
            mockPasswordField.setText("123456");
        });
        
        // Usiamo un AtomicReference per portare fuori dal thread eventuali errori di assertion
        AtomicReference<Throwable> error = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            // APRIAMO IL MOCK DENTRO IL THREAD JAVAFX
            try (MockedConstruction<Bibliotecario> mockedBiblio = Mockito.mockConstruction(Bibliotecario.class,
                    (mock, context) -> {
                        when(mock.loginBibliotecario()).thenReturn(1);
                    })) {

                // Prepariamo la Scena per il cambio pagina
                Stage stage = new Stage();
                Scene scene = new Scene(mockEmailField); // Inseriamo il field nella scena
                stage.setScene(scene);
                stage.show();

                // Eseguiamo il metodo
                invokePrivateMethod(controller, "onLoginClick");

                // Verifichiamo subito (siamo nello stesso thread)
                // Se login ha successo, label errore viene pulita -> ""
                if (!mockErroreLabel.getText().equals("")) {
                    throw new AssertionError("Errore atteso vuoto, trovato: " + mockErroreLabel.getText());
                }

            } catch (Throwable e) {
                // Catturiamo errori (sia eccezioni che errori di assertion)
                // Ignoriamo però IOException da FXMLLoader che è normale nei test
                if (!e.toString().contains("javafx.fxml.LoadException") && !e.toString().contains("Location is not set")) {
                     error.set(e);
                }
            } finally {
                latch.countDown();
            }
        });

        latch.await(5, TimeUnit.SECONDS);
        
        if (error.get() != null) {
            error.get().printStackTrace();
            fail("Test fallito: " + error.get().getMessage());
        }
    }

    // ==========================================
    // TEST 3: Password Errata (Tentativi)
    // ==========================================
    @Test
    public void testLoginFallito() throws Exception {
        Platform.runLater(() -> {
            mockEmailField.setText("admin@test.it");
            mockPasswordField.setText("wrongPass");
        });

        AtomicReference<Throwable> error = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try (MockedConstruction<Bibliotecario> mockedBiblio = Mockito.mockConstruction(Bibliotecario.class,
                    (mock, context) -> {
                        when(mock.loginBibliotecario()).thenReturn(-1);
                    })) {

                invokePrivateMethod(controller, "onLoginClick");

                // Verifica
                String testo = mockErroreLabel.getText();
                if (!testo.contains("Email o password errate")) {
                    throw new AssertionError("Messaggio errato: " + testo);
                }
                
                // Verifica contatore
                Field f = controller.getClass().getDeclaredField("tentativiFalliti");
                f.setAccessible(true);
                int tentativi = (int) f.get(controller);
                if (tentativi != 1) {
                     throw new AssertionError("Tentativi errati: " + tentativi);
                }

            } catch (Throwable e) {
                error.set(e);
            } finally {
                latch.countDown();
            }
        });

        latch.await(5, TimeUnit.SECONDS);
        if (error.get() != null) fail(error.get().getMessage());
    }

    // ==========================================
    // TEST 4: Blocco Account (3 Tentativi)
    // ==========================================
    @Test
    public void testBloccoAccount() throws Exception {
        Platform.runLater(() -> {
            mockEmailField.setText("admin@test.it");
            mockPasswordField.setText("wrongPass");
        });

        AtomicReference<Throwable> error = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try (MockedConstruction<Bibliotecario> mockedBiblio = Mockito.mockConstruction(Bibliotecario.class,
                    (mock, context) -> {
                        when(mock.loginBibliotecario()).thenReturn(-1);
                        when(mock.resetPassword()).thenReturn("NEWPASS123");
                    })) {

                // 3 Tentativi
                invokePrivateMethod(controller, "onLoginClick");
                invokePrivateMethod(controller, "onLoginClick");
                invokePrivateMethod(controller, "onLoginClick");

                String testo = mockErroreLabel.getText();
                // Verifica messaggio di reset
                if (!testo.contains("Password resettata: NEWPASS123")) {
                     throw new AssertionError("Messaggio reset non trovato. Testo attuale: " + testo);
                }

            } catch (Throwable e) {
                error.set(e);
            } finally {
                latch.countDown();
            }
        });

        latch.await(5, TimeUnit.SECONDS);
        if (error.get() != null) fail(error.get().getMessage());
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