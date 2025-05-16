package software.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import software.domain.exceptions.DomainException;
import software.domain.ricette.Ingrediente;
import software.domain.ricette.Istruzione;
import software.domain.ricette.Ricetta;
import software.domain.ricette.Tag;
import software.domain.utenti.Chef;
import software.service.persistence.InMemoryRepository;
import software.ui.viewmodels.RicettaViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Servizio per la gestione delle ricette.
 * Implementa le operazioni di business logic relative alle ricette.
 * Utilizza un repository per la persistenza dei dati.
 */
public class RicettaService {
    private final InMemoryRepository<Ricetta, Integer> ricettaRepository;
    private final ObservableList<RicettaViewModel> ricetteViewModel = FXCollections.observableArrayList();
    
    // Cache per i ViewModel
    private final Map<Integer, RicettaViewModel> viewModelCache = new ConcurrentHashMap<>();
    
    /**
     * Costruttore che utilizza un repository predefinito.
     */
    public RicettaService() {
        this(new InMemoryRepository<>("ricette", Ricetta.class, "id"));
    }
    
    /**
     * Costruttore con dependency injection per il repository.
     *
     * @param ricettaRepository Il repository da utilizzare
     */
    public RicettaService(InMemoryRepository<Ricetta, Integer> ricettaRepository) {
        this.ricettaRepository = ricettaRepository;
        
        // Inizializza i ViewModel dalle ricette nel repository
        for (Ricetta ricetta : ricettaRepository.findAll()) {
            RicettaViewModel viewModel = new RicettaViewModel(ricetta);
            ricetteViewModel.add(viewModel);
            viewModelCache.put(ricetta.getId(), viewModel);
        }
    }
    
    /**
     * Crea una nuova ricetta.
     * 
     * @param nome Il nome della ricetta
     * @param chef Lo chef proprietario della ricetta
     * @return La ricetta creata
     * @throws DomainException Se i parametri non sono validi
     */
    public Ricetta creaRicetta(String nome, Chef chef) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new DomainException("Il nome della ricetta non può essere vuoto");
        }
        
        if (chef == null) {
            throw new DomainException("Lo chef non può essere null");
        }
        
        // Crea una nuova ricetta temporanea
        Ricetta nuovaRicetta = new Ricetta(0, nome, chef);
        
        // Salva la ricetta nel repository, che assegnerà un ID
        nuovaRicetta = ricettaRepository.save(nuovaRicetta);
        
        // Crea e memorizza il ViewModel
        RicettaViewModel viewModel = new RicettaViewModel(nuovaRicetta);
        ricetteViewModel.add(viewModel);
        viewModelCache.put(nuovaRicetta.getId(), viewModel);
        
        return nuovaRicetta;
    }
    
    /**
     * Elimina una ricetta.
     * 
     * @param ricetta La ricetta da eliminare
     * @throws DomainException Se la ricetta è null o in uso
     */
    public void eliminaRicetta(Ricetta ricetta) {
        if (ricetta == null) {
            throw new DomainException("La ricetta non può essere null");
        }
        
        if (ricetta.isInUso()) {
            throw new DomainException("Impossibile eliminare una ricetta in uso");
        }
        
        // Elimina la ricetta dal repository
        ricettaRepository.delete(ricetta);
        
        // Rimuovi il ViewModel dalla lista e dalla cache
        RicettaViewModel viewModel = viewModelCache.remove(ricetta.getId());
        if (viewModel != null) {
            ricetteViewModel.remove(viewModel);
        }
    }
    
    /**
     * Ottiene la lista osservabile di tutte le ricette come ViewModel.
     * 
     * @return Lista osservabile di ViewModel delle ricette
     */
    public ObservableList<RicettaViewModel> getRicetteViewModel() {
        return ricetteViewModel;
    }
    
    /**
     * Ottiene la lista osservabile delle ricette disponibili (stato "Pubblicata") come ViewModel.
     * 
     * @return Lista osservabile di ViewModel delle ricette disponibili
     */
    public ObservableList<RicettaViewModel> getRicetteDisponibiliViewModel() {
        return ricetteViewModel.filtered(vm -> "Pubblicata".equals(vm.getRicetta().getStato()));
    }
    
    /**
     * Aggiorna una ricetta esistente.
     * 
     * @param ricetta La ricetta da aggiornare
     * @return La ricetta aggiornata
     * @throws DomainException Se la ricetta è null
     */
    public Ricetta aggiornaRicetta(Ricetta ricetta) {
        if (ricetta == null) {
            throw new DomainException("La ricetta non può essere null");
        }
        
        // Aggiorna la ricetta nel repository
        Ricetta ricettaAggiornata = ricettaRepository.save(ricetta);
        
        // Aggiorna il ViewModel se presente
        RicettaViewModel viewModel = viewModelCache.get(ricetta.getId());
        if (viewModel != null) {
            // Crea un nuovo ViewModel con la ricetta aggiornata
            RicettaViewModel nuovoViewModel = new RicettaViewModel(ricettaAggiornata);
            
            // Sostituisci il vecchio ViewModel nella lista
            int index = ricetteViewModel.indexOf(viewModel);
            if (index >= 0) {
                ricetteViewModel.set(index, nuovoViewModel);
            }
            
            // Aggiorna la cache
            viewModelCache.put(ricetta.getId(), nuovoViewModel);
        }
        
        return ricettaAggiornata;
    }
    
    /**
     * Pubblica una ricetta.
     * 
     * @param ricetta La ricetta da pubblicare
     * @throws DomainException Se la ricetta è null
     */
    public void pubblicaRicetta(Ricetta ricetta) {
        if (ricetta == null) {
            throw new DomainException("La ricetta non può essere null");
        }
        
        ricetta.setStato("Pubblicata");
        aggiornaRicetta(ricetta);
    }
    
    /**
     * Trova una ricetta per ID.
     * 
     * @param id L'ID della ricetta
     * @return La ricetta trovata, o null se non esiste
     */
    public Ricetta findById(int id) {
        return ricettaRepository.findById(id);
    }
    
    /**
     * @deprecated Usa getRicetteViewModel() invece per ottenere un'interfaccia UI-friendly
     */
    @Deprecated
    public List<Ricetta> getRicette() {
        return ricettaRepository.findAll();
    }
    
    /**
     * @deprecated Usa getRicetteDisponibiliViewModel() invece per ottenere un'interfaccia UI-friendly
     */
    @Deprecated
    public List<Ricetta> getRicetteDisponibili() {
        return ricettaRepository.findAll().stream()
                .filter(r -> "Pubblicata".equals(r.getStato()))
                .collect(Collectors.toList());
    }
    
    /**
     * Crea ricette di esempio per scopi dimostrativi.
     */
    public void creaRicetteDiEsempio() {
        if (!ricettaRepository.findAll().isEmpty()) {
            // Se ci sono già ricette nel repository, non creare esempi
            return;
        }
        
        // Ottieni lo chef corrente (lo chef che inseriamo nell'esempio è quello dell'applicazione)
        Chef chef = null;
        for (Ricetta ricetta : ricettaRepository.findAll()) {
            chef = ricetta.getProprietario();
            break;
        }
        
        // Se non abbiamo trovato chef, non possiamo procedere
        if (chef == null) {
            // Creiamo uno chef di esempio
            chef = new Chef(1, "Mario", "Rossi", "chef@example.com", "123456789");
        }
        
        // Crea alcune ricette di esempio
        creaRicettaPastaCarbonara(chef);
        creaRicettaRisottoFunghi(chef);
        creaRicettaTiramisù(chef);
        creaRicettaPolloArrosto(chef);
        creaRicettaInsalataDiRiso(chef);
    }

    private Ricetta creaRicettaPastaCarbonara(Chef chef) {
        Ricetta ricetta = creaRicetta("Pasta alla Carbonara", chef);
        ricetta.setDescrizione("Classica pasta alla carbonara romana con uova, guanciale e pecorino.");
        ricetta.setTempoPreparazione(30);
        
        // Aggiungi ingredienti
        ricetta.aggiungiIngrediente(new Ingrediente("Spaghetti", 400, "g"));
        ricetta.aggiungiIngrediente(new Ingrediente("Guanciale", 150, "g"));
        ricetta.aggiungiIngrediente(new Ingrediente("Uova", 4, "pz"));
        ricetta.aggiungiIngrediente(new Ingrediente("Pecorino Romano", 100, "g"));
        ricetta.aggiungiIngrediente(new Ingrediente("Pepe nero", 5, "g"));
        ricetta.aggiungiIngrediente(new Ingrediente("Sale", 5, "g"));
        
        // Aggiungi istruzioni
        ricetta.aggiungiIstruzione(new Istruzione(1, 1, "Tagliare il guanciale a dadini e farlo rosolare in padella."));
        ricetta.aggiungiIstruzione(new Istruzione(2, 2, "Cuocere la pasta in acqua bollente salata."));
        ricetta.aggiungiIstruzione(new Istruzione(3, 3, "Mescolare le uova con il pecorino grattugiato e il pepe."));
        ricetta.aggiungiIstruzione(new Istruzione(4, 4, "Scolare la pasta e unirla al guanciale in padella."));
        ricetta.aggiungiIstruzione(new Istruzione(5, 5, "Aggiungere il composto di uova e formaggio, mescolando velocemente."));
        
        // Aggiungi tag
        ricetta.aggiungiTag(new Tag("Primo piatto"));
        ricetta.aggiungiTag(new Tag("Tradizionale"));
        ricetta.aggiungiTag(new Tag("Cucina romana"));
        
        // Pubblica la ricetta
        ricetta.setStato("Pubblicata");
        aggiornaRicetta(ricetta);
        
        return ricetta;
    }

    private Ricetta creaRicettaRisottoFunghi(Chef chef) {
        Ricetta ricetta = creaRicetta("Risotto ai Funghi Porcini", chef);
        ricetta.setDescrizione("Cremoso risotto con funghi porcini freschi e parmigiano.");
        ricetta.setTempoPreparazione(45);
        
        // Aggiungi ingredienti
        ricetta.aggiungiIngrediente(new Ingrediente("Riso Carnaroli", 320, "g"));
        ricetta.aggiungiIngrediente(new Ingrediente("Funghi porcini", 250, "g"));
        ricetta.aggiungiIngrediente(new Ingrediente("Cipolla", 1, "pz"));
        ricetta.aggiungiIngrediente(new Ingrediente("Brodo vegetale", 1, "l"));
        ricetta.aggiungiIngrediente(new Ingrediente("Vino bianco", 100, "ml"));
        ricetta.aggiungiIngrediente(new Ingrediente("Parmigiano Reggiano", 80, "g"));
        ricetta.aggiungiIngrediente(new Ingrediente("Burro", 50, "g"));
        ricetta.aggiungiIngrediente(new Ingrediente("Olio d'oliva", 30, "ml"));
        ricetta.aggiungiIngrediente(new Ingrediente("Sale", 5, "g"));
        ricetta.aggiungiIngrediente(new Ingrediente("Pepe nero", 3, "g"));
        ricetta.aggiungiIngrediente(new Ingrediente("Prezzemolo", 10, "g"));
        
        // Aggiungi istruzioni
        ricetta.aggiungiIstruzione(new Istruzione(1, 1, "Pulire i funghi e tagliarli a fettine."));
        ricetta.aggiungiIstruzione(new Istruzione(2, 2, "Tritare finemente la cipolla e farla soffriggere in olio."));
        ricetta.aggiungiIstruzione(new Istruzione(3, 3, "Aggiungere i funghi e cuocere per 5 minuti."));
        ricetta.aggiungiIstruzione(new Istruzione(4, 4, "Aggiungere il riso e tostarlo per 2-3 minuti."));
        ricetta.aggiungiIstruzione(new Istruzione(5, 5, "Sfumare con il vino bianco e lasciare evaporare."));
        ricetta.aggiungiIstruzione(new Istruzione(6, 6, "Aggiungere il brodo gradualmente, mescolando spesso."));
        ricetta.aggiungiIstruzione(new Istruzione(7, 7, "Quando il riso è cotto, spegnere il fuoco e mantecare con burro e parmigiano."));
        ricetta.aggiungiIstruzione(new Istruzione(8, 8, "Servire con prezzemolo tritato e pepe nero."));
        
        // Aggiungi tag
        ricetta.aggiungiTag(new Tag("Primo piatto"));
        ricetta.aggiungiTag(new Tag("Vegetariano"));
        ricetta.aggiungiTag(new Tag("Cucina italiana"));
        
        // Pubblica la ricetta
        ricetta.setStato("Pubblicata");
        aggiornaRicetta(ricetta);
        
        return ricetta;
    }

    private Ricetta creaRicettaTiramisù(Chef chef) {
        Ricetta ricetta = creaRicetta("Tiramisù Classico", chef);
        ricetta.setDescrizione("Dolce italiano a base di savoiardi, caffè, mascarpone e cacao.");
        ricetta.setTempoPreparazione(60);
        
        // Aggiungi ingredienti
        ricetta.aggiungiIngrediente(new Ingrediente("Mascarpone", 500, "g"));
        ricetta.aggiungiIngrediente(new Ingrediente("Uova", 4, "pz"));
        ricetta.aggiungiIngrediente(new Ingrediente("Zucchero", 100, "g"));
        ricetta.aggiungiIngrediente(new Ingrediente("Savoiardi", 300, "g"));
        ricetta.aggiungiIngrediente(new Ingrediente("Caffè espresso", 300, "ml"));
        ricetta.aggiungiIngrediente(new Ingrediente("Cacao amaro in polvere", 20, "g"));
        
        // Aggiungi istruzioni
        ricetta.aggiungiIstruzione(new Istruzione(1, 1, "Separare i tuorli dagli albumi."));
        ricetta.aggiungiIstruzione(new Istruzione(2, 2, "Montare i tuorli con lo zucchero fino a ottenere un composto chiaro e spumoso."));
        ricetta.aggiungiIstruzione(new Istruzione(3, 3, "Aggiungere il mascarpone ai tuorli montati e mescolare delicatamente."));
        ricetta.aggiungiIstruzione(new Istruzione(4, 4, "Montare gli albumi a neve ferma e incorporarli al composto di mascarpone."));
        ricetta.aggiungiIstruzione(new Istruzione(5, 5, "Immergere velocemente i savoiardi nel caffè e disporli sul fondo di una pirofila."));
        ricetta.aggiungiIstruzione(new Istruzione(6, 6, "Coprire con uno strato di crema al mascarpone."));
        ricetta.aggiungiIstruzione(new Istruzione(7, 7, "Ripetere gli strati fino a esaurimento degli ingredienti."));
        ricetta.aggiungiIstruzione(new Istruzione(8, 8, "Spolverizzare con cacao e lasciar riposare in frigorifero per almeno 4 ore."));
        
        // Aggiungi tag
        ricetta.aggiungiTag(new Tag("Dessert"));
        ricetta.aggiungiTag(new Tag("Cucina italiana"));
        ricetta.aggiungiTag(new Tag("Senza cottura"));
        
        // Pubblica la ricetta
        ricetta.setStato("Pubblicata");
        aggiornaRicetta(ricetta);
        
        return ricetta;
    }

    private Ricetta creaRicettaPolloArrosto(Chef chef) {
        Ricetta ricetta = creaRicetta("Pollo Arrosto con Patate", chef);
        ricetta.setDescrizione("Pollo intero arrosto con patate, rosmarino e limone.");
        ricetta.setTempoPreparazione(90);
        
        // Aggiungi ingredienti
        ricetta.aggiungiIngrediente(new Ingrediente("Pollo intero", 1500, "g"));
        ricetta.aggiungiIngrediente(new Ingrediente("Patate", 800, "g"));
        ricetta.aggiungiIngrediente(new Ingrediente("Rosmarino", 2, "rametti"));
        ricetta.aggiungiIngrediente(new Ingrediente("Aglio", 3, "spicchi"));
        ricetta.aggiungiIngrediente(new Ingrediente("Limone", 1, "pz"));
        ricetta.aggiungiIngrediente(new Ingrediente("Olio d'oliva", 40, "ml"));
        ricetta.aggiungiIngrediente(new Ingrediente("Sale", 10, "g"));
        ricetta.aggiungiIngrediente(new Ingrediente("Pepe nero", 5, "g"));
        
        // Aggiungi istruzioni
        ricetta.aggiungiIstruzione(new Istruzione(1, 1, "Preriscaldare il forno a 180°C."));
        ricetta.aggiungiIstruzione(new Istruzione(2, 2, "Pulire il pollo e asciugarlo con carta assorbente."));
        ricetta.aggiungiIstruzione(new Istruzione(3, 3, "Tagliare il limone a spicchi e inserirlo nella cavità del pollo con rametti di rosmarino."));
        ricetta.aggiungiIstruzione(new Istruzione(4, 4, "Massaggiare il pollo con olio, sale e pepe."));
        ricetta.aggiungiIstruzione(new Istruzione(5, 5, "Pelare e tagliare le patate a cubetti, condirle con olio, sale e rosmarino."));
        ricetta.aggiungiIstruzione(new Istruzione(6, 6, "Disporre il pollo e le patate in una teglia."));
        ricetta.aggiungiIstruzione(new Istruzione(7, 7, "Cuocere in forno per circa 1 ora e 15 minuti, girando le patate a metà cottura."));
        ricetta.aggiungiIstruzione(new Istruzione(8, 8, "Servire caldo, tagliando il pollo a porzioni."));
        
        // Aggiungi tag
        ricetta.aggiungiTag(new Tag("Secondo piatto"));
        ricetta.aggiungiTag(new Tag("Carne"));
        ricetta.aggiungiTag(new Tag("Pranzo domenicale"));
        
        // Pubblica la ricetta
        ricetta.setStato("Pubblicata");
        aggiornaRicetta(ricetta);
        
        return ricetta;
    }

    private Ricetta creaRicettaInsalataDiRiso(Chef chef) {
        Ricetta ricetta = creaRicetta("Insalata di Riso Estiva", chef);
        ricetta.setDescrizione("Fresca insalata di riso con verdure, tonno e formaggi.");
        ricetta.setTempoPreparazione(40);
        
        // Aggiungi ingredienti
        ricetta.aggiungiIngrediente(new Ingrediente("Riso", 400, "g"));
        ricetta.aggiungiIngrediente(new Ingrediente("Tonno sott'olio", 200, "g"));
        ricetta.aggiungiIngrediente(new Ingrediente("Peperoni", 1, "pz"));
        ricetta.aggiungiIngrediente(new Ingrediente("Pomodorini", 200, "g"));
        ricetta.aggiungiIngrediente(new Ingrediente("Olive verdi", 100, "g"));
        ricetta.aggiungiIngrediente(new Ingrediente("Mais", 150, "g"));
        ricetta.aggiungiIngrediente(new Ingrediente("Piselli", 150, "g"));
        ricetta.aggiungiIngrediente(new Ingrediente("Formaggio a cubetti", 100, "g"));
        ricetta.aggiungiIngrediente(new Ingrediente("Basilico", 10, "foglie"));
        ricetta.aggiungiIngrediente(new Ingrediente("Olio d'oliva", 30, "ml"));
        ricetta.aggiungiIngrediente(new Ingrediente("Sale", 5, "g"));
        
        // Aggiungi istruzioni
        ricetta.aggiungiIstruzione(new Istruzione(1, 1, "Cuocere il riso in acqua bollente salata per il tempo indicato sulla confezione."));
        ricetta.aggiungiIstruzione(new Istruzione(2, 2, "Scolare il riso e lasciarlo raffreddare in una ciotola capiente."));
        ricetta.aggiungiIstruzione(new Istruzione(3, 3, "Lavare e tagliare i peperoni e i pomodorini a cubetti."));
        ricetta.aggiungiIstruzione(new Istruzione(4, 4, "Sgocciolare il tonno e sminuzzarlo."));
        ricetta.aggiungiIstruzione(new Istruzione(5, 5, "Aggiungere al riso: tonno, peperoni, pomodorini, olive, mais, piselli e formaggio."));
        ricetta.aggiungiIstruzione(new Istruzione(6, 6, "Condire con olio e sale."));
        ricetta.aggiungiIstruzione(new Istruzione(7, 7, "Mescolare delicatamente e guarnire con foglie di basilico."));
        ricetta.aggiungiIstruzione(new Istruzione(8, 8, "Servire freddo o a temperatura ambiente."));
        
        // Aggiungi tag
        ricetta.aggiungiTag(new Tag("Piatto unico"));
        ricetta.aggiungiTag(new Tag("Estivo"));
        ricetta.aggiungiTag(new Tag("Freddo"));
        
        // Pubblica la ricetta
        ricetta.setStato("Pubblicata");
        aggiornaRicetta(ricetta);
        
        return ricetta;
    }
}