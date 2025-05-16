package software.domain.exceptions;

/**
 * Eccezione specifica per errori nel dominio applicativo.
 * Questa classe personalizzata di eccezione permette di distinguere
 * tra errori di dominio ed errori tecnici/infrastrutturali.
 */
public class DomainException extends RuntimeException {
    
    /**
     * Crea una nuova DomainException con il messaggio specificato.
     * 
     * @param message Il messaggio di errore
     */
    public DomainException(String message) {
        super(message);
    }
    
    /**
     * Crea una nuova DomainException con il messaggio e la causa specificati.
     * 
     * @param message Il messaggio di errore
     * @param cause La causa dell'eccezione
     */
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Crea una nuova DomainException con la causa specificata.
     * 
     * @param cause La causa dell'eccezione
     */
    public DomainException(Throwable cause) {
        super(cause);
    }
} 