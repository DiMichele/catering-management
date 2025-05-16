package software.domain.ricette;

import software.domain.utenti.Chef;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta una ricetta nel sistema di catering.
 * Questa è una classe del domain model che deve essere indipendente
 * dalla tecnologia di presentazione (JavaFX).
 */
public class Ricetta implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String nome;
    private String descrizione;
    private String stato = "Bozza";
    private int tempoPreparazione;
    private boolean inUso = false;
    private List<Ingrediente> ingredienti = new ArrayList<>();
    private List<Istruzione> istruzioni = new ArrayList<>();
    private List<Tag> tags = new ArrayList<>();
    private Chef proprietario;

    public Ricetta(int id, String nome, Chef proprietario) {
        this.id = id;
        this.nome = nome;
        this.proprietario = proprietario;
    }

    public void aggiungiIngrediente(Ingrediente ingrediente) {
        ingredienti.add(ingrediente);
    }

    public void aggiungiIstruzione(Istruzione istruzione) {
        istruzioni.add(istruzione);
    }

    public void aggiungiTag(Tag tag) {
        tags.add(tag);
    }
    
    // Getters standard
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getDescrizione() { return descrizione; }
    public String getStato() { return stato; }
    public int getTempoPreparazione() { return tempoPreparazione; }
    public boolean isInUso() { return inUso; }
    public List<Ingrediente> getIngredienti() { return ingredienti; }
    public List<Istruzione> getIstruzioni() { return istruzioni; }
    public List<Tag> getTags() { return tags; }
    public Chef getProprietario() { return proprietario; }
    
    // Setters
    public void setNome(String nome) { this.nome = nome; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    public void setStato(String stato) { this.stato = stato; }
    public void setTempoPreparazione(int minuti) { this.tempoPreparazione = minuti; }
    public void setInUso(boolean inUso) { this.inUso = inUso; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ricetta ricetta = (Ricetta) o;
        return id == ricetta.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    
    @Override
    public String toString() {
        return "Ricetta{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", stato='" + stato + '\'' +
                '}';
    }
}