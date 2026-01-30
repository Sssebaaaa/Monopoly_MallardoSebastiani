package Progetto.src;

public class Partita {
    private Tabellone tabellone;
    private Giocatore[] giocatori;
    private Dadi dadi;
    private Banca banca;
    private Mazzo mazzoImprevisti;
    private Mazzo mazzoProbabilita;
    private int giocatoriAttivi;

    private int turnoCorrente = 0;
    private Carta ultimaCartaPescata;
    private String ultimoTipoCarta;
    private boolean dadiLanciati = false;
    private boolean justExitedJail = false;

    public boolean isDadiLanciati() {
        return dadiLanciati;
    }

    public void setDadiLanciati(boolean value) {
        this.dadiLanciati = value;
    }

    public boolean isJustExitedJail() {
        return justExitedJail;
    }

    public Partita() {
        this.tabellone = new Tabellone();
        this.dadi = new Dadi();
        this.banca = new Banca();

        this.mazzoImprevisti = new Mazzo();
        this.mazzoProbabilita = new Mazzo();
    }

    public void iniziaPartita() {
        // Creiamo 4 giocatori
        giocatori = new Giocatore[4];
        giocatori[0] = new Giocatore("Giocatore 1", 1500);
        giocatori[1] = new Giocatore("Giocatore 2", 1500);
        giocatori[2] = new Giocatore("Giocatore 3", 1500);
        giocatori[3] = new Giocatore("Giocatore 4", 1500);

        giocatoriAttivi = 4;
        turnoCorrente = 0;
        log("Partita iniziata! Tocca al " + getGiocatoreCorrente().getNome());

        // Setup Tabellone
        setupTabellone();

        // Setup Mazzi
        setupMazzi();
    }

    private void setupTabellone() {
        for (int i = 0; i < tabellone.getDimensione(); i++) {
            switch (i) {
                case 0:
                    tabellone.setCasella(i, new Casella_speciale("Via", i, 0));
                    break;
                case 10:
                    tabellone.setCasella(i, new Casella_prigione("Prigione", i));
                    break;
                case 30:
                    tabellone.setCasella(i, new Casella_vaiPrigione("Vai in prigione", i));
                    break;
                case 2:
                case 17:
                case 33:
                    tabellone.setCasella(i, new Casella_carta("Probabilità", i, "Probabilita"));
                    break;
                case 7:
                case 22:
                case 36:
                    tabellone.setCasella(i, new Casella_carta("Imprevisti", i, "Imprevisti"));
                    break;
                case 5:
                case 15:
                case 25:
                case 35:
                    tabellone.setCasella(i, new Casella_stazione("Stazione", i, 200));
                    break;
                case 4:
                    tabellone.setCasella(i, new Casella_speciale("Tassa Patrimoniale", i, -200));
                    break;
                case 38:
                    tabellone.setCasella(i, new Casella_speciale("Tassa di Lusso", i, -100));
                    break;
                case 12:
                    tabellone.setCasella(i, new Casella_societa("Società Elettrica", i, 150));
                    break;
                case 28:
                    tabellone.setCasella(i, new Casella_societa("Società Acqua Potabile", i, 150));
                    break;
                default:
                    setupTerreno(i);
                    break;
            }
        }

        // Link caselle
        for (int i = 0; i < tabellone.getDimensione(); i++) {
            Casella c = tabellone.getCasella(i);
            if (c != null) {
                c.setNext(tabellone.getCasella((i + 1) % tabellone.getDimensione()));
                c.setPrev(tabellone.getCasella((i - 1 + tabellone.getDimensione()) % tabellone.getDimensione()));
            }
        }
    }

    private void setupTerreno(int i) {
        String colore = "";
        int prezzo = 0;
        int rendita = 0;
        int costoCasa = 50;

        switch (i) {
            case 1:
                colore = "Marrone";
                prezzo = 60;
                rendita = 2;
                break;
            case 3:
                colore = "Marrone";
                prezzo = 60;
                rendita = 4;
                break;
            case 6:
                colore = "Azzurro";
                prezzo = 100;
                rendita = 6;
                costoCasa = 50;
                break;
            case 8:
                colore = "Azzurro";
                prezzo = 100;
                rendita = 6;
                costoCasa = 50;
                break;
            case 9:
                colore = "Azzurro";
                prezzo = 120;
                rendita = 8;
                costoCasa = 50;
                break;
            case 11:
                colore = "Magenta";
                prezzo = 140;
                rendita = 10;
                costoCasa = 100;
                break;
            case 13:
                colore = "Magenta";
                prezzo = 140;
                rendita = 10;
                costoCasa = 100;
                break;
            case 14:
                colore = "Magenta";
                prezzo = 160;
                rendita = 12;
                costoCasa = 100;
                break;
            case 16:
                colore = "Arancione";
                prezzo = 180;
                rendita = 14;
                costoCasa = 100;
                break;
            case 18:
                colore = "Arancione";
                prezzo = 180;
                rendita = 14;
                costoCasa = 100;
                break;
            case 19:
                colore = "Arancione";
                prezzo = 200;
                rendita = 16;
                costoCasa = 100;
                break;
            case 21:
                colore = "Rosso";
                prezzo = 220;
                rendita = 18;
                costoCasa = 150;
                break;
            case 23:
                colore = "Rosso";
                prezzo = 220;
                rendita = 18;
                costoCasa = 150;
                break;
            case 24:
                colore = "Rosso";
                prezzo = 240;
                rendita = 20;
                costoCasa = 150;
                break;
            case 26:
                colore = "Giallo";
                prezzo = 260;
                rendita = 22;
                costoCasa = 150;
                break;
            case 27:
                colore = "Giallo";
                prezzo = 260;
                rendita = 22;
                costoCasa = 150;
                break;
            case 29:
                colore = "Giallo";
                prezzo = 280;
                rendita = 24;
                costoCasa = 150;
                break;
            case 31:
                colore = "Verde";
                prezzo = 300;
                rendita = 26;
                costoCasa = 200;
                break;
            case 32:
                colore = "Verde";
                prezzo = 300;
                rendita = 26;
                costoCasa = 200;
                break;
            case 34:
                colore = "Verde";
                prezzo = 320;
                rendita = 28;
                costoCasa = 200;
                break;
            case 37:
                colore = "Blu";
                prezzo = 350;
                rendita = 35;
                costoCasa = 200;
                break;
            case 39:
                colore = "Blu";
                prezzo = 400;
                rendita = 50;
                costoCasa = 200;
                break;
            default:
                colore = "Grigio";
                prezzo = 0;
                rendita = 0;
                break;
        }

        tabellone.setCasella(i, new Casella_terreno(getOfficialName(i), i, prezzo, rendita, costoCasa, colore));
    }

    private String getOfficialName(int i) {
        switch (i) {
            case 1:
                return "Vicolo Corto";
            case 3:
                return "Vicolo Stretto";
            case 6:
                return "Bastioni Gran Sasso";
            case 8:
                return "Viale Monterosa";
            case 9:
                return "Viale Vesuvio";
            case 11:
                return "Via Accademia";
            case 13:
                return "Corso Ateneo";
            case 14:
                return "Piazza Università";
            case 16:
                return "Via Verdi";
            case 18:
                return "Corso Raffaello";
            case 19:
                return "Piazza Dante";
            case 21:
                return "Via Marco Polo";
            case 23:
                return "Corso Magellano";
            case 24:
                return "Largo Colombo";
            case 26:
                return "Viale Costantino";
            case 27:
                return "Viale Traiano";
            case 29:
                return "Piazza Giulio Cesare";
            case 31:
                return "Via Roma";
            case 32:
                return "Corso Impero";
            case 34:
                return "Largo Augusto";
            case 37:
                return "Viale dei Giardini";
            case 39:
                return "Parco della Vittoria";
            default:
                return "Scemo chi legge  ";
        }
    }

    private void setupMazzi() {
        mazzoImprevisti = new Mazzo();
        mazzoImprevisti.aggiungiCarta(new Carta("Andate fino al VIA", 4, 0));
        mazzoImprevisti.aggiungiCarta(new Carta("Andate in Prigione senza passare dal VIA", 3, 0));
        mazzoImprevisti.aggiungiCarta(new Carta("Avete vinto un terno al lotto: ritirate 100€", 1, 100));
        mazzoImprevisti.aggiungiCarta(new Carta("Multa per eccesso di velocità: pagate 50€", 1, -50));
        mazzoImprevisti.aggiungiCarta(new Carta("Andate al Parco della Vittoria", 5, 39));
        mazzoImprevisti.aggiungiCarta(new Carta("Tornate indietro di 3 caselle", 2, -3));
        mazzoImprevisti.aggiungiCarta(new Carta("Matrimonio in famiglia: pagate 150€", 1, -150));
        mazzoImprevisti.mescola();

        mazzoProbabilita = new Mazzo();
        mazzoProbabilita.aggiungiCarta(new Carta("Errore bancario a vostro favore: ritirate 200€", 1, 200));
        mazzoProbabilita.aggiungiCarta(new Carta("Pagate la retta scolastica: 100€", 1, -100));
        mazzoProbabilita.aggiungiCarta(new Carta("Andate al Vicolo Corto", 5, 1));
        mazzoProbabilita.aggiungiCarta(
                new Carta("Avete vinto il secondo premio in un concorso di bellezza: ritirate 10€", 1, 10));
        mazzoProbabilita.aggiungiCarta(new Carta("Siete creditori verso la banca: ritirate 50€", 1, 50));
        mazzoProbabilita.aggiungiCarta(new Carta("Pagate il contributo di miglioria stradale: 100€", 1, -100));
        mazzoProbabilita.aggiungiCarta(new Carta("Andate in prigione!", 3, 0));
        mazzoProbabilita.mescola();
    }

    private void resetMoneyChanges() {
        for (Giocatore g : giocatori) {
            if (g != null)
                g.setUltimoCambioSoldi(0);
        }
    }

    public void pagaEsciPrigione() {
        Giocatore g = getGiocatoreCorrente();
        if (g != null && g.isInPrigione() && g.getSoldi() >= 500) {
            g.paga(500, null);
            g.esciDiPrigione();
            log(g.getNome() + " ha pagato 500€ ed è uscito di prigione.");
            justExitedJail = true;
        }
    }

    public void eseguiTurnoCorrente() {
        resetMoneyChanges();
        ultimaCartaPescata = null;
        ultimoTipoCarta = null;
        justExitedJail = false;
        Giocatore g = getGiocatoreCorrente();

        if (g == null) {
            passaTurno();
            return;
        }

        if (dadiLanciati) {
            log("Attenzione: dadi già lanciati per questo turno!");
            return;
        }

        int v1, v2, passi;

        if (g.isInPrigione()) {
            dadi.lancia();
            v1 = dadi.getValore1();
            v2 = dadi.getValore2();
            passi = v1 + v2;
            log("Lancio dadi per uscire di prigione: " + v1 + ", " + v2);
            // Richiesto "doppio 6" per uscire gratis
            if (v1 == 6 && v2 == 6) {
                g.esciDiPrigione();
                justExitedJail = true;
                log("Doppio 6! Sei uscito di prigione gratis.");
            } else {
                g.aumentaTurniPrigione();
                log("Niente doppio 6, resti in prigione.");
                dadiLanciati = true; // Segnamo comunque lanciati per sicurezza
                return;
            }
        } else {
            dadi.lancia();
            v1 = dadi.getValore1();
            v2 = dadi.getValore2();
            passi = v1 + v2;
        }

        dadiLanciati = true;
        log("Hai lanciato: " + v1 + " + " + v2 + " = " + passi);

        int posizioneAttuale = g.getPosizioneCorrente();
        int nuovaPosizione = tabellone.calcolaProssimaPosizione(posizioneAttuale, passi);

        g.muovi(passi);

        // Verifica passaggio dal VIA
        if (passi > 0 && nuovaPosizione < posizioneAttuale) {
            g.incassa(200);
            log(g.getNome() + " ha passato il VIA! Ritira 200€");
        }

        Casella casellaArrivo = tabellone.getCasella(nuovaPosizione);
        if (casellaArrivo != null) {
            log("Sei atterrato su: " + casellaArrivo.getNome());
            casellaArrivo.azione(g, this);
        }

        if (g.getSoldi() < 0) {
            log("GIOCATORE FALLITO! " + g.getNome() + " esce dal gioco.");
            rimuoviGiocatore(turnoCorrente);
        }
    }

    public void terminaTurno() {
        resetMoneyChanges();
        if (!dadi.isDoppio() || getGiocatoreCorrente().isInPrigione()) {
            passaTurno();
        }
        dadiLanciati = false;
        justExitedJail = false;
        ultimaCartaPescata = null;
        ultimoTipoCarta = null;
    }

    private void passaTurno() {
        dadiLanciati = false;
        justExitedJail = false;
        if (giocatoriAttivi > 0) {
            do {
                turnoCorrente = (turnoCorrente + 1) % 4;
            } while (giocatori[turnoCorrente] == null);
        }
    }

    public void rimuoviGiocatore(int indice) {
        Giocatore g = giocatori[indice];
        if (g != null) {
            g.rimuoviProprieta();
        }
        giocatori[indice] = null;
        giocatoriAttivi--;
        passaTurno();
    }

    public void clearLogs() {
    }

    public void log(String msg) {
        System.out.println(msg);
    }

    public Giocatore getGiocatoreCorrente() {
        return giocatori[turnoCorrente];
    }

    public int getIndiceGiocatoreCorrente() {
        return turnoCorrente;
    }

    public Giocatore[] getGiocatori() {
        return giocatori;
    }

    public Dadi getDadi() {
        return dadi;
    }

    public Tabellone getTabellone() {
        return tabellone;
    }

    public Banca getBanca() {
        return banca;
    }

    public Mazzo getMazzoImprevisti() {
        return mazzoImprevisti;
    }

    public Mazzo getMazzoProbabilita() {
        return mazzoProbabilita;
    }

    public int getGiocatoriAttivi() {
        return giocatoriAttivi;
    }

    public Carta getUltimaCartaPescata() {
        return ultimaCartaPescata;
    }

    public String getUltimoTipoCarta() {
        return ultimoTipoCarta;
    }

    public Carta pesca(String tipo) {
        Carta carta = null;
        if (tipo.equalsIgnoreCase("Imprevisti")) {
            carta = mazzoImprevisti.pesca();
        } else if (tipo.toLowerCase().startsWith("prob")) {
            carta = mazzoProbabilita.pesca();
        }
        ultimaCartaPescata = carta;
        ultimoTipoCarta = (carta != null) ? tipo : null;
        return carta;
    }

    // --- AZIONI DI GIOCO ---

    public void compraCasellaCorrente() {
        Giocatore g = getGiocatoreCorrente();
        if (g == null)
            return;

        Casella cell = tabellone.getCasella(g.getPosizioneCorrente());
        if (cell == null)
            return;

        banca.vendiTerreno(cell, g);
        log(g.getNome() + " ha acquistato " + cell.getNome());
    }

    public void costruisciCasaCellaCorrente() {
        Giocatore g = getGiocatoreCorrente();
        if (g == null)
            return;

        Casella cell = tabellone.getCasella(g.getPosizioneCorrente());
        if (!(cell instanceof Casella_terreno))
            return;

        Casella_terreno terreno = (Casella_terreno) cell;
        if (terreno.getProprietario() != g || !g.haSerieCompleta(terreno.getColore()))
            return;

        int costoCasa = terreno.getCostoCasa();
        if (g.getSoldi() < costoCasa) {
            log(g.getNome() + " non ha abbastanza soldi per costruire");
            return;
        }

        if (terreno.costruisciCasa()) {
            g.paga(costoCasa, null);
            log(g.getNome() + " ha costruito una casa su " + cell.getNome());
        }
    }
}
