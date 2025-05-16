package software.ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import software.domain.ricette.Ingrediente;
import software.domain.ricette.Istruzione;
import software.domain.ricette.Ricetta;
import software.domain.ricette.Tag;
import software.domain.utenti.Chef;
import software.service.RicettaService;
import software.ui.utils.AlertUtils;

import java.util.Optional;

/**
 * Controller per la gestione delle ricette.
 */
public class RicettaController {
    
    @FXML private TextField txtNome;
    @FXML private TextArea txtDescrizione;
    @FXML private Spinner<Integer> spnTempoPreparazione;
    @FXML private ListView<Ingrediente> lstIngredienti;
    @FXML private ListView<Istruzione> lstIstruzioni;
    @FXML private ListView<Tag> lstTags;
    @FXML private ListView<Ricetta> lstRicette;
    @FXML private Button btnAggiungiIngrediente;
    @FXML private Button btnAggiungiIstruzione;
    @FXML private Button btnAggiungiTag;
    @FXML private Button btnCreaRicetta;
    @FXML private Button btnPubblicaRicetta;
    
    private final RicettaService ricettaService;
    private final Chef chef;
    private ObservableList<Ingrediente> ingredienti = FXCollections.observableArrayList();
    private ObservableList<Istruzione> istruzioni = FXCollections.observableArrayList();
    private ObservableList<Tag> tags = FXCollections.observableArrayList();
    
    public RicettaController(RicettaService ricettaService, Chef chef) {
        this.ricettaService = ricettaService;
        this.chef = chef;
    }
    
    @FXML
    public void initialize() {
        // Configura spinner
        spnTempoPreparazione.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 240, 30, 5));
        
        // Collega le liste ai modelli
        lstIngredienti.setItems(ingredienti);
        lstIstruzioni.setItems(istruzioni);
        lstTags.setItems(tags);
        
        // Lista ricette
        ObservableList<Ricetta> ricette = FXCollections.observableArrayList();
        ricettaService.getRicetteViewModel().forEach(vm -> ricette.add(vm.getRicetta()));
        lstRicette.setItems(ricette);
        
        // Configura il rendering personalizzato
        lstRicette.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Ricetta item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNome() + " - " + item.getStato());
                }
            }
        });
        
        // Gestione eventi
        setupButtonHandlers();
    }
    
    private void setupButtonHandlers() {
        btnAggiungiIngrediente.setOnAction(e -> mostraDialogIngrediente());
        btnAggiungiIstruzione.setOnAction(e -> mostraDialogIstruzione());
        btnAggiungiTag.setOnAction(e -> mostraDialogTag());
        btnCreaRicetta.setOnAction(e -> creaRicetta());
        btnPubblicaRicetta.setOnAction(e -> pubblicaRicettaSelezionata());
    }
    
    private void mostraDialogIngrediente() {
        Dialog<Ingrediente> dialog = new Dialog<>();
        dialog.setTitle("Aggiungi Ingrediente");
        dialog.setHeaderText("Inserisci i dettagli dell'ingrediente");
        
        // Bottoni
        ButtonType btnOk = new ButtonType("Aggiungi", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnOk, ButtonType.CANCEL);
        
        // Form
        VBox content = new VBox(10);
        TextField txtNomeIngr = new TextField();
        txtNomeIngr.setPromptText("Nome ingrediente");
        
        TextField txtDose = new TextField();
        txtDose.setPromptText("Dose (es. 100)");
        
        TextField txtUnita = new TextField();
        txtUnita.setPromptText("Unità di misura (es. g)");
        
        content.getChildren().addAll(
                new Label("Nome:"), txtNomeIngr,
                new Label("Dose:"), txtDose,
                new Label("Unità di misura:"), txtUnita
        );
        
        dialog.getDialogPane().setContent(content);
        
        // Conversione risultato
        dialog.setResultConverter(buttonType -> {
            if (buttonType == btnOk) {
                try {
                    double dose = Double.parseDouble(txtDose.getText());
                    return new Ingrediente(txtNomeIngr.getText(), dose, txtUnita.getText());
                } catch (Exception e) {
                    AlertUtils.showError("Errore", "Controlla i dati inseriti");
                    return null;
                }
            }
            return null;
        });
        
        Optional<Ingrediente> result = dialog.showAndWait();
        result.ifPresent(ingredienti::add);
    }
    
    private void mostraDialogIstruzione() {
        Dialog<Istruzione> dialog = new Dialog<>();
        dialog.setTitle("Aggiungi Istruzione");
        dialog.setHeaderText("Inserisci i dettagli dell'istruzione");
        
        // Bottoni
        ButtonType btnOk = new ButtonType("Aggiungi", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnOk, ButtonType.CANCEL);
        
        // Form
        VBox content = new VBox(10);
        TextArea txtIstruzione = new TextArea();
        txtIstruzione.setPromptText("Descrizione dell'istruzione");
        txtIstruzione.setPrefRowCount(4);
        
        content.getChildren().addAll(
                new Label("Descrizione:"), txtIstruzione
        );
        
        dialog.getDialogPane().setContent(content);
        
        // Conversione risultato
        dialog.setResultConverter(buttonType -> {
            if (buttonType == btnOk) {
                int id = istruzioni.size() + 1;
                int ordine = istruzioni.size() + 1;
                return new Istruzione(id, ordine, txtIstruzione.getText());
            }
            return null;
        });
        
        Optional<Istruzione> result = dialog.showAndWait();
        result.ifPresent(istruzioni::add);
    }
    
    private void mostraDialogTag() {
        Dialog<Tag> dialog = new Dialog<>();
        dialog.setTitle("Aggiungi Tag");
        dialog.setHeaderText("Inserisci il nome del tag");
        
        // Bottoni
        ButtonType btnOk = new ButtonType("Aggiungi", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnOk, ButtonType.CANCEL);
        
        // Form
        VBox content = new VBox(10);
        TextField txtNomeTag = new TextField();
        txtNomeTag.setPromptText("Nome tag");
        
        content.getChildren().addAll(
                new Label("Nome:"), txtNomeTag
        );
        
        dialog.getDialogPane().setContent(content);
        
        // Conversione risultato
        dialog.setResultConverter(buttonType -> {
            if (buttonType == btnOk) {
                return new Tag(txtNomeTag.getText());
            }
            return null;
        });
        
        Optional<Tag> result = dialog.showAndWait();
        result.ifPresent(tags::add);
    }
    
    private void creaRicetta() {
        String nome = txtNome.getText();
        String descrizione = txtDescrizione.getText();
        int tempoPreparazione = spnTempoPreparazione.getValue();
        
        if (nome == null || nome.trim().isEmpty()) {
            AlertUtils.showWarning("Dati mancanti", "Il nome della ricetta è obbligatorio");
            return;
        }
        
        try {
            // Crea la ricetta
            Ricetta ricetta = ricettaService.creaRicetta(nome, chef);
            
            // Aggiorna le proprietà
            ricetta.setDescrizione(descrizione);
            ricetta.setTempoPreparazione(tempoPreparazione);
            
            // Aggiungi ingredienti, istruzioni e tag
            for (Ingrediente ingrediente : ingredienti) {
                ricetta.aggiungiIngrediente(ingrediente);
            }
            
            for (Istruzione istruzione : istruzioni) {
                ricetta.aggiungiIstruzione(istruzione);
            }
            
            for (Tag tag : tags) {
                ricetta.aggiungiTag(tag);
            }
            
            // Aggiorna la ricetta
            ricettaService.aggiornaRicetta(ricetta);
            
            // Aggiorna lista ricette
            ObservableList<Ricetta> ricette = FXCollections.observableArrayList();
            ricettaService.getRicetteViewModel().forEach(vm -> ricette.add(vm.getRicetta()));
            lstRicette.setItems(ricette);
            
            // Seleziona la nuova ricetta
            lstRicette.getSelectionModel().select(ricetta);
            
            // Reset campi
            resetForm();
            
            AlertUtils.showInfo("Ricetta creata", "La ricetta è stata creata con successo");
            
        } catch (Exception e) {
            AlertUtils.showError("Errore", "Errore durante la creazione della ricetta: " + e.getMessage());
        }
    }
    
    private void pubblicaRicettaSelezionata() {
        Ricetta ricetta = lstRicette.getSelectionModel().getSelectedItem();
        if (ricetta != null) {
            ricettaService.pubblicaRicetta(ricetta);
            
            // Aggiorna lista ricette
            ObservableList<Ricetta> ricette = FXCollections.observableArrayList();
            ricettaService.getRicetteViewModel().forEach(vm -> ricette.add(vm.getRicetta()));
            lstRicette.setItems(ricette);
            
            // Seleziona la ricetta aggiornata
            lstRicette.getSelectionModel().select(ricetta);
            
            AlertUtils.showInfo("Ricetta pubblicata", "La ricetta è stata pubblicata con successo");
        } else {
            AlertUtils.showWarning("Selezione", "Seleziona una ricetta da pubblicare");
        }
    }
    
    private void resetForm() {
        txtNome.clear();
        txtDescrizione.clear();
        spnTempoPreparazione.getValueFactory().setValue(30);
        ingredienti.clear();
        istruzioni.clear();
        tags.clear();
    }
} 