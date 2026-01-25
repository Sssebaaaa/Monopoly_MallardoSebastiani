package Progetto.src;
public class Tabellone {
    private Casella[] caselle;

    public Tabellone() {
        this.caselle = new Casella[40];
    }
    
    public void setCasella(int indice, Casella c) {
        if (indice >= 0 && indice < 40) {
            caselle[indice] = c;
        }
    }
    
    public Casella getCasella(int indice) {
        return caselle[indice];
    }

    public int calcolaProssimaPosizione(int posIniziale, int passi) {
        int nuovaPos = (posIniziale + passi) % 40;
        return nuovaPos;
    }
    
    public int getDimensione() {
        return caselle.length;
    }
}