package software.service;

import software.domain.compiti.Compito;
import software.domain.menu.Menu;
import software.domain.menu.SezioneMenu;
import software.domain.ricette.Ricetta;
import software.domain.utenti.Chef;
import software.domain.utenti.Cuoco;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Servizio per la gestione dei compiti della cucina.
 * Implementa le operazioni del caso d'uso "Gestione dei Compiti della cucina".
 */
public class CompitiCucinaService {
    private final List<Compito> compiti = new ArrayList<>();
    private final AtomicInteger nextCompitoId = new AtomicInteger(1);
    
    /**
     * Crea un riepilogo dei compiti basato su un menu.
     * Riferimento: UC "Gestione dei compiti della cucina" - Passo 1
     * @param menu Il menu per cui creare i compiti
     * @return Lista dei compiti creati
     */
    public List<Compito> creaRiepilogoCompiti(Menu menu) {
        List<Compito> compitiMenu = new ArrayList<>();
        
        // Per ogni ricetta nel menu, crea un compito (senza assegnare chef)
        for (SezioneMenu sezione : menu.getSezioni()) {
            for (Ricetta ricetta : sezione.getRicette()) {
                // Non assegna ancora un chef o un turno specifico
                compitiMenu.add(new Compito(
                    nextCompitoId.getAndIncrement(),
                    ricetta,
                    null, // chef non ancora assegnato
                    null, // turno non ancora assegnato
                    60, // durata di default (60 minuti)
                    1   // quantità di default (1 porzione)
                ));
            }
        }
        
        compiti.addAll(compitiMenu);
        return compitiMenu;
    }
    
    /**
     * Assegna un compito a un cuoco specifico in un turno.
     * Riferimento: UC "Gestione dei compiti della cucina" - Passo 3
     * @param idCompito ID del compito da assegnare
     * @param cuoco Il cuoco a cui assegnare il compito
     * @param turno Il turno in cui eseguire il compito
     * @param durata La durata stimata del compito in minuti
     * @param quantita La quantità da preparare
     * @return Il compito aggiornato, null se il compito non esiste
     */
    public Compito assegnaCompito(int idCompito, Cuoco cuoco, String turno, int durata, int quantita) {
        Compito compito = getCompitoById(idCompito);
        
        if (compito != null) {
            compito.setCuocoAssegnato(cuoco);
            compito.setTurno(turno);
            compito.setDurata(durata);
            compito.setQuantita(quantita);
        }
        
        return compito;
    }
    
    /**
     * Ordina i compiti per importanza.
     * Riferimento: UC "Gestione dei compiti della cucina" - Passo 4
     * @param listaCompiti Lista dei compiti da ordinare
     * @return Lista ordinata di compiti
     */
    public List<Compito> ordinaCompiti(List<Compito> listaCompiti) {
        // Crea una copia per non modificare la lista originale
        List<Compito> compitiOrdinati = new ArrayList<>(listaCompiti);
        
        // Ordina i compiti per importanza (decrescente)
        Collections.sort(compitiOrdinati, Comparator.comparing(Compito::getImportanza).reversed());
        
        return compitiOrdinati;
    }
    
    /**
     * Finalizza il piano dei compiti per un menu.
     * Riferimento: UC "Gestione dei compiti della cucina" - Passo 7
     * @param compitiDaFinalizzare Lista dei compiti da finalizzare
     */
    public void finalizzaPiano(List<Compito> compitiDaFinalizzare) {
        for (Compito compito : compitiDaFinalizzare) {
            // Verifica che il compito abbia un cuoco assegnato
            if (compito.getCuocoAssegnato() == null) {
                throw new IllegalStateException("Non è possibile finalizzare un compito senza un cuoco assegnato");
            }
            
            // Verifica che il compito abbia un turno assegnato
            if (compito.getTurno() == null) {
                throw new IllegalStateException("Non è possibile finalizzare un compito senza un turno assegnato");
            }
        }
        
        // Se tutti i controlli sono passati, imposta lo stato a "Pianificato"
        for (Compito compito : compitiDaFinalizzare) {
            compito.setStato("Pianificato");
        }
    }
    
    /**
     * Verifica lo stato di avanzamento dei compiti.
     * Riferimento: UC "Gestione dei compiti della cucina" - Passo 8
     * @param listaCompiti Lista dei compiti da verificare
     * @return Percentuale di completamento (0-100)
     */
    public int monitoraAvanzamento(List<Compito> listaCompiti) {
        if (listaCompiti.isEmpty()) return 0;
        
        int completati = 0;
        
        for (Compito compito : listaCompiti) {
            if ("Completato".equals(compito.getStato())) {
                completati++;
            }
        }
        
        return (completati * 100) / listaCompiti.size();
    }
    
    /**
     * Ottiene un compito tramite il suo ID.
     * @param id L'ID del compito da cercare
     * @return Il compito trovato, null se non esiste
     */
    public Compito getCompitoById(int id) {
        for (Compito compito : compiti) {
            if (compito.getId() == id) {
                return compito;
            }
        }
        return null;
    }
    
    /**
     * Ottiene tutti i compiti gestiti dal servizio.
     * @return Lista di tutti i compiti
     */
    public List<Compito> getCompiti() {
        return new ArrayList<>(compiti);
    }
} 