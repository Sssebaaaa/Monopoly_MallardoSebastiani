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
