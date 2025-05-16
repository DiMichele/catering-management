package software.domain.eventi;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class Evento {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty nome = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> dataInizio = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> dataFine = new SimpleObjectProperty<>();
    private final StringProperty luogo = new SimpleStringProperty();
    private final IntegerProperty numeroDiPersone = new SimpleIntegerProperty();
    private final BooleanProperty ricorrente = new SimpleBooleanProperty(false);
    private final StringProperty stato = new SimpleStringProperty("Pianificato");
    private final StringProperty note = new SimpleStringProperty();
    private final ObservableList<Servizio> servizi = FXCollections.observableArrayList();

    public Evento(int id, String nome, LocalDate dataInizio, LocalDate dataFine, 
                 String luogo, int numeroDiPersone) {
        this.id.set(id);
        this.nome.set(nome);
        this.dataInizio.set(dataInizio);
        this.dataFine.set(dataFine);
        this.luogo.set(luogo);
        this.numeroDiPersone.set(numeroDiPersone);
    }

    public void aggiungiServizio(Servizio servizio) {
        servizi.add(servizio);
    }

    // Getters per proprietà JavaFX
    public IntegerProperty idProperty() { return id; }
    public StringProperty nomeProperty() { return nome; }
    public ObjectProperty<LocalDate> dataInizioProperty() { return dataInizio; }
    public ObjectProperty<LocalDate> dataFineProperty() { return dataFine; }
    public StringProperty luogoProperty() { return luogo; }
    public IntegerProperty numeroDiPersoneProperty() { return numeroDiPersone; }
    public BooleanProperty ricorrenteProperty() { return ricorrente; }
    public StringProperty statoProperty() { return stato; }
    public StringProperty noteProperty() { return note; }
    public ObservableList<Servizio> getServizi() { return servizi; }
    
    // Getters standard
    public int getId() { return id.get(); }
    public String getNome() { return nome.get(); }
    public LocalDate getDataInizio() { return dataInizio.get(); }
    public LocalDate getDataFine() { return dataFine.get(); }
    public String getLuogo() { return luogo.get(); }
    public int getNumeroDiPersone() { return numeroDiPersone.get(); }
    public boolean isRicorrente() { return ricorrente.get(); }
    public String getStato() { return stato.get(); }
    public String getNote() { return note.get(); }
    
    // Setters
    public void setNome(String nome) { this.nome.set(nome); }
    public void setDataInizio(LocalDate dataInizio) { this.dataInizio.set(dataInizio); }
    public void setDataFine(LocalDate dataFine) { this.dataFine.set(dataFine); }
    public void setLuogo(String luogo) { this.luogo.set(luogo); }
    public void setNumeroDiPersone(int numeroDiPersone) { this.numeroDiPersone.set(numeroDiPersone); }
    public void setRicorrente(boolean ricorrente) { this.ricorrente.set(ricorrente); }
    public void setStato(String stato) { this.stato.set(stato); }
    public void setNote(String note) { this.note.set(note); }
}