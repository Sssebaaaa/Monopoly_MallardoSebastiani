package Progetto.src;

class Casella_terreno extends Casella {
    private int valoreAcquisto;
    private int renditaBase;
    private int numeroCase;
    private Giocatore proprietario;
    private int costoCasa;
    private String colore;


    public Casella_terreno(String nome, int id, int valoreAcquisto, int renditaBase, int costoCasa, String colore) {
        super(nome, id);
        this.valoreAcquisto = valoreAcquisto;
        this.renditaBase = renditaBase;
        this.costoCasa = costoCasa;
        this.colore = colore;

        this.numeroCase = 0;
        this.proprietario = null;
    }

    public int getValoreAcquisto() {
        return this.valoreAcquisto;
    }

    public void setValoreAcquisto(int valoreAcquisto) {
        this.valoreAcquisto = valoreAcquisto;
    }

    public int getRenditaBase() {
        return this.renditaBase;
    }

    public void setRenditaBase(int renditaBase) {
        this.renditaBase = renditaBase;
    }

    public int getNumeroCase() {
        return this.numeroCase;
    }

    public void setNumeroCase(int numeroCase) {
        this.numeroCase = numeroCase;
    }

    public Giocatore getProprietario() {
        return this.proprietario;
    }

    public void setProprietario(Giocatore proprietario) {
        this.proprietario = proprietario;
    }

    public int getCostoCasa() {
        return this.costoCasa;
    }

    public void setCostoCasa(int costoCasa) {
        this.costoCasa = costoCasa;
    }

    public String getColore() {
        return this.colore;
    }

    public void setColore(String colore) {
        this.colore = colore;
    }

    public void azione(Giocatore g, Partita p) {
        if (this.proprietario != null && this.proprietario != g) {
            // Pagamento dell'affitto al proprietario
            int affitto = calcolaAffitto();
            p.log(g.getNome() + " paga " + affitto + "€ di affitto a " + proprietario.getNome());
            g.paga(affitto, proprietario);
        } else if (this.proprietario == null) {
            // Log per indicare che è disponibile
            p.log(nome + " è in vendita per " + valoreAcquisto + "€");
        } else if (this.proprietario == g) {
            p.log("Sei sulla tua proprietà.");
        }
    }

    public int calcolaAffitto() {
        if (numeroCase == 0) {
            // Se il proprietario possiede tutta la serie l'affitto è doppio
            if (proprietario != null && proprietario.haSerieCompleta(this.colore)) {
                return renditaBase * 2;
            }
            return renditaBase;
        } else {
            return renditaBase + (100 * numeroCase);
        }
    }

    public boolean costruisciCasa() {
        if (numeroCase <= 4) {
            this.numeroCase++;
            return true;
        } else {
            return false;
        }
    }
}
