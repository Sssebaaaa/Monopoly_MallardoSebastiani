package Progetto.src;

public class Partita {
    // Componenti principali del gioco
    private Tabellone tabellone;
    private Giocatore[] giocatori; // Array dei partecipanti
    private Dadi dadi;
    private Banca banca;
    private Mazzo mazzoImprevisti;
    private Mazzo mazzoProbabilita;// Variabile per contare quanti giocatori sono ancora in gara
    private int giocatoriAttivi;

    public Partita() {
        this.tabellone = new Tabellone();
        this.dadi = new Dadi();
        this.banca = new Banca();
        // Mazzi vuoti per ora
        this.mazzoImprevisti = new Mazzo(); 
        this.mazzoProbabilita = new Mazzo();
    }
    // Preparazione iniziale della partita
    public void iniziaPartita() {
        // 1. Creazione Giocatori
        // Esempio: Creiamo 4 giocatori
        giocatori = new Giocatore[4];
        giocatori[0] = new Giocatore("Rosso", 1000); // Nome e budget iniziale
        giocatori[1] = new Giocatore("Blu", 1000);
        giocatori[2] = new Giocatore("Verde", 1000);
        giocatori[3] = new Giocatore("Giallo", 1000);
        
        giocatoriAttivi = 4;

        // 2. Creazione del tabellone
        // Inserire qui le caselle (es. Casella_speciale, Casella_terreno...)
        System.out.println("Partita inizializzata. Il tabellone e i giocatori sono pronti.");
    }// Gestisce un singolo turno di un giocatore
    public void eseguiTurno(int indiceGiocatore) {
        Giocatore g = giocatori[indiceGiocatore];

    // Se il giocatore è null è stato eliminato, salto
        if (g == null) return;

        System.out.println("\n--- Turno di " + g.getNome() + " ---");

        // Gestione prigione: se è in prigione prova a uscire
        if (g.isInPrigione()) {
            // Semplice: tira i dadi per provare il doppio
            dadi.lancia();
            if (dadi.isDoppio()) {
                g.esciDiPrigione();
                System.out.println("Doppio! Sei uscito di prigione.");
            } else {
                g.aumentaTurniPrigione();
                // Se è al 3° turno deve pagare per forza (logica da implementare in Giocatore)
                System.out.println("Niente doppio, resti in prigione.");
                return; // Finisce il turno qui se non esce
            }
    }
    // Movimento normale
        dadi.lancia();
        int passi = dadi.getTotale();
        
        // Calcolo dove finisce la pedina
        int posizioneAttuale = g.getPosizioneCorrente();
        int nuovaPosizione = tabellone.calcolaProssimaPosizione(posizioneAttuale, passi);
        
    // Muovo il giocatore
        g.muovi(nuovaPosizione);
        
        // Recupero la casella su cui è atterrato
        Casella casellaArrivo = tabellone.getCasella(nuovaPosizione);
        System.out.println("Sei atterrato su: " + casellaArrivo.getNome());

    // Esegui l'azione della casella (affitto, pesca carta, ecc.)
    // Passo 'this' in caso la casella debba accedere a mazzi o banca
        casellaArrivo.azione(g, this);// CONTROLLO FALLIMENTO
        if (g.getSoldi() < 0) {
            System.out.println("GIOCATORE FALLITO! " + g.getNome() + " esce dal gioco.");
            rimuoviGiocatore(indiceGiocatore);
        } else {
            // GESTIONE DOPPI (Rigioca)
            // Se ha fatto doppio e non è finito in prigione in questo turno
            if (dadi.isDoppio() && !g.isInPrigione()) {
                System.out.println("Hai fatto doppio! Tiri di nuovo.");
                eseguiTurno(indiceGiocatore); // Chiamata ricorsiva per rigiocare subito
            }
        }
    }
    // Rimuove un giocatore che ha finito i soldi
    public void rimuoviGiocatore(int indice) {
        Giocatore g = giocatori[indice];
            if (g != null) {
                // Rimuovo le proprietà possedute dal giocatore e le resetto (metodo presente in Giocatore)
                g.rimuoviProprieta();
            }

            // Metto a null la posizione nell'array per indicare che non gioca più
            giocatori[indice] = null;
            giocatoriAttivi = giocatoriAttivi - 1;
    }// Metodo utile per il Main per sapere se continuare il ciclo
    public int getGiocatoriAttivi() {
        return giocatoriAttivi;
    }

    // Getter per permettere alle caselle (es. Probabilità) di accedere ai mazzi
    public Mazzo getMazzoImprevisti() { return mazzoImprevisti; }
    public Mazzo getMazzoProbabilita() { return mazzoProbabilita; }
    public Banca getBanca() { return banca; }
    public Tabellone getTabellone() { return tabellone; }

    // Restituisce una carta dal mazzo richiesto: "Imprevisti" o "Probabilita"
    public Carta pesca(String tipo) {
        if (tipo == null) return null;
        if (tipo.equalsIgnoreCase("Imprevisti")) {
            return mazzoImprevisti.pesca();
        } else if (tipo.equalsIgnoreCase("Probabilita") || tipo.equalsIgnoreCase("Probabilità")) {
            return mazzoProbabilita.pesca();
        } else {
            return null;
        }
    }
}
