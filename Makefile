.PHONY: run clean test package

# Avvia l'applicazione
run:
	mvn clean compile javafx:run

# Pulisce i file di build
clean:
	mvn clean

# Esegue i test
test:
	mvn test

# Crea il pacchetto
package:
	mvn package 