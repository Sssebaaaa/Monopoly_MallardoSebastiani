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
                    if (gameStarted && cheat.isCheatsAvailable()) {
                        String casella = params.get("casella");
                        if (casella != null && !casella.isEmpty()) {
                            try {
                                int posizioneDestinazione = Integer.parseInt(casella);
                                cheat.vaiAllaCasella(posizioneDestinazione);
                            } catch (NumberFormatException e) {
                                System.err.println("[CHEAT] Posizione non valida: " + casella);
                            }
                        }
                    }
                    break;

                case "cheat_bonus200":
                    if (gameStarted && cheat.isCheatsAvailable()) {
                        cheat.bonus200Euro();
                    }
                    break;

                case "toggle_cheat":
                    cheat.toggleCheats();
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
                String contentType = getContentType(path);
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

        private String getContentType(String path) {
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

        String lato = getCellSide(indice);
        String classi = "cell " + lato;

        if (èAngolo(indice)) {
            classi = classi + " corner";
        }
        if (èSpeciale(indice)) {
            classi = classi + " special";
        }

        String posizione = getCellGrid(indice);

        cella.append("<div class='").append(classi).append("' style='").append(posizione).append("'>");

        // Barra colore della proprietà
        String colore = getCellColor(indice);
        if (colore != null && !èAngolo(indice) && !èSpeciale(indice)) {
            cella.append("<div class='colorbar ").append(colore).append("'></div>");
        }

        cella.append("<div class='name'>").append(nome).append("</div>");

        // Icona speciale
        String icona = getSpecialIcon(indice);
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

    