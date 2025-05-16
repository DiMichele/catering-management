package software.domain.utenti;

import java.io.Serializable;

/**
 * Classe base per tutti gli utenti del sistema.
 */
public abstract class Utente implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String nome;
    private String cognome;
    private String email;
    private String telefono;
    private boolean attivo = true;

    public Utente(int id, String nome, String cognome, String email, String telefono) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.telefono = telefono;
    }
    
    // Getters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public boolean isAttivo() { return attivo; }
    
    // Setters
    public void setNome(String nome) { this.nome = nome; }
    public void setCognome(String cognome) { this.cognome = cognome; }
    public void setEmail(String email) { this.email = email; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setAttivo(boolean attivo) { this.attivo = attivo; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utente utente = (Utente) o;
        return id == utente.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    
    @Override
    public String toString() {
        return nome + " " + cognome;
    }
}