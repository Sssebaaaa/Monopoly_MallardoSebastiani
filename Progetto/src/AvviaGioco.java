package Progetto.src;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class AvviaGioco {

    private static Partita partita = new Partita();
    private static Cheat cheat = new Cheat(partita);
    private static boolean gameStarted = false;

    public static void main(String[] args) throws IOException {
        int port = 3000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", new GameHandler());
        server.setExecutor(null);
        server.start();

        System.out.println("Server avviato su http://localhost:" + port);
        apriBrowser(port);
    }

    // SEZIONE: SERVER
    // - Creazione del server HTTP
    // - Dispatcher delle richieste e routing
    // - GameHandler: riceve richieste, coordina azioni e rendering

    static class GameHandler implements HttpHandler {

        // HANDLER: metodo principale che gestisce ogni richiesta HTTP
        // - Estrae path e parametri
        // - Esegue azioni di gioco quando richiesto
        // - Renderizza o serve file statici
        @Override
        public void handle(HttpExchange t) throws IOException {
            try {
                // Estrae il percorso e i parametri dalla richiesta
                String path = t.getRequestURI().getPath();
                String query = t.getRequestURI().getQuery();
                Map<String, String> params = elaboraQuery(query);

                // Se è la radice, esegui azioni del gioco e mostra pagina
                if (path.equals("/")) {
                    gestisciAzioni(params);
                    String pagina = renderizzaPagina();
                    inviHtml(t, pagina);
                }
                // Altrimenti, serve un file statico (CSS, JS, immagini)
                else {
                    serviFile(t, path);
                }
            } catch (Exception e) {
                e.printStackTrace();
                t.sendResponseHeaders(500, 0);
                t.close();
            }
        }

        // GESTIONE AZIONI DI GIOCO
        private void gestisciAzioni(Map<String, String> params) {
            // mappa le azioni ricevute via query string alle operazioni di Partita
            String action = params.get("action");

            // Resetta gli stati delle animazioni dei soldi all'inizio di ogni azione
            partita.azzeraVariazioni();

            if (action == null) {
                return;
            }

            switch (action) {
                case "newgame":
                    partita.iniziaPartita();
                    gameStarted = true;
                    break;

                case "roll":
                    if (gameStarted) {
                        partita.eseguiTurnoCorrente();
                    }
                    break;

                case "buy":
                    if (gameStarted && partita.isDadiLanciati()) {
                        partita.compraCasellaCorrente();
                    }
                    break;

                case "build":
                    if (gameStarted && partita.isDadiLanciati()) {
                        partita.costruisciCasaCellaCorrente();
                    }
                    break;

                case "endTurn":
                    if (gameStarted) {
                        partita.terminaTurno();
                    }
                    break;

                case "payJail":
                    if (gameStarted) {
                        partita.pagaEsciPrigione();
                    }
                    break;

                case "cheat_vaiallacasella":
                    if (gameStarted && cheat.trucchiAttivi()) {
                        String casella = params.get("casella");
                        if (casella != null && !casella.isEmpty()) {
                            try {
                                int posizioneDestinazione = Integer.parseInt(casella);
                                cheat.vaiAllaCasella(posizioneDestinazione);
                            } catch (NumberFormatException e) {
                                System.err.println("[TRUCCO] Posizione non valida: " + casella);
                            }
                        }
                    }
                    break;

                case "cheat_bonus200":
                    if (gameStarted && cheat.trucchiAttivi()) {
                        cheat.bonus200Euro();
                    }
                    break;

                case "cheat_bankrupt":
                    if (gameStarted && cheat.trucchiAttivi()) {
                        String playerIndexStr = params.get("playerIndex");
                        if (playerIndexStr != null && !playerIndexStr.isEmpty()) {
                            try {
                                int playerIndex = Integer.parseInt(playerIndexStr);
                                cheat.mandaInBancarotta(playerIndex);
                            } catch (NumberFormatException e) {
                                System.err.println("[TRUCCO] Indice giocatore non valido: " + playerIndexStr);
                            }
                        }
                    }
                    break;

                case "toggle_cheat":
                    cheat.invertiTrucchi();
                    break;
            }
        }

        private void inviHtml(HttpExchange t, String html) throws IOException {
            // converte il testo HTML in array di byte
            byte[] bytes = html.getBytes("UTF-8");

            // setta l'intestazione della risposta specificando il tipo di contenuto e la
            // invia
            t.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            t.sendResponseHeaders(200, bytes.length);

            // ottiene lo stream di output dove scrivere i dati
            OutputStream os = t.getResponseBody();

            // scrive i byte dell'HTML nello stream e poi lo chiude
            os.write(bytes);
            os.close();

            // chiude la connessione HTTP con il browser
            t.close();
        }

        // - Cerca risorse e invia il file con il content-type corretto
        private void serviFile(HttpExchange t, String path) throws IOException {
            File webRoot = trovaRadiceWeb();

            String filePath = path;
            if (filePath.startsWith("/")) {
                filePath = filePath.substring(1);
            }

            File file = new File(webRoot, filePath);

            if (file.exists() && !file.isDirectory()) {
                String contentType = tipoContenuto(path);
                t.getResponseHeaders().set("Content-Type", contentType);
                t.sendResponseHeaders(200, file.length());

                OutputStream os = t.getResponseBody();
                Files.copy(file.toPath(), os);
                os.close();
            } else {
                String error = "404 Not Found";
                t.sendResponseHeaders(404, error.length());

                OutputStream os = t.getResponseBody();
                os.write(error.getBytes());
                os.close();
            }

            t.close();
        }

        // controlla diversi percorsi comuni per trovare la directory `web`
        private File trovaRadiceWeb() {
            String[] percorsiPossibili = { "web", "Progetto/src/web", "src/web" };

            for (String percorso : percorsiPossibili) {
                File directory = new File(percorso);
                if (directory.exists()) {
                    return directory;
                }
            }

            return new File(".");
        }

        private String tipoContenuto(String path) {
            if (path.endsWith(".css")) {
                return "text/css";
            }
            if (path.endsWith(".js")) {
                return "application/javascript";
            }
            if (path.endsWith(".png")) {
                return "image/png";
            }
            if (path.endsWith(".jpg")) {
                return "image/jpeg";
            }

            return "text/plain";
        }

        // converte la query URL in una mappa chiave->valore
        private Map<String, String> elaboraQuery(String query) {
            Map<String, String> params = new HashMap<>();

            if (query == null) {
                return params;
            }

            String[] coppie = query.split("&");

            for (String coppia : coppie) {
                String[] keyValue = coppia.split("=");

                String chiave = keyValue[0];
                String valore = "";

                if (keyValue.length > 1) {
                    valore = keyValue[1];
                }

                params.put(chiave, valore);
            }

            return params;
        }
    }

    // SEZIONE: GENERAZIONE HTML
    // - Funzioni che costruiscono l'intera pagina HTML lato server
    // - Suddivise in: menu, tabellone, area centrale, sidebar, card di azione

    private static String renderizzaPagina() {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html lang='it'>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<title>Monopoly Web</title>");
        html.append("<link rel='stylesheet' href='styles.css'>");
        html.append(
                "<link href='https://fonts.googleapis.com/css2?family=Outfit:wght@300;500;700;900&display=swap' rel='stylesheet'>");
        html.append(
                "<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css'>");
        html.append("</head>");

        html.append("<body>");
        html.append("<div class='app-container'>");

        if (!gameStarted) {
            String menu = renderizzaMenuPrincipale();
            html.append(menu);
        }

        // Se c'è un vincitore, mostra la schermata di vittoria
        Giocatore vincitore = partita.getVincitore();
        if (vincitore != null) {
            String vittoria = renderizzaSchermataVittoria(vincitore);
            html.append(vittoria);
        }

        html.append("<div class='game-board-wrapper'>");
        String tabellone = renderizzaTabellone();
        html.append(tabellone);
        html.append("</div>");

        String sidebar = renderizzaBarra();
        html.append(sidebar);

        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    // Menu principale
    private static String renderizzaMenuPrincipale() {
        StringBuilder menu = new StringBuilder();

        menu.append("<div id='main-menu' class='modal-overlay'>");
        menu.append("<div class='menu-content'>");
        menu.append("<h1 class='game-title'>MONOPOLY</h1>");
        menu.append("<p class='subtitle'>di Sebastiani e Mallardo</p>");

        menu.append("<div class='player-previews'>");
        menu.append(
                "<div class='player-preview' style='color: #ff4757'><i class='fa-solid fa-car'></i><span>G1</span></div>");
        menu.append(
                "<div class='player-preview' style='color: #2ed573'><i class='fa-solid fa-ship'></i><span>G2</span></div>");
        menu.append(
                "<div class='player-preview' style='color: #1e90ff'><i class='fa-solid fa-plane'></i><span>G3</span></div>");
        menu.append(
                "<div class='player-preview' style='color: #ffa502'><i class='fa-solid fa-dog'></i><span>G4</span></div>");
        menu.append("</div>");

        menu.append("<div class='menu-actions'>");
        menu.append(
                "<a href='/?action=newgame' class='menu-btn primary'><i class='fa-solid fa-play'></i> NUOVA PARTITA</a>");
        menu.append("</div>");

        menu.append("</div>");
        menu.append("</div>");

        return menu.toString();
    }

    // Schermata di vittoria
    private static String renderizzaSchermataVittoria(Giocatore vincitore) {
        StringBuilder vittoria = new StringBuilder();

        // Trova l'indice del vincitore per il colore e l'icona
        int indiceVincitore = -1;
        Giocatore[] giocatori = partita.getGiocatori();
        for (int i = 0; i < giocatori.length; i++) {
            if (giocatori[i] == vincitore) {
                indiceVincitore = i;
                break;
            }
        }

        String coloreVincitore = coloreGiocatore(indiceVincitore);
        String iconaVincitore = iconaGiocatore(indiceVincitore);

        vittoria.append("<div id='victory-screen' class='modal-overlay victory'>");
        vittoria.append("<div class='victory-content'>");

        vittoria.append("<div class='confetti-container'></div>");

        vittoria.append("<div class='trophy-icon'><i class='fa-solid fa-trophy'></i></div>");
        vittoria.append("<h1 class='victory-title'>VITTORIA!</h1>");

        vittoria.append("<div class='winner-announce'>");
        vittoria.append("<div class='winner-avatar' style='color:").append(coloreVincitore).append("'>");
        vittoria.append(iconaVincitore);
        vittoria.append("</div>");
        vittoria.append("<div class='winner-name'>").append(vincitore.getNome()).append("</div>");
        vittoria.append("<p class='winner-msg'>Ha dominato il mercato!</p>");
        vittoria.append("</div>");

        vittoria.append("<div class='victory-stats'>");
        vittoria.append("<div class='stat-item'>");
        vittoria.append("<span class='stat-label'>Patrimonio Finale</span>");
        vittoria.append("<span class='stat-value'>€ ").append(vincitore.getSoldi()).append("</span>");
        vittoria.append("</div>");
        vittoria.append("</div>");

        vittoria.append("<div class='menu-actions'>");
        vittoria.append(
                "<a href='/?action=newgame' class='menu-btn primary victory-btn'><i class='fa-solid fa-rotate-left'></i> GIOCA ANCORA</a>");
        vittoria.append("</div>");

        vittoria.append("</div>");
        vittoria.append("</div>");

        return vittoria.toString();
    }

    // Tabellone di gioco (griglia con 40 caselle)
    private static String renderizzaTabellone() {
        StringBuilder board = new StringBuilder();

        board.append("<div id='board' class='board'>");

        // Renderizza tutte le 40 caselle
        for (int i = 0; i < 40; i++) {
            String cella = renderizzaCasella(i);
            board.append(cella);
        }

        // Area centrale con dadi e pulsanti
        board.append("<div class='center-area'>");
        board.append("<div class='brand'>MONOPOLY</div>");

        String areaDadi = renderizzaAreaDadi();
        board.append(areaDadi);

        String pulsanti = renderizzaPulsantiAzione();
        board.append(pulsanti);

        String messaggio = renderizzaMessaggioStato();
        board.append(messaggio);

        board.append("</div>");
        board.append("</div>");

        return board.toString();
    }

    // Singola casella - determina nome, colori, icone speciali, case/hotel e
    // gettoni presenti
    private static String renderizzaCasella(int indice) {
        StringBuilder cella = new StringBuilder();

        Casella casellaObj = partita.getTabellone().getCasella(indice);
        String nome = "";
        if (casellaObj != null) {
            nome = casellaObj.getNome();
        }

        String lato = latoCella(indice);
        String classi = "cell " + lato;

        if (èAngolo(indice)) {
            classi = classi + " corner";
        }
        if (èSpeciale(indice)) {
            classi = classi + " special";
        }

        String posizione = posizioneGriglia(indice);

        cella.append("<div class='").append(classi).append("' style='").append(posizione).append("'>");

        // Barra colore della proprietà
        String colore = coloreCella(indice);
        if (colore != null && !èAngolo(indice) && !èSpeciale(indice)) {
            cella.append("<div class='colorbar ").append(colore).append("'></div>");
        }

        cella.append("<div class='name'>").append(nome).append("</div>");

        // Icona speciale
        String icona = iconaValoreSpeciale(indice);
        if (icona != null) {
            cella.append("<div class='special-icon'>").append(icona).append("</div>");
        }

        // Mostra case e hotel
        if (casellaObj instanceof Casella_terreno) {
            Casella_terreno terreno = (Casella_terreno) casellaObj;
            int numeroCase = terreno.getNumeroCase();
            if (numeroCase > 0) {
                String case_hotel = renderizzaCaseHotel(numeroCase);
                cella.append(case_hotel);
            }
        }

        // Mostra i gettoni dei giocatori
        if (gameStarted) {
            String gettoni = renderizzaGettoniSuCasella(indice);
            cella.append(gettoni);
        }

        cella.append("</div>");

        return cella.toString();
    }

    // Area centrale - dadi
    private static String renderizzaAreaDadi() {
        StringBuilder dadi = new StringBuilder();

        int dado1 = partita.getDadi().getValore1();
        if (dado1 < 1) {
            dado1 = 1;
        }

        int dado2 = partita.getDadi().getValore2();
        if (dado2 < 1) {
            dado2 = 1;
        }

        String facce = "<div class='cube__face cube__face--1'>1</div>";
        facce = facce + "<div class='cube__face cube__face--2'>2</div>";
        facce = facce + "<div class='cube__face cube__face--3'>3</div>";
        facce = facce + "<div class='cube__face cube__face--4'>4</div>";
        facce = facce + "<div class='cube__face cube__face--5'>5</div>";
        facce = facce + "<div class='cube__face cube__face--6'>6</div>";

        dadi.append("<div class='dice-area'>");
        dadi.append("<div class='scene'><div class='cube show-").append(dado1).append("'>").append(facce)
                .append("</div></div>");
        dadi.append("<div class='scene'><div class='cube show-").append(dado2).append("'>").append(facce)
                .append("</div></div>");
        dadi.append("</div>");

        return dadi.toString();
    }

    // Pulsanti di azione (lancia, fine turno)
    private static String renderizzaPulsantiAzione() {
        StringBuilder pulsanti = new StringBuilder();

        if (!gameStarted || partita.getVincitore() != null) {
            pulsanti.append("<button class='action-btn' disabled>GIOCO TERMINATO</button>");
            return pulsanti.toString();
        }

        boolean dadiLanciati = partita.isDadiLanciati();

        if (!dadiLanciati) {
            boolean isDoppio = partita.getDadi().isDoppio();
            Giocatore giocatoreCorrente = partita.getGiocatoreCorrente();
            boolean inPrigione = giocatoreCorrente.isInPrigione();

            String testo = "LANCIA DADI";
            if (isDoppio && !inPrigione) {
                testo = "DOPPIO! RITIRA";
            }

            pulsanti.append("<a href='/?action=roll' class='action-btn'>").append(testo).append("</a>");
        } else {
            pulsanti.append("<a href='/?action=endTurn' class='action-btn secondary'>");
            pulsanti.append("<i class='fa-solid fa-check'></i> FINE TURNO");
            pulsanti.append("</a>");
        }

        return pulsanti.toString();
    }

    // Messaggi
    private static String renderizzaMessaggioStato() {
        StringBuilder messaggio = new StringBuilder();

        String testo = "";
        if (gameStarted) {
            Giocatore vincitore = partita.getVincitore();
            if (vincitore != null) {
                testo = "Partita terminata! Vince <b>" + vincitore.getNome() + "</b>";
            } else {
                int indiceGiocatore = partita.getIndiceGiocatoreCorrente();
                String coloreG = coloreGiocatore(indiceGiocatore);
                Giocatore g = partita.getGiocatoreCorrente();
                String nomeGiocatore = (g != null) ? g.getNome() : "---";

                testo = "Turno di <span style='color:" + coloreG + "'>" + nomeGiocatore + "</span>";
            }
        } else {
            testo = "In attesa...";
        }

        messaggio.append("<div class='info-area'>");
        messaggio.append("<div class='status-msg'>").append(testo).append("</div>");
        messaggio.append("</div>");

        return messaggio.toString();
    }

    // Sidebar
    private static String renderizzaBarra() {
        StringBuilder sidebar = new StringBuilder();

        sidebar.append("<div class='sidebar'>");

        sidebar.append("<div class='sidebar-header'>");
        sidebar.append("<h2>MONOPOLY</h2>");
        String cheatButtonStyle = cheat.trucchiAttivi() ? "cheat-btn-active" : "";
        sidebar.append("<a href='/?action=toggle_cheat' class='cheat-toggle-btn ").append(cheatButtonStyle).append("'>");
        sidebar.append("<i class='fa-solid fa-wand-magic-sparkles'></i>");
        sidebar.append("</a>");
        sidebar.append("</div>");

        sidebar.append("<div class='sidebar-content'>");

        String statsGiocatori = renderizzaStatisticheGiocatori();
        sidebar.append(statsGiocatori);

        if (gameStarted) {
            String cheatCard = renderizzaCardCheat();
            sidebar.append(cheatCard);

            // Se non c'è un vincitore, mostra le azioni di contesto
            if (partita.getVincitore() == null) {
                String azioni = renderizzaAzioniContextoCorrente();
                sidebar.append(azioni);
            }
        }

        sidebar.append("</div>");
        sidebar.append("</div>");

        return sidebar.toString();
    }

    // CARD: Cheat Menu
    private static String renderizzaCardCheat() {
        StringBuilder card = new StringBuilder();

        if (!cheat.trucchiAttivi()) {
            return "";
        }

        card.append("<div class='action-card cheat-style'>");
        card.append("<div class='card-header'>");
        card.append("<i class='fa-solid fa-wand-magic-sparkles'></i> CHEAT MENU");
        card.append("</div>");
        card.append("<div class='card-body'>");

        // Sezione "Vai alla Casella"
        card.append("<div class='cheat-action'>");
        card.append("<label for='casella-input' class='cheat-label'>");
        card.append("<i class='fa-solid fa-location-arrow'></i> Vai alla Casella");
        card.append("</label>");
        card.append("<div class='cheat-input-group'>");
        card.append("<input type='number' id='casella-input' min='0' max='39' placeholder='0-39' class='cheat-input'>");
        card.append("<a href='javascript:void(0)' onclick=\"document.location='/?action=cheat_vaiallacasella&casella=' + document.getElementById('casella-input').value\" class='cheat-action-btn'>");
        card.append("<i class='fa-solid fa-arrow-right'></i> VAI");
        card.append("</a>");
        card.append("</div>"); // Chiusura corretta di cheat-input-group
        card.append("</div>");

        // Sezione "+200 Euro"
        card.append("<div class='cheat-action'>");
        card.append("<a href='/?action=cheat_bonus200' class='cheat-money-btn'>");
        card.append("<i class='fa-solid fa-coins'></i> +200€ BONUS");
        card.append("</a>");
        card.append("<p class='cheat-hint'>Guadagna 200€ istantaneamente</p>");
        card.append("</div>");

        // Sezione Bancarotta
        card.append("<div class='cheat-action'>");
        card.append("<p class='cheat-label'><i class='fa-solid fa-skull'></i> Bancarotta</p>");
        card.append("<div class='cheat-bankrupt-group'>");
        
        Giocatore[] giocatori = partita.getGiocatori();
        for (int i = 0; i < giocatori.length; i++) {
            if (giocatori[i] != null) {
                String color = coloreGiocatore(i);
                card.append("<a href='/?action=cheat_bankrupt&playerIndex=").append(i).append("' ");
                card.append("class='cheat-bankrupt-btn' style='border-color:").append(color).append("; color:").append(color).append(";'>");
                card.append("G").append(i+1);
                card.append("</a>");
            }
        }
        
        card.append("</div>");
        card.append("</div>");

        card.append("</div>"); // Corpo della scheda
        card.append("</div>"); // Scheda d'azione

        return card.toString();
    }

    // CARD: statistiche giocatori (soldi, icone, stato) ---
    private static String renderizzaStatisticheGiocatori() {
        StringBuilder stats = new StringBuilder();

        stats.append("<div class='action-card info-style'>");
        stats.append("<div class='card-header'>");
        stats.append("<i class='fa-solid fa-users'></i> Giocatori");
        stats.append("</div>");
        stats.append("<div class='card-body'>");

        if (gameStarted) {
            Giocatore[] giocatori = partita.getGiocatori();
            int indiceCorrente = partita.getIndiceGiocatoreCorrente();

            for (int i = 0; i < giocatori.length; i++) {
                Giocatore g = giocatori[i];

                if (g == null) {
                    continue;
                }

                String classeStatus = "not-turn";
                if (i == indiceCorrente) {
                    classeStatus = "active";
                }
                if (g.getSoldi() < 0) {
                    classeStatus = "eliminated";
                }

                String coloreG = coloreGiocatore(i);
                String iconaG = iconaGiocatore(i);
                String nomeGiocatore = g.getNome();
                int soldiGiocatore = g.getSoldi();
                boolean inPrigione = g.isInPrigione();

                stats.append("<div class='player-card ").append(classeStatus).append("' style='--highlight-color:")
                        .append(coloreG).append("'>");

                stats.append("<div class='token-icon' style='color:").append(coloreG).append("'>");
                stats.append(iconaG);
                stats.append("</div>");

                stats.append("<div class='p-info'>");
                stats.append("<span class='p-name'>").append(nomeGiocatore);

                if (inPrigione) {
                    stats.append(" <i class='fa-solid fa-lock' style='color:#ff4757'></i>");
                }

                stats.append("</span>");
                stats.append("<span class='p-money'>€ ").append(soldiGiocatore).append("</span>");
                stats.append("</div>");

                stats.append("</div>");
            }
        }

        stats.append("</div>");
        stats.append("</div>");

        return stats.toString();
    }

    // CARD: azioni basate sulla casella corrente
    private static String renderizzaAzioniContextoCorrente() {
        StringBuilder azioni = new StringBuilder();

        Giocatore giocatoreCorrente = partita.getGiocatoreCorrente();
        if (giocatoreCorrente == null) {
            return "";
        }
        
        int posizioneCorrente = giocatoreCorrente.getPosizioneCorrente();
        Casella casellaCorrente = partita.getTabellone().getCasella(posizioneCorrente);

        // sezione Prigione
        if (giocatoreCorrente.isInPrigione()) {
            azioni.append("<div class='action-card prison-style'>");
            azioni.append("<div class='card-header'>");
            azioni.append("<i class='fa-solid fa-lock'></i> Prigione ");
            azioni.append("</div>");

            azioni.append("<div class='card-body'>");
            azioni.append("<p><b>").append(giocatoreCorrente.getNome()).append("</b> è in arresto.</p>");

            boolean dadiNonLanciati = !partita.isDadiLanciati();
            int soldiGiocatore = giocatoreCorrente.getSoldi();

            if (dadiNonLanciati && soldiGiocatore >= 500) {
                azioni.append("<a href='/?action=payJail' class='btn-system btn-primary' style='background:#ff4757;'>");
                azioni.append("ESCI (500€)");
                azioni.append("</a>");
            }

            azioni.append("</div>");
            azioni.append("</div>");
        }

        // sezione Acquisto/Costruzione
        if (partita.isDadiLanciati()) {
            String acquisto = renderizzaCardAcquistoOCostruzione(giocatoreCorrente, casellaCorrente);
            azioni.append(acquisto);

            String info = renderizzaCardVisita(giocatoreCorrente, casellaCorrente);
            azioni.append(info);
        }

        return azioni.toString();
    }

    // CARD: acquisto / costruzione (ostra opzioni di acquisto o aggiunta casa se
    // applicabile)
    private static String renderizzaCardAcquistoOCostruzione(Giocatore giocatore, Casella casella) {
        if (giocatore == null || casella == null) {
            return "";
        }
        StringBuilder card = new StringBuilder();
        // Controlla lo stato di proprietà e il prezzo (se applicabile)
        int prezzo = -1;
        Giocatore proprietario = null;

        if (casella instanceof Casella_terreno) {
            Casella_terreno terreno = (Casella_terreno) casella;
            prezzo = terreno.getValoreAcquisto();
            proprietario = terreno.getProprietario();
        } else if (casella instanceof Casella_stazione) {
            Casella_stazione stazione = (Casella_stazione) casella;
            prezzo = stazione.getValoreAcquisto();
            proprietario = stazione.getProprietario();
        } else if (casella instanceof Casella_societa) {
            Casella_societa societa = (Casella_societa) casella;
            prezzo = societa.getValoreAcquisto();
            proprietario = societa.getProprietario();
        }

        // Mostra la card di acquisto solo per tipi acquistabili
        if (prezzo > 0) {
            // Se non ha proprietario: mostra pulsante COMPRA
            if (proprietario == null) {
                card.append("<div class='action-card purchase-style'>");
                card.append("<div class='card-header'>");
                card.append("<i class='fa-solid fa-cart-shopping'></i> ACQUISTO");
                card.append("</div>");

                card.append("<div class='card-body'>");
                card.append("<span class='card-subtitle'>").append(casella.getNome()).append("</span>");
                card.append("<p>Prezzo: <b>").append(prezzo).append("€</b></p>");

                int soldiGiocatore = giocatore.getSoldi();
                if (soldiGiocatore >= prezzo) {
                    card.append("<a href='/?action=buy' class='btn-system btn-primary'>COMPRA ORA</a>");
                } else {
                    card.append("<div class='btn-system btn-disabled'>FONDI INSUFFICIENTI</div>");
                }

                card.append("</div>");
                card.append("</div>");
            }
            // Se il proprietario è il giocatore corrente: mostra la card ma con messaggio
            // "Proprietà già posseduta"
            else if (proprietario == giocatore) {
                card.append("<div class='action-card purchase-style'>");
                card.append("<div class='card-header'>");
                card.append("<i class='fa-solid fa-cart-shopping'></i> ACQUISTO");
                card.append("</div>");

                card.append("<div class='card-body'>");
                card.append("<span class='card-subtitle'>").append(casella.getNome()).append("</span>");
                card.append("<p>Prezzo: <b>").append(prezzo).append("€</b></p>");

                card.append("<div class='btn-system btn-disabled'>Proprietà già posseduta</div>");

                card.append("</div>");
                card.append("</div>");
            }
            // Altrimenti (proprietario diverso): non mostrare la card di acquisto
        }

        // mostra la card di costruzione
        if (casella instanceof Casella_terreno) {
            Casella_terreno terreno = (Casella_terreno) casella;
            proprietario = terreno.getProprietario();
            String colore = terreno.getColore();
            int numeroCase = terreno.getNumeroCase();

            boolean appartienealGiocatore = (proprietario == giocatore);
            boolean serieCompleta = giocatore.haSerieCompleta(colore);
            boolean puoConstruire = (numeroCase < 5);

            if (appartienealGiocatore && serieCompleta && puoConstruire) {
                int costoCasa = terreno.getCostoCasa();
                int soldiGiocatore = giocatore.getSoldi();

                card.append("<div class='action-card'>");
                card.append("<div class='card-header'>");
                card.append("<i class='fa-solid fa-hammer'></i> COSTRUISCI");
                card.append("</div>");

                card.append("<div class='card-body'>");
                card.append("<span class='card-subtitle'>").append(terreno.getNome()).append("</span>");
                card.append("<p>Costo Casa: <b>").append(costoCasa).append("€</b></p>");

                if (soldiGiocatore >= costoCasa) {
                    card.append("<a href='/?action=build' class='btn-system btn-primary'>AGGIUNGI CASA</a>");
                }

                card.append("</div>");
                card.append("</div>");
            }
        }

        return card.toString();
    }

    // CARD: info casella
    private static String renderizzaCardVisita(Giocatore giocatore, Casella casella) {
        StringBuilder card = new StringBuilder();

        card.append("<div class='action-card info-style'>");
        card.append("<div class='card-header'>");
        card.append("<i class='fa-solid fa-location-dot'></i> Info Casella");
        card.append("</div>");

        card.append("<div class='card-body'>");
        card.append("<span class='card-subtitle'>").append(casella.getNome()).append("</span>");

        // Se è una casella carta, mostra la descrizione
        if (casella instanceof Casella_carta) {
            Carta cartaPescata = partita.getUltimaCartaPescata();
            if (cartaPescata != null) {
                String descrizione = cartaPescata.getDescrizione();
                card.append("<p class='card-description'>\"").append(descrizione).append("\"</p>");
            }
        }
        // Se è un terreno con proprietario, mostra proprietario e affitto
        else if (casella instanceof Casella_terreno) {
            Casella_terreno terreno = (Casella_terreno) casella;
            Giocatore proprietario = terreno.getProprietario();

            if (proprietario != null && proprietario != giocatore) {
                String nomeProprietario = proprietario.getNome();
                int affitto = terreno.calcolaAffitto();

                card.append("<p>Proprietario: ").append(nomeProprietario).append("</p>");
                card.append("<p>Affitto: <b>").append(affitto).append("€</b></p>");
            }
        }

        card.append("</div>");
        card.append("</div>");

        return card.toString();
    }

    // case e Hotel
    private static String renderizzaCaseHotel(int numero) {
        StringBuilder case_hotel = new StringBuilder();

        case_hotel.append("<div class='house-container'>");

        if (numero >= 5) {
            case_hotel.append("<i class='fa-solid fa-hotel hotel-icon'></i>");
        } else {
            for (int i = 0; i < numero; i++) {
                case_hotel.append("<i class='fa-solid fa-house house-icon'></i>");
            }
        }

        case_hotel.append("</div>");

        return case_hotel.toString();
    }

    // gettoni dei giocatori sulla casella
    private static String renderizzaGettoniSuCasella(int indice) {
        StringBuilder gettoni = new StringBuilder();

        gettoni.append("<div class='tokens-container'>");

        Giocatore[] giocatori = partita.getGiocatori();

        for (int j = 0; j < giocatori.length; j++) {
            Giocatore g = giocatori[j];

            if (g != null && g.getPosizioneCorrente() == indice) {
                String colore = coloreGiocatore(j);
                String icona = iconaGiocatore(j);
                int cambioSoldi = g.getUltimoCambioSoldi();

                gettoni.append("<div class='player-token' style='background:").append(colore)
                        .append("; color:white;'>");
                gettoni.append(icona);

                if (cambioSoldi != 0) {
                    String classe = "plus";
                    String simbolo = "+";

                    if (cambioSoldi < 0) {
                        classe = "minus";
                        simbolo = "";
                    }

                    gettoni.append("<span class='money-indicator ").append(classe).append("'>");
                    gettoni.append(simbolo).append(cambioSoldi).append(" €");
                    gettoni.append("</span>");
                }

                gettoni.append("</div>");
            }
        }

        gettoni.append("</div>");

        return gettoni.toString();
    }

    // contiene funzioni per determinare lati, colore, icone, e controllo caselle

    private static boolean èAngolo(int indice) {
        if (indice == 0) {
            return true;
        }
        if (indice == 10) {
            return true;
        }
        if (indice == 20) {
            return true;
        }
        if (indice == 30) {
            return true;
        }

        return false;
    }

    private static boolean èSpeciale(int indice) {
        if (indice == 4)
            return true;
        if (indice == 38)
            return true;
        if (indice == 2)
            return true;
        if (indice == 7)
            return true;
        if (indice == 17)
            return true;
        if (indice == 33)
            return true;
        if (indice == 22)
            return true;
        if (indice == 36)
            return true;
        if (indice == 5)
            return true;
        if (indice == 15)
            return true;
        if (indice == 25)
            return true;
        if (indice == 35)
            return true;
        if (indice == 12)
            return true;
        if (indice == 28)
            return true;

        return false;
    }

    private static String latoCella(int indice) {
        if (indice <= 10) {
            return "bottom";
        }
        if (indice <= 20) {
            return "left";
        }
        if (indice <= 30) {
            return "top";
        }

        return "right";
    }

    private static String posizioneGriglia(int indice) {
        int riga = 0;
        int colonna = 0;

        if (indice <= 10) {
            riga = 11;
            colonna = 11 - indice;
        } else if (indice <= 20) {
            colonna = 1;
            riga = 11 - (indice - 10);
        } else if (indice <= 30) {
            riga = 1;
            colonna = (indice - 20) + 1;
        } else {
            colonna = 11;
            riga = (indice - 30) + 1;
        }

        return "grid-column:" + colonna + "; grid-row:" + riga + ";";
    }

    private static String coloreCella(int indice) {
        // marrone
        if (indice == 1 || indice == 3) {
            return "bg-marrone";
        }

        // azzurro
        if (indice == 6 || indice == 8 || indice == 9) {
            return "bg-azzurro";
        }

        // magenta
        if (indice == 11 || indice == 13 || indice == 14) {
            return "bg-magenta";
        }

        // arancione
        if (indice == 16 || indice == 18 || indice == 19) {
            return "bg-arancione";
        }

        // rosso
        if (indice == 21 || indice == 23 || indice == 24) {
            return "bg-rosso";
        }

        // giallo
        if (indice == 26 || indice == 27 || indice == 29) {
            return "bg-giallo";
        }

        // verde
        if (indice == 31 || indice == 32 || indice == 34) {
            return "bg-verde";
        }

        // blu
        if (indice == 37 || indice == 39) {
            return "bg-blu";
        }

        return null;
    }

    private static String coloreGiocatore(int indice) {
        String[] colori = { "#ff4757", "#2ed573", "#1e90ff", "#ffa502" };

        if (indice >= 0 && indice < colori.length) {
            return colori[indice];
        }

        return "#000";
    }

    private static String iconaGiocatore(int indice) {
        String[] icone = { "fa-car", "fa-ship", "fa-plane", "fa-dog" };

        if (indice >= 0 && indice < icone.length) {
            String icona = icone[indice];
            return "<i class='fa-solid " + icona + "'></i>";
        }

        return "P";
    }

    private static String iconaValoreSpeciale(int indice) {
        // stazioni
        if (indice == 5 || indice == 15 || indice == 25 || indice == 35) {
            return "<i class='fa-solid fa-train-subway'></i>";
        }

        // servizi - elettricità
        if (indice == 12) {
            return "<i class='fa-solid fa-bolt'></i>";
        }

        // servizi - Acqua
        if (indice == 28) {
            return "<i class='fa-solid fa-faucet-drip'></i>";
        }

        // probabilità
        if (indice == 2 || indice == 17 || indice == 33) {
            return "<i class='fa-solid fa-question'></i>";
        }

        // imprevisti
        if (indice == 7 || indice == 22 || indice == 36) {
            return "<i class='fa-solid fa-sack-dollar'></i>";
        }

        // Tassa Patrimoniale
        if (indice == 4) {
            return "<i class='fa-solid fa-gem'></i>";
        }

        // tassa di Lusso
        if (indice == 38) {
            return "<i class='fa-solid fa-file-invoice-dollar'></i>";
        }

        // vai in prigione
        if (indice == 30) {
            return "<i class='fa-solid fa-gavel'></i>";
        }

        // prigione
        if (indice == 10) {
            return "<i class='fa-solid fa-bars'></i>";
        }

        return null;
    }

    // tenta di aprire la URL della app in default browser
    private static void apriBrowser(int port) {
        try {
            if (Desktop.isDesktopSupported()) {
                String url = "http://localhost:" + port;
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
        }
    }
}