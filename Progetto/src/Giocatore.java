package Progetto.src;

public class Giocatore {
    private String nome;
    private int soldi;
    private int posizioneCorrente;
    private Casella[] proprietaPossedute; 
    private int turniInPrigione;
    private boolean inPrigione;

    public Giocatore(String nome, int soldi){
        this.nome = nome;
        this.soldi = soldi;
        this.posizioneCorrente = 0;

        this.proprietaPossedute = new Casella[0];
        this.turniInPrigione = 0;
        this.inPrigione = false;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getSoldi() {
        return this.soldi;
    }

    public void setSoldi(int soldi) {
        this.soldi = soldi;
    }

    public int getPosizioneCorrente() {
        return this.posizioneCorrente;
    }

    public void setPosizioneCorrente(int posizioneCorrente) {
        this.posizioneCorrente = posizioneCorrente;
    }

    public Casella[] getProprietaPossedute() {
        return this.proprietaPossedute;
    }

    public void setProprietaPossedute(Casella[] proprietaPossedute) {
        this.proprietaPossedute = proprietaPossedute;
    }

    public int getTurniInPrigione() {
        return this.turniInPrigione;
    }

    public void setTurniInPrigione(int turniInPrigione) {
        this.turniInPrigione = turniInPrigione;
    }

    public boolean isInPrigione() {
        return this.inPrigione;
    }

    public void setInPrigione(boolean inPrigione) {
        this.inPrigione = inPrigione;
    }

   
    public void incassa(int somma){
        this.soldi += somma;
    }
    
    public void paga(int somma, Giocatore beneficiario){
        if(this.soldi >= somma){
            this.soldi -= somma;
            if(beneficiario != null){
                beneficiario.incassa(somma);
            }
        }
    }

    public void muovi(int passi) {
        this.posizioneCorrente = (this.posizioneCorrente + passi) % 40;
    }

    public void esciDiPrigione() {
        this.inPrigione = false;
        this.turniInPrigione = 0;
    }

    // Incrementa il contatore dei turni in prigione.
    // Se raggiunge 3 turni, il giocatore paga automaticamente 50 (regola semplificata)
    // e viene rimesso in libertà.
    public void aumentaTurniPrigione() {
        this.turniInPrigione++;
        if (this.turniInPrigione >= 3) {
            // Regola semplice: paga 50 e esce di prigione
            int tassaUscita = 50;
            if (this.soldi >= tassaUscita) {
                this.paga(tassaUscita, null);
                this.esciDiPrigione();
                System.out.println(this.nome + " ha pagato " + tassaUscita + "€ ed è uscito di prigione.");
            } else {
                // Se non ha soldi sufficienti, rimane in prigione ma il contatore non supera 3
                System.out.println(this.nome + " non ha abbastanza soldi per pagare la cauzione.");
            }
        }
    }

    public boolean haSerieCompleta(String colore) {
        int contatoreColore = 0;

        for (int i = 0; i < proprietaPossedute.length; i++) {
            if (proprietaPossedute[i] instanceof Casella_terreno) {
                Casella_terreno terreno = (Casella_terreno) proprietaPossedute[i];
                if (terreno.getColore().equals(colore)) {
                    contatoreColore++;
                }
            }
        }

        if ((colore.equals("Marrone") || colore.equals("Blu")) && contatoreColore == 2) {
            return true;
        } else if((colore.equals("Rosa") || colore.equals("Arancione") || colore.equals("Rosso") || colore.equals("Giallo") || colore.equals("Verde") || colore.equals("Azzurro")) && contatoreColore == 3){
            return true;
        } else {
            return false;
        }
    }

    public void acquistaProprieta(Casella proprieta){
        Casella[] vettore = new Casella[this.proprietaPossedute.length+1];

        for(int i=0; i<proprietaPossedute.length; i++){
            vettore[i] = this.proprietaPossedute[i];
        }
        vettore[this.proprietaPossedute.length] = proprieta;

        this.proprietaPossedute = vettore;
    }

    public void rimuoviProprieta() {
        for (int i = 0; i < this.proprietaPossedute.length; i++) {
            
            Casella c = this.proprietaPossedute[i];
            if (c instanceof Casella_terreno) {
                ((Casella_terreno) c).setProprietario(null);
            } else if (c instanceof Casella_stazione) {
                ((Casella_stazione) c).setProprietario(null);
            }
        }

        this.proprietaPossedute = new Casella[0];
    }


}
