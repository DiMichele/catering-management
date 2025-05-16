package software.ui.controllers;

import software.domain.compiti.Compito;
import software.domain.compiti.Turno;
import software.domain.eventi.Evento;
import software.domain.ricette.Ricetta;
import software.domain.utenti.Chef;
import software.domain.utenti.Cuoco;
import software.service.CompitoCucinaService;
import software.service.EventoService;
import software.service.RicettaService;
import software.service.UtenteService;
import software.ui.utils.AlertUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller per la gestione dei compiti di cucina.
 * Implementa il pattern GRASP Controller per il caso d'uso "Gestione dei Compiti della cucina".
 */
public class CompitiCucinaController {
    
    @FXML private ComboBox<Evento> cmbEventi;
    @FXML private TableView<Compito> tblCompiti;
    @FXML private TableColumn<Compito, String> colRicetta;
    @FXML private TableColumn<Compito, String> colCuoco;
    @FXML private TableColumn<Compito, String> colTurno;
    @FXML private TableColumn<Compito, String> colStato;
    @FXML private TableColumn<Compito, Integer> colTempo;
    @FXML private TableColumn<Compito, Double> colQuantita;
    @FXML private ComboBox<Cuoco> cmbCuochi;
    @FXML private ComboBox<Turno> cmbTurni;
    @FXML private ComboBox<Ricetta> cmbRicette;
    @FXML private Spinner<Integer> spnTempoStimato;
    @FXML private Spinner<Double> spnQuantita;
    @FXML private ToggleGroup grpImportanza;
    @FXML private DatePicker dtpData;
    @FXML private TextField txtOraInizio;
    @FXML private TextField txtOraFine;
    @FXML private TextField txtLuogo;
    
    // Nuovi elementi per la gestione dei turni
    @FXML private TableView<Turno> tblTurni;
    @FXML private TableColumn<Turno, LocalDate> colData;
    @FXML private TableColumn<Turno, LocalTime> colOraInizio;
    @FXML private TableColumn<Turno, LocalTime> colOraFine;
    @FXML private TableColumn<Turno, String> colLuogo;
    @FXML private TableColumn<Turno, String> colTipo;
    
    private final CompitoCucinaService compitoCucinaService;
    private final EventoService eventoService;
    private final UtenteService utenteService;
    private final RicettaService ricettaService;
    private Chef chefCorrente;
    private Evento eventoCorrente;
    private ObservableList<Compito> compitiCorrente;
    
    public CompitiCucinaController(CompitoCucinaService compitoCucinaService, 
                                EventoService eventoService,
                                UtenteService utenteService,
                                RicettaService ricettaService,
                                Chef chef) {
        this.compitoCucinaService = compitoCucinaService;
        this.eventoService = eventoService;
        this.utenteService = utenteService;
        this.ricettaService = ricettaService;
        this.chefCorrente = chef;
    }
    
    @FXML
    public void initialize() {
        // Inizializza le liste
        cmbEventi.setItems(eventoService.getEventi());
        cmbCuochi.setItems(utenteService.getCuochi());
        cmbTurni.setItems(compitoCucinaService.getTurni());
        
        // Converti List<Ricetta> a ObservableList<Ricetta>
        ObservableList<Ricetta> ricetteObs = FXCollections.observableArrayList();
        ricettaService.getRicetteViewModel().forEach(vm -> ricetteObs.add(vm.getRicetta()));
        cmbRicette.setItems(ricetteObs);
        
        // Configurazione TableView per compiti
        colRicetta.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getRicetta().getNome()));
        colCuoco.setCellValueFactory(cellData -> {
            Cuoco cuoco = cellData.getValue().getCuocoAssegnato();
            if (cuoco == null) {
                return new SimpleStringProperty("Non assegnato");
            }
            return new SimpleStringProperty(cuoco.getNome() + " " + cuoco.getCognome());
        });
        colTurno.setCellValueFactory(new PropertyValueFactory<>("turno"));
        colStato.setCellValueFactory(cellData -> cellData.getValue().statoProperty());
        colTempo.setCellValueFactory(new PropertyValueFactory<>("tempoStimato"));
        colQuantita.setCellValueFactory(new PropertyValueFactory<>("quantita"));
        
        // Configurazione TableView per turni (se presente nel FXML)
        if (tblTurni != null) {
            initTurniTableView();
        }
        
        // Configura ComboBox rendering
        configureCellFactories();
        
        // Configura Spinners
        spnTempoStimato.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 480, 30, 5));
        spnQuantita.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 100.0, 1.0, 0.1));
        
        // Aggiungi listener per il cambio di evento
        cmbEventi.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(oldVal)) {
                onCreaRiepilogoCompiti();
            }
        });
        
        // Mostra guida all'avvio
        mostraGuidaUtilizzo();
    }
    
    private void configureCellFactories() {
        // Configura la visualizzazione degli eventi nella ComboBox
        cmbEventi.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Evento item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNome() + " (" + item.getDataInizio() + ")");
            }
        });
        
        cmbEventi.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Evento item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNome() + " (" + item.getDataInizio() + ")");
            }
        });
        
        // Configura la visualizzazione dei cuochi nella ComboBox
        cmbCuochi.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Cuoco item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNome() + " " + item.getCognome());
            }
        });
        
        cmbCuochi.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Cuoco item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNome() + " " + item.getCognome());
            }
        });
        
        // Configura la visualizzazione dei turni nella ComboBox
        cmbTurni.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Turno item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getData() + " " + item.getOraInizio() + "-" + item.getOraFine());
            }
        });
        
        cmbTurni.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Turno item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getData() + " " + item.getOraInizio() + "-" + item.getOraFine());
            }
        });
        
        // Configura la visualizzazione delle ricette nella ComboBox
        cmbRicette.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Ricetta item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNome());
            }
        });
        
        cmbRicette.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Ricetta item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNome());
            }
        });
    }
    
    /**
     * Gestisce la creazione del riepilogo compiti.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Passo 1
     * 
     * Questo pulsante visualizza tutti i compiti associati all'evento selezionato.
     * Per visualizzare i compiti di un determinato evento, selezionare l'evento dalla lista
     * e premere questo pulsante.
     */
    @FXML
    public void onCreaRiepilogoCompiti() {
        eventoCorrente = cmbEventi.getSelectionModel().getSelectedItem();
        if (eventoCorrente != null) {
            // Ottieni compiti filtrati per l'evento corrente
            ObservableList<Compito> compitiEventoFiltrati = compitoCucinaService.creaRiepilogoCompiti(eventoCorrente);
            
            // Crea una nuova lista osservabile per evitare problemi di riferimento
            compitiCorrente = FXCollections.observableArrayList(compitiEventoFiltrati);
            tblCompiti.setItems(compitiCorrente);
            
            // Pulisci la selezione nella tabella
            tblCompiti.getSelectionModel().clearSelection();
            
            if (compitiCorrente.isEmpty()) {
                AlertUtils.showInfo("Nessun compito", "Non ci sono compiti per l'evento '" + eventoCorrente.getNome() + "'. Puoi crearne di nuovi assegnandoli ai cuochi.");
            } else {
                AlertUtils.showInfo("Compiti caricati", "Sono stati caricati " + compitiCorrente.size() + " compiti per l'evento '" + eventoCorrente.getNome() + "'");
            }
        } else {
            AlertUtils.showWarning("Seleziona evento", "Devi selezionare un evento");
        }
    }
    
    /**
     * Verifica i cuochi disponibili.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Passo 2
     */
    @FXML
    public void onVerificaCuochiDisponibili() {
        Turno turnoSelezionato = cmbTurni.getSelectionModel().getSelectedItem();
        if (turnoSelezionato != null) {
            List<Cuoco> cuochiDisponibili = compitoCucinaService.getCuochiDisponibili(turnoSelezionato);
            
            // Verifica sempre che ci siano cuochi disponibili
            if (cuochiDisponibili.isEmpty()) {
                // Se non ci sono cuochi disponibili, usa tutti i cuochi
                ObservableList<Cuoco> tuttiCuochi = utenteService.getCuochi();
                if (tuttiCuochi.isEmpty()) {
                    AlertUtils.showWarning("Nessun cuoco", "Non ci sono cuochi disponibili nel sistema");
                    return;
                }
                cuochiDisponibili = new ArrayList<>(tuttiCuochi);
            }
            
            // Aggiorna la lista dei cuochi
            ObservableList<Cuoco> cuochiObs = FXCollections.observableArrayList(cuochiDisponibili);
            cmbCuochi.setItems(cuochiObs);
            
            // Seleziona il primo cuoco nella lista
            if (!cuochiObs.isEmpty()) {
                cmbCuochi.getSelectionModel().select(0);
                AlertUtils.showInfo("Cuochi disponibili", "Sono disponibili " + cuochiObs.size() + " cuochi per questo turno");
            }
        } else {
            AlertUtils.showWarning("Seleziona turno", "Devi selezionare un turno");
        }
    }
    
    /**
     * Assegna un compito a un cuoco.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Passo 3
     */
    @FXML
    public void onAssegnaCompito() {
        Cuoco cuocoSelezionato = cmbCuochi.getSelectionModel().getSelectedItem();
        Turno turnoSelezionato = cmbTurni.getSelectionModel().getSelectedItem();
        Ricetta ricettaSelezionata = cmbRicette.getSelectionModel().getSelectedItem();
        
        if (cuocoSelezionato == null) {
            AlertUtils.showWarning("Cuoco mancante", "Seleziona un cuoco per il compito");
            return;
        }
        
        if (turnoSelezionato == null) {
            AlertUtils.showWarning("Turno mancante", "Seleziona un turno per il compito");
            return;
        }
        
        if (ricettaSelezionata == null) {
            AlertUtils.showWarning("Ricetta mancante", "Seleziona una ricetta per il compito");
            return;
        }
        
        int tempoStimato = spnTempoStimato.getValue();
        double quantita = spnQuantita.getValue();
        
        // Verifica se il turno è pieno
        if (compitoCucinaService.isTurnoPieno(turnoSelezionato)) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Turno pieno");
            alert.setHeaderText("Il turno selezionato è già pieno");
            alert.setContentText("Vuoi comunque assegnare il compito a questo turno?");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    creaCompito(cuocoSelezionato, turnoSelezionato, ricettaSelezionata, tempoStimato, quantita);
                    AlertUtils.showInfo("Compito assegnato", "Il compito è stato assegnato con successo");
                }
            });
        } else {
            creaCompito(cuocoSelezionato, turnoSelezionato, ricettaSelezionata, tempoStimato, quantita);
            AlertUtils.showInfo("Compito assegnato", "Il compito è stato assegnato con successo");
        }
    }
    
    private void creaCompito(Cuoco cuoco, Turno turno, Ricetta ricetta, int tempoStimato, double quantita) {
        String turnoStr = turno.getData() + " " + turno.getOraInizio() + "-" + turno.getOraFine();
        Compito nuovoCompito = compitoCucinaService.assegnaCompito(chefCorrente, cuoco, turnoStr, ricetta, tempoStimato, quantita);
        
        // Aggiungi il compito alla lista corrente solo se è relativo all'evento corrente
        if (eventoCorrente != null) {
            boolean compitoRelativoEventoCorrente = eventoCorrente.getServizi().stream()
                .anyMatch(servizio -> servizio.getMenu() != null && 
                         servizio.getMenu().getSezioni().stream()
                             .flatMap(sezione -> sezione.getRicette().stream())
                             .anyMatch(r -> r.equals(ricetta)));
            
            if (compitoRelativoEventoCorrente) {
                if (compitiCorrente == null) {
                    compitiCorrente = FXCollections.observableArrayList();
                }
                compitiCorrente.add(nuovoCompito);
                tblCompiti.setItems(compitiCorrente);
                
                // Seleziona il nuovo compito nella tabella
                tblCompiti.getSelectionModel().select(nuovoCompito);
                tblCompiti.scrollTo(nuovoCompito);
            }
        }
    }
    
    /**
     * Ordina i compiti per importanza.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Passo 4
     */
    @FXML
    public void onOrdinaCompiti() {
        if (compitiCorrente != null && !compitiCorrente.isEmpty()) {
            ObservableList<Compito> compitiOrdinati = compitoCucinaService.ordinaCompitiPerImportanza(compitiCorrente);
            tblCompiti.setItems(compitiOrdinati);
        }
    }
    
    /**
     * Crea un nuovo turno.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Passo 5
     */
    @FXML
    public void onCreaTurno() {
        LocalDate data = dtpData.getValue();
        String oraInizioStr = txtOraInizio.getText();
        String oraFineStr = txtOraFine.getText();
        String luogo = txtLuogo.getText();
        
        if (data != null && !oraInizioStr.isEmpty() && !oraFineStr.isEmpty() && !luogo.isEmpty()) {
            try {
                LocalTime oraInizio = LocalTime.parse(oraInizioStr);
                LocalTime oraFine = LocalTime.parse(oraFineStr);
                
                // Controlla se il turno esiste già
                ObservableList<Turno> turni = compitoCucinaService.getTurni();
                boolean turnoEsistente = false;
                
                for (Turno t : turni) {
                    if (t.getData().equals(data) && 
                        t.getOraInizio().equals(oraInizio) && 
                        t.getOraFine().equals(oraFine) && 
                        t.getLuogo().equals(luogo)) {
                        turnoEsistente = true;
                        cmbTurni.getSelectionModel().select(t);
                        AlertUtils.showWarning("Turno esistente", "Questo turno esiste già");
                        break;
                    }
                }
                
                // Crea il turno solo se non esiste già
                if (!turnoEsistente) {
                    Turno nuovoTurno = compitoCucinaService.creaTurno(data, oraInizio, oraFine, luogo, "Preparatorio");
                    
                    // Aggiorna la ComboBox con il nuovo turno
                    cmbTurni.setItems(compitoCucinaService.getTurni());
                    cmbTurni.getSelectionModel().select(nuovoTurno);
                    
                    AlertUtils.showInfo("Turno creato", "Nuovo turno creato: " + data + " " + oraInizio + "-" + oraFine);
                }
            } catch (Exception e) {
                AlertUtils.showError("Formato ora non valido", "Inserisci l'ora nel formato HH:MM");
            }
        } else {
            AlertUtils.showWarning("Dati incompleti", "Compila tutti i campi per creare un turno");
        }
    }
    
    /**
     * Monitora l'avanzamento dei compiti.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Passo 6
     * 
     * Questo pulsante aggiorna la visualizzazione dei compiti e mostra un riepilogo
     * dello stato di avanzamento (quanti compiti sono stati completati, quanti sono in corso, ecc.)
     */
    @FXML
    public void onMonitoraAvanzamento() {
        if (compitiCorrente != null && !compitiCorrente.isEmpty()) {
            // Aggiorna la tabella per mostrare lo stato corrente
            tblCompiti.refresh();
            
            // Calcola statistiche sullo stato dei compiti
            int totale = compitiCorrente.size();
            int completati = 0;
            int inCorso = 0;
            int daFare = 0;
            int bloccati = 0;
            
            for (Compito compito : compitiCorrente) {
                String stato = compito.getStato().toLowerCase();
                if (stato.contains("complet")) {
                    completati++;
                } else if (stato.contains("corso")) {
                    inCorso++;
                } else if (stato.contains("blocc")) {
                    bloccati++;
                } else {
                    daFare++;
                }
            }
            
            // Calcola percentuale di completamento
            int percentualeCompletamento = (completati * 100) / totale;
            
            // Mostra finestra con statistiche
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Avanzamento Compiti");
            alert.setHeaderText("Stato di avanzamento dei compiti");
            alert.setContentText(
                "Completati: " + completati + " (" + percentualeCompletamento + "%)\n" +
                "In corso: " + inCorso + "\n" +
                "Da fare: " + daFare + "\n" +
                "Bloccati: " + bloccati
            );
            alert.showAndWait();
        } else {
            AlertUtils.showWarning("Nessun compito", "Non ci sono compiti da monitorare. Seleziona un evento e premi 'Crea riepilogo compiti'.");
        }
    }
    
    /**
     * Controlla lo stato del turno.
     * Riferimento: UC "Gestione dei Compiti della cucina" - Passo 7
     */
    @FXML
    public void onControllaStatoTurno() {
        Turno turnoSelezionato = cmbTurni.getSelectionModel().getSelectedItem();
        if (turnoSelezionato != null) {
            List<Compito> compitiTurno = compitoCucinaService.controllaStatoTurno(turnoSelezionato);
            
            // Crea una nuova lista modificabile invece di filtrare
            ObservableList<Compito> compitiTurnoObs = FXCollections.observableArrayList(compitiTurno);
            tblCompiti.setItems(compitiTurnoObs);
        } else {
            AlertUtils.showWarning("Seleziona turno", "Devi selezionare un turno");
        }
    }
    
    /**
     * Gestisce l'aggiornamento dello stato di un compito.
     */
    @FXML
    public void onAggiornaStatoCompito() {
        Compito compitoSelezionato = tblCompiti.getSelectionModel().getSelectedItem();
        if (compitoSelezionato != null) {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("Completato", 
                List.of("Da fare", "In corso", "Completato", "Bloccato"));
            dialog.setTitle("Aggiorna stato");
            dialog.setHeaderText("Seleziona il nuovo stato per il compito");
            dialog.setContentText("Stato:");
            
            dialog.showAndWait().ifPresent(nuovoStato -> {
                compitoSelezionato.aggiornaStato(nuovoStato);
                tblCompiti.refresh();
            });
        } else {
            AlertUtils.showWarning("Seleziona compito", "Devi selezionare un compito");
        }
    }
    
    /**
     * Gestisce la registrazione di feedback su un compito.
     */
    @FXML
    public void onRegistraFeedback() {
        Compito compitoSelezionato = tblCompiti.getSelectionModel().getSelectedItem();
        if (compitoSelezionato != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Registra feedback");
            dialog.setHeaderText("Inserisci feedback per il compito");
            dialog.setContentText("Feedback:");
            
            dialog.showAndWait().ifPresent(feedback -> {
                compitoSelezionato.registraFeedback(feedback);
                tblCompiti.refresh();
            });
        } else {
            AlertUtils.showWarning("Seleziona compito", "Devi selezionare un compito");
        }
    }
    
    /**
     * Mostra una guida all'utilizzo dell'interfaccia
     */
    private void mostraGuidaUtilizzo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Guida all'utilizzo");
        alert.setHeaderText("Gestione dei Compiti della Cucina");
        
        String contenuto = 
            "Questa interfaccia permette di gestire i compiti di cucina per i diversi eventi. " +
            "Ecco come utilizzarla:\n\n" +
            
            "1. VISUALIZZARE COMPITI PER EVENTO:\n" +
            "   - Seleziona un evento dalla lista a discesa\n" +
            "   - Premi il pulsante 'Crea riepilogo compiti'\n\n" +
            
            "2. ASSEGNARE UN COMPITO:\n" +
            "   - Seleziona un turno dalla lista\n" +
            "   - Premi 'Verifica cuochi disponibili' per vedere chi è disponibile nel turno\n" +
            "   - Seleziona un cuoco e una ricetta\n" +
            "   - Imposta tempo stimato e quantità\n" +
            "   - Premi 'Assegna compito'\n\n" +
            
            "3. MONITORARE L'AVANZAMENTO:\n" +
            "   - Dopo aver caricato i compiti di un evento, premi 'Monitora avanzamento'\n" +
            "   - Verrà mostrato un riepilogo dello stato dei compiti\n\n" +
            
            "4. AGGIORNARE LO STATO DI UN COMPITO:\n" +
            "   - Seleziona un compito dalla tabella\n" +
            "   - Premi 'Aggiorna stato compito' e scegli il nuovo stato\n\n" +
            
            "5. CREARE UN NUOVO TURNO:\n" +
            "   - Compila i campi data, ora inizio, ora fine e luogo\n" +
            "   - Premi 'Crea turno'\n\n" +
            
            "6. GESTIRE I TURNI:\n" +
            "   - Premi 'Gestisci turni' per vedere tutti i turni creati\n" +
            "   - Clicca con il tasto destro su un turno per modificarlo o eliminarlo";
            
        alert.setContentText(contenuto);
        alert.showAndWait();
    }
    
    /**
     * Mostra la guida quando richiesto dall'utente
     */
    @FXML
    public void onMostraGuida() {
        mostraGuidaUtilizzo();
    }
    
    /**
     * Inizializza la tabella dei turni
     */
    private void initTurniTableView() {
        // Configurazione delle colonne
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colOraInizio.setCellValueFactory(new PropertyValueFactory<>("oraInizio"));
        colOraFine.setCellValueFactory(new PropertyValueFactory<>("oraFine"));
        colLuogo.setCellValueFactory(new PropertyValueFactory<>("luogo"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        
        // Imposta i dati nella tabella
        tblTurni.setItems(compitoCucinaService.getTurni());
        
        // Aggiungi un menu contestuale per modificare/eliminare
        tblTurni.setRowFactory(tv -> {
            TableRow<Turno> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();
            
            MenuItem modifyItem = new MenuItem("Modifica");
            modifyItem.setOnAction(event -> modificaTurno(row.getItem()));
            
            MenuItem deleteItem = new MenuItem("Elimina");
            deleteItem.setOnAction(event -> eliminaTurno(row.getItem()));
            
            contextMenu.getItems().addAll(modifyItem, deleteItem);
            
            // Imposta il menu contestuale solo per le righe non vuote
            row.contextMenuProperty().bind(
                Bindings.when(row.emptyProperty())
                .then((ContextMenu)null)
                .otherwise(contextMenu)
            );
            
            return row;
        });
    }
    
    /**
     * Visualizza tutti i turni in una finestra separata
     */
    @FXML
    public void onVisualizzaTurni() {
        // Crea una nuova finestra di dialogo
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Gestione Turni");
        dialog.setHeaderText("Visualizza, modifica ed elimina i turni");
        
        // Aggiungi pulsante Chiudi
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        
        // Crea una TableView per i turni
        TableView<Turno> tableTurni = new TableView<>();
        tableTurni.setPrefHeight(400);
        tableTurni.setPrefWidth(600);
        
        // Crea le colonne
        TableColumn<Turno, LocalDate> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colData.setPrefWidth(100);
        
        TableColumn<Turno, LocalTime> colOraInizio = new TableColumn<>("Ora Inizio");
        colOraInizio.setCellValueFactory(new PropertyValueFactory<>("oraInizio"));
        colOraInizio.setPrefWidth(100);
        
        TableColumn<Turno, LocalTime> colOraFine = new TableColumn<>("Ora Fine");
        colOraFine.setCellValueFactory(new PropertyValueFactory<>("oraFine"));
        colOraFine.setPrefWidth(100);
        
        TableColumn<Turno, String> colLuogo = new TableColumn<>("Luogo");
        colLuogo.setCellValueFactory(new PropertyValueFactory<>("luogo"));
        colLuogo.setPrefWidth(150);
        
        TableColumn<Turno, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colTipo.setPrefWidth(100);
        
        // Aggiungi le colonne alla tabella
        tableTurni.getColumns().addAll(colData, colOraInizio, colOraFine, colLuogo, colTipo);
        
        // Aggiungi i dati
        tableTurni.setItems(compitoCucinaService.getTurni());
        
        // Aggiungi menu contestuale
        tableTurni.setRowFactory(tv -> {
            TableRow<Turno> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();
            
            MenuItem modifyItem = new MenuItem("Modifica");
            modifyItem.setOnAction(event -> {
                modificaTurno(row.getItem());
                tableTurni.refresh(); // Aggiorna la tabella dopo la modifica
            });
            
            MenuItem deleteItem = new MenuItem("Elimina");
            deleteItem.setOnAction(event -> {
                if (eliminaTurno(row.getItem())) {
                    tableTurni.setItems(compitoCucinaService.getTurni()); // Aggiorna la lista dopo l'eliminazione
                }
            });
            
            contextMenu.getItems().addAll(modifyItem, deleteItem);
            
            // Imposta il menu contestuale solo per le righe non vuote
            row.contextMenuProperty().bind(
                Bindings.when(row.emptyProperty())
                .then((ContextMenu)null)
                .otherwise(contextMenu)
            );
            
            return row;
        });
        
        // Aggiungi etichetta informativa
        Label lblInfo = new Label("Clicca con il tasto destro su un turno per modificarlo o eliminarlo.");
        
        // Organizza in un layout
        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
        vbox.getChildren().addAll(lblInfo, tableTurni);
        vbox.setPadding(new javafx.geometry.Insets(10));
        
        // Imposta il contenuto
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().setPrefSize(650, 500);
        
        // Mostra la finestra
        dialog.showAndWait();
    }
    
    /**
     * Modifica un turno selezionato
     * @return true se la modifica è stata completata con successo
     */
    private boolean modificaTurno(Turno turno) {
        if (turno == null) return false;
        
        // Crea una nuova finestra di dialogo
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifica Turno");
        dialog.setHeaderText("Modifica i dettagli del turno");
        
        // Aggiungi pulsanti
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Crea controlli per l'input
        DatePicker datePicker = new DatePicker(turno.getData());
        TextField txtStartTime = new TextField(turno.getOraInizio().toString());
        TextField txtEndTime = new TextField(turno.getOraFine().toString());
        TextField txtLocation = new TextField(turno.getLuogo());
        TextField txtType = new TextField(turno.getTipo());
        
        // Crea layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        grid.add(new Label("Data:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Ora inizio:"), 0, 1);
        grid.add(txtStartTime, 1, 1);
        grid.add(new Label("Ora fine:"), 0, 2);
        grid.add(txtEndTime, 1, 2);
        grid.add(new Label("Luogo:"), 0, 3);
        grid.add(txtLocation, 1, 3);
        grid.add(new Label("Tipo:"), 0, 4);
        grid.add(txtType, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        
        // Converti risultato
        Optional<ButtonType> result = dialog.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                LocalTime oraInizio = LocalTime.parse(txtStartTime.getText());
                LocalTime oraFine = LocalTime.parse(txtEndTime.getText());
                // Per la modifica completa, avremmo bisogno di più metodi setter nella classe Turno
                
                // Per ora, aggiorniamo solo l'orario
                turno.aggiornaOrario(oraInizio, oraFine);
                
                // Aggiorna anche la lista nel ComboBox dei turni
                cmbTurni.setItems(compitoCucinaService.getTurni());
                
                AlertUtils.showInfo("Turno aggiornato", "Il turno è stato aggiornato con successo");
                return true;
            } catch (Exception e) {
                AlertUtils.showError("Errore", "Formato non valido: " + e.getMessage());
            }
        }
        return false;
    }
    
    /**
     * Elimina un turno
     * @return true se l'eliminazione è stata completata con successo
     */
    private boolean eliminaTurno(Turno turno) {
        if (turno == null) return false;
        
        // Verifica se ci sono compiti associati al turno
        String turnoStr = turno.getData() + " " + turno.getOraInizio() + "-" + turno.getOraFine();
        boolean hasAssociatedTasks = compitoCucinaService.getCompiti().stream()
            .anyMatch(compito -> turnoStr.equals(compito.getTurno()));
        
        if (hasAssociatedTasks) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Impossibile eliminare");
            alert.setHeaderText("Ci sono compiti associati a questo turno");
            alert.setContentText("Prima di eliminare il turno, è necessario riassegnare o eliminare i compiti associati.");
            alert.showAndWait();
            return false;
        }
        
        // Chiedi conferma prima di eliminare
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma eliminazione");
        alert.setHeaderText("Stai per eliminare questo turno");
        alert.setContentText("Sei sicuro di voler eliminare il turno del " + turno.getData() + " " + 
                            turno.getOraInizio() + "-" + turno.getOraFine() + "?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Rimuovi il turno dalla lista
            compitoCucinaService.getTurni().remove(turno);
            
            // Aggiorna anche la lista nel ComboBox
            cmbTurni.setItems(compitoCucinaService.getTurni());
            
            AlertUtils.showInfo("Turno eliminato", "Il turno è stato eliminato con successo");
            return true;
        }
        return false;
    }
}