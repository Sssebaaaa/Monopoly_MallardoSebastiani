package Progetto.src;

class Casella_carta extends Casella {
    private String tipoMazzo;

    public Casella_carta(String nome, int id, String tipoMazzo) {
        super(nome, id);
        this.tipoMazzo = tipoMazzo;
    }

    public String getTipoMazzo() {
        return this.tipoMazzo;
    }

    public void setTipoMazzo(String tipoMazzo) {
        this.tipoMazzo = tipoMazzo;
    }

    public void azione(Giocatore g, Partita p) {
        p.registra("Sei atterrato su: " + nome + " (" + tipoMazzo + ")");
        Carta c = p.pesca(tipoMazzo);

        if (c != null) {
            p.registra("Carta pescata: " + c.getDescrizione());
            c.applicaEffetto(g, p);
        }
    }

}
