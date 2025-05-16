#!/bin/bash

# Colori per il terminale
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Avvio dell'applicazione Catering Management...${NC}"

# Verifica l'installazione di Java
if ! command -v java &> /dev/null; then
    echo -e "${RED}Java non trovato! Per favore installa Java 11 o superiore.${NC}"
    echo "Puoi installarlo con Homebrew: brew install openjdk@11"
    exit 1
fi

# Verifica l'installazione di Maven
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}Maven non trovato! Per favore installa Maven.${NC}"
    echo "Puoi installarlo con Homebrew: brew install maven"
    exit 1
fi

# Esegui la compilazione
echo -e "${YELLOW}Compilazione del progetto...${NC}"
mvn clean compile

# Controlla se la compilazione è andata a buon fine
if [ $? -ne 0 ]; then
    echo -e "${RED}Errore durante la compilazione!${NC}"
    exit 1
fi

# Avvia l'applicazione
echo -e "${GREEN}Avvio dell'applicazione...${NC}"
mvn javafx:run

# Gestisce eventuali errori
if [ $? -ne 0 ]; then
    echo -e "${RED}Si è verificato un errore durante l'avvio dell'applicazione.${NC}"
    exit 1
fi 