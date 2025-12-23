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

    public boolean getInPrigione() {
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

    



}
