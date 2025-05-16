package software.domain.ricette;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import software.domain.exceptions.DomainException;

import static org.junit.jupiter.api.Assertions.*;

public class IngredienteTest {

    @Test
    @DisplayName("Creazione di un ingrediente con valori validi")
    public void testCreazioneIngredienteValido() {
        // Arrange & Act
        Ingrediente ingrediente = new Ingrediente("Farina", 500, "g");
        
        // Assert
        assertEquals("Farina", ingrediente.getNome());
        assertEquals(500, ingrediente.getDose());
        assertEquals("g", ingrediente.getUnitaMisura());
        assertFalse(ingrediente.isIngredienteBase());
    }
    
    @Test
    @DisplayName("Creazione di un ingrediente con nome nullo lancia eccezione")
    public void testCreazioneIngredienteNomeNullo() {
        // Arrange, Act & Assert
        assertThrows(DomainException.class, () -> {
            new Ingrediente(null, 500, "g");
        });
    }
    
    @Test
    @DisplayName("Creazione di un ingrediente con nome vuoto lancia eccezione")
    public void testCreazioneIngredienteNomeVuoto() {
        // Arrange, Act & Assert
        assertThrows(DomainException.class, () -> {
            new Ingrediente("", 500, "g");
        });
    }
    
    @Test
    @DisplayName("Creazione di un ingrediente con dose non positiva lancia eccezione")
    public void testCreazioneIngredienteDoseNonPositiva() {
        // Arrange, Act & Assert
        assertThrows(DomainException.class, () -> {
            new Ingrediente("Farina", 0, "g");
        });
        
        assertThrows(DomainException.class, () -> {
            new Ingrediente("Farina", -1, "g");
        });
    }
    
    @Test
    @DisplayName("Creazione di un ingrediente con unità di misura nulla lancia eccezione")
    public void testCreazioneIngredienteUnitaMisuraNulla() {
        // Arrange, Act & Assert
        assertThrows(DomainException.class, () -> {
            new Ingrediente("Farina", 500, null);
        });
    }
    
    @Test
    @DisplayName("Creazione di un ingrediente con unità di misura vuota lancia eccezione")
    public void testCreazioneIngredienteUnitaMisuraVuota() {
        // Arrange, Act & Assert
        assertThrows(DomainException.class, () -> {
            new Ingrediente("Farina", 500, "");
        });
    }
    
    @Test
    @DisplayName("Test dei setters")
    public void testSetters() {
        // Arrange
        Ingrediente ingrediente = new Ingrediente("Farina", 500, "g");
        
        // Act
        ingrediente.setNome("Zucchero");
        ingrediente.setDose(300);
        ingrediente.setUnitaMisura("kg");
        ingrediente.setIngredienteBase(true);
        
        // Assert
        assertEquals("Zucchero", ingrediente.getNome());
        assertEquals(300, ingrediente.getDose());
        assertEquals("kg", ingrediente.getUnitaMisura());
        assertTrue(ingrediente.isIngredienteBase());
    }
    
    @Test
    @DisplayName("Test equals e hashCode")
    public void testEqualsHashCode() {
        // Arrange
        Ingrediente ingrediente1 = new Ingrediente("Farina", 500, "g");
        Ingrediente ingrediente2 = new Ingrediente("Farina", 500, "g");
        Ingrediente ingrediente3 = new Ingrediente("Zucchero", 500, "g");
        
        // Assert
        assertEquals(ingrediente1, ingrediente2);
        assertEquals(ingrediente1.hashCode(), ingrediente2.hashCode());
        
        assertNotEquals(ingrediente1, ingrediente3);
        assertNotEquals(ingrediente1.hashCode(), ingrediente3.hashCode());
    }
    
    @Test
    @DisplayName("Test clone")
    public void testClone() {
        // Arrange
        Ingrediente originale = new Ingrediente("Farina", 500, "g");
        originale.setIngredienteBase(true);
        
        // Act
        Ingrediente clone = originale.clone();
        
        // Assert
        assertEquals(originale.getNome(), clone.getNome());
        assertEquals(originale.getDose(), clone.getDose());
        assertEquals(originale.getUnitaMisura(), clone.getUnitaMisura());
        assertEquals(originale.isIngredienteBase(), clone.isIngredienteBase());
        
        // Verifica che sia una copia indipendente
        clone.setNome("Zucchero");
        assertNotEquals(originale.getNome(), clone.getNome());
    }
} 