package software.domain.menu;

import software.domain.ricette.Ricetta;
import software.domain.utenti.Chef;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un menù creato da uno Chef.
 * Implementa le operazioni del caso d'uso "Gestione dei Menù".
 */
public class Menu {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty titolo = new SimpleStringProperty();
    private final StringProperty note = new SimpleStringProperty();
    private final BooleanProperty piattiCaldi = new SimpleBooleanProperty();
    private final BooleanProperty piattiFreddi = new SimpleBooleanProperty();
    private final BooleanProperty richiedeCucina = new SimpleBooleanProperty();
    private final BooleanProperty adattoBuffet = new SimpleBooleanProperty();
    private final BooleanProperty fingerFood = new SimpleBooleanProperty();
    private final StringProperty stato = new SimpleStringProperty("Bozza");
    private final ObservableList<SezioneMenu> sezioni = FXCollections.observableArrayList();
    private final ObjectProperty<Chef> creatore = new SimpleObjectProperty<>();

    public Menu(int id, Chef creatore) {
        this.id.set(id);
        this.creatore.set(creatore);
    }

    /**
     * Aggiunge un titolo al menu.
     * Riferimento: UC "Gestione dei Menù" - Passo 2
     */
    public void setTitolo(String titolo) {
        this.titolo.set(titolo);
    }

    /**
     * Definisce le sezioni del menu.
     * Riferimento: UC "Gestione dei Menù" - Passo 3
     */
    public void definisciSezioni(List<String> nomiSezioni) {
        sezioni.clear();
        for (String nome : nomiSezioni) {
            sezioni.add(new SezioneMenu(sezioni.size() + 1, nome));
        }
    }

    /**
     * Inserisce una ricetta in una sezione del menu.
     * Riferimento: UC "Gestione dei Menù" - Passo 4
     */
    public void inserisciRicetta(Ricetta ricetta, SezioneMenu sezione) {
        sezione.aggiungiRicetta(ricetta);
    }

    /**
     * Aggiunge note e informazioni utili al menu.
     * Riferimento: UC "Gestione dei Menù" - Passo 6
     */
    public void annotaInformazioni(String note) {
        this.note.set(note);
    }

    /**
     * Pubblica il menu, cambiando lo stato e rendendolo disponibile.
     * Riferimento: UC "Gestione dei Menù" - Passo 7
     */
    public void pubblica() {
        this.stato.set("Pubblicato");
    }

    // Getters per proprietà JavaFX (per binding)
    public IntegerProperty idProperty() { return id; }
    public StringProperty titoloProperty() { return titolo; }
    public StringProperty noteProperty() { return note; }
    public StringProperty statoProperty() { return stato; }
    public ObservableList<SezioneMenu> getSezioni() { return sezioni; }
    
    // Altri getters e setters
    public int getId() { return id.get(); }
    public String getTitolo() { return titolo.get(); }
    public String getNote() { return note.get(); }
    public String getStato() { return stato.get(); }
    public Chef getCreatore() { return creatore.get(); }
    public void setStato(String stato) { this.stato.set(stato); }
}