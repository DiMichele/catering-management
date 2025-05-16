package software.domain.ricette;

import java.io.Serializable;
import java.util.Objects;
import software.domain.exceptions.DomainException;

/**
 * Rappresenta un ingrediente di una ricetta.
 * Questa classe è responsabile di garantire la validità dei dati di un ingrediente.
 */
public class Ingrediente implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String nome;
    private double dose;
    private String unitaMisura;
    private boolean ingredienteBase;
    
    /**
     * Crea un nuovo ingrediente.
     * 
     * @param nome Il nome dell'ingrediente (non può essere null o vuoto)
     * @param dose La quantità richiesta (deve essere maggiore di zero)
     * @param unitaMisura L'unità di misura per la dose (non può essere null o vuota)
     * @throws DomainException se i parametri non sono validi
     */
    public Ingrediente(String nome, double dose, String unitaMisura) {
        setNome(nome);
        setDose(dose);
        setUnitaMisura(unitaMisura);
        this.ingredienteBase = false;
    }
    
    // Getters
    public String getNome() {
        return nome;
    }
    
    public double getDose() {
        return dose;
    }
    
    public String getUnitaMisura() {
        return unitaMisura;
    }
    
    public boolean isIngredienteBase() {
        return ingredienteBase;
    }
    
    // Setters con validazione
    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new DomainException("Il nome dell'ingrediente non può essere vuoto");
        }
        this.nome = nome.trim();
    }
    
    public void setDose(double dose) {
        if (dose <= 0) {
            throw new DomainException("La dose deve essere maggiore di zero");
        }
        this.dose = dose;
    }
    
    public void setUnitaMisura(String unitaMisura) {
        if (unitaMisura == null || unitaMisura.trim().isEmpty()) {
            throw new DomainException("L'unità di misura non può essere vuota");
        }
        this.unitaMisura = unitaMisura.trim();
    }
    
    public void setIngredienteBase(boolean ingredienteBase) {
        this.ingredienteBase = ingredienteBase;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingrediente that = (Ingrediente) o;
        return Double.compare(that.dose, dose) == 0 && 
               ingredienteBase == that.ingredienteBase && 
               Objects.equals(nome, that.nome) && 
               Objects.equals(unitaMisura, that.unitaMisura);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(nome, dose, unitaMisura, ingredienteBase);
    }
    
    @Override
    public String toString() {
        return dose + " " + unitaMisura + " " + nome + (ingredienteBase ? " (base)" : "");
    }
    
    /**
     * Crea una copia profonda dell'ingrediente.
     */
    public Ingrediente clone() {
        Ingrediente clone = new Ingrediente(this.nome, this.dose, this.unitaMisura);
        clone.setIngredienteBase(this.ingredienteBase);
        return clone;
    }
}