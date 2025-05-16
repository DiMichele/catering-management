package software.service;

import software.domain.menu.Menu;
import software.domain.menu.SezioneMenu;
import software.domain.ricette.Ricetta;
import software.domain.utenti.Chef;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Servizio per la gestione dei menu.
 * Implementa le operazioni del caso d'uso "Gestione dei Menù".
 */
public class MenuService {
    private final ObservableList<Menu> menus = FXCollections.observableArrayList();
    private int nextMenuId = 1;
    private final AtomicInteger nextSezioneId = new AtomicInteger(1);
    
    /**
     * Crea un nuovo menu.
     * Riferimento: UC "Gestione dei Menù" - Passo 1
     */
    public Menu creaNuovoMenu(Chef chef) {
        Menu nuovoMenu = chef.creaNuovoMenu(nextMenuId++);
        menus.add(nuovoMenu);
        return nuovoMenu;
    }
    
    /**
     * Crea una nuova sezione di menu con il nome specificato.
     */
    public SezioneMenu creaSezioneMenu(String nome) {
        return new SezioneMenu(nextSezioneId.getAndIncrement(), nome);
    }
    
    /**
     * Modifica un menu esistente creando una copia.
     * Riferimento: UC "Gestione dei Menù" - Estensione 1b
     */
    public Menu modificaMenuEsistente(Menu menuOriginale, Chef chef) {
        Menu nuovoMenu = chef.creaNuovoMenu(nextMenuId++);
        nuovoMenu.setTitolo("Copia di " + menuOriginale.getTitolo());
        
        // Copia le sezioni
        List<String> nomiSezioni = new ArrayList<>();
        for (SezioneMenu sezione : menuOriginale.getSezioni()) {
            nomiSezioni.add(sezione.getNome());
        }
        nuovoMenu.definisciSezioni(nomiSezioni);
        
        // Copia le ricette in ogni sezione
        for (int i = 0; i < menuOriginale.getSezioni().size(); i++) {
            SezioneMenu sezioneOriginale = menuOriginale.getSezioni().get(i);
            SezioneMenu nuovaSezione = nuovoMenu.getSezioni().get(i);
            
            for (Ricetta ricetta : sezioneOriginale.getRicette()) {
                nuovoMenu.inserisciRicetta(ricetta, nuovaSezione);
            }
        }
        
        nuovoMenu.annotaInformazioni(menuOriginale.getNote());
        menus.add(nuovoMenu);
        return nuovoMenu;
    }
    
    /**
     * Pubblica un menu.
     * Riferimento: UC "Gestione dei Menù" - Passo 7
     */
    public void pubblicaMenu(Menu menu, String formato, List<String> destinatari) {
        menu.pubblica();
        // Qui ci sarebbe la logica per l'esportazione in diversi formati
    }
    
    /**
     * Elimina un menu.
     * Riferimento: UC "Gestione dei Menù" - Estensione 7a
     */
    public void eliminaMenu(Menu menu) {
        menus.remove(menu);
    }
    
    /**
     * Modifica una ricetta all'interno di un menu.
     * Riferimento: UC "Gestione dei Menù" - Estensione 4a
     * @param menu Il menu contenente la ricetta
     * @param ricettaDaModificare La ricetta da modificare
     * @param nuovaRicetta La nuova ricetta che sostituirà quella precedente
     * @return true se la ricetta è stata modificata con successo, false altrimenti
     */
    public boolean modificaRicetta(Menu menu, Ricetta ricettaDaModificare, Ricetta nuovaRicetta) {
        boolean ricettaTrovata = false;
        
        for (SezioneMenu sezione : menu.getSezioni()) {
            for (int i = 0; i < sezione.getRicette().size(); i++) {
                if (sezione.getRicette().get(i).equals(ricettaDaModificare)) {
                    sezione.getRicette().set(i, nuovaRicetta);
                    ricettaTrovata = true;
                    break;
                }
            }
            if (ricettaTrovata) break;
        }
        
        return ricettaTrovata;
    }
    
    public ObservableList<Menu> getMenus() {
        return menus;
    }
    
    public Menu getMenuById(int id) {
        for (Menu menu : menus) {
            if (menu.getId() == id) {
                return menu;
            }
        }
        return null;
    }
}