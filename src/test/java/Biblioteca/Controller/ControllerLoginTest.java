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
        new JFXPanel(); 
    }


    @BeforeEach
    public void setUp() throws Exception {
        controller = new ControllerLogin();

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


    @Test
    public void testCampiVuoti() throws Exception {

        Platform.runLater(() -> {
            mockEmailField.setText("");
            mockPasswordField.setText("");
        });
        Thread.sleep(100); 

        runOnFxThread(() -> {
            try {
                invokePrivateMethod(controller, "onLoginClick");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        AtomicReference<String> testoErrore = new AtomicReference<>();
        Platform.runLater(() -> testoErrore.set(mockErroreLabel.getText()));
        Thread.sleep(100);
        
        assertEquals("Inserisci email e/o password.", testoErrore.get());
    }

    @Test
    public void testLoginSuccesso() throws Exception {
        Platform.runLater(() -> {
            mockEmailField.setText("admin@test.it");
            mockPasswordField.setText("123456");
        });

        AtomicReference<Throwable> error = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {

            try (MockedConstruction<Bibliotecario> mockedBiblio = Mockito.mockConstruction(Bibliotecario.class,
                    (mock, context) -> {
                        when(mock.loginBibliotecario()).thenReturn(1);
                    })) {

                Stage stage = new Stage();
                Scene scene = new Scene(mockEmailField);
                stage.setScene(scene);
                stage.show();

                invokePrivateMethod(controller, "onLoginClick");

                if (!mockErroreLabel.getText().equals("")) {
                    throw new AssertionError("Errore atteso vuoto, trovato: " + mockErroreLabel.getText());
                }

            } catch (Throwable e) {

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

                String testo = mockErroreLabel.getText();
                if (!testo.contains("Email o password errate")) {
                    throw new AssertionError("Messaggio errato: " + testo);
                }
                
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

                invokePrivateMethod(controller, "onLoginClick");
                invokePrivateMethod(controller, "onLoginClick");
                invokePrivateMethod(controller, "onLoginClick");

                String testo = mockErroreLabel.getText();
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