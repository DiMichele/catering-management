# Versione Senza Persistenza

## Descrizione

Questa è una versione semplificata dell'applicazione che non utilizza più persistenza su file. Tutti i dati vengono mantenuti solo in memoria durante l'esecuzione dell'applicazione e vengono persi quando l'applicazione viene chiusa.

## Come Funziona

Il sistema utilizza un `InMemoryRepository` che:

1. Mantiene tutti i dati in una struttura dati in memoria (ConcurrentHashMap)
2. Simula tutte le operazioni CRUD come un normale repository
3. Non scrive mai su disco
4. È molto più veloce rispetto alla persistenza su file

## Vantaggi

- **Nessun file di dati**: Non c'è più bisogno di gestire file JSON o altri file di dati
- **Velocità**: Tutte le operazioni sono molto più veloci 
- **Semplicità**: Nessuna preoccupazione per errori di I/O o problemi di permessi
- **Ideale per prototipi**: Perfetto per dimostrazioni e test

## Svantaggi

- **Nessuna persistenza**: I dati vengono persi alla chiusura dell'applicazione
- **Dati di esempio**: È necessario creare dati di esempio all'avvio ogni volta

## Ripristinare la Persistenza

Se in futuro desideri ripristinare la persistenza, puoi semplicemente:

1. Ripristinare i riferimenti a `SimpleJsonRepository` in tutti i servizi
2. Assicurarti che la directory `data/` esista

## Note

Questa versione è ideale per scopi dimostrativi e per lo sviluppo di prototipi, ma non è adatta per un'applicazione di produzione dove i dati devono essere conservati tra diverse esecuzioni dell'applicazione. 