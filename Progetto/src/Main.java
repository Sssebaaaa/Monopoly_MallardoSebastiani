package Progetto.src;

public class Main {
    public static void main(String[] args) {
        // Creo l'oggetto partita
        Partita partita = new Partita();
        
        // Preparo tabellone e giocatori
        partita.iniziaPartita();

        int turnoGiocatore = 0;

        // CICLO DEL GIOCO
        // Continua finché c'è più di un giocatore (cioè finché non vince qualcuno)
        while (partita.getGiocatoriAttivi() > 1) {
            
            // Esegue il turno per il giocatore corrente
            partita.eseguiTurno(turnoGiocatore);

            // Passa al prossimo giocatore
            turnoGiocatore++;
            
            // Se supero l'ultimo indice dell'array, ricomincio da 0 (giro circolare)
            if (turnoGiocatore >= 4) { // Assumendo 4 giocatori fissi come nell'esempio
                turnoGiocatore = 0;
            }
        }

        System.out.println("LA PARTITA È FINITA! ABBIAMO UN VINCITORE.");
    }
}
