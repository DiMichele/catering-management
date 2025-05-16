package software.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import software.domain.eventi.Evento;
import software.domain.eventi.Servizio;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EventoService {
    private final ObservableList<Evento> eventi = FXCollections.observableArrayList();
    private int nextEventoId = 1;
    private int nextServizioId = 1;
    
    public Evento creaEvento(String nome, LocalDate dataInizio, LocalDate dataFine, 
                           String luogo, int numeroDiPersone) {
        Evento nuovoEvento = new Evento(nextEventoId++, nome, dataInizio, dataFine, luogo, numeroDiPersone);
        eventi.add(nuovoEvento);
        return nuovoEvento;
    }
    
    public Servizio creaServizio(Evento evento, String tipo, LocalDateTime dataOraInizio, 
                               LocalDateTime dataOraFine, String luogo) {
        Servizio nuovoServizio = new Servizio(nextServizioId++, tipo, dataOraInizio, dataOraFine, luogo);
        evento.aggiungiServizio(nuovoServizio);
        return nuovoServizio;
    }
    
    public ObservableList<Evento> getEventi() {
        return eventi;
    }
    
    public void creaEventiDiEsempio() {
        // Crea eventi di esempio
        // per test e demo dell'applicazione
        LocalDate oggi = LocalDate.now();
        
        Evento evento1 = creaEvento("Matrimonio Bianchi", oggi.plusDays(30), oggi.plusDays(30), 
                                   "Villa Aurora", 120);
        creaServizio(evento1, "Pranzo", 
                   LocalDateTime.of(oggi.plusDays(30).getYear(), 
                                  oggi.plusDays(30).getMonthValue(), 
                                  oggi.plusDays(30).getDayOfMonth(), 12, 30), 
                   LocalDateTime.of(oggi.plusDays(30).getYear(), 
                                  oggi.plusDays(30).getMonthValue(), 
                                  oggi.plusDays(30).getDayOfMonth(), 16, 30), 
                   "Sala Principale");
        
        Evento evento2 = creaEvento("Conferenza Aziendale", oggi.plusDays(15), oggi.plusDays(16), 
                                   "Hotel Excelsior", 80);
        creaServizio(evento2, "Coffee Break", 
                   LocalDateTime.of(oggi.plusDays(15).getYear(), 
                                  oggi.plusDays(15).getMonthValue(), 
                                  oggi.plusDays(15).getDayOfMonth(), 10, 30), 
                   LocalDateTime.of(oggi.plusDays(15).getYear(), 
                                  oggi.plusDays(15).getMonthValue(), 
                                  oggi.plusDays(15).getDayOfMonth(), 11, 30), 
                   "Sala Conferenze");
    }
}