package software.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import software.domain.menu.Menu;
import software.domain.menu.SezioneMenu;
import software.domain.ricette.Ricetta;
import software.domain.utenti.Chef;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MenuServiceTest {

    private MenuService menuService;
    private Chef chef;
    
    @BeforeEach
    public void setup() {
        menuService = new MenuService();
        chef = new Chef(1, "Mario", "Rossi", "chef@example.com", "123456789");
    }
    
    @Test
    @DisplayName("Test creazione nuovo menu")
    public void testCreaNuovoMenu() {
        // Act
        Menu risultato = menuService.creaNuovoMenu(chef);
        
        // Assert
        assertNotNull(risultato);
        assertEquals(1, risultato.getId());
        assertEquals(chef, risultato.getCreatore());
        assertEquals(1, menuService.getMenus().size());
        assertEquals(risultato, menuService.getMenus().get(0));
    }
    
    @Test
    @DisplayName("Test getMenuById con menu esistente")
    public void testGetMenuByIdEsistente() {
        // Arrange
        Menu menu = menuService.creaNuovoMenu(chef);
        
        // Act
        Menu risultato = menuService.getMenuById(1);
        
        // Assert
        assertNotNull(risultato);
        assertEquals(menu, risultato);
    }
    
    @Test
    @DisplayName("Test getMenuById con menu non esistente")
    public void testGetMenuByIdNonEsistente() {
        // Act
        Menu risultato = menuService.getMenuById(999);
        
        // Assert
        assertNull(risultato);
    }
    
    @Test
    @DisplayName("Test creaSezioneMenu")
    public void testCreaSezioneMenu() {
        // Act
        SezioneMenu sezione = menuService.creaSezioneMenu("Antipasti");
        
        // Assert
        assertNotNull(sezione);
        assertEquals(1, sezione.getId());
        assertEquals("Antipasti", sezione.getNome());
    }
    
    @Test
    @DisplayName("Test creaSezioneMenu incrementa ID")
    public void testCreaSezioneMenuIncrementaId() {
        // Act
        SezioneMenu sezione1 = menuService.creaSezioneMenu("Antipasti");
        SezioneMenu sezione2 = menuService.creaSezioneMenu("Primi");
        
        // Assert
        assertEquals(1, sezione1.getId());
        assertEquals(2, sezione2.getId());
    }
    
    @Test
    @DisplayName("Test modificaMenuEsistente")
    public void testModificaMenuEsistente() {
        // Arrange
        Menu menu = menuService.creaNuovoMenu(chef);
        menu.setTitolo("Menu Originale");
        List<String> sezioni = Arrays.asList("Antipasti", "Primi");
        menu.definisciSezioni(sezioni);
        
        // Act
        Menu menuModificato = menuService.modificaMenuEsistente(menu, chef);
        
        // Assert
        assertNotNull(menuModificato);
        assertEquals(2, menuModificato.getId()); // Nuovo ID
        assertEquals("Copia di Menu Originale", menuModificato.getTitolo());
        assertEquals(2, menuModificato.getSezioni().size());
        assertEquals("Antipasti", menuModificato.getSezioni().get(0).getNome());
        assertEquals("Primi", menuModificato.getSezioni().get(1).getNome());
        assertEquals(2, menuService.getMenus().size()); // Ora ci sono due menu
    }
    
    @Test
    @DisplayName("Test modificaMenuEsistente copia ricette")
    public void testModificaMenuEsistenteCopiaRicette() {
        // Arrange
        Menu menu = menuService.creaNuovoMenu(chef);
        menu.setTitolo("Menu Originale");
        List<String> sezioni = Arrays.asList("Antipasti");
        menu.definisciSezioni(sezioni);
        
        Ricetta ricetta = new Ricetta(1, "Bruschetta", chef);
        menu.inserisciRicetta(ricetta, menu.getSezioni().get(0));
        
        // Act
        Menu menuModificato = menuService.modificaMenuEsistente(menu, chef);
        
        // Assert
        assertNotNull(menuModificato);
        assertEquals(1, menuModificato.getSezioni().get(0).getRicette().size());
        assertEquals(ricetta, menuModificato.getSezioni().get(0).getRicette().get(0));
    }
    
    @Test
    @DisplayName("Test pubblicaMenu")
    public void testPubblicaMenu() {
        // Arrange
        Menu menu = menuService.creaNuovoMenu(chef);
        List<String> destinatari = Arrays.asList("client@example.com");
        
        // Act
        menuService.pubblicaMenu(menu, "PDF", destinatari);
        
        // Assert
        assertEquals("Pubblicato", menu.getStato());
    }
    
    @Test
    @DisplayName("Test eliminaMenu")
    public void testEliminaMenu() {
        // Arrange
        Menu menu = menuService.creaNuovoMenu(chef);
        
        // Act
        menuService.eliminaMenu(menu);
        
        // Assert
        assertEquals(0, menuService.getMenus().size());
        assertNull(menuService.getMenuById(1));
    }
    
    @Test
    @DisplayName("Test flusso completo creazione menu")
    public void testFlussoCompletoMenu() {
        // Step 1: Creazione nuovo menu
        Menu menu = menuService.creaNuovoMenu(chef);
        assertNotNull(menu);
        assertEquals("Bozza", menu.getStato());
        
        // Step 2: Aggiunta titolo
        menu.setTitolo("Menu Primavera");
        assertEquals("Menu Primavera", menu.getTitolo());
        
        // Step 3: Definizione delle sezioni
        List<String> nomiSezioni = Arrays.asList("Antipasti", "Primi", "Secondi", "Dolci");
        menu.definisciSezioni(nomiSezioni);
        assertEquals(4, menu.getSezioni().size());
        
        // Step 4: Inserimento ricette nelle sezioni
        Ricetta ricettaAntipasto = new Ricetta(1, "Bruschetta al pomodoro", chef);
        Ricetta ricettaPrimo = new Ricetta(2, "Pasta al pesto", chef);
        
        menu.inserisciRicetta(ricettaAntipasto, menu.getSezioni().get(0));
        menu.inserisciRicetta(ricettaPrimo, menu.getSezioni().get(1));
        
        assertEquals(1, menu.getSezioni().get(0).getRicette().size());
        assertEquals(1, menu.getSezioni().get(1).getRicette().size());
        
        // Step 6: Aggiunta di note e informazioni
        menu.annotaInformazioni("Menu ideale per occasioni speciali. Prevede piatti freschi e leggeri.");
        assertEquals("Menu ideale per occasioni speciali. Prevede piatti freschi e leggeri.", menu.getNote());
        
        // Step 7: Pubblicazione del menu
        List<String> destinatari = Arrays.asList("cliente@example.com", "organizzatore@example.com");
        menuService.pubblicaMenu(menu, "PDF", destinatari);
        
        assertEquals("Pubblicato", menu.getStato());
    }

    @Test
    @DisplayName("Test modificaRicetta")
    public void testModificaRicetta() {
        // Arrange
        Menu menu = menuService.creaNuovoMenu(chef);
        menu.setTitolo("Menu Test");
        List<String> sezioni = Arrays.asList("Antipasti", "Primi");
        menu.definisciSezioni(sezioni);
        
        Ricetta ricettaOriginale = new Ricetta(1, "Bruschetta", chef);
        menu.inserisciRicetta(ricettaOriginale, menu.getSezioni().get(0));
        
        Ricetta nuovaRicetta = new Ricetta(2, "Bruschetta Gourmet", chef);
        
        // Act
        boolean risultato = menuService.modificaRicetta(menu, ricettaOriginale, nuovaRicetta);
        
        // Assert
        assertTrue(risultato, "La modifica della ricetta dovrebbe riuscire");
        assertEquals(1, menu.getSezioni().get(0).getRicette().size());
        assertEquals(nuovaRicetta, menu.getSezioni().get(0).getRicette().get(0));
    }

    @Test
    @DisplayName("Test modificaRicetta con ricetta non presente")
    public void testModificaRicettaNonPresente() {
        // Arrange
        Menu menu = menuService.creaNuovoMenu(chef);
        menu.setTitolo("Menu Test");
        List<String> sezioni = Arrays.asList("Antipasti", "Primi");
        menu.definisciSezioni(sezioni);
        
        Ricetta ricettaNelMenu = new Ricetta(1, "Bruschetta", chef);
        menu.inserisciRicetta(ricettaNelMenu, menu.getSezioni().get(0));
        
        Ricetta ricettaNonPresente = new Ricetta(3, "Pasta alla Carbonara", chef);
        Ricetta nuovaRicetta = new Ricetta(2, "Bruschetta Gourmet", chef);
        
        // Act
        boolean risultato = menuService.modificaRicetta(menu, ricettaNonPresente, nuovaRicetta);
        
        // Assert
        assertFalse(risultato, "La modifica dovrebbe fallire per ricetta non presente");
        assertEquals(1, menu.getSezioni().get(0).getRicette().size());
        assertEquals(ricettaNelMenu, menu.getSezioni().get(0).getRicette().get(0));
    }
} 