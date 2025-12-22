package Files;
public class Tabellone {
    // Array fisso di 40 caselle (dimensione standard del Monopoly)
    private Casella[] caselle;

    public Tabellone() {
        this.caselle = new Casella[40];
    }