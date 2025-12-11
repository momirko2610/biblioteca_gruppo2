package Biblioteca.Model;



/**
 * @brief Classe che gestisce il database dei prestiti
 * @author Mirko Montella
 * @author Ciro Senese
 * @author Achille Romano
 * @author Sabrina Soriano
 */
public class Prestito {
    /**
     * @brief Costruttore di base
     */
    public Prestito() {};

    /**
     * @brief Aggiorna il database dei prestiti creando un nuovo elemento
     * @pre Il Bibliotecariə deve essere autenticatə
     * @post Lo studente riceve in prestito il libro
     */
    public void registrazionePrestito(){};

    /**
     * @brief Mostra gli elementi presenti nel database dei libri
     * @pre Il Bibliotecariə deve essere autenticatə
     * @post Bibliotecariə visualizza la lista completa dei libri in prestito in ordine alfabetico
     */
    public void visualizzazioneElencoPrestiti(){};

    /**
     * @brief Aggiorna il database dei prestiti eliminando un elemento
     * @pre Il Bibliotecariə deve essere autenticatə
     * @post N/A
     */
    public void registrazioneRestituzione(){};

    /**
     * @brief Verifica se lo studente esiste nel database degli studenti
     * @pre Lo studente deve essere registrato nel database
     * @post Permesso prestito libro
     * @return boolean
     */
    private int checkAccountStudente() {
        return 0;
    };

    /**
     * @brief Verifica se è presente almeno una copia del libro nel database dei libri
     * @pre Accesso database libri e prestiti
     * @post N/A
     * @return numero di copie disponibili/boolean
     */
    private int checkCopieDisponibili() {
        return 0;
    };

    /**
     * @brief Verifica quanti prestiti ha attivo lo studente
     * @pre Accesso database studenti e prestiti
     * @post N/A
     * @return numero di prestiti attivi
     */
    private int checkPrestitiAttiviStudente() {
        return 0;
    };

    /**
     * @brief Verifica se lo studente ha un ritardo nella restituzione di un prestito
     * @pre Accesso database studenti e prestiti
     * @post N/A
     * @return boolean
     */
    private int checkRitardoRestituzionePrestito() {
        return 0;
    };
}




