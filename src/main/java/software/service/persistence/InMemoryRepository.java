package software.service.persistence;

import software.domain.exceptions.DomainException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Repository in-memory ultra semplificato senza persistenza.
 * Mantiene i dati solo durante l'esecuzione dell'applicazione.
 * Ideale per prototipi e testing.
 */
public class InMemoryRepository<T, ID> {
    
    private static final Logger LOGGER = Logger.getLogger(InMemoryRepository.class.getName());
    private static final ConcurrentHashMap<String, List<?>> GLOBAL_STORAGE = new ConcurrentHashMap<>();
    
    private final String entityName;
    private final Class<T> entityClass;
    private final String idFieldName;
    private final AtomicInteger nextId;
    
    /**
     * Costruttore per il repository in-memory.
     *
     * @param entityName Nome dell'entità (usato come chiave nello storage)
     * @param entityClass Classe dell'entità
     * @param idFieldName Nome del campo ID nell'entità
     */
    @SuppressWarnings("unchecked")
    public InMemoryRepository(String entityName, Class<T> entityClass, String idFieldName) {
        this.entityName = entityName;
        this.entityClass = entityClass;
        this.idFieldName = idFieldName;
        
        // Inizializza lo storage se non esiste
        if (!GLOBAL_STORAGE.containsKey(entityName)) {
            GLOBAL_STORAGE.put(entityName, new ArrayList<T>());
        }
        
        // Determina il prossimo ID
        int maxId = 0;
        for (Object entity : getEntities()) {
            Integer id = getId((T) entity);
            if (id != null && id > maxId) {
                maxId = id;
            }
        }
        this.nextId = new AtomicInteger(maxId + 1);
        
        LOGGER.info("Repository in-memory per " + entityName + " inizializzato");
    }
    
    @SuppressWarnings("unchecked")
    private List<T> getEntities() {
        return (List<T>) GLOBAL_STORAGE.get(entityName);
    }
    
    /**
     * Imposta l'ID su un'entità.
     */
    private void setId(T entity, Integer id) {
        try {
            Field idField = entityClass.getDeclaredField(idFieldName);
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new DomainException("Impossibile impostare l'ID sull'entità", e);
        }
    }
    
    /**
     * Ottiene l'ID da un'entità.
     */
    private Integer getId(T entity) {
        try {
            Field idField = entityClass.getDeclaredField(idFieldName);
            idField.setAccessible(true);
            Object idValue = idField.get(entity);
            
            if (idValue instanceof Integer) {
                return (Integer) idValue;
            }
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nell'accesso al campo ID: " + idFieldName, e);
            return null;
        }
    }
    
    /**
     * Salva un'entità.
     */
    @SuppressWarnings("unchecked")
    public T save(T entity) {
        if (entity == null) {
            throw new DomainException("Impossibile salvare un'entità null");
        }
        
        List<T> entities = getEntities();
        Integer id = getId(entity);
        
        // Nuova entità (ID nullo o zero)
        if (id == null || id == 0) {
            int newId = nextId.getAndIncrement();
            setId(entity, newId);
            entities.add(entity);
            return entity;
        }
        
        // Aggiorna entità esistente
        for (int i = 0; i < entities.size(); i++) {
            if (Objects.equals(getId(entities.get(i)), id)) {
                entities.set(i, entity);
                return entity;
            }
        }
        
        // Se non esiste, aggiungi
        entities.add(entity);
        return entity;
    }
    
    /**
     * Trova un'entità per ID.
     */
    @SuppressWarnings("unchecked")
    public T findById(ID id) {
        if (id == null) {
            return null;
        }
        
        return getEntities().stream()
                .filter(e -> Objects.equals(getId(e), id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Trova tutte le entità.
     */
    public List<T> findAll() {
        return new ArrayList<>(getEntities());
    }
    
    /**
     * Elimina un'entità.
     */
    public void delete(T entity) {
        if (entity == null) {
            throw new DomainException("Impossibile eliminare un'entità null");
        }
        
        deleteById((ID) getId(entity));
    }
    
    /**
     * Elimina un'entità per ID.
     */
    @SuppressWarnings("unchecked")
    public void deleteById(ID id) {
        if (id == null) {
            throw new DomainException("ID non può essere null");
        }
        
        List<T> entities = getEntities();
        entities.removeIf(e -> Objects.equals(getId(e), id));
    }
    
    /**
     * Verifica se esiste un'entità con l'ID specificato.
     */
    public boolean existsById(ID id) {
        if (id == null) {
            return false;
        }
        
        return getEntities().stream()
                .anyMatch(e -> Objects.equals(getId(e), id));
    }
    
    /**
     * Trova entità per un valore di un campo specifico.
     */
    public List<T> findByField(String fieldName, Object fieldValue) {
        if (fieldName == null || fieldValue == null) {
            return new ArrayList<>();
        }
        
        return getEntities().stream()
                .filter(e -> {
                    try {
                        Field field = entityClass.getDeclaredField(fieldName);
                        field.setAccessible(true);
                        Object value = field.get(e);
                        return Objects.equals(value, fieldValue);
                    } catch (Exception ex) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }
} 