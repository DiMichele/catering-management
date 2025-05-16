package software.service;

import software.domain.compiti.Compito;
import software.domain.compiti.Turno;
import software.domain.eventi.Evento;
import software.domain.ricette.Ricetta;
import software.domain.utenti.Chef;
import software.domain.utenti.Cuoco;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * Servizio per la gestione dei compiti di cucina.
 * Implementa le operazioni del caso d'uso "Gestione dei Compiti della cucina".
 */
public class CompitoCucinaService {
    private final ObservableList<Compito> compiti = FXCollections.observableArrayList();
    private final ObservableList<Turno> turni = FXCollections.observableArrayList();
    private int nextCompitoId = 1;
    private int nextTurnoId = 1;
    
    /**
     * Crea un riepilogo dei compiti per un evento.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Passo 1
     */
    public ObservableList<Compito> creaRiepilogoCompiti(Evento evento) {
        if (evento == null) {
            return FXCollections.observableArrayList(); // Lista vuota se l'evento è nullo
        }
        
        // Ottieni le ricette associate all'evento
        List<Ricetta> ricetteEvento = new ArrayList<>();
        evento.getServizi().stream()
            .filter(servizio -> servizio.getMenu() != null)
            .forEach(servizio -> 
                servizio.getMenu().getSezioni().stream()
                    .flatMap(sezione -> sezione.getRicette().stream())
                    .forEach(ricetteEvento::add)
            );
        
        if (ricetteEvento.isEmpty()) {
            // Log di debug
            System.out.println("Non sono state trovate ricette per l'evento: " + evento.getNome());
            // Se non ci sono ricette, per scopi dimostrativi, includiamo tutti i compiti
            // Questo permette all'interfaccia di funzionare anche con dati incompleti
            return FXCollections.observableArrayList(compiti);
        }
        
        // Debug: stampa le ricette trovate
        System.out.println("Ricette trovate per l'evento " + evento.getNome() + ": " + ricetteEvento.size());
        for (Ricetta r : ricetteEvento) {
            System.out.println(" - " + r.getNome());
        }
        
        // Filtra i compiti per ricette dell'evento
        List<Compito> compitiEvento = compiti.stream()
            .filter(compito -> 
                compito.getRicetta() != null && 
                ricetteEvento.contains(compito.getRicetta()))
            .collect(Collectors.toList());
        
        System.out.println("Compiti filtrati per l'evento: " + compitiEvento.size());
        
        // Se non ci sono compiti per questo evento, creiamo compiti di esempio
        if (compitiEvento.isEmpty() && !ricetteEvento.isEmpty()) {
            System.out.println("Nessun compito trovato per l'evento, si potrebbero creare compiti di esempio");
        }
        
        return FXCollections.observableArrayList(compitiEvento);
    }
    
    /**
     * Restituisce i cuochi disponibili per un turno.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Passo 2
     */
    public List<Cuoco> getCuochiDisponibili(Turno turno) {
        // In questa fase di sviluppo, assumiamo che tutti i cuochi siano disponibili
        // In un'implementazione completa, qui ci sarebbe la logica per filtrare i cuochi in base alle disponibilità
        return FXCollections.observableArrayList(
            compiti.stream()
                .map(Compito::getCuocoAssegnato)
                .filter(cuoco -> cuoco != null)
                .distinct()
                .collect(Collectors.toList())
        );
    }
    
    /**
     * Assegna un compito a un cuoco.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Passo 3
     */
    public Compito assegnaCompito(Chef chef, Cuoco cuoco, String turno, 
                                  Ricetta ricetta, int tempoStimato, double quantita) {
        Compito nuovoCompito = chef.assegnaCompito(nextCompitoId++, cuoco, turno, ricetta, tempoStimato, (int)quantita);
        compiti.add(nuovoCompito);
        return nuovoCompito;
    }
    
    /**
     * Ordina i compiti per importanza.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Passo 4
     */
    public ObservableList<Compito> ordinaCompitiPerImportanza(List<Compito> compitiDaOrdinare) {
        // Logica di ordinamento (esempio: prima le ricette con lunghe lievitazioni)
        ObservableList<Compito> compitiOrdinati = FXCollections.observableArrayList(compitiDaOrdinare);
        compitiOrdinati.sort((c1, c2) -> c2.getTempoStimato() - c1.getTempoStimato());
        return compitiOrdinati;
    }
    
    /**
     * Verifica se un turno è pieno.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Estensione 5a
     */
    public boolean isTurnoPieno(Turno turno) {
        if (turno == null) {
            return false;
        }
        
        String turnoStr = turno.getData() + " " + turno.getOraInizio() + "-" + turno.getOraFine();
        int tempoTotale = compiti.stream()
            .filter(compito -> turnoStr.equals(compito.getTurno()))
            .mapToInt(Compito::getTempoStimato)
            .sum();
        
        // Calcola tempo disponibile nel turno in minuti
        long minutiDisponibili = java.time.Duration.between(
            turno.getOraInizio(), turno.getOraFine()).toMinutes();
            
        // Se il tempo totale supera l'80% del tempo disponibile, consideriamo il turno come pieno
        return tempoTotale >= (minutiDisponibili * 0.8);
    }
    
    /**
     * Monitora l'avanzamento dei compiti.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Passo 6
     */
    public ObservableList<Compito> monitoraAvanzamento() {
        return compiti;
    }
    
    /**
     * Controlla lo stato dei compiti a fine turno.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Passo 7
     */
    public List<Compito> controllaStatoTurno(Turno turno) {
        String turnoStr = turno.getData() + " " + turno.getOraInizio() + "-" + turno.getOraFine();
        return compiti.stream()
            .filter(compito -> turnoStr.equals(compito.getTurno()))
            .collect(Collectors.toList());
    }
    
    /**
     * Crea un nuovo turno.
     */
    public Turno creaTurno(java.time.LocalDate data, java.time.LocalTime oraInizio, 
                        java.time.LocalTime oraFine, String luogo, String tipo) {
        Turno nuovoTurno = new Turno(nextTurnoId++, data, oraInizio, oraFine, luogo, tipo);
        turni.add(nuovoTurno);
        return nuovoTurno;
    }
    
    public ObservableList<Compito> getCompiti() {
        return compiti;
    }
    
    public ObservableList<Turno> getTurni() {
        return turni;
    }
}