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
