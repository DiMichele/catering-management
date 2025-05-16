package software;

import software.domain.utenti.Chef;
import software.service.CompitoCucinaService;
import software.service.EventoService;
import software.service.MenuService;
import software.service.RicettaService;
import software.service.UtenteService;
import software.ui.controllers.CompitiCucinaController;
import software.ui.controllers.MenuController;
import software.ui.controllers.RicettaController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    
    private MenuService menuService;
    private CompitoCucinaService compitoCucinaService;
    private EventoService eventoService;
    private RicettaService ricettaService;
    private UtenteService utenteService;
    private Chef chefCorrente;
    
    @Override
    public void start(Stage primaryStage) {
        // Inizializza i servizi
        inizializzaServizi();
        
        try {
            // Crea il TabPane principale
            TabPane tabPane = new TabPane();
            
            // Carica la vista per la gestione dei menu
            FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/software/ui/views/menu-view.fxml"));
            MenuController menuController = new MenuController(menuService, ricettaService, chefCorrente);
            menuLoader.setController(menuController);
            Tab menuTab = new Tab("Gestione Menù");
            menuTab.setContent(menuLoader.load());
            menuTab.setClosable(false);
            
            // Carica la vista per la gestione dei compiti di cucina
            FXMLLoader compitiLoader = new FXMLLoader(getClass().getResource("/software/ui/views/compiti-cucina-view.fxml"));
            CompitiCucinaController compitiController = new CompitiCucinaController(
                compitoCucinaService, eventoService, utenteService, ricettaService, chefCorrente);
            compitiLoader.setController(compitiController);
            Tab compitiTab = new Tab("Gestione Compiti Cucina");
            compitiTab.setContent(compitiLoader.load());
            compitiTab.setClosable(false);
            
            // Carica la vista per la gestione delle ricette
            FXMLLoader ricetteLoader = new FXMLLoader(getClass().getResource("/software/ui/views/ricette-view.fxml"));
            RicettaController ricetteController = new RicettaController(ricettaService, chefCorrente);
            ricetteLoader.setController(ricetteController);
            Tab ricetteTab = new Tab("Gestione Ricette");
            ricetteTab.setContent(ricetteLoader.load());
            ricetteTab.setClosable(false);
            
            // Aggiungi le tab al TabPane
            tabPane.getTabs().addAll(menuTab, compitiTab, ricetteTab);
            
            // Crea la scena
            Scene scene = new Scene(tabPane, 900, 700);
            primaryStage.setTitle("Sistema Gestione Catering");
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void inizializzaServizi() {
        // Crea i servizi
        menuService = new MenuService();
        compitoCucinaService = new CompitoCucinaService();
        eventoService = new EventoService();
        ricettaService = new RicettaService();
        utenteService = new UtenteService();
        
        // Crea lo chef corrente (utente di esempio)
        chefCorrente = new Chef(1, "Mario", "Rossi", "chef@example.com", "123456789");
        
        // Aggiungi dati di esempio (in una app reale questi verrebbero caricati da file/database)
        utenteService.creaUtentiDiEsempio();
        // Riabilito la creazione delle ricette di esempio
        ricettaService.creaRicetteDiEsempio();
        eventoService.creaEventiDiEsempio();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}