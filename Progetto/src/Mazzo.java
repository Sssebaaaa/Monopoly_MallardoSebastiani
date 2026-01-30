package Progetto.src;

import java.util.ArrayList;
import java.util.Collections;

class Mazzo {
    private ArrayList<Carta> carte;

    public Mazzo() {
        this.carte = new ArrayList<>();
    }

    public void mescola() {
        Collections.shuffle(this.carte);
    }

    public Carta pesca() {
        if (carte.isEmpty())
            return null;
        Carta c = carte.remove(0);
        carte.add(c); // Cicla la carta in fondo
        return c;
    }

    public void aggiungiCarta(Carta c) {
        if (c != null) {
            this.carte.add(c);
        }
    }
}
