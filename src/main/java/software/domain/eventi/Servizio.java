package software.domain.eventi;

import javafx.beans.property.*;
import software.domain.menu.Menu;

import java.time.LocalDateTime;

public class Servizio {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty tipo = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> dataOraInizio = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> dataOraFine = new SimpleObjectProperty<>();
    private final StringProperty luogo = new SimpleStringProperty();
    private final ObjectProperty<Menu> menu = new SimpleObjectProperty<>();

    public Servizio(int id, String tipo, LocalDateTime dataOraInizio, LocalDateTime dataOraFine, String luogo) {
        this.id.set(id);
        this.tipo.set(tipo);
        this.dataOraInizio.set(dataOraInizio);
        this.dataOraFine.set(dataOraFine);
        this.luogo.set(luogo);
    }

    // Getters per proprietà JavaFX
    public IntegerProperty idProperty() { return id; }
    public StringProperty tipoProperty() { return tipo; }
    public ObjectProperty<LocalDateTime> dataOraInizioProperty() { return dataOraInizio; }
    public ObjectProperty<LocalDateTime> dataOraFineProperty() { return dataOraFine; }
    public StringProperty luogoProperty() { return luogo; }
    public ObjectProperty<Menu> menuProperty() { return menu; }
    
    // Getters standard
    public int getId() { return id.get(); }
    public String getTipo() { return tipo.get(); }
    public LocalDateTime getDataOraInizio() { return dataOraInizio.get(); }
    public LocalDateTime getDataOraFine() { return dataOraFine.get(); }
    public String getLuogo() { return luogo.get(); }
    public Menu getMenu() { return menu.get(); }
    
    // Setters
    public void setTipo(String tipo) { this.tipo.set(tipo); }
    public void setDataOraInizio(LocalDateTime dataOraInizio) { this.dataOraInizio.set(dataOraInizio); }
    public void setDataOraFine(LocalDateTime dataOraFine) { this.dataOraFine.set(dataOraFine); }
    public void setLuogo(String luogo) { this.luogo.set(luogo); }
    public void setMenu(Menu menu) { this.menu.set(menu); }
}