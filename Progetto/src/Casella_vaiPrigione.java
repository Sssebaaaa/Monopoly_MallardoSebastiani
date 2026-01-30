package Progetto.src;

class Casella_vaiPrigione extends Casella {

    public Casella_vaiPrigione(String nome, int id) {
        super(nome, id);
    }

    public void azione(Giocatore g, Partita p) {
        // Manda il giocatore in prigione
        p.log(g.getNome() + " va in prigione!");
        g.setInPrigione(true);
        g.setPosizioneCorrente(10); 
        g.setTurniInPrigione(0);
    }

}
