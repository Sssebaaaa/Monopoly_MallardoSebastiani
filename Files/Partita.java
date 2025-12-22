package Files;

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
        // I mazzi verranno creati da Davide, qui li istanzio vuoti o come richiesto
        this.mazzoImprevisti = new Mazzo(); 
        this.mazzoProbabilita = new Mazzo();
    }// Configura tutto prima di iniziare
    public void iniziaPartita() {
        // 1. Creazione Giocatori
        // Esempio: Creiamo 4 giocatori
        giocatori = new Giocatore[4];
        giocatori[0] = new Giocatore("Rosso", 1000); // Nome e budget iniziale
        giocatori[1] = new Giocatore("Blu", 1000);
        giocatori[2] = new Giocatore("Verde", 1000);
        giocatori[3] = new Giocatore("Giallo", 1000);
        
        giocatoriAttivi = 4;

        // 2. Creazione del Tabellone
        // Qui dovrai inserire le caselle create da Davide.
        // Esempio fittizio (questo pezzo lo completerai con le classi reali di Davide):
        // tabellone.setCasella(0, new Casella_speciale("Via", 200));
        // tabellone.setCasella(1, new Casella_terreno("Vicolo Corto", ...));
        System.out.println("Partita inizializzata. Il tabellone e i giocatori sono pronti.");
    }// Gestisce un singolo turno di un giocatore
    public void eseguiTurno(int indiceGiocatore) {
        Giocatore g = giocatori[indiceGiocatore];

        // Se il giocatore è null significa che è stato eliminato, quindi salto
        if (g == null) return;

        System.out.println("\n--- Turno di " + g.getNome() + " ---");

        // GESTIONE PRIGIONE
        // Se il giocatore è in prigione, controlliamo se può uscire
        if (g.isInPrigione()) {
            // Logica semplificata: prova a tirare i dadi per fare doppio
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
        }// MOVIMENTO NORMALE
        dadi.lancia();
        int passi = dadi.getTotale();
        
        // Calcolo dove finisce la pedina
        int posizioneAttuale = g.getPosizioneCorrente();
        int nuovaPosizione = tabellone.calcolaProssimaPosizione(posizioneAttuale, passi);
        
        // Muovo il giocatore (metodo di Davide)
        g.muovi(nuovaPosizione);
        
        // Recupero la casella su cui è atterrato
        Casella casellaArrivo = tabellone.getCasella(nuovaPosizione);
        System.out.println("Sei atterrato su: " + casellaArrivo.getNome());

        // Eseguo l'azione della casella (paga affitto, pesca carta, compra, ecc.)
        // Passo 'this' (l'oggetto Partita) perché alcune caselle devono accedere a mazzi o banca
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
    }// Rimuove un giocatore che ha finito i soldi
    public void rimuoviGiocatore(int indice) {
        Giocatore g = giocatori[indice];
        
        // Restituisco le proprietà alla banca (metodo ipotetico di Davide)
        // g.restituisciProprieta(banca);
        
        // Metto a null la posizione nell'array per indicare che non gioca più
        giocatori[indice] = null;
        giocatoriAttivi = giocatoriAttivi - 1;
    }
