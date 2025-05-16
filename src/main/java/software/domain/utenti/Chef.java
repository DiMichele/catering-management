package software.domain.utenti;

import software.domain.compiti.Compito;
import software.domain.compiti.Turno;
import software.domain.menu.Menu;
import software.domain.ricette.Ricetta;

/**
 * Rappresenta uno chef nel sistema di catering.
 * Questo è un ruolo specializzato di Utente con funzionalità aggiuntive.
 */
public class Chef extends Utente {
    private static final long serialVersionUID = 1L;
    
    public Chef(int id, String nome, String cognome, String email, String telefono) {
        super(id, nome, cognome, email, telefono);
    }
    
    /**
     * Crea un nuovo menu.
     * Riferimento: UC "Gestione dei Menù" - Passo 1
     */
    public Menu creaNuovoMenu(int idMenu) {
        return new Menu(idMenu, this);
    }
    
    /**
     * Assegna un compito a un cuoco.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Passo 3
     */
    public Compito assegnaCompito(int idCompito, Cuoco cuoco, 
                                String turno, 
                                Ricetta ricetta, 
                                int durata, 
                                int quantita) {
        return new Compito(idCompito, ricetta, cuoco, turno, durata, quantita);
    }
    
    @Override
    public String toString() {
        return "Chef " + getNome() + " " + getCognome();
    }
}