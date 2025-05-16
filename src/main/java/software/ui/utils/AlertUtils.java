package software.ui.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Classe di utilità per mostrare dialoghi e alert.
 */
public class AlertUtils {
    
    /**
     * Mostra un messaggio di informazione.
     * 
     * @param title Il titolo del dialogo
     * @param message Il messaggio da mostrare
     */
    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Mostra un messaggio di avviso.
     * 
     * @param title Il titolo del dialogo
     * @param message Il messaggio da mostrare
     */
    public static void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Mostra un messaggio di errore.
     * 
     * @param title Il titolo del dialogo
     * @param message Il messaggio da mostrare
     */
    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Mostra un dialogo di conferma.
     * 
     * @param title Il titolo del dialogo
     * @param message Il messaggio da mostrare
     * @return true se l'utente ha confermato, false altrimenti
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}