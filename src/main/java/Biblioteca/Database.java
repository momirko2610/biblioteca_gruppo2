package Biblioteca;

import com.google.gson.*;
import java.io.*;
import java.util.ArrayList;

/**
 * @brief Classe che gestisce la creazione dei database
 * @author Mirko Montella
 * @author Ciro Senese
 * @author Achille Romano
 * @author Sabrina Soriano
 */

public class Database {

    /**< Nome del database che verrà creato */
    private static final String NAME = "database.json";
    /**< Oggetto della funzione per la creazione dei file JSON */
    private static final Gson database = new GsonBuilder().setPrettyPrinting().create(); /*!<Oggetto della funzione per la creazione dei file JSON*/

    //se il file database.json non esiste lo crea, altrimenti non fa nulla
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
}
