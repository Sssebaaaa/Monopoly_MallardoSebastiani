package Progetto.src;

class Casella_prigione extends Casella {

    public Casella_prigione(String nome, int id) {
        super(nome, id);
    }

    public void azione(Giocatore g, Partita p) {
        if (g.isInPrigione()) {
            p.log(g.getNome() + " è in prigione");
        } else {
            p.log("Solo passaggio");
        }
    }
}
