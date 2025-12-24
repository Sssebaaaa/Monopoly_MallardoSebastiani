package Progetto.src;

public class Main {
    public static void main(String[] args) {
    // Crea la partita
        Partita partita = new Partita();
        
    // Prepara tabellone e giocatori
        partita.iniziaPartita();

        int turnoGiocatore = 0;

    // Ciclo principale del gioco: prosegue finché rimane più di un giocatore
        while (partita.getGiocatoriAttivi() > 1) {
            
            // Esegue il turno del giocatore corrente
            partita.eseguiTurno(turnoGiocatore);

            // Prossimo giocatore
            turnoGiocatore++;
            
            // Se supero l'ultimo indice dell'array, ricomincio da 0 (giro circolare)
            if (turnoGiocatore >= 4) { // Assumendo 4 giocatori fissi come nell'esempio
                turnoGiocatore = 0;
            }
        }

        System.out.println("LA PARTITA È FINITA! ABBIAMO UN VINCITORE.");
    }
}
