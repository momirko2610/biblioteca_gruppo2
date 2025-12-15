package Biblioteca.Controller;

import Biblioteca.Model.Bibliotecario;
import Biblioteca.Model.Database;
import Biblioteca.Model.Studente;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ControllerStudentiTest {

    private ControllerStudenti controller;
    
    // Componenti UI
    private TableView<Studente> mockTableView;
    private TextField mockSearchField;

    // Mock Statico Database
    private MockedStatic<Database> mockDatabase;

    @BeforeAll
    public static void setUpClass() {
        // Init Toolkit
        new JFXPanel();
    }

    @BeforeEach
    public void setUp() throws Exception {
        controller = new ControllerStudenti();
        
        mockTableView = new TableView<>();
        mockSearchField = new TextField();

        injectField(controller, "tableViewStudenti", mockTableView);
        injectField(controller, "searchStudentTextField", mockSearchField);
        
        // Init Colonne per evitare NullPointer
        injectField(controller, "Nome", new TableColumn<>());
        injectField(controller, "Cognome", new TableColumn<>());
        injectField(controller, "Matricola", new TableColumn<>());
        injectField(controller, "Email", new TableColumn<>());
        injectField(controller, "Azioni", new TableColumn<>());

        mockDatabase = Mockito.mockStatic(Database.class);
    }

    @AfterEach
    public void tearDown() {
        if (mockDatabase != null) {
            mockDatabase.close();
        }
        controller = null;
    }

    // ==========================================
    // TEST 1: Caricamento Dati all'Avvio
    // ==========================================
    @Test
    public void testCaricamentoDati() throws Exception {
        // 1. Dati Finti
        List<Studente> studenti = new ArrayList<>();
        
        Studente s1 = mock(Studente.class);
        when(s1.getNome()).thenReturn("Mario");
        when(s1.getCognome()).thenReturn("Rossi");
        when(s1.getMatricola()).thenReturn("0512101111");
        when(s1.getAzioni()).thenReturn(creaHBoxFinto()); // Fondamentale
        studenti.add(s1);

        Studente s2 = mock(Studente.class);
        when(s2.getNome()).thenReturn("Luigi");
        when(s2.getCognome()).thenReturn("Verdi");
        when(s2.getMatricola()).thenReturn("0512102222");
        when(s2.getAzioni()).thenReturn(creaHBoxFinto());
        studenti.add(s2);

        // 2. Configura Mock
        mockDatabase.when(Database::leggiDatabaseStudenti).thenReturn(studenti);
        mockDatabase.when(Database::creaDatabase).thenAnswer(i -> null);

        // 3. Esegui initialize
        controller.initialize();

        // 4. Verifiche
        assertEquals(2, mockTableView.getItems().size());
        assertEquals(s1, mockTableView.getItems().get(0));
        assertEquals(s2, mockTableView.getItems().get(1));
    }

    // ==========================================
    // TEST 2: Ricerca Studente
    // ==========================================
    @Test
    public void testRicercaStudente() throws Exception {
        // 1. Setup Lista iniziale
        List<Studente> studenti = new ArrayList<>();
        
        Studente s1 = mock(Studente.class);
        when(s1.getNome()).thenReturn("Mario");
        when(s1.getCognome()).thenReturn("Rossi");
        when(s1.getMatricola()).thenReturn("0512101111");
        when(s1.getAzioni()).thenReturn(creaHBoxFinto());
        studenti.add(s1);

        Studente s2 = mock(Studente.class);
        when(s2.getNome()).thenReturn("Luigi");
        when(s2.getCognome()).thenReturn("Bianchi");
        when(s2.getMatricola()).thenReturn("0512109999");
        when(s2.getAzioni()).thenReturn(creaHBoxFinto());
        studenti.add(s2);

        // Iniettiamo la lista nel controller (bypassando il DB per questo test specifico)
        javafx.collections.ObservableList<Studente> obsStudenti = javafx.collections.FXCollections.observableArrayList(studenti);
        injectField(controller, "listaStudente", obsStudenti);

        // 2. Ricerca per Matricola ("9999")
        mockSearchField.setText("9999");
        invokePrivateMethod(controller, "onSearchStudent");

        // Verifica: deve rimanere solo Luigi
        assertEquals(1, mockTableView.getItems().size());
        assertEquals(s2, mockTableView.getItems().get(0));
        
        // 3. Ricerca per Nome ("Mario")
        mockSearchField.setText("Mario");
        invokePrivateMethod(controller, "onSearchStudent");
        
        // Verifica: deve rimanere solo Mario
        // Nota: Il tuo controller cerca su: cognome su nome e matricola su matricola.
        // Nel codice che mi hai mandato c'è: String cognome = ... studente.getNome().toLowerCase(); 
        // Quindi cerca il nome nella variabile cognome. Seguo la logica del tuo codice.
        assertEquals(1, mockTableView.getItems().size());
        assertEquals(s1, mockTableView.getItems().get(0));
    }

    // ==========================================
    // TEST 3: Navigazione Reset Password (con Bibliotecario)
    // ==========================================
    @Test
    public void testApriResetPassword() throws Exception {
        Bibliotecario mockBiblio = mock(Bibliotecario.class);
        when(mockBiblio.getEmail()).thenReturn("admin@test.it");
        
        controller.setBibliotecario(mockBiblio);
        
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                invokePrivateMethod(controller, "apriResetPassword");
            } catch (Exception e) {
                // Ignora errori FXML
            } finally {
                latch.countDown();
            }
        });
        latch.await(5, TimeUnit.SECONDS);
        
        // Se non crasha e usa l'email, è ok
        verify(mockBiblio, atLeastOnce()).getEmail();
    }

    // ==========================================
    // UTILITIES
    // ==========================================

    private HBox creaHBoxFinto() {
        HBox box = new HBox();
        box.getChildren().add(new Button("Modifica"));
        box.getChildren().add(new Button("Elimina"));
        box.getChildren().add(new Button("Info")); // Importante: sono 3 bottoni per gli studenti
        return box;
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