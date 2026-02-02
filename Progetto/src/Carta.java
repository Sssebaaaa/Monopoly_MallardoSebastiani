package Progetto.src;

class Carta {
    private String descrizione;
    private int idEffetto;
    private int valore;

    public Carta(String descrizione, int idEffetto, int valore) {
        this.descrizione = descrizione;
        this.idEffetto = idEffetto;
        this.valore = valore;
    }

    public String getDescrizione() {
        return this.descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public int getIdEffetto() {
        return this.idEffetto;
    }

    public void setIdEffetto(int idEffetto) {
        this.idEffetto = idEffetto;
    }

    public int getValore() {
        return this.valore;
    }

    public void setValore(int valore) {
        this.valore = valore;
    }

    public void applicaEffetto(Giocatore g, Partita p) {
        switch (idEffetto) {
            case 1: // soldi
                if (valore > 0) {
                    g.incassa(valore);
                } else {
                    int soldiDaPagare = valore * -1;
                    g.paga(soldiDaPagare, null);
                }
                break;

            case 2: // movimento relativo
                int oldP2 = g.getPosizioneCorrente();
                g.muovi(valore);
                if (g.getPosizioneCorrente() < oldP2 && valore > 0) {
                    g.incassa(200);
                    p.registra(g.getNome() + " ha passato il VIA tramite una carta! Ritira 200€");
                }
                // Attiva l'azione della casella di arrivo (solo se non è un'altra carta per
                // evitare loop infiniti)
                Casella dest2 = p.getTabellone().getCasella(g.getPosizioneCorrente());
                if (dest2 != null && !(dest2 instanceof Casella_carta)) {
                    dest2.azione(g, p);
                }
                break;

            case 3: // vai in prigione
                g.setInPrigione(true);
                g.setPosizioneCorrente(10); // Sposta fisicamente in prigione
                break;

            case 4: // vai al VIA
                int oldP4 = g.getPosizioneCorrente();
                int passi4 = 40 - oldP4;
                g.muovi(passi4);
                // Se era già sul via (0), passi4 è 40. Se era altrove, passi4 > 0 e nuova pos è
                // 0.
                // In entrambi i casi ha "passato" o "raggiunto" il via.
                g.incassa(200);
                p.registra(g.getNome() + " va al VIA! Ritira 200€");
                // Il VIA di solito non ha azione (o ha azione 0 già settata), ma per coerenza:
                Casella dest4 = p.getTabellone().getCasella(0);
                if (dest4 != null)
                    dest4.azione(g, p);
                break;

            case 5: // vai alla casella ID specifica
                int target = valore;
                int currentPos = g.getPosizioneCorrente();
                g.setPosizioneCorrente(target);
                p.registra(g.getNome() + " si sposta in: " + p.getTabellone().getCasella(target).getNome());

                // Se la nuova posizione è minore della precedente, ha passato il VIA
                if (target < currentPos) {
                    g.incassa(200);
                    p.registra(g.getNome() + " ha passato il VIA! Ritira 200€");
                }

                Casella dest5 = p.getTabellone().getCasella(target);
                if (dest5 != null && !(dest5 instanceof Casella_carta)) {
                    dest5.azione(g, p);
                }
                break;

            default:
                p.registra("Carta senza effetto trattato in questo switch.");
        }
    }

}