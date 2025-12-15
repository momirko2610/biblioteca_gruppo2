package Biblioteca.Model;

import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BibliotecarioTest {
    
    private Bibliotecario bibliotecarioMailErrata;
    private Bibliotecario bibliotecarioPasswordErrata;
    private Bibliotecario bibliotecarioCorretto;
    
    // Definiamo il file temporaneo per i test
    private static final String TEST_FILE_NAME = "database.json";

    public BibliotecarioTest() {
    }
    
    @BeforeEach
    public void setUp() throws IOException {
        // 1. Creiamo un database.json finto per il test
        creaDatabaseDiTest();

        // 2. Inizializziamo gli oggetti
        bibliotecarioMailErrata = new Bibliotecario("mailSbagliata@test.it", "123456789");
        bibliotecarioPasswordErrata = new Bibliotecario("bibliotecario@unisa.it", "passwordSbagliata");
        bibliotecarioCorretto = new Bibliotecario("bibliotecario@unisa.it", "123456789");
    }
    
    @AfterEach
    public void tearDown() {
        // 3. Puliamo cancellando il file creato
        File file = new File(TEST_FILE_NAME);
        if(file.exists()){
            file.delete();
        }
    }

    /**
     * Metodo helper per creare il file JSON
     */
    private void creaDatabaseDiTest() throws IOException {
        String jsonContent = "{\n" +
                "  \"bibliotecari\": [\n" +
                "    {\n" +
                "      \"e_mail\": \"bibliotecario@unisa.it\",\n" +
                "      \"password\": \"123456789\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        
        try (FileWriter writer = new FileWriter(TEST_FILE_NAME)) {
            writer.write(jsonContent);
        }
    }

    @Test
    public void testLoginMailErrata() throws Exception {
        System.out.println("Test: loginBibliotecario con Mail Errata");
        Bibliotecario instance = bibliotecarioMailErrata;
        int expResult = 0; // Deve fallire
        int result = instance.loginBibliotecario();
        assertEquals(expResult, result, "Il login doveva fallire per mail errata");
    }

    @Test
    public void testLoginCorretto() throws Exception {
        System.out.println("Test: loginBibliotecario Corretto");
        Bibliotecario instance = bibliotecarioCorretto;
        int expResult = 1; // Deve avere successo
        int result = instance.loginBibliotecario();
        assertEquals(expResult, result, "Il login doveva avere successo");
    }
    
    @Test
    public void testLoginPasswordErrata() throws Exception {
        System.out.println("Test: loginBibliotecario con Password Errata");
        Bibliotecario instance = bibliotecarioPasswordErrata;
        int expResult = 0; // Deve fallire
        int result = instance.loginBibliotecario();
        assertEquals(expResult, result, "Il login doveva fallire per password errata");
    }
}