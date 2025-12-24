package Progetto.src;

class Casella_stazione extends Casella {
    private int valoreAcquisto;
    private Giocatore proprietario;
    private int renditaBase; 

    public Casella_stazione(String nome, int id, int valoreAcquisto) {
        super(nome, id);
        this.valoreAcquisto = valoreAcquisto;
        this.renditaBase = 25; 
        this.proprietario = null;
    }

    public int getValoreAcquisto() {
        return this.valoreAcquisto;
    }

    public void setValoreAcquisto(int valoreAcquisto) {
        this.valoreAcquisto = valoreAcquisto;
    }

    public Giocatore getProprietario() {
        return this.proprietario;
    }

    public void setProprietario(Giocatore proprietario) {
        this.proprietario = proprietario;
    }

    public int getRenditaBase() {
        return this.renditaBase;
    }

    public void setRenditaBase(int renditaBase) {
        this.renditaBase = renditaBase;
    }

    public void azione(Giocatore g, Partita p){
        if(this.proprietario != null && this.proprietario != g){
            g.paga(calcolaAffitto(), proprietario);
        } else if(this.proprietario == null){
            System.out.println("Stazione in vendita: " + nome + " - prezzo: " + valoreAcquisto + "€");
            if (g.getSoldi() >= valoreAcquisto) {
                p.getBanca().vendiTerreno(this, g);
                System.out.println(g.getNome() + " ha acquistato la stazione " + nome + " per " + valoreAcquisto + "€");
            }
        }
    }
    
    public int calcolaAffitto(){
        int stazioniPossedute = 0;

        Casella[] proprietaPossedute = proprietario.getProprietaPossedute();
        for (int i = 0; i < proprietaPossedute.length; i++) {
            if (proprietaPossedute[i] instanceof Casella_stazione) {
                stazioniPossedute++;
            }
        }

        if(stazioniPossedute == 1){
            return 25;
        } else if(stazioniPossedute == 2){
            return 50;
        } else if(stazioniPossedute == 3){
            return 100;
        } else if(stazioniPossedute == 4){
            return 200;
        } else {
            return 25;
        }
    }
    
}
