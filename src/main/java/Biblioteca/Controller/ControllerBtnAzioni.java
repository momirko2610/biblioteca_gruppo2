/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biblioteca.Controller;

/**
 *
 * @author achil
 */
/*
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class ControllerBtnAzioni {

    // Collega la colonna dell'FXML a questa variabile
    @FXML
    private TableColumn<Libro, Void> colAzioni;

    @FXML
    public void initialize() {
        // Configuriamo la CellFactory per la colonna Azioni
        addButtonToTable();
    }

    private void addButtonToTable() {
        Callback<TableColumn<Libro, Void>, TableCell<Libro, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Libro, Void> call(final TableColumn<Libro, Void> param) {
                return new TableCell<>() {

                    // 1. Definiamo i bottoni
                    private final Button btnModifica = new Button();
                    private final Button btnElimina = new Button();
                    private final HBox pane = new HBox(10); // Spazio tra i bottoni

                    {
                        // 2. Carichiamo le icone (Assicurati che i percorsi siano corretti)
                        // Usa getResourceAsStream per caricare dalla cartella resources
                        ImageView iconEdit = new ImageView(new Image(getClass().getResourceAsStream("/Biblioteca/icons/pencil-filed.png")));
                        ImageView iconDelete = new ImageView(new Image(getClass().getResourceAsStream("/Biblioteca/icons/trash-filed.png")));

                        // Impostiamo dimensioni icone (opzionale se l'immagine è già piccola)
                        iconEdit.setFitWidth(16); iconEdit.setFitHeight(16);
                        iconDelete.setFitWidth(16); iconDelete.setFitHeight(16);

                        // Assegniamo l'icona al bottone
                        btnModifica.setGraphic(iconEdit);
                        btnElimina.setGraphic(iconDelete);
                        
                        // Rimuoviamo lo stile standard del bottone per lasciare solo l'icona (opzionale)
                        btnModifica.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                        btnElimina.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

                        // 3. Azioni al click
                        btnModifica.setOnAction(event -> {
                            Libro libro = getTableView().getItems().get(getIndex());
                            gestisciPopup("Modifica", libro);
                        });

                        btnElimina.setOnAction(event -> {
                            Libro libro = getTableView().getItems().get(getIndex());
                            gestisciPopup("Elimina", libro);
                        });

                        pane.getChildren().addAll(btnModifica, btnElimina);
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(pane);
                        }
                    }
                };
            }
        };

        colAzioni.setCellFactory(cellFactory);
    }

    private void gestisciPopup(String azione, Libro libro) {
        System.out.println(azione + ": " + libro.getTitolo());
        // Qui scriverai il codice per aprire il Dialog/Popup
    }
}
*/