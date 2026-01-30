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
     * Abilita/disabilita i cheat
     */
    public void toggleCheats() {
        cheatsAvailable = !cheatsAvailable;
        String stato = cheatsAvailable ? "ABILITATI" : "DISABILITATI";
        System.out.println("[CHEAT] Cheat " + stato);
    }

    /**
     * Verifica se i cheat sono abilitati
     */
    public boolean isCheatsAvailable() {
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
            System.err.println("[CHEAT] Cheat non disponibile!");
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
        partita.log("[CHEAT] " + giocatore.getNome() + " è stato teleportato alla casella " + posizioneDestinazione);

        // Setta il flag dadiLanciati per permettere acquisti/costruzioni
        partita.setDadiLanciati(true);

        // Esegue l'azione della casella su cui è atterrato
        Casella casellaArrivo = partita.getTabellone().getCasella(posizioneDestinazione);
        if (casellaArrivo != null) {
            partita.log("[CHEAT] Azione della casella '" + casellaArrivo.getNome() + "' in corso...");
            casellaArrivo.azione(giocatore, partita);
        }

        // Controlla se il giocatore è fallito
        if (giocatore.getSoldi() < 0) {
            partita.log("[CHEAT] GIOCATORE FALLITO! " + giocatore.getNome() + " esce dal gioco.");
            partita.rimuoviGiocatore(partita.getIndiceGiocatoreCorrente());
        }
    }

    /**
     * Aggiunge 200€ al giocatore corrente
     */
    public void bonus200Euro() {
        if (!cheatsAvailable) {
            System.err.println("[CHEAT] Cheat non disponibile!");
            return;
        }

        Giocatore giocatore = partita.getGiocatoreCorrente();
        if (giocatore == null) {
            System.err.println("[CHEAT] Nessun giocatore attivo");
            return;
        }

        giocatore.incassa(200);
        partita.log("[CHEAT] " + giocatore.getNome() + " ha ricevuto 200€ di bonus!");
    }
}
