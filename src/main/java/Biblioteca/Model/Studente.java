package Biblioteca.Model;



import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @brief Classe che gestisce il database degli studenti
 * @author Mirko Montella
 * @author Ciro Senese
 * @author Achille Romano
 * @author Sabrina Soriano
 */
public class Studente {
    private String nome; /*!<Nome dello studente*/
    private String cognome; /*!<Cognome dello studente*/
    private int matricola; /*!<matricola dello studente*/
    private String e_mail; /*!<e-mail dello studente*/
    /**< Nome del database che verrà creato */
    private static final String NAME = "database.json";
    /**< Oggetto della funzione per la creazione dei file JSON */
    private static final Gson database = new GsonBuilder().setPrettyPrinting().create();

    /**
     * @brief Costruttore di base
     * @param nome nome dello studente
     * @param cognome nome dello studente
     * @param matricola matricola nome dello studente
     * @param e_mail e-mail nome dello studente
     */
    public Studente(String nome, String cognome, int matricola, String e_mail) {
        this.nome = nome;
        this.cognome = cognome;
        this.matricola = matricola;
        this.e_mail = e_mail;
    }

    /**
     * @brief Aggiorna il database degli studenti creando un nuovo elemento
     * @pre Il Bibliotecariə deve essere autenticatə
     * @post Il database contenente l’elenco degli studenti è aggiornato.
     */
    public void inserisciDatiStudente() {};

    /**
     * @brief Aggiorna il database degli studenti modificando un elemento
     * @pre Il Bibliotecariə deve essere autenticatə
     * @post Il database contenente l’elenco degli studenti è aggiornato.
     */
    public void modificaDatiStudente() {};

    /**
     * @brief Aggiorna il database degli studenti eliminando un elemento
     * @pre Il Bibliotecariə deve essere autenticatə
     * @post Il database contenente l’elenco degli studenti è aggiornato.
     */
    public void cancellazioneDatiStudente () {};

    /**
     * @brief Mostra gli elementi presenti nel database degli studentui
     * @pre Il Bibliotecariə deve essere autenticatə
     * @post Bibliotecariə visualizza la lista completa degli studenti in ordine alfabetico
     */
    public void visualizzazioneElencoStudenti() {};

    /**
     * @brief Mostra l'elemento cercato dal database degli studenti
     * @pre lo studente è presente nel database
     * @post il bibliotecariə visualizza le informazioni dello studente cercato
     */
    public void stampaStudente(){};

    /**
     * @brief Permette allo studente di prenotare un libro da ritirare in biblioteca
     * @pre N/A
     * @post Prenota un libro da ritirare in biblioteca
     */
    public void prenotazioneLibro (){};

    /**
     * @brief Cerca un elemento dal database degli studenti
     * @pre N/A
     * @post Bibliotecariə visualizza a schermo i dati dello studente selezionato
     * @return posizione del libro nel database o -1 in caso di libro non presente
     */
    private int ricercaStudente() {
        return 0;
    };
}
