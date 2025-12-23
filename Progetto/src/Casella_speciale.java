package Progetto.src;

class Casella_speciale extends Casella {
    private int importo;

    public Casella_speciale(String nome, int id, int importo){
        super(nome, id);
        this.importo = importo;
    }

    public int getImporto() {
        return this.importo;
    }

    public void setImporto(int importo) {
        this.importo = importo;
    }

    public void azione(Giocatore g, Partita p){
        g.incassa(importo); //casella "VIA"
    }



}
