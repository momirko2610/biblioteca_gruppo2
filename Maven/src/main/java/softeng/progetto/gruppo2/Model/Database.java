package softeng.progetto.gruppo2.Model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

/**
 * @file Database.java
 * @brief Classe che gestisce i file JSON.
 * Questa classe implementa le funzionalità di accesso ai database di libri, studenti e prestiti
 * @author Sabrina Soriano
 * @author Mirko Montella
 * @author Ciro Senese
 * @author Achille Romano
 */
public class Database {

    /** @brief Nome del file database JSON utilizzato per la persistenza */
    private static final String NAME = "database.json";
    
    /** @brief Configurazione dell'oggetto Gson con adattatori personalizzati per la gestione delle date (LocalDate). */
    private static final Gson database = new GsonBuilder()
            .setPrettyPrinting().registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
                @Override
                public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(src.toString());
                }
            })
            .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
                @Override
                public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return LocalDate.parse(json.getAsString());
                }
            })
            .create();

    /** @brief Comparatore per l'ordinamento dei libri per titolo. */
    private static final Comparator<JsonObject> CONF_TITOLO = (a, b) -> a.get("titolo").getAsString().compareToIgnoreCase(b.get("titolo").getAsString());

    /** @brief Comparatore per l'ordinamento degli studenti per cognome. */
    private static final Comparator<JsonObject> CONF_COGNOME = (a, b) -> a.get("cognome").getAsString().compareToIgnoreCase(b.get("cognome").getAsString());

    /** @brief Comparatore per l'ordinamento dei prestiti per data di restituzione prevista. */
    private static final Comparator<JsonObject> CONF_DATA_PRESTITO = (a, b) -> a.get("dataFinePrevista").getAsString().compareToIgnoreCase(b.get("dataFinePrevista").getAsString());

    /**
     * @brief Crea il database se non esiste inizializzandolo con dati predefiniti.
     * Controlla l'esistenza del file database.json. Se assente, crea la struttura con array vuoti
     * @pre Il sistema deve avere i permessi di scrittura nella cartella di esecuzione.
     * @post Viene creato il file database.json con la struttura base.
     * @throws IOException Se si verifica un errore durante la scrittura del file.
     */
    public static void creaDatabase() throws IOException {
        File file = new File(NAME);

        if (!file.exists()) {
            JsonObject label = new JsonObject();
            label.add("libri", new JsonArray());
            label.add("studenti", new JsonArray());
            label.add("prestiti", new JsonArray());
            
            JsonArray lib = new JsonArray();
            JsonObject librarian = new JsonObject();
            librarian.addProperty("e_mail", "bibliotecario@unisa.it");
            librarian.addProperty("password", "123456789");

            lib.add(librarian);
            label.add("bibliotecari", lib);
            
            try (FileWriter writer = new FileWriter(NAME)) {
                database.toJson(label, writer);
            }
        }
    }

    /**
     * @brief Legge l'elenco dei libri dal database.
     * @return List<Libro> Elenco dei libri salvati o null in caso di errore.
     * @throws IOException In caso di problemi di lettura del file.
     */
    public static List<Libro> leggiDatabaseLibri () throws IOException {
        File file = new File(NAME);
        JsonObject label;
        try (FileReader reader = new FileReader(file)) {
            label = database.fromJson(reader, JsonObject.class);
            JsonElement bookArray = label.get("libri");
            Type bookList = new TypeToken<List<Libro>>() {}.getType();
            return database.fromJson(bookArray, bookList);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
    
    /**
     * @brief Legge l'elenco degli studenti dal database.
     * @return List<Studente> Elenco degli studenti salvati o null in caso di errore.
     * @throws IOException In caso di problemi di lettura del file.
     */
    public static List<Studente> leggiDatabaseStudenti () throws IOException {
        File file = new File(NAME);
        JsonObject label;
        try (FileReader reader = new FileReader(file)) {
            label = database.fromJson(reader, JsonObject.class);
            JsonElement studentArray = label.get("studenti");
            Type studentList = new TypeToken<List<Studente>>() {}.getType();
            return database.fromJson(studentArray, studentList);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
    
    /**
     * @brief Legge l'elenco dei prestiti attivi dal database.
     * @return List<Prestito> Elenco dei prestiti registrati.
     * @throws IOException In caso di problemi di lettura del file.
     */
   public static List<Prestito> leggiDatabasePrestiti() throws IOException {
    File file = new File(NAME);

    if (!file.exists()) {
        return new ArrayList<>();
    }

    try (FileReader reader = new FileReader(file)) {
        JsonObject label = database.fromJson(reader, JsonObject.class);
        if (label == null || !label.has("prestiti")) {
            return new ArrayList<>();
        }
        JsonElement loanArray = label.get("prestiti");
        Type loanListType = new TypeToken<List<Prestito>>() {}.getType();
        List<Prestito> lista = database.fromJson(loanArray, loanListType);
        return lista != null ? lista : new ArrayList<>();
    } catch (Exception e) {
        e.printStackTrace();
        return new ArrayList<>();
    }
   }
   
    /**
     * @brief Ordina i libri per titolo e aggiorna il file JSON.
     * @param array JsonArray contenente i libri.
     * @param file Riferimento al file database.
     * @param label JsonObject radice del database.
     * @throws IOException In caso di errori durante il salvataggio.
     */
    public static void ordinaDatabaseLibro(JsonArray array, File file, JsonObject label) throws IOException {
        if (array == null) return;
        List<JsonObject> bookList = new ArrayList<>();
        for (JsonElement element : array) {
            bookList.add(element.getAsJsonObject());
        }
        bookList.sort(CONF_TITOLO);
        JsonArray sortedArray = new JsonArray();
        for (JsonObject book : bookList) {
            sortedArray.add(book);
        }
        label.add("libri", sortedArray);
        salva(file, label);
    }
    
    /**
     * @brief Ordina gli studenti per cognome e aggiorna il file JSON.
     * @param array JsonArray contenente gli studenti.
     * @param file Riferimento al file database.
     * @param label JsonObject radice del database.
     * @throws IOException In caso di errori durante il salvataggio.
     */
    public static void ordinaDatabaseStudente(JsonArray array, File file, JsonObject label) throws IOException {
        if (array == null) return;
        List<JsonObject> studentList = new ArrayList<>();
        for (JsonElement element : array) {
            studentList.add(element.getAsJsonObject());
        }
        studentList.sort(CONF_COGNOME);
        JsonArray sortedArray = new JsonArray();
        for (JsonObject student : studentList) {
            sortedArray.add(student);
        }
        label.add("studenti", sortedArray);
        salva(file, label);
    }
    
    /**
     * @brief Ordina i prestiti per data di restituzione e aggiorna il file JSON.
     * @param array JsonArray contenente i prestiti.
     * @param file Riferimento al file database.
     * @param label JsonObject radice del database.
     * @throws IOException In caso di errori durante il salvataggio.
     */
    public static void ordinaDatabasePrestito(JsonArray array, File file, JsonObject label) throws IOException {
        if (array == null) return;
        List<JsonObject> loanList = new ArrayList<>();
        for (JsonElement element : array) {
            loanList.add(element.getAsJsonObject());
        }
        loanList.sort(CONF_DATA_PRESTITO);
        JsonArray sortedArray = new JsonArray();
        for (JsonObject loan : loanList) {
            sortedArray.add(loan);
        }
        label.add("prestiti", sortedArray);
        salva(file, label);
    }
    
    /**
     * @brief Legge l'intero contenuto del database come JsonObject.
     * Utilizzato per operazioni di modifica massiva dei dati.
     * @param file Il file JSON da leggere.
     * @return JsonObject Il database completo in formato JSON.
     * @throws IOException In caso di errori di lettura.
     */
    public static JsonObject leggiDatabase(File file) throws IOException{
        try (FileReader reader = new FileReader(file)) {
            return (database.fromJson(reader, JsonObject.class));
        }
    }
    
    /**
     * @brief Salva lo stato corrente del JsonObject sul file json.
     * * @param file Il file di destinazione.
     * @param label Il JsonObject contenente i dati aggiornati.
     * @throws IOException In caso di errori di scrittura sul disco.
     */
    public static void salva(File file, JsonObject label) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            database.toJson(label, writer);
        }
    }
}