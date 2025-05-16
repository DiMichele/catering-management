package software.domain.menu;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import software.domain.ricette.Ricetta;
import software.domain.utenti.Chef;

import static org.junit.jupiter.api.Assertions.*;

public class SezioneMenuTest {

    private SezioneMenu sezione;
    private Chef chef;
    
    @BeforeEach
    public void setup() {
        sezione = new SezioneMenu(1, "Antipasti");
        chef = new Chef(1, "Mario", "Rossi", "chef@example.com", "123456789");
    }
    
    @Test
    @DisplayName("Test costruttore sezione menu")
    public void testCostruttoreSezioneMenu() {
        assertEquals(1, sezione.getId());
        assertEquals("Antipasti", sezione.getNome());
        assertTrue(sezione.getRicette().isEmpty()); // Nessuna ricetta iniziale
    }
    
    @Test
    @DisplayName("Test aggiungiRicetta")
    public void testAggiungiRicetta() {
        // Arrange
        Ricetta ricetta1 = new Ricetta(1, "Bruschetta", chef);
        Ricetta ricetta2 = new Ricetta(2, "Caprese", chef);
        
        // Act
        sezione.aggiungiRicetta(ricetta1);
        sezione.aggiungiRicetta(ricetta2);
        
        // Assert
        assertEquals(2, sezione.getRicette().size());
        assertEquals(ricetta1, sezione.getRicette().get(0));
        assertEquals(ricetta2, sezione.getRicette().get(1));
    }
    
    @Test
    @DisplayName("Test rimuoviRicetta")
    public void testRimuoviRicetta() {
        // Arrange
        Ricetta ricetta1 = new Ricetta(1, "Bruschetta", chef);
        Ricetta ricetta2 = new Ricetta(2, "Caprese", chef);
        sezione.aggiungiRicetta(ricetta1);
        sezione.aggiungiRicetta(ricetta2);
        
        // Act
        sezione.rimuoviRicetta(ricetta1);
        
        // Assert
        assertEquals(1, sezione.getRicette().size());
        assertEquals(ricetta2, sezione.getRicette().get(0));
    }
    
    @Test
    @DisplayName("Test JavaFX properties")
    public void testJavaFXProperties() {
        // Assert - Verifica che le properties abbiano i valori corretti
        assertEquals(1, sezione.idProperty().get());
        assertEquals("Antipasti", sezione.nomeProperty().get());
        
        // Verifica che cambiando le property si aggiorni il valore interno
        sezione.nomeProperty().set("Aperitivi");
        assertEquals("Aperitivi", sezione.getNome());
    }
} 