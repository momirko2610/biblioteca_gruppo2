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
import javafx.scene.control.Label;
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
    private Label mockErrore;

    @BeforeAll
    public static void setUpClass() {
        new JFXPanel(); // Inizializza il toolkit JavaFX
    }

    @BeforeEach
    public void setUp() throws Exception {
        // Eseguiamo il setup nel thread FX per sicurezza
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller = new ControllerPopupPrestiti();
                
                mockComboLibri = new ComboBox<>();
                mockComboStudenti = new ComboBox<>();
                mockDatePicker = new DatePicker();
                mockLabel = new Text();
                mockErrore = new Label();

                injectField(controller, "comboLibri", mockComboLibri);
                injectField(controller, "comboStudenti", mockComboStudenti);
                injectField(controller, "datePickerRestituzione", mockDatePicker);
                injectField(controller, "label", mockLabel);
                injectField(controller, "errore", mockErrore);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });
        latch.await(5, TimeUnit.SECONDS);
    }

    @AfterEach
    public void tearDown() {
        controller = null;
    }

    @Test
    public void testCaricamentoDati() throws Exception {
        AtomicReference<Throwable> error = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        // Eseguiamo TUTTO il test nel thread JavaFX
        Platform.runLater(() -> {
            // Mockiamo il database DENTRO il thread
            try (MockedStatic<Database> mockDatabase = Mockito.mockStatic(Database.class)) {
                
                // 1. Dati finti
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

                // 2. Configurazione Mock DB
                mockDatabase.when(Database::leggiDatabaseLibri).thenReturn(libri);
                mockDatabase.when(Database::leggiDatabaseStudenti).thenReturn(studenti);
                mockDatabase.when(Database::creaDatabase).thenAnswer(i -> null);

                // 3. Esecuzione metodo
                controller.initialize();

                // 4. Verifiche (Assert)
                // Controlliamo che le liste siano state popolate
                assertEquals(1, mockComboLibri.getItems().size(), "La combo libri dovrebbe avere 1 elemento");
                assertEquals("123 - Java Book", mockComboLibri.getItems().get(0));
                
                assertEquals(1, mockComboStudenti.getItems().size(), "La combo studenti dovrebbe avere 1 elemento");
                assertEquals("MAT001 - Rossi", mockComboStudenti.getItems().get(0));

            } catch (Throwable t) {
                error.set(t); // Catturiamo eventuali errori
            } finally {
                latch.countDown(); // Sblocchiamo il test
            }
        });

        // Il test aspetta qui che il thread FX finisca
        boolean completed = latch.await(5, TimeUnit.SECONDS);
        assertTrue(completed, "Il test è andato in timeout!");
        
        // Se c'è stato un errore nel thread FX, lo lanciamo qui per far fallire il test
        if (error.get() != null) {
            throw new Exception("Errore nel thread FX: " + error.get().getMessage(), error.get());
        }
    }

    @Test
    public void testSalvaPrestito_CopieEsaurite() throws Exception {
        AtomicReference<Throwable> error = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try (MockedStatic<Database> mockDatabase = Mockito.mockStatic(Database.class)) {
                
                // Setup DB Mock
                setupMockDatabase(mockDatabase);
                
                controller.initialize(); // Carica le combo

                // Setup Input Utente
                mockComboLibri.setValue("999 - Test Book");
                mockComboStudenti.setValue("TEST01 - Bianchi");
                mockDatePicker.setValue(LocalDate.now().plusDays(10));
                
                // Stage fittizio per evitare NullPointer sulla scena
                Stage stage = new Stage();
                stage.setScene(new Scene(new StackPane(mockLabel))); 
                stage.show();

                // Mock del costruttore di Prestito
                try (MockedConstruction<Prestito> mockedPrestito = Mockito.mockConstruction(Prestito.class,
                        (mock, context) -> {
                            // Simuliamo che il metodo ritorni -4 (Copie Esaurite)
                            when(mock.registrazionePrestito(anyString(), anyLong())).thenReturn(-4);
                        })) {

                    // Eseguiamo il salvataggio
                    invokePrivateMethod(controller, "salva");

                    // Verifichiamo che la label mostri l'errore corretto
                    assertEquals("Copie esaurite per questo libro!", mockErrore.getText());
                }

            } catch (Throwable e) {
                error.set(e);
            } finally {
                latch.countDown();
            }
        });
        
        latch.await(5, TimeUnit.SECONDS);
        if (error.get() != null) throw new Exception(error.get());
    }

    // --- Metodi di utilità ---

    private void setupMockDatabase(MockedStatic<Database> mockDatabase) {
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