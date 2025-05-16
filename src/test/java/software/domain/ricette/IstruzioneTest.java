package software.domain.ricette;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class IstruzioneTest {

    @Test
    @DisplayName("Creazione di un'istruzione con valori validi")
    public void testCreazioneIstruzioneValida() {
        // Arrange & Act
        Istruzione istruzione = new Istruzione(1, 2, "Mescolare gli ingredienti");
        
        // Assert
        assertEquals(1, istruzione.getId());
        assertEquals(2, istruzione.getOrdine());
        assertEquals("Mescolare gli ingredienti", istruzione.getDescrizione());
    }
    
    @Test
    @DisplayName("Test dei setters")
    public void testSetters() {
        // Arrange
        Istruzione istruzione = new Istruzione(1, 2, "Mescolare gli ingredienti");
        
        // Act
        istruzione.setOrdine(3);
        istruzione.setDescrizione("Cuocere a fuoco lento");
        
        // Assert
        assertEquals(3, istruzione.getOrdine());
        assertEquals("Cuocere a fuoco lento", istruzione.getDescrizione());
    }
    
    @Test
    @DisplayName("Test delle proprietà JavaFX")
    public void testJavaFXProperties() {
        // Arrange
        Istruzione istruzione = new Istruzione(1, 2, "Mescolare gli ingredienti");
        
        // Assert
        assertEquals(1, istruzione.idProperty().get());
        assertEquals(2, istruzione.ordineProperty().get());
        assertEquals("Mescolare gli ingredienti", istruzione.descrizioneProperty().get());
        
        // Act - modifica tramite property
        istruzione.ordineProperty().set(3);
        istruzione.descrizioneProperty().set("Cuocere a fuoco lento");
        
        // Assert - verifica che le property aggiornino i getter
        assertEquals(3, istruzione.getOrdine());
        assertEquals("Cuocere a fuoco lento", istruzione.getDescrizione());
    }
    
    @Test
    @DisplayName("Test serializzazione e deserializzazione")
    public void testSerializzazione(@TempDir Path tempDir) throws IOException, ClassNotFoundException {
        // Arrange
        Istruzione originale = new Istruzione(1, 2, "Mescolare gli ingredienti");
        File tempFile = tempDir.resolve("istruzione.ser").toFile();
        
        // Act - Serializza
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tempFile))) {
            oos.writeObject(originale);
        }
        
        // Act - Deserializza
        Istruzione deserializzata;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(tempFile))) {
            deserializzata = (Istruzione) ois.readObject();
        }
        
        // Assert
        assertNotNull(deserializzata);
        assertEquals(originale.getId(), deserializzata.getId());
        assertEquals(originale.getOrdine(), deserializzata.getOrdine());
        assertEquals(originale.getDescrizione(), deserializzata.getDescrizione());
    }
    
    @Test
    @DisplayName("Test serializzazione dopo modifica")
    public void testSerializzazioneDopoCambio(@TempDir Path tempDir) throws IOException, ClassNotFoundException {
        // Arrange
        Istruzione originale = new Istruzione(1, 2, "Mescolare gli ingredienti");
        originale.setOrdine(3);
        originale.setDescrizione("Cuocere a fuoco lento");
        File tempFile = tempDir.resolve("istruzione_modificata.ser").toFile();
        
        // Act - Serializza
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tempFile))) {
            oos.writeObject(originale);
        }
        
        // Act - Deserializza
        Istruzione deserializzata;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(tempFile))) {
            deserializzata = (Istruzione) ois.readObject();
        }
        
        // Assert
        assertNotNull(deserializzata);
        assertEquals(1, deserializzata.getId()); // ID non modificato
        assertEquals(3, deserializzata.getOrdine()); // Ordine modificato
        assertEquals("Cuocere a fuoco lento", deserializzata.getDescrizione()); // Descrizione modificata
    }
} 