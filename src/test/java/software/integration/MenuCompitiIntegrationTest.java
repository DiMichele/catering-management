package software.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import software.domain.compiti.Compito;
import software.domain.menu.Menu;
import software.domain.ricette.Ricetta;
import software.domain.utenti.Chef;
import software.domain.utenti.Cuoco;
import software.service.CompitiCucinaService;
import software.service.MenuService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test di integrazione che mostra il flusso completo dalla creazione di un menu
 * fino alla pianificazione e monitoraggio dei compiti di cucina.
 */
public class MenuCompitiIntegrationTest {

    private MenuService menuService;
    private CompitiCucinaService compitiService;
    private Chef chefCreatore;
    private Cuoco chefEsecutore;
    
    @BeforeEach
    public void setup() {
        menuService = new MenuService();
        compitiService = new CompitiCucinaService();
        
        chefCreatore = new Chef(1, "Mario", "Rossi", "mario@example.com", "123456789");
        chefEsecutore = new Cuoco(2, "Luigi", "Verdi", "luigi@example.com", "987654321");
    }
    
    @Test
    @DisplayName("Test integrazione flusso completo")
    public void testFlussoCompletoMenuCompiti() {
        // FASE 1: Creazione e pubblicazione del menu
        
        // Crea un nuovo menu
        Menu menu = menuService.creaNuovoMenu(chefCreatore);
        assertNotNull(menu);
        
        // Aggiungi titolo al menu
        menu.setTitolo("Menu Evento Aziendale");
        
        // Definisci le sezioni del menu
        List<String> sezioni = Arrays.asList("Antipasti", "Primi", "Secondi", "Dolci");
        menu.definisciSezioni(sezioni);
        assertEquals(4, menu.getSezioni().size());
        
        // Crea ricette e inseriscile nel menu
        Ricetta bruschetta = new Ricetta(1, "Bruschetta al pomodoro", chefCreatore);
        Ricetta risotto = new Ricetta(2, "Risotto ai funghi", chefCreatore);
        Ricetta arrosto = new Ricetta(3, "Arrosto di vitello", chefCreatore);
        Ricetta tiramisu = new Ricetta(4, "Tiramisù", chefCreatore);
        
        menu.inserisciRicetta(bruschetta, menu.getSezioni().get(0)); // Antipasti
        menu.inserisciRicetta(risotto, menu.getSezioni().get(1));    // Primi
        menu.inserisciRicetta(arrosto, menu.getSezioni().get(2));    // Secondi
        menu.inserisciRicetta(tiramisu, menu.getSezioni().get(3));   // Dolci
        
        // Aggiungi note al menu
        menu.annotaInformazioni("Menu per evento aziendale di 50 persone. Evento previsto per il 15/12/2023.");
        
        // Pubblica il menu
        List<String> destinatari = Arrays.asList("cliente@example.com");
        menuService.pubblicaMenu(menu, "PDF", destinatari);
        assertEquals("Pubblicato", menu.getStato());
        
        // FASE 2: Creazione dei compiti della cucina basati sul menu
        
        // Crea riepilogo compiti
        List<Compito> compiti = compitiService.creaRiepilogoCompiti(menu);
        assertEquals(4, compiti.size());
        
        // Assegna compiti al chef esecutore
        for (Compito compito : compiti) {
            String nomeTurno = compito.getRicetta().getNome().contains("Tiramisù") ? "Mattina" : "Pomeriggio";
            int durata = compito.getRicetta().getNome().contains("Arrosto") ? 120 : 60;
            int quantita = 50; // 50 porzioni per ogni ricetta
            
            Compito compitoAggiornato = compitiService.assegnaCompito(compito.getId(), chefEsecutore, nomeTurno, durata, quantita);
            assertNotNull(compitoAggiornato);
        }
        
        // Imposta livelli di importanza per i compiti
        compiti.get(0).setImportanza(2); // Bruschetta
        compiti.get(1).setImportanza(3); // Risotto
        compiti.get(2).setImportanza(5); // Arrosto (alto)
        compiti.get(3).setImportanza(4); // Tiramisù
        
        // Ordina i compiti per importanza
        List<Compito> compitiOrdinati = compitiService.ordinaCompiti(compiti);
        assertEquals(5, compitiOrdinati.get(0).getImportanza(), "Il compito più importante dovrebbe essere l'arrosto");
        assertEquals("Arrosto di vitello", compitiOrdinati.get(0).getRicetta().getNome());
        
        // Finalizza il piano dei compiti
        compitiService.finalizzaPiano(compiti);
        
        // Verifica che tutti i compiti siano stati pianificati
        for (Compito compito : compiti) {
            assertEquals("Pianificato", compito.getStato());
        }
        
        // FASE 3: Monitoraggio dell'avanzamento
        
        // Simula l'avanzamento dei compiti
        compiti.get(0).setStato("Completato"); // Bruschetta completata
        compiti.get(3).setStato("Completato"); // Tiramisù completato
        
        // Monitora l'avanzamento
        int percentualeCompletamento = compitiService.monitoraAvanzamento(compiti);
        assertEquals(50, percentualeCompletamento, "La percentuale di completamento dovrebbe essere 50%");
    }
} 