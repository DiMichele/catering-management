package software.domain.menu;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import software.domain.ricette.Ricetta;
import software.domain.utenti.Chef;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MenuTest {

    private Chef chef;
    private Menu menu;
    
    @BeforeEach
    public void setup() {
        chef = new Chef(1, "Mario", "Rossi", "chef@example.com", "123456789");
        menu = new Menu(1, chef);
    }
    
    @Test
    @DisplayName("Test costruttore menu")
    public void testCostruttoreMenu() {
        assertEquals(1, menu.getId());
        assertEquals(chef, menu.getCreatore());
        assertEquals("Bozza", menu.getStato()); // Stato predefinito
        assertTrue(menu.getSezioni().isEmpty()); // Nessuna sezione iniziale
    }
    
    @Test
    @DisplayName("Test setTitolo")
    public void testSetTitolo() {
        // Act
        menu.setTitolo("Menu Degustazione");
        
        // Assert
        assertEquals("Menu Degustazione", menu.getTitolo());
    }
    
    @Test
    @DisplayName("Test definisciSezioni")
    public void testDefinisciSezioni() {
        // Arrange
        List<String> nomiSezioni = Arrays.asList("Antipasti", "Primi", "Secondi", "Dessert");
        
        // Act
        menu.definisciSezioni(nomiSezioni);
        
        // Assert
        assertEquals(4, menu.getSezioni().size());
        assertEquals("Antipasti", menu.getSezioni().get(0).getNome());
        assertEquals("Primi", menu.getSezioni().get(1).getNome());
        assertEquals("Secondi", menu.getSezioni().get(2).getNome());
        assertEquals("Dessert", menu.getSezioni().get(3).getNome());
    }
    
    @Test
    @DisplayName("Test ridefinisciSezioni cancella sezioni esistenti")
    public void testRidefinisciSezioni() {
        // Arrange - Prima definizione
        List<String> primaDefinizione = Arrays.asList("Antipasti", "Primi", "Secondi");
        menu.definisciSezioni(primaDefinizione);
        
        // Act - Ridefinizione
        List<String> secondaDefinizione = Arrays.asList("Finger Food", "Piatti Principali");
        menu.definisciSezioni(secondaDefinizione);
        
        // Assert
        assertEquals(2, menu.getSezioni().size());
        assertEquals("Finger Food", menu.getSezioni().get(0).getNome());
        assertEquals("Piatti Principali", menu.getSezioni().get(1).getNome());
    }
    
    @Test
    @DisplayName("Test inserisciRicetta")
    public void testInserisciRicetta() {
        // Arrange
        List<String> nomiSezioni = Arrays.asList("Antipasti", "Primi");
        menu.definisciSezioni(nomiSezioni);
        SezioneMenu sezioneAntipasti = menu.getSezioni().get(0);
        Ricetta ricetta = new Ricetta(1, "Bruschetta", chef);
        
        // Act
        menu.inserisciRicetta(ricetta, sezioneAntipasti);
        
        // Assert
        assertEquals(1, sezioneAntipasti.getRicette().size());
        assertEquals(ricetta, sezioneAntipasti.getRicette().get(0));
    }
    
    @Test
    @DisplayName("Test annotaInformazioni")
    public void testAnnotaInformazioni() {
        // Act
        menu.annotaInformazioni("Menu ideale per cene formali");
        
        // Assert
        assertEquals("Menu ideale per cene formali", menu.getNote());
    }
    
    @Test
    @DisplayName("Test pubblica")
    public void testPubblica() {
        // Act
        menu.pubblica();
        
        // Assert
        assertEquals("Pubblicato", menu.getStato());
    }
    
    @Test
    @DisplayName("Test JavaFX properties")
    public void testJavaFXProperties() {
        // Act
        menu.setTitolo("Menu Degustazione");
        menu.annotaInformazioni("Note di test");
        menu.setStato("Bozza Avanzata");
        
        // Assert - Verifica che le properties abbiano i valori corretti
        assertEquals(1, menu.idProperty().get());
        assertEquals("Menu Degustazione", menu.titoloProperty().get());
        assertEquals("Note di test", menu.noteProperty().get());
        assertEquals("Bozza Avanzata", menu.statoProperty().get());
        
        // Verifica anche che cambiando le property si aggiornino i getter
        menu.titoloProperty().set("Nuovo Titolo");
        assertEquals("Nuovo Titolo", menu.getTitolo());
    }
} 