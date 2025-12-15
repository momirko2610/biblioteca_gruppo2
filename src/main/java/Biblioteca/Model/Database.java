package Biblioteca.Model;

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
 * @brief Classe che gestisce la creazione dei database
 * @author Sabrina Soriano
 * @author Mirko Montella
 * @author Ciro Senese
 * @author Achille Romano
 */

public class Database {

    /**< Nome del database che verrà creato */
    private static final String NAME = "database.json";
    /**< Oggetto della funzione per la creazione dei file JSON */
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
    private static final Comparator<JsonObject> CONF_TITOLO = (a, b) -> a.get("titolo").getAsString().compareToIgnoreCase(b.get("titolo").getAsString());

    private static final Comparator<JsonObject> CONF_COGNOME = (a, b) -> a.get("cognome").getAsString().compareToIgnoreCase(b.get("cognome").getAsString());

    private static final Comparator<JsonObject> CONF_DATA_PRESTITO = (a, b) -> a.get("dataFinePrevista").getAsString().compareToIgnoreCase(b.get("dataFinePrevista").getAsString());

    /**
     * @brief Metodo che crea il database se non esiste
     * * Controlla l'esistenza del file database.json. Se non esiste, ne crea uno nuovo
     * inizializzando la struttura con array vuoti per libri, studenti, prestiti e bibliotecari
     * @pre Il sistema deve avere i permessi di scrittura
     * @post Viene creato il file database.json
     * @throws IOException Se si verifica un errore durante la scrittura del file.
     */
    public static void creaDatabase() throws IOException {
        File file = new File(NAME);

        if (!file.exists()) {
            //Creiamo un oggetto json con 4 array vuoti al suo interno
            JsonObject label = new JsonObject();
            label.add("libri", new JsonArray());
            label.add("studenti", new JsonArray());
            label.add("prestiti", new JsonArray());
            label.add("bibliotecari", new JsonArray());

            //Scriviamo sul database
            try (FileWriter writer = new FileWriter(NAME)) {
                database.toJson(label, writer); /*!<Scrive sul file*/
            }
        }
    }
    //Implementa leggiDatabaseLibri, leggiDatabaseStudenti, leggiDatabasePrestiti  
    public static List<Libro> leggiDatabaseLibri () throws IOException {
        
        File file = new File(NAME);
        
        JsonObject label;
        try (FileReader reader = new FileReader(file)) {
            //Leggo il database
            label = database.fromJson(reader, JsonObject.class);
            
            //Copio i libri in un array libri
            JsonElement bookArray = label.get("libri");
            
            //Creo una lista di tipo List<Libro>
            Type bookList = new TypeToken<List<Libro>>() {}.getType();
            
            //Converto JsonElement in List<Libro>
            return database.fromJson(bookArray, bookList);
            
        } 
        catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
    
    public static List<Studente> leggiDatabaseStudenti () throws IOException {
        
        File file = new File(NAME);
        
        JsonObject label;
        try (FileReader reader = new FileReader(file)) {
            //Leggo il database
            label = database.fromJson(reader, JsonObject.class);
            
            //Copio i libri in un array libri
            JsonElement studentArray = label.get("studenti");
            
            //Creo una lista di tipo List<Studente>
            Type studentList = new TypeToken<List<Studente>>() {}.getType();
            
            //Converto JsonElement in List<Studente>
            return database.fromJson(studentArray, studentList);
            
        } 
        catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
    
   public static List<Prestito> leggiDatabasePrestiti() throws IOException {
    File file = new File(NAME);

    // Se il file non esiste, ritorno lista vuota invece di null o crash
    if (!file.exists()) {
        return new ArrayList<>();
    }

    try (FileReader reader = new FileReader(file)) {
        // 1. Leggo tutto il JSON
        JsonObject label = database.fromJson(reader, JsonObject.class);

        // 2. Controllo se esiste la chiave "prestiti"
        if (label == null || !label.has("prestiti")) {
            return new ArrayList<>();
        }

        JsonElement loanArray = label.get("prestiti");

        // 3. Converto
        Type loanListType = new TypeToken<List<Prestito>>() {}.getType();
        List<Prestito> lista = database.fromJson(loanArray, loanListType);

        return lista != null ? lista : new ArrayList<>();

    } catch (JsonSyntaxException | JsonIOException e) {
        System.err.println("Errore nel formato del JSON o delle Date:");
        e.printStackTrace(); 
        return new ArrayList<>();
    } catch (Exception e) {
        e.printStackTrace();
        return new ArrayList<>();
    }
   }
   
   /**
     * @param array
     * @param file
     * @param label
     * @throws java.io.IOException
     * @brief ordina i libri presenti nel database per titolo
     * @pre N/A
     * @post Database libri ordinato
     */
    public static void ordinaDatabaseLibro(JsonArray array, File file, JsonObject label) throws IOException {
        if (array == null) return;
        List<JsonObject> bookList = new ArrayList<>();
        for (JsonElement element : array) {
            bookList.add(element.getAsJsonObject());
        }
        
        //Ordino la lista in base al titolo
        bookList.sort(CONF_TITOLO);
        //Inserisco i libri in un Array ordinato
        JsonArray sortedArray = new JsonArray();
        for (JsonObject book : bookList) {
            sortedArray.add(book);
        }
            
        //Aggiorno l'Array
        label.add("libri", sortedArray);

        //Salvo
        try (FileWriter writer = new FileWriter(file)) {
            database.toJson(label, writer);
        }
       
    }
    
    /**
     * @param array
     * @param file
     * @param label
     * @throws java.io.IOException
     * @brief ordina gli studenti presenti nel database per cognome
     * @pre N/A
     * @post Database studenti ordinato
     */
    public static void ordinaDatabaseStudente(JsonArray array, File file, JsonObject label) throws IOException {
        if (array == null) return;
        List<JsonObject> studentList = new ArrayList<>();
        for (JsonElement element : array) {
            studentList.add(element.getAsJsonObject());
        }
        
        //Ordino la lista in base al titolo
        studentList.sort(CONF_COGNOME);
        //Inserisco i libri in un Array ordinato
        JsonArray sortedArray = new JsonArray();
        for (JsonObject student : studentList) {
            sortedArray.add(student);
        }
            
        //Aggiorno l'Array
        label.add("studenti", sortedArray);

        //Salvo
        try (FileWriter writer = new FileWriter(file)) {
            database.toJson(label, writer);
        } 
    }
    
    /**
     * @param array
     * @param file
     * @param label
     * @throws java.io.IOException
     * @brief ordina i prestiti presenti nel database per data prevista di consegna
     * @pre N/A
     * @post Database prestiti ordinato
     */
    public static void ordinaDatabasePrestito(JsonArray array, File file, JsonObject label) throws IOException {
        if (array == null) return;
        List<JsonObject> loanList = new ArrayList<>();
        for (JsonElement element : array) {
            loanList.add(element.getAsJsonObject());
        }
        
        //Ordino la lista in base al titolo
        loanList.sort(CONF_DATA_PRESTITO);
        //Inserisco i libri in un Array ordinato
        JsonArray sortedArray = new JsonArray();
        for (JsonObject loan : loanList) {
            sortedArray.add(loan);
        }
            
        //Aggiorno l'Array
        label.add("prestiti", sortedArray);

        //Salvo
        try (FileWriter writer = new FileWriter(file)) {
            database.toJson(label, writer);
        } 
    }
    
    /**
     * @throws java.io.IOException
     * @brief salva in un JsonObject i libri contenuti nel database
     * @pre deve esistere un database
     * @post JsonObject con i libri contenuti nel database
     */
    
    public static JsonObject leggiDatabase(File file) throws IOException{
        //Ritorno un file JsonObject contenente i libri salvati nel database
        try (FileReader reader = new FileReader(file)) {
            return (database.fromJson(reader, JsonObject.class));
        }
    }
    
     /**
     * @throws java.io.IOException
     * @brief salva dati sul database
     * @pre presenza database
     * @post database aggiornato
     */
    
    public static void salva(File file, JsonObject label) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            database.toJson(label, writer);
        }
    }
}

