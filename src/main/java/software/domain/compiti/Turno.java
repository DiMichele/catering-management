package software.domain.compiti;

import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class Turno {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDate> data = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> oraInizio = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> oraFine = new SimpleObjectProperty<>();
    private final StringProperty luogo = new SimpleStringProperty();
    private final StringProperty tipo = new SimpleStringProperty();
    private final BooleanProperty modificabile = new SimpleBooleanProperty(true);

    public Turno(int id, LocalDate data, LocalTime oraInizio, LocalTime oraFine, String luogo, String tipo) {
        this.id.set(id);
        this.data.set(data);
        this.oraInizio.set(oraInizio);
        this.oraFine.set(oraFine);
        this.luogo.set(luogo);
        this.tipo.set(tipo);
    }

    /**
     * Aggiorna orario del turno.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Estensione 5b
     */
    public void aggiornaOrario(LocalTime nuovoInizio, LocalTime nuovoFine) {
        if (modificabile.get()) {
            this.oraInizio.set(nuovoInizio);
            this.oraFine.set(nuovoFine);
        }
    }

    // Getters per proprietà JavaFX
    public IntegerProperty idProperty() { return id; }
    public ObjectProperty<LocalDate> dataProperty() { return data; }
    public ObjectProperty<LocalTime> oraInizioProperty() { return oraInizio; }
    public ObjectProperty<LocalTime> oraFineProperty() { return oraFine; }
    public StringProperty luogoProperty() { return luogo; }
    public StringProperty tipoProperty() { return tipo; }
    public BooleanProperty modificabileProperty() { return modificabile; }
    
    // Getters standard
    public int getId() { return id.get(); }
    public LocalDate getData() { return data.get(); }
    public LocalTime getOraInizio() { return oraInizio.get(); }
    public LocalTime getOraFine() { return oraFine.get(); }
    public String getLuogo() { return luogo.get(); }
    public String getTipo() { return tipo.get(); }
    public boolean isModificabile() { return modificabile.get(); }
}