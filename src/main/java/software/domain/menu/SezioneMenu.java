package software.domain.menu;

import software.domain.ricette.Ricetta;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SezioneMenu {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty nome = new SimpleStringProperty();
    private final ObservableList<Ricetta> ricette = FXCollections.observableArrayList();

    public SezioneMenu(int id, String nome) {
        this.id.set(id);
        this.nome.set(nome);
    }

    public void aggiungiRicetta(Ricetta ricetta) {
        ricette.add(ricetta);
    }
    
    public void rimuoviRicetta(Ricetta ricetta) {
        ricette.remove(ricetta);
    }

    // Getters per proprietà JavaFX
    public IntegerProperty idProperty() { return id; }
    public StringProperty nomeProperty() { return nome; }
    public ObservableList<Ricetta> getRicette() { return ricette; }
    
    // Getters standard
    public int getId() { return id.get(); }
    public String getNome() { return nome.get(); }
}