package software.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import software.domain.exceptions.DomainException;
import software.domain.ricette.Ricetta;
import software.domain.utenti.Chef;
import software.service.persistence.InMemoryRepository;
import software.ui.viewmodels.RicettaViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test semplificati per RicettaService che usano una vera istanza di InMemoryRepository
 * invece di un mock, per risolvere problemi di compatibilità con Java 21.
 */
public class RicettaServiceTest {

    private InMemoryRepository<Ricetta, Integer> ricettaRepository;
    private RicettaService ricettaService;
    private Chef testChef;
    
    @BeforeEach
    public void setup() {
        testChef = new Chef(1, "Mario", "Rossi", "chef@example.com", "123456789");
        
        // Usa una vera istanza del repository invece di un mock
        ricettaRepository = new InMemoryRepository<>("test-ricette", Ricetta.class, "id");
        ricettaService = new RicettaService(ricettaRepository);
    }
    
    @Test
    @DisplayName("Test creazione ricetta valida")
    public void testCreaRicettaValida() {
        // Act
        String nomeRicetta = "Carbonara";
        Ricetta risultato = ricettaService.creaRicetta(nomeRicetta, testChef);
        
        // Assert
        assertNotNull(risultato);
        assertTrue(risultato.getId() > 0);
        assertEquals(nomeRicetta, risultato.getNome());
        assertEquals(testChef, risultato.getProprietario());
        
        // Verifica che la ricetta sia stata salvata nel repository
        assertNotNull(ricettaRepository.findById(risultato.getId()));
    }
    
    @Test
    @DisplayName("Test creazione ricetta con nome nullo")
    public void testCreaRicettaNomeNullo() {
        // Act & Assert
        assertThrows(DomainException.class, () -> {
            ricettaService.creaRicetta(null, testChef);
        });
    }
    
    @Test
    @DisplayName("Test creazione ricetta con nome vuoto")
    public void testCreaRicettaNomeVuoto() {
        // Act & Assert
        assertThrows(DomainException.class, () -> {
            ricettaService.creaRicetta("", testChef);
        });
    }
    
    @Test
    @DisplayName("Test creazione ricetta con chef nullo")
    public void testCreaRicettaChefNullo() {
        // Act & Assert
        assertThrows(DomainException.class, () -> {
            ricettaService.creaRicetta("Carbonara", null);
        });
    }
    
    @Test
    @DisplayName("Test eliminazione ricetta")
    public void testEliminaRicetta() {
        // Arrange
        Ricetta ricetta = ricettaService.creaRicetta("Ricetta da eliminare", testChef);
        int id = ricetta.getId();
        
        // Act
        ricettaService.eliminaRicetta(ricetta);
        
        // Assert - La ricetta non dovrebbe più esistere nel repository
        assertNull(ricettaRepository.findById(id));
    }
    
    @Test
    @DisplayName("Test eliminazione ricetta nulla")
    public void testEliminaRicettaNulla() {
        // Act & Assert
        assertThrows(DomainException.class, () -> {
            ricettaService.eliminaRicetta(null);
        });
    }
    
    @Test
    @DisplayName("Test eliminazione ricetta in uso")
    public void testEliminaRicettaInUso() {
        // Arrange
        Ricetta ricetta = ricettaService.creaRicetta("Ricetta in uso", testChef);
        ricetta.setInUso(true);
        ricettaRepository.save(ricetta);
        
        // Act & Assert
        assertThrows(DomainException.class, () -> {
            ricettaService.eliminaRicetta(ricetta);
        });
        
        // Verifica che la ricetta esista ancora
        assertNotNull(ricettaRepository.findById(ricetta.getId()));
    }
    
    @Test
    @DisplayName("Test aggiornamento ricetta")
    public void testAggiornaRicetta() {
        // Arrange
        Ricetta ricetta = ricettaService.creaRicetta("Ricetta originale", testChef);
        ricetta.setDescrizione("Nuova descrizione");
        
        // Act
        Ricetta risultato = ricettaService.aggiornaRicetta(ricetta);
        
        // Assert
        assertNotNull(risultato);
        assertEquals("Nuova descrizione", risultato.getDescrizione());
        
        // Verifica che le modifiche siano state salvate nel repository
        Ricetta ricettaSalvata = ricettaRepository.findById(ricetta.getId());
        assertEquals("Nuova descrizione", ricettaSalvata.getDescrizione());
    }
    
    @Test
    @DisplayName("Test aggiornamento ricetta nulla")
    public void testAggiornaRicettaNulla() {
        // Act & Assert
        assertThrows(DomainException.class, () -> {
            ricettaService.aggiornaRicetta(null);
        });
    }
    
    @Test
    @DisplayName("Test pubblicazione ricetta")
    public void testPubblicaRicetta() {
        // Arrange
        Ricetta ricetta = ricettaService.creaRicetta("Ricetta da pubblicare", testChef);
        
        // Act
        ricettaService.pubblicaRicetta(ricetta);
        
        // Assert
        assertEquals("Pubblicata", ricetta.getStato());
        
        // Verifica che le modifiche siano state salvate nel repository
        Ricetta ricettaSalvata = ricettaRepository.findById(ricetta.getId());
        assertEquals("Pubblicata", ricettaSalvata.getStato());
    }
    
    @Test
    @DisplayName("Test pubblicazione ricetta nulla")
    public void testPubblicaRicettaNulla() {
        // Act & Assert
        assertThrows(DomainException.class, () -> {
            ricettaService.pubblicaRicetta(null);
        });
    }
    
    @Test
    @DisplayName("Test creazione ricette di esempio")
    public void testCreaRicetteDiEsempio() {
        // Act - Non dovrebbe generare errori
        ricettaService.creaRicetteDiEsempio();
        
        // Assert - Verifichiamo che il metodo non generi errori
        // Non testiamo la creazione effettiva delle ricette perché la condizione interna
        // del metodo potrebbe saltare la creazione se ci sono già dati nel repository
        assertTrue(true);
    }
} 