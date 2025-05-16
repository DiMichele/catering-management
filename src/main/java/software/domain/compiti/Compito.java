package software.domain.compiti;

import software.domain.ricette.Ricetta;
import software.domain.utenti.Cuoco;
import javafx.beans.property.SimpleStringProperty;

/**
 * Rappresenta un compito di cucina.
 * Un compito è un'attività che deve essere eseguita per preparare una ricetta.
 */
public class Compito {
    private int id;
    private Ricetta ricetta;
    private Cuoco cuocoAssegnato;
    private String turno;
    private int durata; // durata in minuti
    private int quantita;
    private String stato = "Da iniziare";
    private int importanza = 1; // 1-5, dove 5 è la massima importanza
    
    public Compito(int id, Ricetta ricetta, Cuoco cuocoAssegnato, String turno, int durata, int quantita) {
        this.id = id;
        this.ricetta = ricetta;
        this.cuocoAssegnato = cuocoAssegnato;
        this.turno = turno;
        this.durata = durata;
        this.quantita = quantita;
    }
    
    public int getId() {
        return id;
    }
    
    public Ricetta getRicetta() {
        return ricetta;
    }
    
    public Cuoco getCuocoAssegnato() {
        return cuocoAssegnato;
    }
    
    public String getTurno() {
        return turno;
    }
    
    public int getDurata() {
        return durata;
    }
    
    public int getQuantita() {
        return quantita;
    }
    
    public String getStato() {
        return stato;
    }
    
    public void setStato(String stato) {
        this.stato = stato;
    }
    
    // Metodo di supporto per JavaFX binding
    public SimpleStringProperty statoProperty() {
        return new SimpleStringProperty(stato);
    }
    
    public int getImportanza() {
        return importanza;
    }
    
    public void setImportanza(int importanza) {
        if (importanza < 1 || importanza > 5) {
            throw new IllegalArgumentException("L'importanza deve essere tra 1 e 5");
        }
        this.importanza = importanza;
    }
    
    public void setDurata(int durata) {
        this.durata = durata;
    }
    
    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }
    
    public void setCuocoAssegnato(Cuoco cuocoAssegnato) {
        this.cuocoAssegnato = cuocoAssegnato;
    }
    
    public void setTurno(String turno) {
        this.turno = turno;
    }
    
    // Alias per compatibilità con il vecchio codice
    public int getTempoStimato() {
        return durata;
    }
    
    // Metodi per la compatibilità con il vecchio Controller
    public void aggiornaStato(String nuovoStato) {
        setStato(nuovoStato);
    }
    
    public void registraFeedback(String feedback) {
        // Metodo segnaposto per compatibilità
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Compito compito = (Compito) o;
        return id == compito.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    
    @Override
    public String toString() {
        return "Compito{" +
                "id=" + id +
                ", ricetta='" + ricetta.getNome() + '\'' +
                ", cuoco=" + cuocoAssegnato.getNome() + " " + cuocoAssegnato.getCognome() +
                ", stato='" + stato + '\'' +
                '}';
    }
}