package software.domain.utenti;

import software.domain.compiti.Disponibilita;
import software.domain.ricette.Ricetta;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Cuoco extends Utente {
    private final List<Disponibilita> disponibilita = new ArrayList<>();
    
    public Cuoco(int id, String nome, String cognome, String email, String telefono) {
        super(id, nome, cognome, email, telefono);
    }
    
    /**
     * Fornisce disponibilità per una data e orario.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Estensione 2a
     */
    public Disponibilita fornisceDisponibilita(int idDisponibilita, LocalDate data, 
                                             LocalTime oraInizio, 
                                             LocalTime oraFine) {
        Disponibilita nuovaDisponibilita = new Disponibilita(idDisponibilita, data, oraInizio, oraFine);
        disponibilita.add(nuovaDisponibilita);
        return nuovaDisponibilita;
    }
    
    /**
     * Crea una nuova ricetta.
     */
    public Ricetta creaRicetta(int idRicetta, String nome) {
        return new Ricetta(idRicetta, nome, null);
    }
    
    public List<Disponibilita> getDisponibilita() {
        return new ArrayList<>(disponibilita);
    }
}