package software.service.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import software.domain.exceptions.DomainException;
import software.domain.ricette.Ricetta;
import software.domain.utenti.Chef;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per il repository in memoria.
 */
public class InMemoryRepositoryTest {

    private InMemoryRepository<Ricetta, Integer> repository;
    private Chef testChef;
    
    @BeforeEach
    public void setup() {
        repository = new InMemoryRepository<>("test-ricette", Ricetta.class, "id");
        testChef = new Chef(1, "Mario", "Rossi", "chef@example.com", "123456789");
    }
    
    @Test
    @DisplayName("Test salvataggio e recupero entità")
    public void testSaveAndFindById() {
        // Arrange
        Ricetta ricetta = new Ricetta(0, "Test Ricetta", testChef);
        
        // Act
        Ricetta saved = repository.save(ricetta);
        Ricetta found = repository.findById(saved.getId());
        
        // Assert
        assertNotNull(saved);
        assertTrue(saved.getId() > 0, "L'ID dovrebbe essere stato assegnato");
        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals("Test Ricetta", found.getNome());
    }
    
    @Test
    @DisplayName("Test recupero tutte le entità")
    public void testFindAll() {
        // Arrange
        repository.save(new Ricetta(0, "Ricetta 1", testChef));
        repository.save(new Ricetta(0, "Ricetta 2", testChef));
        
        // Act
        var result = repository.findAll();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }
    
    @Test
    @DisplayName("Test eliminazione entità")
    public void testDelete() {
        // Arrange
        Ricetta ricetta = repository.save(new Ricetta(0, "Ricetta da eliminare", testChef));
        int id = ricetta.getId();
        
        // Act
        repository.delete(ricetta);
        
        // Assert
        assertNull(repository.findById(id));
    }
} 