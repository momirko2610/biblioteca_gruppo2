package Biblioteca.Controller;

import Biblioteca.Model.Database;
import Biblioteca.Model.Libro;
import Biblioteca.Model.Prestito;
import Biblioteca.Model.Studente;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ControllerPopupPrestitiTest {

    private ControllerPopupPrestiti controller;
    
    private ComboBox<String> mockComboLibri;
    private ComboBox<String> mockComboStudenti;
    private DatePicker mockDatePicker;
    private Text mockLabel;

    @BeforeAll
    public static void setUpClass() {
        new JFXPanel(); 
    }

    @BeforeEach
    public void setUp() throws Exception {
        controller = new ControllerPopupPrestiti();
        
        mockComboLibri = new ComboBox<>();
        mockComboStudenti = new ComboBox<>();
        mockDatePicker = new DatePicker();
        mockLabel = new Text();

        injectField(controller, "comboLibri", mockComboLibri);
        injectField(controller, "comboStudenti", mockComboStudenti);
        injectField(controller, "datePickerRestituzione", mockDatePicker);
        injectField(controller, "label", mockLabel);
    }

    @AfterEach
    public void tearDown() {
        controller = null;
    }


    @Test
    public void testCaricamentoDati() throws Exception {
        try (MockedStatic<Database> mockDatabase = Mockito.mockStatic(Database.class)) {
            List<Libro> libri = new ArrayList<>();
            Libro l1 = mock(Libro.class);
            when(l1.getIsbn()).thenReturn(123L);
            when(l1.getTitolo()).thenReturn("Java Book");
            libri.add(l1);

            List<Studente> studenti = new ArrayList<>();
            Studente s1 = mock(Studente.class);
            when(s1.getMatricola()).thenReturn("MAT001");
            when(s1.getCognome()).thenReturn("Rossi");
            studenti.add(s1);

            mockDatabase.when(Database::leggiDatabaseLibri).thenReturn(libri);
            mockDatabase.when(Database::leggiDatabaseStudenti).thenReturn(studenti);
            mockDatabase.when(Database::creaDatabase).thenAnswer(i -> null);

            controller.initialize();


            assertEquals(1, mockComboLibri.getItems().size());
            assertEquals("123 - Java Book", mockComboLibri.getItems().get(0));
            
            assertEquals(1, mockComboStudenti.getItems().size());
            assertEquals("MAT001 - Rossi", mockComboStudenti.getItems().get(0));
        }
    }


    @Test
    public void testSalvaPrestito() throws Exception {
        AtomicReference<Throwable> error = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try (MockedStatic<Database> mockDatabase = Mockito.mockStatic(Database.class)) {

                List<Libro> libri = new ArrayList<>();
                Libro l1 = mock(Libro.class);
                when(l1.getIsbn()).thenReturn(999L);
                when(l1.getTitolo()).thenReturn("Test Book");
                libri.add(l1);

                List<Studente> studenti = new ArrayList<>();
                Studente s1 = mock(Studente.class);
                when(s1.getMatricola()).thenReturn("TEST01");
                when(s1.getCognome()).thenReturn("Bianchi");
                studenti.add(s1);

                mockDatabase.when(Database::leggiDatabaseLibri).thenReturn(libri);
                mockDatabase.when(Database::leggiDatabaseStudenti).thenReturn(studenti);
                mockDatabase.when(Database::creaDatabase).thenAnswer(i -> null);
                
                controller.initialize();

                mockComboLibri.setValue("999 - Test Book");
                mockComboStudenti.setValue("TEST01 - Bianchi");
                mockDatePicker.setValue(LocalDate.now().plusDays(10));
                
                Stage stage = new Stage();
                Scene scene = new Scene(new StackPane(mockLabel)); 
                stage.setScene(scene);
                stage.show();

                try (MockedConstruction<Prestito> mockedPrestito = Mockito.mockConstruction(Prestito.class,
                        (mock, context) -> {
                            doNothing().when(mock).registrazionePrestito(anyString(), anyLong());
                        })) {

                    invokePrivateMethod(controller, "salva");

                    if (mockedPrestito.constructed().isEmpty()) {
                         throw new AssertionError("Nessun Prestito creato.");
                    }
                    Prestito p = mockedPrestito.constructed().get(0);
                    verify(p, times(1)).registrazionePrestito(eq("TEST01"), eq(999L));
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