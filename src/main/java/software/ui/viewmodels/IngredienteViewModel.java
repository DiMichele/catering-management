package software.ui.viewmodels;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.util.StringConverter;
import software.domain.exceptions.DomainException;
import software.domain.ricette.Ingrediente;

/**
 * ViewModel che adatta la classe Ingrediente del dominio per l'utilizzo nelle viste JavaFX.
 * Fornisce supporto per binding bidirezionale con validazione.
 */
public class IngredienteViewModel {
    private final Ingrediente ingrediente;
    
    // Proprietà JavaFX per il binding UI
    private final StringProperty nome = new SimpleStringProperty();
    private final DoubleProperty dose = new SimpleDoubleProperty();
    private final StringProperty unitaMisura = new SimpleStringProperty();
    private final BooleanProperty ingredienteBase = new SimpleBooleanProperty();
    private final StringProperty errori = new SimpleStringProperty("");
    private final BooleanProperty valido = new SimpleBooleanProperty(true);
    
    /**
     * Crea un nuovo ViewModel per un ingrediente esistente.
     * 
     * @param ingrediente L'ingrediente da rappresentare
     */
    public IngredienteViewModel(Ingrediente ingrediente) {
        this.ingrediente = ingrediente;
        
        // Inizializza le proprietà JavaFX dal modello di dominio
        nome.set(ingrediente.getNome());
        dose.set(ingrediente.getDose());
        unitaMisura.set(ingrediente.getUnitaMisura());
        ingredienteBase.set(ingrediente.isIngredienteBase());
        
        // Aggiungi listener per la validazione
        setupValidation();
    }
    
    /**
     * Crea un nuovo ViewModel per un nuovo ingrediente.
     */
    public IngredienteViewModel() {
        this.ingrediente = new Ingrediente("", 0.0, "g");
        setupValidation();
    }
    
    private void setupValidation() {
        // Aggiungi validazione quando le proprietà cambiano
        nome.addListener((observable, oldValue, newValue) -> validaNome(newValue));
        dose.addListener((observable, oldValue, newValue) -> validaDose(newValue.doubleValue()));
        unitaMisura.addListener((observable, oldValue, newValue) -> validaUnitaMisura(newValue));
    }
    
    private void validaNome(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                errori.set("Il nome dell'ingrediente non può essere vuoto");
                valido.set(false);
            } else {
                errori.set("");
                valido.set(true);
            }
        } catch (Exception e) {
            errori.set(e.getMessage());
            valido.set(false);
        }
    }
    
    private void validaDose(double value) {
        try {
            if (value <= 0) {
                errori.set("La dose deve essere maggiore di zero");
                valido.set(false);
            } else {
                errori.set("");
                valido.set(true);
            }
        } catch (Exception e) {
            errori.set(e.getMessage());
            valido.set(false);
        }
    }
    
    private void validaUnitaMisura(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                errori.set("L'unità di misura non può essere vuota");
                valido.set(false);
            } else {
                errori.set("");
                valido.set(true);
            }
        } catch (Exception e) {
            errori.set(e.getMessage());
            valido.set(false);
        }
    }
    
    /**
     * Metodo per salvare le modifiche dal ViewModel al modello.
     * 
     * @return true se il commit è avvenuto con successo
     * @throws DomainException se ci sono errori di validazione
     */
    public boolean commit() {
        if (!valido.get()) {
            throw new DomainException(errori.get());
        }
        
        try {
            ingrediente.setNome(nome.get());
            ingrediente.setDose(dose.get());
            ingrediente.setUnitaMisura(unitaMisura.get());
            ingrediente.setIngredienteBase(ingredienteBase.get());
            return true;
        } catch (DomainException e) {
            errori.set(e.getMessage());
            valido.set(false);
            return false;
        }
    }
    
    /**
     * Verifica se l'ingrediente è valido per il salvataggio.
     * @return true se l'ingrediente è valido
     */
    public boolean isValido() {
        return valido.get() && 
               nome.get() != null && !nome.get().trim().isEmpty() &&
               dose.get() > 0 &&
               unitaMisura.get() != null && !unitaMisura.get().trim().isEmpty();
    }
    
    /**
     * Resetta le proprietà ai valori originali dell'ingrediente.
     */
    public void reset() {
        nome.set(ingrediente.getNome());
        dose.set(ingrediente.getDose());
        unitaMisura.set(ingrediente.getUnitaMisura());
        ingredienteBase.set(ingrediente.isIngredienteBase());
        errori.set("");
        valido.set(true);
    }
    
    // Getter per il modello di dominio sottostante
    public Ingrediente getIngrediente() {
        return ingrediente;
    }
    
    // Getters per proprietà JavaFX
    public StringProperty nomeProperty() { return nome; }
    public DoubleProperty doseProperty() { return dose; }
    public StringProperty unitaMisuraProperty() { return unitaMisura; }
    public BooleanProperty ingredienteBaseProperty() { return ingredienteBase; }
    public StringProperty erroriProperty() { return errori; }
    public BooleanProperty validoProperty() { return valido; }
    
    /**
     * Crea un nuovo StringConverter per convertire tra Double e String.
     * Utile per il binding con TextFields.
     */
    public static StringConverter<Double> createDoseConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(Double value) {
                return value == null ? "" : value.toString();
            }
            
            @Override
            public Double fromString(String string) {
                if (string == null || string.trim().isEmpty()) {
                    return 0.0;
                }
                try {
                    return Double.parseDouble(string);
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }
        };
    }
    
    @Override
    public String toString() {
        return dose.get() + " " + unitaMisura.get() + " " + nome.get() + 
               (ingredienteBase.get() ? " (base)" : "");
    }
} 