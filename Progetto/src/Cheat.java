package Progetto.src;

/**
 * Classe Cheat - Gestisce i cheat della partita
 * Permette ai giocatori di:
 * - Teleportarsi in una casella specifica
 * - Ottenere 200€ bonus
 * 
 * I cheat sono abilitabili da un pulsante nella sidebar
 */
public class Cheat {
    private Partita partita;
    private boolean cheatsAvailable = false;

    public Cheat(Partita partita) {
        this.partita = partita;
    }

    /**
     * Abilita/disabilita i trucchi
     */
    public void invertiTrucchi() {
        cheatsAvailable = !cheatsAvailable;
        String stato = cheatsAvailable ? "ABILITATI" : "DISABILITATI";
        System.out.println("[TRUCCO] Trucchi " + stato);
    }

    /**
     * Verifica se i trucchi sono abilitati
     */
    public boolean trucchiAttivi() {
        return cheatsAvailable;
    }

    /**
     * Permette al giocatore corrente di teleportarsi in una casella specifica
     * Una volta atterrato, viene eseguita l'azione della casella
     * 
     * @param posizioneDestinazione - Indice della casella (0-39) - POSIZIONE ASSOLUTA
     */
    public void vaiAllaCasella(int posizioneDestinazione) {
        if (!cheatsAvailable) {
            System.err.println("[TRUCCO] Trucchetto non disponibile!");
            return;
        }

        // Validazione della posizione
        if (posizioneDestinazione < 0 || posizioneDestinazione > 39) {
            System.err.println("[CHEAT] Posizione non valida: " + posizioneDestinazione);
            return;
        }

        Giocatore giocatore = partita.getGiocatoreCorrente();
        if (giocatore == null) {
            System.err.println("[CHEAT] Nessun giocatore attivo");
            return;
        }

        // Sposta il giocatore nella nuova posizione (ASSOLUTA, non relativa)
        // NON incassa il VIA perché il teletrasporto non passa per le caselle
        giocatore.setPosizioneCorrente(posizioneDestinazione);
        partita.registra("[TRUCCO] " + giocatore.getNome() + " è stato teletrasportato alla casella " + posizioneDestinazione);

        // Setta il flag dadiLanciati per permettere acquisti/costruzioni
        partita.setDadiLanciati(true);

        // Esegue l'azione della casella su cui è atterrato
        Casella casellaArrivo = partita.getTabellone().getCasella(posizioneDestinazione);
        if (casellaArrivo != null) {
            partita.registra("[TRUCCO] Azione della casella '" + casellaArrivo.getNome() + "' in corso...");
            casellaArrivo.azione(giocatore, partita);
        }

        // Controlla se il giocatore è fallito
        if (giocatore.getSoldi() < 0) {
            partita.registra("[TRUCCO] GIOCATORE FALLITO! " + giocatore.getNome() + " esce dal gioco.");
            partita.rimuoviGiocatore(partita.getIndiceGiocatoreCorrente());
        }
    }

    /**
     * Aggiunge 200€ al giocatore corrente
     */
    public void bonus200Euro() {
        if (!cheatsAvailable) {
            System.err.println("[TRUCCO] Trucchetto non disponibile!");
            return;
        }

        Giocatore giocatore = partita.getGiocatoreCorrente();
        if (giocatore == null) {
            System.err.println("[CHEAT] Nessun giocatore attivo");
            return;
        }

        giocatore.incassa(200);
        partita.registra("[TRUCCO] " + giocatore.getNome() + " ha ricevuto 200€ di bonus!");
    }

    /**
     * Forza la bancarotta di un giocatore specifico
     * 
     * @param playerIndex Indice del giocatore da mandare in bancarotta
     */
    public void mandaInBancarotta(int playerIndex) {
        if (!cheatsAvailable) {
            System.err.println("[TRUCCO] Trucchetto non disponibile!");
            return;
        }

        Giocatore[] giocatori = partita.getGiocatori();
        if (playerIndex < 0 || playerIndex >= giocatori.length || giocatori[playerIndex] == null) {
            System.err.println("[CHEAT] Indice giocatore non valido: " + playerIndex);
            return;
        }

        Giocatore g = giocatori[playerIndex];
        partita.registra("[TRUCCO] BANCAROTTA FORZATA per " + g.getNome());
        
        // Imposta i soldi a un valore negativo per attivare la logica di fallimento se necessario,
        // ma chiamiamo direttamente rimuoviGiocatore per immediatezza.
        g.setSoldi(-1);
        partita.rimuoviGiocatore(playerIndex);
    }
}
