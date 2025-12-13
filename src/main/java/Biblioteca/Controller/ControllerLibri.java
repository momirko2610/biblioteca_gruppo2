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
public class ControllerLibri {
    //popola tableview collega bottone nuovo e modifica 
    
}

private void apriModifica(Libro libro) {
    apriPopup("/fxml/.fxml", controller -> {
        ((ControllerPopupLibro) controller).setLibroDaModificare(libro);
    });
}
private void apriInsert(Libro libro) {
    // due bottoni nuovo e modifica, stessa view diversi controller
    apriPopup("/fxml/.fxml", controller -> {
        ((ControllerPopupLibro) controller).setLibroDaModificare(libro);
    });
}

private void apriDelete(Libro libro) {
    apriPopup("/fxml/popup_elimina.fxml", controller -> {
        ((ControllerDelete) controller).setOggettoDaEliminare(libro);
    });
}