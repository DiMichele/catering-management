package software.ui.controllers;

import software.domain.menu.Menu;
import software.domain.menu.SezioneMenu;
import software.domain.ricette.Ricetta;
import software.domain.utenti.Chef;
import software.service.MenuService;
import software.service.RicettaService;
import software.ui.utils.AlertUtils;
import software.ui.viewmodels.RicettaViewModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller per la gestione dei menu.
 * Implementa il pattern GRASP Controller per il caso d'uso "Gestione dei Menù".
 */
public class MenuController {
    
    @FXML private TextField txtTitolo;
    @FXML private TextArea txtNote;
    @FXML private ComboBox<Menu> cmbMenuEsistenti;
    @FXML private TextField txtSezioni;
    @FXML private ListView<RicettaViewModel> lstRicetteDisponibili;
    @FXML private TabPane tabSezioni;
    @FXML private VBox contenitoreMenu;
    @FXML private CheckBox chkPiattiCaldi;
    @FXML private CheckBox chkPiattiFreddi;
    
    private final MenuService menuService;
    private final RicettaService ricettaService;
    private Chef chefCorrente;
    private Menu menuCorrente;
    private Tab tabSelezionato;
    
    public MenuController(MenuService menuService, RicettaService ricettaService, Chef chef) {
        this.menuService = menuService;
        this.ricettaService = ricettaService;
        this.chefCorrente = chef;
    }
    
    @FXML
    public void initialize() {
        // Inizializza le liste
        cmbMenuEsistenti.setItems(menuService.getMenus());
        lstRicetteDisponibili.setItems(ricettaService.getRicetteDisponibiliViewModel());
        
        // Configurazione rendering elementi ComboBox
        cmbMenuEsistenti.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Menu item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getTitolo());
            }
        });
        cmbMenuEsistenti.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Menu item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getTitolo());
            }
        });
        
        // Configurazione rendering elementi ListView
        lstRicetteDisponibili.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(RicettaViewModel item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || item == null) {
                    setText(null);
                } else {
                    Ricetta ricetta = item.getRicetta();
                    setText(ricetta.getNome() + " (" + ricetta.getTempoPreparazione() + " min)");
                }
            }
        });
        
        // Aggiorna UI quando si seleziona un menu esistente
        cmbMenuEsistenti.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                menuCorrente = newVal;
                aggiornaUIConMenu(menuCorrente);
            }
        });
        
        // Aggiorna qual è la tab selezionata
        tabSezioni.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> tabSelezionato = newVal
        );
        
        // Disabilita la sezione di modifica del menu inizialmente
        contenitoreMenu.setDisable(true);
    }
    
    /**
     * Gestisce la creazione di un nuovo menu.
     * Riferimento: UC "Gestione dei Menù" - Passo 1
     */
    @FXML
    public void onNuovoMenu() {
        menuCorrente = menuService.creaNuovoMenu(chefCorrente);
        
        // Abilita l'interfaccia e pulisce i campi
        contenitoreMenu.setDisable(false);
        txtTitolo.clear();
        txtSezioni.clear();
        txtNote.clear();
        tabSezioni.getTabs().clear();
        
        // Focus sul titolo
        txtTitolo.requestFocus();
    }
    
    /**
     * Gestisce la modifica di un menu esistente.
     * Riferimento: UC "Gestione dei Menù" - Estensione 1b
     */
    @FXML
    public void onModificaMenu() {
        Menu menu = cmbMenuEsistenti.getValue();
        if (menu != null) {
            menuCorrente = menu;
            contenitoreMenu.setDisable(false);
            aggiornaUIConMenu(menu);
        } else {
            AlertUtils.showWarning("Selezione richiesta", "Seleziona un menu dalla lista");
        }
    }
    
    /**
     * Gestisce l'inserimento del titolo.
     * Riferimento: UC "Gestione dei Menù" - Passo 2
     */
    @FXML
    public void onSalvaTitolo() {
        if (menuCorrente != null && !txtTitolo.getText().trim().isEmpty()) {
            menuCorrente.setTitolo(txtTitolo.getText().trim());
            AlertUtils.showInfo("Salvato", "Titolo aggiornato con successo");
        }
    }
    
    /**
     * Gestisce la definizione delle sezioni.
     * Riferimento: UC "Gestione dei Menù" - Passo 3
     */
    @FXML
    public void onDefinisciSezioni() {
        if (menuCorrente == null) return;
        
        String sezioniTesto = txtSezioni.getText().trim();
        if (sezioniTesto.isEmpty()) {
            AlertUtils.showWarning("Input richiesto", "Inserisci le sezioni del menu");
            return;
        }
        
        // Parsa le sezioni dal testo (separate da virgola)
        List<String> nomiSezioni = Arrays.stream(sezioniTesto.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());
            
        if (nomiSezioni.isEmpty()) {
            AlertUtils.showWarning("Input non valido", "Inserisci almeno una sezione valida");
            return;
        }
        
        // Cancella le sezioni esistenti e crea quelle nuove
        menuCorrente.getSezioni().clear();
        tabSezioni.getTabs().clear();
        
        for (String nome : nomiSezioni) {
            // Crea una nuova sezione del menu
            SezioneMenu sezione = menuService.creaSezioneMenu(nome);
            menuCorrente.getSezioni().add(sezione);
            
            // Crea la tab corrispondente
            Tab tab = creaTabPerSezione(sezione);
            tabSezioni.getTabs().add(tab);
        }
        
        AlertUtils.showInfo("Operazione completata", "Sezioni del menu definite con successo");
    }
    
    /**
     * Gestisce l'inserimento di una ricetta nel menu.
     * Riferimento: UC "Gestione dei Menù" - Passo 4
     */
    @FXML
    public void onInserisciRicetta() {
        if (menuCorrente == null) return;
        
        RicettaViewModel ricettaViewModel = lstRicetteDisponibili.getSelectionModel().getSelectedItem();
        
        String sezioneNome = tabSelezionato != null ? tabSelezionato.getText() : null;
        
        if (ricettaViewModel != null && tabSelezionato != null) {
            // Trova la sezione corrispondente
            Optional<SezioneMenu> sezioneMenu = menuCorrente.getSezioni().stream()
                .filter(s -> s.getNome().equals(sezioneNome))
                .findFirst();
                
            if (sezioneMenu.isPresent()) {
                menuCorrente.inserisciRicetta(ricettaViewModel.getRicetta(), sezioneMenu.get());
                aggiornaContenutoSezione(sezioneMenu.get());
            }
        } else {
            AlertUtils.showWarning("Selezione incompleta", "Seleziona una ricetta e una sezione");
        }
    }
    
    /**
     * Gestisce l'annotazione di informazioni.
     * Riferimento: UC "Gestione dei Menù" - Passo 6
     */
    @FXML
    public void onSalvaNote() {
        if (menuCorrente != null) {
            menuCorrente.annotaInformazioni(txtNote.getText());
            AlertUtils.showInfo("Salvato", "Note aggiornate con successo");
        }
    }
    
    /**
     * Gestisce la pubblicazione del menu.
     * Riferimento: UC "Gestione dei Menù" - Passo 7
     */
    @FXML
    public void onPubblicaMenu() {
        if (menuCorrente != null) {
            menuCorrente.setStato("Pubblicato");
            AlertUtils.showInfo("Operazione completata", "Menu pubblicato con successo");
        }
    }
    
    /**
     * Gestisce l'eliminazione del menu.
     * Riferimento: UC "Gestione dei Menù" - Estensione 7a
     */
    @FXML
    public void onEliminaMenu() {
        if (menuCorrente != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Conferma eliminazione");
            alert.setHeaderText("Sei sicuro di voler eliminare questo menu?");
            alert.setContentText("Questa operazione non può essere annullata.");
            
            ButtonType buttonTypeOk = new ButtonType("Elimina", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
            
            alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == buttonTypeOk) {
                menuService.eliminaMenu(menuCorrente);
                menuCorrente = null;
                contenitoreMenu.setDisable(true);
                aggiornaListaMenu();
            }
        }
    }
    
    /**
     * Crea una nuova tab per una sezione del menu.
     */
    private Tab creaTabPerSezione(SezioneMenu sezione) {
        Tab tab = new Tab(sezione.getNome());
        tab.setClosable(false);
        
        // Lista delle ricette nella sezione
        ListView<RicettaViewModel> listView = new ListView<>();
        
        // Popola la lista con le ricette nella sezione
        ObservableList<RicettaViewModel> ricetteInSezione = ricettaService.getRicetteViewModel()
            .filtered(vm -> sezione.getRicette().contains(vm.getRicetta()));
            
        listView.setItems(ricetteInSezione);
        
        // Personalizza la visualizzazione delle ricette
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(RicettaViewModel item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || item == null) {
                    setText(null);
                } else {
                    Ricetta ricetta = item.getRicetta();
                    setText(ricetta.getNome());
                }
            }
        });
        
        tab.setContent(listView);
        return tab;
    }
    
    /**
     * Aggiorna il contenuto di una sezione del menu.
     */
    private void aggiornaContenutoSezione(SezioneMenu sezione) {
        // Trova la tab corrispondente
        Optional<Tab> tab = tabSezioni.getTabs().stream()
            .filter(t -> t.getText().equals(sezione.getNome()))
            .findFirst();
            
        if (tab.isPresent()) {
            ListView<RicettaViewModel> listView = (ListView<RicettaViewModel>) tab.get().getContent();
            
            // Aggiorna la lista delle ricette
            ObservableList<RicettaViewModel> ricetteInSezione = ricettaService.getRicetteViewModel()
                .filtered(vm -> sezione.getRicette().contains(vm.getRicetta()));
                
            listView.setItems(ricetteInSezione);
        }
    }
    
    /**
     * Aggiorna l'UI con i dati di un menu esistente.
     */
    private void aggiornaUIConMenu(Menu menu) {
        txtTitolo.setText(menu.getTitolo());
        txtNote.setText(menu.getNote());
        
        // Aggiorna le sezioni
        tabSezioni.getTabs().clear();
        
        String sezioniTesto = menu.getSezioni().stream()
            .map(SezioneMenu::getNome)
            .collect(Collectors.joining(", "));
            
        txtSezioni.setText(sezioniTesto);
        
        // Crea le tab per ogni sezione
        for (SezioneMenu sezione : menu.getSezioni()) {
            Tab tab = creaTabPerSezione(sezione);
            tabSezioni.getTabs().add(tab);
        }
    }
    
    /**
     * Aggiorna la lista dei menu esistenti.
     */
    private void aggiornaListaMenu() {
        cmbMenuEsistenti.setItems(menuService.getMenus());
    }
}