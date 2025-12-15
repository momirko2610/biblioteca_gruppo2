package Biblioteca.Controller;

import Biblioteca.Model.Prestito;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ControllerRitornoTest {

    private ControllerRitorno controller;
    private Text mockMessaggio;

    @BeforeAll
    public static void setUpClass() {
        new JFXPanel();
    }

    @BeforeEach
    public void setUp() throws Exception {
        controller = new ControllerRitorno();
        
        mockMessaggio = new Text();
        
        injectField(controller, "messaggio", mockMessaggio);
    }

    @AfterEach
    public void tearDown() {
        controller = null;
    }

    @Test
    public void testSetDatiRestituzione() throws Exception {
        String matricola = "MAT123";
        Long isbn = 987654321L;
        
        Platform.runLater(() -> {
            controller.setDatiRestituzione(matricola, isbn);
            
            String testoGenerato = mockMessaggio.getText();
            String dataOggi = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            
            assertTrue(testoGenerato.contains(matricola));
            assertTrue(testoGenerato.contains(isbn.toString()));
            assertTrue(testoGenerato.contains(dataOggi));
        });
        
        Thread.sleep(500);
    }

    @Test
    public void testConferma() throws Exception {
        AtomicReference<Throwable> error = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try (MockedStatic<Prestito> mockPrestitoStatic = Mockito.mockStatic(Prestito.class)) {
                
                controller.setDatiRestituzione("MAT123", 111L);

                Stage stage = new Stage();
                Scene scene = new Scene(new StackPane(mockMessaggio));
                stage.setScene(scene);
                stage.show();

                invokePrivateMethod(controller, "conferma");

                mockPrestitoStatic.verify(() -> 
                    Prestito.registrazioneRestituzione(eq("MAT123"), eq(111L)), 
                    times(1)
                );

            } catch (Throwable e) {
                error.set(e);
            } finally {
                latch.countDown();
            }
        });

        latch.await(5, TimeUnit.SECONDS);
        if (error.get() != null) fail(error.get().getMessage());
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