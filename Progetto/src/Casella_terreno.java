package Progetto.src;

class Casella_terreno extends Casella {
    private int valoreAcquisto;
    private int renditaBase;
    private int numeroCase;
    private Giocatore proprietario;
    private int costoCasa;
    private String colore;

    private static final int COSTO_COSTRUZIONE = 150;

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

    public void azione(Giocatore g, Partita p){
        if(this.proprietario != null && this.proprietario != g){
            // paghi affitto al proprietario
            int affitto = calcolaAffitto();
            g.paga(affitto, proprietario);
        } else if(this.proprietario == null){
            // La banca vende il terreno
            System.out.println("Terreno in vendita: " + nome + " - prezzo: " + valoreAcquisto + "€");
            // il giocatore compra se ha abbastanza soldi
            if (g.getSoldi() >= valoreAcquisto) {
                p.getBanca().vendiTerreno(this, g);
                System.out.println(g.getNome() + " ha acquistato " + nome + " per " + valoreAcquisto + "€");
            }
        } else if(this.proprietario == g) {
            // Se il proprietario possiede tutta la serie e ci sono meno di 4 case può costruire
            if (g.haSerieCompleta(this.colore) && this.numeroCase < 4) {
                if (g.getSoldi() >= COSTO_COSTRUZIONE) {
                    g.paga(COSTO_COSTRUZIONE, null);
                    this.numeroCase++;
                    System.out.println(g.getNome() + " ha costruito una casa su " + nome + ". Case totali: " + numeroCase);
                }
            }
        }
    }

    public int calcolaAffitto(){
        if(numeroCase == 0){
            // Se il proprietario possiede tutta la serie l'affitto è doppio
            if (proprietario != null && proprietario.haSerieCompleta(this.colore)) {
                return renditaBase * 2;
            }
            return renditaBase;
        } else {
            return renditaBase + (100 * numeroCase);
        }
    }

    public boolean costruisciCasa(){
        if(numeroCase <= 4){
            this.numeroCase++;
            return true;
        } else {
            return false;
        }
    }
}
