package software.config;

import java.util.logging.Logger;

/**
 * Classe di configurazione dell'applicazione semplificata.
 * Non gestisce più la persistenza dei dati.
 */
public class AppConfig {
    private static final Logger LOGGER = Logger.getLogger(AppConfig.class.getName());
    private static AppConfig instance;
    
    // Configurazione
    private String appName = "Software Gestione Ricette";
    private String appVersion = "1.0.0";
    
    /**
     * Costruttore privato per il singleton.
     */
    private AppConfig() {
        LOGGER.fine("AppConfig inizializzato");
    }
    
    /**
     * Ottiene l'istanza singleton di AppConfig.
     */
    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }
    
    /**
     * Ottiene il nome dell'applicazione.
     */
    public String getAppName() {
        return appName;
    }
    
    /**
     * Ottiene la versione dell'applicazione.
     */
    public String getAppVersion() {
        return appVersion;
    }
} 