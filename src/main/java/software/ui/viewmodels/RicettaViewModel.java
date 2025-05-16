package software.ui.viewmodels;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import software.domain.ricette.Ingrediente;
import software.domain.ricette.Istruzione;
import software.domain.ricette.Ricetta;
import software.domain.ricette.Tag;
import software.domain.utenti.Chef;

/**
 * ViewModel che adatta la classe Ricetta del dominio per l'utilizzo nelle viste JavaFX.
 * Questo adapter separa il modello di dominio dalla UI, applicando il principio di separazione delle responsabilità.
 */
public class RicettaViewModel {
    private final Ricetta ricetta;
    
    // Proprietà JavaFX per il binding UI
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty nome = new SimpleStringProperty();
    private final StringProperty descrizione = new SimpleStringProperty();
    private final StringProperty stato = new SimpleStringProperty();
    private final IntegerProperty tempoPreparazione = new SimpleIntegerProperty();
    private final BooleanProperty inUso = new SimpleBooleanProperty();
    private final ObservableList<Ingrediente> ingredienti = FXCollections.observableArrayList();
    private final ObservableList<Istruzione> istruzioni = FXCollections.observableArrayList();
    private final ObservableList<Tag> tags = FXCollections.observableArrayList();
    private final ObjectProperty<Chef> proprietario = new SimpleObjectProperty<>();
    
    public RicettaViewModel(Ricetta ricetta) {
        this.ricetta = ricetta;
        
        // Inizializza le proprietà JavaFX dal modello di dominio
        id.set(ricetta.getId());
        nome.set(ricetta.getNome());
        descrizione.set(ricetta.getDescrizione());
        stato.set(ricetta.getStato());
        tempoPreparazione.set(ricetta.getTempoPreparazione());
        inUso.set(ricetta.isInUso());
        proprietario.set(ricetta.getProprietario());
        
        // Inizializza le collezioni osservabili
        ingredienti.addAll(ricetta.getIngredienti());
        istruzioni.addAll(ricetta.getIstruzioni());
        tags.addAll(ricetta.getTags());
    }
    
    // Metodo per salvare le modifiche dal ViewModel al modello
    public void commit() {
        ricetta.setNome(nome.get());
        ricetta.setDescrizione(descrizione.get());
        ricetta.setStato(stato.get());
        ricetta.setTempoPreparazione(tempoPreparazione.get());
        ricetta.setInUso(inUso.get());
    }
    
    // Getter per il modello di dominio sottostante
    public Ricetta getRicetta() {
        return ricetta;
    }
    
    // Getters per proprietà JavaFX
    public IntegerProperty idProperty() { return id; }
    public StringProperty nomeProperty() { return nome; }
    public StringProperty descrizioneProperty() { return descrizione; }
    public StringProperty statoProperty() { return stato; }
    public IntegerProperty tempoPreparazioneProperty() { return tempoPreparazione; }
    public BooleanProperty inUsoProperty() { return inUso; }
    public ObservableList<Ingrediente> getIngredienti() { return ingredienti; }
    public ObservableList<Istruzione> getIstruzioni() { return istruzioni; }
    public ObservableList<Tag> getTags() { return tags; }
    public ObjectProperty<Chef> proprietarioProperty() { return proprietario; }
} 