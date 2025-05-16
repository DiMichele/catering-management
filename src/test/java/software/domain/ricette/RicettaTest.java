package software.domain.ricette;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import software.domain.utenti.Chef;

import static org.junit.jupiter.api.Assertions.*;

public class RicettaTest {
    
    private Chef chef;
    private Ricetta ricetta;
    
    @BeforeEach
    public void setup() {
        chef = new Chef(1, "Mario", "Rossi", "chef@example.com", "123456789");
        ricetta = new Ricetta(1, "Carbonara", chef);
    }
    
    @Test
    @DisplayName("Test costruttore ricetta")
    public void testCostruttoreRicetta() {
        assertEquals(1, ricetta.getId());
        assertEquals("Carbonara", ricetta.getNome());
        assertEquals(chef, ricetta.getProprietario());
        assertEquals("Bozza", ricetta.getStato()); // Stato predefinito
        assertFalse(ricetta.isInUso()); // Non in uso per default
        assertEquals(0, ricetta.getTempoPreparazione()); // Tempo predefinito
        assertTrue(ricetta.getIngredienti().isEmpty()); // Nessun ingrediente
        assertTrue(ricetta.getIstruzioni().isEmpty()); // Nessuna istruzione
        assertTrue(ricetta.getTags().isEmpty()); // Nessun tag
    }
    
    @Test
    @DisplayName("Test aggiunta di ingredienti")
    public void testAggiungiIngrediente() {
        // Arrange & Act
        Ingrediente ingrediente1 = new Ingrediente("Pasta", 350, "g");
        Ingrediente ingrediente2 = new Ingrediente("Uova", 4, "pz");
        ricetta.aggiungiIngrediente(ingrediente1);
        ricetta.aggiungiIngrediente(ingrediente2);
        
        // Assert
        assertEquals(2, ricetta.getIngredienti().size());
        assertTrue(ricetta.getIngredienti().contains(ingrediente1));
        assertTrue(ricetta.getIngredienti().contains(ingrediente2));
    }
    
    @Test
    @DisplayName("Test aggiunta di istruzioni")
    public void testAggiungiIstruzione() {
        // Arrange & Act
        Istruzione istruzione1 = new Istruzione(1, 1, "Bollire la pasta");
        Istruzione istruzione2 = new Istruzione(2, 2, "Preparare il condimento");
        ricetta.aggiungiIstruzione(istruzione1);
        ricetta.aggiungiIstruzione(istruzione2);
        
        // Assert
        assertEquals(2, ricetta.getIstruzioni().size());
        assertTrue(ricetta.getIstruzioni().contains(istruzione1));
        assertTrue(ricetta.getIstruzioni().contains(istruzione2));
    }
    
    @Test
    @DisplayName("Test aggiunta di tag")
    public void testAggiungiTag() {
        // Arrange & Act
        Tag tag1 = new Tag("Primo piatto");
        Tag tag2 = new Tag("Cucina italiana");
        ricetta.aggiungiTag(tag1);
        ricetta.aggiungiTag(tag2);
        
        // Assert
        assertEquals(2, ricetta.getTags().size());
        assertTrue(ricetta.getTags().contains(tag1));
        assertTrue(ricetta.getTags().contains(tag2));
    }
    
    @Test
    @DisplayName("Test setters")
    public void testSetters() {
        // Act
        ricetta.setNome("Carbonara modificata");
        ricetta.setDescrizione("La vera carbonara romana");
        ricetta.setStato("Pubblicata");
        ricetta.setTempoPreparazione(30);
        ricetta.setInUso(true);
        
        // Assert
        assertEquals("Carbonara modificata", ricetta.getNome());
        assertEquals("La vera carbonara romana", ricetta.getDescrizione());
        assertEquals("Pubblicata", ricetta.getStato());
        assertEquals(30, ricetta.getTempoPreparazione());
        assertTrue(ricetta.isInUso());
    }
    
    @Test
    @DisplayName("Test equals e hashCode")
    public void testEqualsHashCode() {
        // Arrange
        Ricetta ricetta1 = new Ricetta(1, "Carbonara", chef);
        Ricetta ricetta2 = new Ricetta(1, "Nome diverso", chef); // Stesso ID
        Ricetta ricetta3 = new Ricetta(2, "Carbonara", chef); // ID diverso
        
        // Assert
        assertEquals(ricetta1, ricetta2); // Uguali perché hanno lo stesso ID
        assertEquals(ricetta1.hashCode(), ricetta2.hashCode());
        
        assertNotEquals(ricetta1, ricetta3); // Diversi per ID diverso
        assertNotEquals(ricetta1.hashCode(), ricetta3.hashCode());
    }
    
    @Test
    @DisplayName("Test toString")
    public void testToString() {
        // Act
        String result = ricetta.toString();
        
        // Assert
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("nome='Carbonara'"));
        assertTrue(result.contains("stato='Bozza'"));
    }
} 