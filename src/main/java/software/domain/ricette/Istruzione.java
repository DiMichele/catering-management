package software.domain.ricette;

import javafx.beans.property.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.IOException;

public class Istruzione implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Utilizziamo transient per le proprietà JavaFX che non sono serializzabili
    private transient IntegerProperty id = new SimpleIntegerProperty();
    private transient IntegerProperty ordine = new SimpleIntegerProperty();
    private transient StringProperty descrizione = new SimpleStringProperty();
    
    // Campi di backup per serializzazione
    private int idValue;
    private int ordineValue;
    private String descrizioneValue;

    public Istruzione(int id, int ordine, String descrizione) {
        this.id.set(id);
        this.ordine.set(ordine);
        this.descrizione.set(descrizione);
        
        // Aggiorna anche i campi di backup
        this.idValue = id;
        this.ordineValue = ordine;
        this.descrizioneValue = descrizione;
    }

    // Getters per proprietà JavaFX
    public IntegerProperty idProperty() { return id; }
    public IntegerProperty ordineProperty() { return ordine; }
    public StringProperty descrizioneProperty() { return descrizione; }
    
    // Getters standard
    public int getId() { return id.get(); }
    public int getOrdine() { return ordine.get(); }
    public String getDescrizione() { return descrizione.get(); }
    
    // Setters
    public void setOrdine(int ordine) { 
        this.ordine.set(ordine);
        this.ordineValue = ordine;
    }
    
    public void setDescrizione(String descrizione) { 
        this.descrizione.set(descrizione);
        this.descrizioneValue = descrizione;
    }
    
    // Metodi custom per gestire la serializzazione
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(getId());
        out.writeInt(getOrdine());
        out.writeObject(getDescrizione());
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        
        // Ricrea le proprietà JavaFX
        id = new SimpleIntegerProperty(in.readInt());
        ordine = new SimpleIntegerProperty(in.readInt());
        descrizione = new SimpleStringProperty((String) in.readObject());
    }
}