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
        return Math.floorMod(posIniziale + passi, 40);
    }

    public int getDimensione() {
        return caselle.length;
    }
}