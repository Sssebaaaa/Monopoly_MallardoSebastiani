package Files;
public class Tabellone {
    // Array fisso di 40 caselle (dimensione standard del Monopoly)
    private Casella[] caselle;

    public Tabellone() {
        this.caselle = new Casella[40];
    }// Metodo per inserire una casella in una posizione specifica
    // Utile quando la classe Partita inizializza il gioco
    public void setCasella(int indice, Casella c) {
        if (indice >= 0 && indice < 40) {
            caselle[indice] = c;
        }
    }