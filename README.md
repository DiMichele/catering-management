# Sistema di Gestione Catering

Un'applicazione Java per la gestione di ricette e catering. Questo prototipo permette di creare e gestire ricette, menu e compiti di cucina.

## Caratteristiche

- Gestione delle ricette (creazione, modifica, pubblicazione)
- Creazione e personalizzazione di menu
- Pianificazione di compiti di cucina
- Gestione di eventi e servizi
- Interfaccia grafica JavaFX

## Tecnologie

- Java 11+
- JavaFX
- Maven

## Requisiti

- Java 11 o superiore
- Maven 3.6 o superiore

## Installazione

1. Clona il repository:
```
git clone https://github.com/tuonome/catering-management.git
```

2. Entra nella directory del progetto:
```
cd catering-management
```

3. Compila il progetto:
```
mvn clean compile
```

4. Esegui l'applicazione:
```
mvn javafx:run
```

## Struttura del progetto

- `src/main/java/software/domain`: Classi di dominio
- `src/main/java/software/service`: Servizi per la logica di business
- `src/main/java/software/ui`: Componenti dell'interfaccia utente
- `src/main/resources`: File FXML e altre risorse

## Note sulla versione attuale

Questa è una versione semplificata dell'applicazione che non utilizza persistenza su file. Tutti i dati vengono mantenuti solo in memoria durante l'esecuzione dell'applicazione. Per maggiori dettagli, consulta il file [README_NO_PERSISTENZA.md](README_NO_PERSISTENZA.md).

## Contribuire al progetto

1. Crea un branch per la tua funzionalità:
```
git checkout -b nome-funzionalita
```

2. Fai commit delle tue modifiche:
```
git commit -m "Descrizione delle modifiche"
```

3. Pusha il branch sul repository remoto:
```
git push origin nome-funzionalita
```

4. Crea una Pull Request su GitHub

## Licenza

[Specificare la licenza] 