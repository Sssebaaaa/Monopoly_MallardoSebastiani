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
    }// Restituisce l'oggetto Casella dato un indice
    public Casella getCasella(int indice) {
        return caselle[indice];
    }

    // Calcola la nuova posizione usando l'operatore modulo (%)
    // Questo serve perché se sono alla casella 39 e faccio 2 passi,
    // devo finire alla casella 1, non alla 41 (che non esiste)
    public int calcolaProssimaPosizione(int posIniziale, int passi) {
        int nuovaPos = (posIniziale + passi) % 40;
        return nuovaPos;
    }// Restituisce la dimensione del tabellone (utile per i cicli)
    public int getDimensione() {
        return caselle.length;
    }
}