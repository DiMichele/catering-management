package software.domain.ricette;

import javafx.beans.property.*;

public class Preparazione {
    private final StringProperty nome = new SimpleStringProperty();
    private final DoubleProperty quantitaRisultante = new SimpleDoubleProperty();
    private final StringProperty unitaMisura = new SimpleStringProperty();

    public Preparazione(String nome, double quantitaRisultante, String unitaMisura) {
        this.nome.set(nome);
        this.quantitaRisultante.set(quantitaRisultante);
        this.unitaMisura.set(unitaMisura);
    }

    // Getters e setters
    public String getNome() { return nome.get(); }
    public double getQuantitaRisultante() { return quantitaRisultante.get(); }
    public String getUnitaMisura() { return unitaMisura.get(); }
    
    // Property getters per JavaFX
    public StringProperty nomeProperty() { return nome; }
    public DoubleProperty quantitaRisultanteProperty() { return quantitaRisultante; }
    public StringProperty unitaMisuraProperty() { return unitaMisura; }
}