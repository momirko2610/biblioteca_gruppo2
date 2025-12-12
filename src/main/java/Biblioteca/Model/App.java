package Biblioteca.Model;

//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.stage.Stage;

//import java.io.IOException;

//import com.google.gson.*;
//import java.io.FileWriter;
import com.google.gson.JsonArray;
import java.io.IOException;
import java.util.List;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * JavaFX App
 */

public class App extends Application {

    static void setRoot(String secondary) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void start(Stage stage) {
        //Crea il database se non esiste
        try {
            Database.creaDatabase();
        } catch (IOException exception1) {
            exception1.printStackTrace();
            return; // esce se non riesce a creare il database
        }
        /*
        try {
            Bibliotecario.inserisciDatiBibliotecario();
        } catch (IOException exception1) {
            exception1.printStackTrace();
            return; // esce se non riesce a creare il database
        }
        */
        
       
        //Homepage
        VBox root = new VBox(10);
        
        //Opzione inserisci libri
        Button enterStudent = new Button("Inserisci Studente");
        //Opzione inserisci libri
        Button enterBook = new Button("Inserisci Libro");
        //Opzione cerca un libro specifico
        Button findStudentNum = new Button("Cerca Studente Matricola");
        //Opzione cerca un libro specifico
        Button findStudentSur = new Button("Cerca Studente Cognome");
        //Opzione cerca un libro specifico
        Button findBook = new Button("Cerca Libro ISBN");
        //Opzione cerca un libro specifico
        Button findBookT = new Button("Cerca Libro Titolo");
        //Opzione cerca un libro specifico
        Button findBookA = new Button("Cerca Libro Autore (nome e  cognome)");
        //Opzione visualizza l'elenco di tutti i libri presenti nel database
        Button listStudent = new Button("Visualizza Lista Studenti");
        //Opzione elimina un libro presente nel database
        Button deleteStudent = new Button("Elimina Studente");
        //Opzione modifica un libro presente nel database
        Button modifyStudent = new Button("Modifica Studente");
        //Opzione modifica un libro presente nel database
        Button loginLibrarian = new Button("Login Bibliotecario");
        
        //Aggiunge all'home page solo il bottone inserisci libri
        root.getChildren().addAll(enterStudent, enterBook, findStudentNum, findStudentSur, listStudent, deleteStudent, modifyStudent, loginLibrarian, findBook, findBookT, findBookA);
     
        //Quando premo il bottone inserisci libro
        enterStudent.setOnAction(e -> {
            //Pulisco la schermata
            root.getChildren().clear();
            
            //Creo le label
            Label instruction = new Label("Inserisci i dati dello studente e premi Conferma:");
            Label instruction1 = new Label("NOME:");
            Label instruction2 = new Label("COGNOME:");
            Label instruction3 = new Label("MATRICOLA:");
            Label instruction4 = new Label("E_MAIL:");
            
            //Creo i campi per inserire i valori
            TextField tf1 = new TextField();
            tf1.setPromptText("Scrivi nome...");
        
            TextField tf2 = new TextField();
            tf2.setPromptText("Scrivi cognome...");
            
            TextField tf3 = new TextField();
            tf3.setPromptText("Scrivi matricola...");
        
            TextField tf4 = new TextField();
            tf4.setPromptText("Scrivi e_mail...");

            Button confirm = new Button("Conferma");
            
            //Quando premo conferma
            confirm.setOnAction(event -> {
                String nome = tf1.getText();
                String cognome = tf2.getText();
                String matricola = tf3.getText();
                String e_mail = tf4.getText();

                Studente studente = new Studente(nome, cognome, matricola, e_mail);

                try {
                    studente.inserisciDatiStudente();
                    System.out.println("Studente inserito!");
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            });
            
             //Aggiungo gli elementi alla schermata
            root.getChildren().addAll(instruction, instruction1, tf1, instruction2, tf2, instruction3, tf3, instruction4, tf4, confirm);
        });
        //Quando premo il bottone inserisci libro
        enterBook.setOnAction(e -> {
            //Pulisco la schermata
            root.getChildren().clear();
            
            //Creo le label
            Label instruction = new Label("Inserisci i dati del libro e premi Conferma:");
            Label instruction1 = new Label("TITOLO:");
            Label instruction2 = new Label("AUTORE:");
            Label instruction3 = new Label("ANNO DI PUBBLICAZIONE:");
            Label instruction4 = new Label("ISBN:");
            
            //Creo i campi per inserire i valori
            TextField tf1 = new TextField();
            tf1.setPromptText("Scrivi titolo...");
        
            TextField tf2 = new TextField();
            tf2.setPromptText("Scrivi autore...");
            
            TextField tf3 = new TextField();
            tf3.setPromptText("Scrivi anno di pubblicazione...");
        
            TextField tf4 = new TextField();
            tf4.setPromptText("Scrivi ISBN...");

            Button confirm = new Button("Conferma");
            
            //Quando premo conferma
            confirm.setOnAction(event -> {
                String titolo = tf1.getText();
                String autore = tf2.getText();
                int annoPub = Integer.parseInt(tf3.getText());
                long ISBN = Long.parseLong(tf4.getText());

                Libro libro = new Libro(titolo, autore, annoPub, ISBN);

                try {
                    libro.inserisciLibro();
                    System.out.println("Libro inserito!");
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            });
            
             //Aggiungo gli elementi alla schermata
            root.getChildren().addAll(instruction, instruction1, tf1, instruction2, tf2, instruction3, tf3, instruction4, tf4, confirm);
        });
        
        //Quando premo il bottone cerca libro
        findStudentNum.setOnAction(e -> {
            //Pulisco la schermata
            root.getChildren().clear();
            
            //Creo le label
            Label instruction = new Label("Inserisci la matricola dello studente e premi Conferma:");
            Label instruction1 = new Label("MATRICOLA:");
            
            //Creo i campi per inserire i valori
            TextField tf1 = new TextField();
            tf1.setPromptText("Scrivi matricola...");
        
            Button confirm = new Button("Conferma");
            
            //Quando premo conferma
            confirm.setOnAction(event -> {
                String matricola = tf1.getText();
                
                try {
                    Studente.cercaStudenteMatricola(matricola);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            });
            
             //Aggiungo gli elementi alla schermata
            root.getChildren().addAll(instruction, instruction1, tf1, confirm);
            
        });
        
        //Quando premo il bottone cerca libro
        findStudentSur.setOnAction(e -> {
            //Pulisco la schermata
            root.getChildren().clear();
            
            //Creo le label
            Label instruction = new Label("Inserisci il cognome dello studente e premi Conferma:");
            Label instruction1 = new Label("COGNOME:");
            
            //Creo i campi per inserire i valori
            TextField tf1 = new TextField();
            tf1.setPromptText("Scrivi cognome...");
        
            Button confirm = new Button("Conferma");
            
            //Quando premo conferma
            confirm.setOnAction(event -> {
                String cognome = tf1.getText();
                
                try {
                    Studente.cercaStudenteCognome(cognome);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            });
            
             //Aggiungo gli elementi alla schermata
            root.getChildren().addAll(instruction, instruction1, tf1, confirm);
            
        });
        
        //Quando premo il bottone cerca libro
        findBook.setOnAction(e -> {
            //Pulisco la schermata
            root.getChildren().clear();
            
            //Creo le label
            Label instruction = new Label("Inserisci l'ISBN dello studente e premi Conferma:");
            Label instruction1 = new Label("ISBN:");
            
            //Creo i campi per inserire i valori
            TextField tf1 = new TextField();
            tf1.setPromptText("Scrivi ISBN...");
        
            Button confirm = new Button("Conferma");
            
            //Quando premo conferma
            confirm.setOnAction(event -> {
                Long ISBN = Long.parseLong(tf1.getText());
                
                try {
                    Libro.cercaLibroISBN(ISBN);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            });
            
             //Aggiungo gli elementi alla schermata
            root.getChildren().addAll(instruction, instruction1, tf1, confirm);
            
        });
        
        
        //Quando premo il bottone cerca libro
        findBookT.setOnAction(e -> {
            //Pulisco la schermata
            root.getChildren().clear();
            
            //Creo le label
            Label instruction = new Label("Inserisci il Titolo e premi Conferma:");
            Label instruction1 = new Label("Titolo:");
            
            //Creo i campi per inserire i valori
            TextField tf1 = new TextField();
            tf1.setPromptText("Scrivi Titolo...");
        
            Button confirm = new Button("Conferma");
            
            //Quando premo conferma
            confirm.setOnAction(event -> {
                String titolo = tf1.getText();
                
                try {
                    Libro.cercaLibroTitolo(titolo);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            });
            
             //Aggiungo gli elementi alla schermata
            root.getChildren().addAll(instruction, instruction1, tf1, confirm);
            
        });
        
        //Quando premo il bottone cerca libro
        findBookA.setOnAction(e -> {
            //Pulisco la schermata
            root.getChildren().clear();
            
            //Creo le label
            Label instruction = new Label("Inserisci l'autore e premi Conferma:");
            Label instruction1 = new Label("Autore:");
            
            //Creo i campi per inserire i valori
            TextField tf1 = new TextField();
            tf1.setPromptText("Scrivi Autore...");
        
            Button confirm = new Button("Conferma");
            
            //Quando premo conferma
            confirm.setOnAction(event -> {
                String autore = tf1.getText();
                
                try {
                    Libro.cercaLibroAutore(autore);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            });
            
             //Aggiungo gli elementi alla schermata
            root.getChildren().addAll(instruction, instruction1, tf1, confirm);
            
        });

        
        //Quando premo il bottone visualizza lista libri
        listStudent.setOnAction(e -> {

            try {
                Studente.visualizzazioneElencoStudenti();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
        
        //Quando premo il bottone elimina il libro
        deleteStudent.setOnAction(e -> {
            //Pulisco la schermata
            root.getChildren().clear();
            
            //Creo il menù a tendina
            List<Studente> students;
            try {
                students = Database.leggiDatabaseStudenti();
            } catch (IOException exception1) {
                exception1.printStackTrace();
                return; // esce se non riesce a leggere il database
            }
            ComboBox<Studente> menu = new ComboBox<>();
            menu.getItems().addAll(students);
            
            Label instruction6 = new Label("Seleziona uno studente...");
            
            root.getChildren().addAll(instruction6, menu);
            
            menu.setOnAction(event -> {
                Studente student = menu.getValue();
               
                try {
                    student.cancellazioneDatiStudente();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }              
            });
            
        });
        
        //Quando premo il bottone modifica libro
        modifyStudent.setOnAction(e -> {
            //Pulisco la schermata
            root.getChildren().clear();
            
            //Creo il menù a tendina
            List<Studente> students;
            try {
                students = Database.leggiDatabaseStudenti();
            } catch (IOException exception1) {
                exception1.printStackTrace();
                return; // esce se non riesce a leggere il database
            }
            ComboBox<Studente> menu = new ComboBox<>();
            menu.getItems().addAll(students);
            
            Label instruction6 = new Label("Seleziona un libro...");
            
            root.getChildren().addAll(instruction6, menu);
            
            menu.setOnAction(event -> {
                Studente student = menu.getValue();
                //Creo le label
                Label instruction = new Label("Modifica i dati dello studente:");
                Label instruction1 = new Label("NUOVO NOME:");
                Label instruction2 = new Label("NUOVO COGNOME:");
                Label instruction3 = new Label("NUOVA MATRICOLA:");
                Label instruction4 = new Label("NUOVA E_MAIL:");
                
                
                //Creo i campi per inserire i valori
                TextField tf1 = new TextField();
                tf1.setPromptText("Scrivi nuovo nome...");
                TextField tf2 = new TextField();
                tf2.setPromptText("Scrivi nuovo cognome...");
                TextField tf3 = new TextField();
                tf3.setPromptText("Scrivi nuova matricola...");
                TextField tf4 = new TextField();
                tf4.setPromptText("Scrivi nuova e_mail...");
                
                Button confirm = new Button("Conferma");
                
                //Quando premo conferma
                confirm.setOnAction(ev -> {
                    String newName = tf1.getText();
                    String newSurname = tf2.getText();
                    String newNum = tf3.getText();
                    String newE_Mail = tf4.getText();
                    
                    try {
                        student.modificaDatiStudente(newName, newSurname, newNum, newE_Mail);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                });
                //Aggiungo gli elementi alla schermata
                root.getChildren().addAll(instruction, instruction1, tf1, instruction2, tf2, instruction3, tf3, instruction4, tf4, confirm);
                
            });
        });
        
        //Quando premo il bottone inserisci libro
        loginLibrarian.setOnAction(e -> {
            //Pulisco la schermata
            root.getChildren().clear();
            
            //Creo le label
            Label instruction = new Label("Inserisci e_mail e password e premi Conferma:");
            Label instruction1 = new Label("E_MAIL:");
            Label instruction2 = new Label("PASSWORD:");
            
            //Creo i campi per inserire i valori
            TextField tf1 = new TextField();
            tf1.setPromptText("Scrivi e_mail...");
        
            TextField tf2 = new TextField();
            tf2.setPromptText("Scrivi password...");
            
            Button confirm = new Button("Conferma");
            
            //Quando premo conferma
            confirm.setOnAction(event -> {
                String e_mail = tf1.getText();
                String password = tf2.getText();
                
                Bibliotecario bibliotecario = new Bibliotecario(e_mail, password);

                try {
                    int i = bibliotecario.loginBibliotecario();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            });
            
             //Aggiungo gli elementi alla schermata
            root.getChildren().addAll(instruction, instruction1, tf1, instruction2, tf2,confirm);
        });
        
        Scene scene = new Scene(root, 320, 500);
        stage.setScene(scene);
        stage.setTitle("Esempio JavaFX");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
