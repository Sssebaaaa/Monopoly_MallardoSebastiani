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

        // 2. Creazione del tabellone: riempiamo 40 caselle
        for (int i = 0; i < tabellone.getDimensione(); i++) {
            switch (i) {
                case 0:
                    tabellone.setCasella(i, new Casella_speciale("Via", i, 200));
                    break;
                case 10:
                    tabellone.setCasella(i, new Casella_prigione("Prigione", i));
                    break;
                case 30:
                    tabellone.setCasella(i, new Casella_vaiPrigione("Vai in prigione", i));
                    break;
                case 2:
                case 7:
                case 17:
                case 33:
                    tabellone.setCasella(i, new Casella_carta("Imprevisti", i, "Imprevisti"));
                    break;
                case 5:
                case 15:
                case 25:
                case 35:
                    tabellone.setCasella(i, new Casella_stazione("Stazione", i, 200));
                    break;
                default:
                    String colore;
                    int prezzo;
                    int rendita;
                    int costoCasa = 150;
                    if (i % 3 == 0) { colore = "Azzurro"; prezzo = 100; rendita = 10; }
                    else if (i % 3 == 1) { colore = "Arancione"; prezzo = 200; rendita = 20; }
                    else { colore = "Verde"; prezzo = 300; rendita = 30; }
                    tabellone.setCasella(i, new Casella_terreno("Terreno " + i, i, prezzo, rendita, costoCasa, colore));
                    break;
            }
        }

        // Colleghiamo next e prev per ogni casella (ciclo circolare)
        for (int i = 0; i < tabellone.getDimensione(); i++) {
            Casella c = tabellone.getCasella(i);
            Casella next = tabellone.getCasella((i + 1) % tabellone.getDimensione());
            Casella prev = tabellone.getCasella((i - 1 + tabellone.getDimensione()) % tabellone.getDimensione());
            if (c != null) {
                c.setNext(next);
                c.setPrev(prev);
            }
        }

        // 3. Creazione del mazzo Imprevisti
        mazzoImprevisti = new Mazzo();
        mazzoImprevisti.aggiungiCarta(new Carta("Hai trovato 100€ per strada", 1, 100));
        mazzoImprevisti.aggiungiCarta(new Carta("Paghi 50€ di multa", 1, -50));
        mazzoImprevisti.aggiungiCarta(new Carta("Avanzi di 3 caselle", 2, 3));
        mazzoImprevisti.aggiungiCarta(new Carta("Torna al VIA", 4, 0));
        mazzoImprevisti.aggiungiCarta(new Carta("Vai in prigione", 3, 0));
        mazzoImprevisti.aggiungiCarta(new Carta("Ricevi 200€ dalla banca", 1, 200));
        mazzoImprevisti.mescola();

        System.out.println("Partita inizializzata. Tabellone e mazzo imprevisti pronti.");
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
