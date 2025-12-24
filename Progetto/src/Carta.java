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

    public void applicaEffetto(Giocatore g, Partita p){
        switch (idEffetto) {
            case 1: // soldi
                if (valore > 0){
                    g.incassa(valore);
                } else {
                    int soldiDaPagare = valore * -1; 
                    g.paga(soldiDaPagare, null);
                }
                break;

            case 2: // movimento
                g.muovi(valore);
                break;

            case 3: // vai in prigione
                g.setInPrigione(true);
                break;

            case 4: // vai al VIA
                int passi = 40 - g.getPosizioneCorrente(); // passi fino al VIA
                g.muovi(passi);
                break;
                
            default:
                System.out.println("-");
        }
    }


}