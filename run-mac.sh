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

# Imposta il percorso dei moduli JavaFX
JAVAFX_PATH="$HOME/.m2/repository/org/openjfx"
JAVAFX_MODULES="$JAVAFX_PATH/javafx-controls/21.0.2:$JAVAFX_PATH/javafx-fxml/21.0.2:$JAVAFX_PATH/javafx-base/21.0.2:$JAVAFX_PATH/javafx-graphics/21.0.2"

# Esegue la classe Main
java --module-path "$JAVAFX_MODULES" --add-modules javafx.controls,javafx.fxml,javafx.base,javafx.graphics -cp target/classes software.Main

# Gestisce eventuali errori
if [ $? -ne 0 ]; then
    echo -e "${RED}Si è verificato un errore durante l'avvio dell'applicazione.${NC}"
    exit 1
fi 