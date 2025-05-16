package software.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import software.domain.compiti.Compito;
import software.domain.menu.Menu;
import software.domain.ricette.Ricetta;
import software.domain.utenti.Chef;
import software.domain.utenti.Cuoco;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CompitiCucinaServiceTest {

    private CompitiCucinaService compitiService;
    private MenuService menuService;
    private Chef chef;
    private Cuoco cuoco;
    
    @BeforeEach
    public void setup() {
        compitiService = new CompitiCucinaService();
        menuService = new MenuService();
        chef = new Chef(1, "Mario", "Rossi", "chef@example.com", "123456789");
        cuoco = new Cuoco(2, "Luigi", "Verdi", "luigi@example.com", "987654321");
    }
    
    @Test
    @DisplayName("Test creazione riepilogo compiti da menu")
    public void testCreaRiepilogoCompiti() {
        // Arrange - Crea un menu con ricette
        Menu menu = menuService.creaNuovoMenu(chef);
        menu.setTitolo("Menu Test");
        List<String> sezioni = Arrays.asList("Antipasti", "Primi");
        menu.definisciSezioni(sezioni);
        
        Ricetta ricetta1 = new Ricetta(1, "Bruschetta", chef);
        Ricetta ricetta2 = new Ricetta(2, "Pasta al pomodoro", chef);
        
        menu.inserisciRicetta(ricetta1, menu.getSezioni().get(0));
        menu.inserisciRicetta(ricetta2, menu.getSezioni().get(1));
        
        // Act
        List<Compito> compiti = compitiService.creaRiepilogoCompiti(menu);
        
        // Assert
        assertEquals(2, compiti.size(), "Dovrebbero essere creati 2 compiti");
        
        // I compiti dovrebbero corrispondere alle ricette nel menu
        boolean hasBruschetta = false;
        boolean hasPasta = false;
        
        for (Compito compito : compiti) {
            if ("Bruschetta".equals(compito.getRicetta().getNome())) {
                hasBruschetta = true;
            } else if ("Pasta al pomodoro".equals(compito.getRicetta().getNome())) {
                hasPasta = true;
            }
        }
        
        assertTrue(hasBruschetta, "Dovrebbe esserci un compito per Bruschetta");
        assertTrue(hasPasta, "Dovrebbe esserci un compito per Pasta al pomodoro");
    }
    
    @Test
    @DisplayName("Test assegnazione compito a cuoco")
    public void testAssegnaCompito() {
        // Arrange - Crea un menu e i compiti
        Menu menu = menuService.creaNuovoMenu(chef);
        menu.setTitolo("Menu Test");
        List<String> sezioni = Arrays.asList("Antipasti");
        menu.definisciSezioni(sezioni);
        
        Ricetta ricetta = new Ricetta(1, "Bruschetta", chef);
        menu.inserisciRicetta(ricetta, menu.getSezioni().get(0));
        
        List<Compito> compiti = compitiService.creaRiepilogoCompiti(menu);
        assertEquals(1, compiti.size());
        
        Compito compito = compiti.get(0);
        
        // Act
        Compito compitoAggiornato = compitiService.assegnaCompito(compito.getId(), cuoco, "Mattina", 45, 10);
        
        // Assert
        assertNotNull(compitoAggiornato);
        assertEquals(cuoco, compitoAggiornato.getCuocoAssegnato());
        assertEquals("Mattina", compitoAggiornato.getTurno());
        assertEquals(45, compitoAggiornato.getDurata());
        assertEquals(10, compitoAggiornato.getQuantita());
    }
    
    @Test
    @DisplayName("Test ordinamento compiti per importanza")
    public void testOrdinaCompiti() {
        // Arrange - Crea un menu e compiti
        Menu menu = menuService.creaNuovoMenu(chef);
        menu.setTitolo("Menu Test");
        List<String> sezioni = Arrays.asList("Antipasti", "Primi");
        menu.definisciSezioni(sezioni);
        
        Ricetta ricetta1 = new Ricetta(1, "Bruschetta", chef);
        Ricetta ricetta2 = new Ricetta(2, "Pasta al pomodoro", chef);
        
        menu.inserisciRicetta(ricetta1, menu.getSezioni().get(0));
        menu.inserisciRicetta(ricetta2, menu.getSezioni().get(1));
        
        List<Compito> compiti = compitiService.creaRiepilogoCompiti(menu);
        assertEquals(2, compiti.size());
        
        // Imposta diverse importanze
        Compito compito1 = compiti.get(0);
        Compito compito2 = compiti.get(1);
        
        compito1.setImportanza(2);
        compito2.setImportanza(5);
        
        // Act
        List<Compito> compitiOrdinati = compitiService.ordinaCompiti(compiti);
        
        // Assert
        assertEquals(2, compitiOrdinati.size());
        assertEquals(5, compitiOrdinati.get(0).getImportanza(), "Il compito più importante dovrebbe essere primo");
        assertEquals(2, compitiOrdinati.get(1).getImportanza(), "Il compito meno importante dovrebbe essere secondo");
    }
    
    @Test
    @DisplayName("Test finalizzazione piano")
    public void testFinalizzaPiano() {
        // Arrange - Crea un menu e compiti
        Menu menu = menuService.creaNuovoMenu(chef);
        menu.definisciSezioni(Arrays.asList("Antipasti"));
        
        Ricetta ricetta = new Ricetta(1, "Bruschetta", chef);
        menu.inserisciRicetta(ricetta, menu.getSezioni().get(0));
        
        List<Compito> compiti = compitiService.creaRiepilogoCompiti(menu);
        Compito compito = compiti.get(0);
        
        // Assegna cuoco e turno al compito
        compitiService.assegnaCompito(compito.getId(), cuoco, "Mattina", 45, 10);
        
        // Act
        compitiService.finalizzaPiano(compiti);
        
        // Assert
        assertEquals("Pianificato", compito.getStato(), "Lo stato del compito dovrebbe essere Pianificato");
    }
    
    @Test
    @DisplayName("Test finalizzazione piano senza cuoco o turno")
    public void testFinalizzaPianoSenzaAssegnazione() {
        // Arrange - Crea un menu e compiti
        Menu menu = menuService.creaNuovoMenu(chef);
        menu.definisciSezioni(Arrays.asList("Antipasti"));
        
        Ricetta ricetta = new Ricetta(1, "Bruschetta", chef);
        menu.inserisciRicetta(ricetta, menu.getSezioni().get(0));
        
        List<Compito> compiti = compitiService.creaRiepilogoCompiti(menu);
        
        // Act & Assert - Senza cuoco assegnato
        assertThrows(IllegalStateException.class, () -> {
            compitiService.finalizzaPiano(compiti);
        });
    }
    
    @Test
    @DisplayName("Test monitoraggio avanzamento compiti")
    public void testMonitoraAvanzamento() {
        // Arrange
        Menu menu = menuService.creaNuovoMenu(chef);
        menu.definisciSezioni(Arrays.asList("Antipasti", "Primi", "Secondi"));
        
        Ricetta ricetta1 = new Ricetta(1, "Bruschetta", chef);
        Ricetta ricetta2 = new Ricetta(2, "Pasta", chef);
        Ricetta ricetta3 = new Ricetta(3, "Arrosto", chef);
        
        menu.inserisciRicetta(ricetta1, menu.getSezioni().get(0));
        menu.inserisciRicetta(ricetta2, menu.getSezioni().get(1));
        menu.inserisciRicetta(ricetta3, menu.getSezioni().get(2));
        
        List<Compito> compiti = compitiService.creaRiepilogoCompiti(menu);
        
        // Assegna stati ai compiti
        compiti.get(0).setStato("Completato");
        compiti.get(1).setStato("In corso");
        compiti.get(2).setStato("Da iniziare");
        
        // Act
        int percentualeCompletamento = compitiService.monitoraAvanzamento(compiti);
        
        // Assert
        assertEquals(33, percentualeCompletamento, "La percentuale di completamento dovrebbe essere circa 33%");
    }
} 