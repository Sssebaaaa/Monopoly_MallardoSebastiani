package Progetto.src;

class Banca {

    public void incassa(int somma) {
        System.out.println("Banca incassa: " + somma + "€");
    }

    public void vendiTerreno(Casella t, Giocatore g) {
        int prezzo = 0;

        if (t instanceof Casella_terreno) {
            prezzo = ((Casella_terreno) t).getValoreAcquisto();
        } else if (t instanceof Casella_stazione) {
            prezzo = ((Casella_stazione) t).getValoreAcquisto();
        } else if (t instanceof Casella_societa) {
            prezzo = ((Casella_societa) t).getValoreAcquisto();
        } else {
            return;
        }

        if (g.getSoldi() >= prezzo) {
            g.paga(prezzo, null);
            g.acquistaProprieta(t);

            if (t instanceof Casella_terreno) {
                ((Casella_terreno) t).setProprietario(g);
            } else if (t instanceof Casella_stazione) {
                ((Casella_stazione) t).setProprietario(g);
            } else if (t instanceof Casella_societa) {
                ((Casella_societa) t).setProprietario(g);
            }
        }
    }

}
