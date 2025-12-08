/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biblioteca;

import com.google.gson.*;
import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author ssabr
 */


public class Database {
    
    private static final String NAME = "database.json";
    private static final Gson database = new GsonBuilder().setPrettyPrinting().create();
    
    //se il file database.json non esiste lo crea, altrimenti non fa nulla
    
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
                database.toJson(label, writer);
            }
            
        }
    }
}
