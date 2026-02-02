package Progetto.src;

public class Casella_societa extends Casella {
    private int valoreAcquisto;
    private Giocatore proprietario;

    public Casella_societa(String nome, int id, int valoreAcquisto) {
        super(nome, id);
        this.valoreAcquisto = valoreAcquisto;
        this.proprietario = null;
    }

    public int getValoreAcquisto() {
        return valoreAcquisto;
    }

    public Giocatore getProprietario() {
        return proprietario;
    }

    public void setProprietario(Giocatore proprietario) {
        this.proprietario = proprietario;
    }

    @Override
    public void azione(Giocatore g, Partita p) {
        if (proprietario != null && proprietario != g) {
            int pedaggio = calcolaAffitto(p.getDadi().getTotale());
            g.paga(pedaggio, proprietario);
            p.registra(g.getNome() + " ha pagato " + pedaggio + "€ di pedaggio a " + proprietario.getNome());
        } else if (proprietario == null) {
            p.registra(nome + " è in vendita per " + valoreAcquisto + "€");
        }
    }

    public int calcolaAffitto(int sommaDadi) {
        if (proprietario == null)
            return 0;

        int societaPossedute = 0;
        for (Casella c : proprietario.getProprietaPossedute()) {
            if (c instanceof Casella_societa) {
                societaPossedute++;
            }
        }

        // Regola standard: 4x il lancio per 1 società, 10x per entrambe
        int moltiplicatore = (societaPossedute >= 2) ? 10 : 4;
        return sommaDadi * moltiplicatore;
    }
}
