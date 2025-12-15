package Biblioteca.Controller;

import Biblioteca.Model.Bibliotecario;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
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

public class ControllerResetPasswordTest {

    private ControllerResetPassword controller;
    
    private TextField mockTxtNuovaPass;
    private Label mockErrore;

    @BeforeAll
    public static void setUpClass() {
        new JFXPanel(); 
    }

    @BeforeEach
    public void setUp() throws Exception {
        controller = new ControllerResetPassword();
        
        mockTxtNuovaPass = new TextField();
        mockErrore = new Label();

        injectField(controller, "txtNuovaPass", mockTxtNuovaPass);
        injectField(controller, "errore", mockErrore);
    }
    
    @AfterEach
    public void tearDown() {
        controller = null;
    }

    @Test
    public void testPasswordVuota() throws Exception {
        Platform.runLater(() -> mockTxtNuovaPass.setText(""));
        
        runOnFxThread(() -> {
            try {
                invokePrivateMethod(controller, "confermaCambio");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        AtomicReference<String> errorText = new AtomicReference<>();
        runOnFxThread(() -> errorText.set(mockErrore.getText()));
        
        assertEquals("Inserisci una password valida", errorText.get());
    }

    @Test
    public void testCambioPasswordSuccesso() throws Exception {
        controller.setDatiUtente("admin@test.it");
        
        Platform.runLater(() -> mockTxtNuovaPass.setText("NuovaPassword123"));

        AtomicReference<Throwable> error = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try (MockedConstruction<Bibliotecario> mockedBiblio = Mockito.mockConstruction(Bibliotecario.class,
                    (mock, context) -> {
                        when(mock.cambiaPassword(anyString())).thenReturn(1);
                    })) {

                Stage stage = new Stage();
                Scene scene = new Scene(new StackPane(mockTxtNuovaPass)); 
                stage.setScene(scene);
                stage.show();

                invokePrivateMethod(controller, "confermaCambio");

                if (mockedBiblio.constructed().isEmpty()) {
                    throw new AssertionError("Costruttore Bibliotecario non chiamato");
                }
                
                Bibliotecario b = mockedBiblio.constructed().get(0);
                verify(b, times(1)).cambiaPassword("NuovaPassword123");
                

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
    public void testCambioPasswordFallito() throws Exception {
        controller.setDatiUtente("inesistente@test.it");
        Platform.runLater(() -> mockTxtNuovaPass.setText("Pass123"));

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try (MockedConstruction<Bibliotecario> mockedBiblio = Mockito.mockConstruction(Bibliotecario.class,
                    (mock, context) -> {
                        when(mock.cambiaPassword(anyString())).thenReturn(-1);
                    })) {

                invokePrivateMethod(controller, "confermaCambio");

                if (!mockErrore.getText().contains("Impossibile trovare l'utente")) {
                    throw new AssertionError("Messaggio errore errato: " + mockErrore.getText());
                }

            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });
        latch.await(5, TimeUnit.SECONDS);
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