package software.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import software.domain.utenti.Chef;
import software.domain.utenti.Cuoco;

public class UtenteService {
    private final ObservableList<Chef> chefs = FXCollections.observableArrayList();
    private final ObservableList<Cuoco> cuochi = FXCollections.observableArrayList();
    private int nextUtenteId = 1;
    
    public Chef creaChef(String nome, String cognome, String email, String telefono) {
        Chef nuovoChef = new Chef(nextUtenteId++, nome, cognome, email, telefono);
        chefs.add(nuovoChef);
        return nuovoChef;
    }
    
    public Cuoco creaCuoco(String nome, String cognome, String email, String telefono) {
        Cuoco nuovoCuoco = new Cuoco(nextUtenteId++, nome, cognome, email, telefono);
        cuochi.add(nuovoCuoco);
        return nuovoCuoco;
    }
    
    public ObservableList<Chef> getChefs() {
        return chefs;
    }
    
    public ObservableList<Cuoco> getCuochi() {
        return cuochi;
    }
    
    public void creaUtentiDiEsempio() {
        // Crea chef e cuochi di esempio
        // per test e demo dell'applicazione
        creaChef("Mario", "Rossi", "mario.rossi@example.com", "1234567890");
        creaCuoco("Luigi", "Verdi", "luigi.verdi@example.com", "0987654321");
        creaCuoco("Anna", "Bianchi", "anna.bianchi@example.com", "1122334455");
    }
}