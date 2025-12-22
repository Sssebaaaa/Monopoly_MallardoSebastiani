# 🎲 Monopoly - Java Project

**Benvenuti nella repository del progetto Monopoly.**
Questa è un'implementazione software del celebre gioco da tavolo, sviluppata interamente in **Java** con interfaccia grafica prevista in **JavaFX**. Il progetto simula le meccaniche classiche di acquisto proprietà, gestione economica e imprevisti.

---

## 👥 Componenti del Team e Suddivisione del Lavoro

Il lavoro è stato ripartito in modo bilanciato per coprire sia la struttura logica del gioco che le interazioni tra le entità.

### 👨‍💻 **Nicholas Sebastiani**
*Ruolo: Gestione Strutturale e Core del Gioco*
* **Partita**: Gestione del flusso dei turni, condizioni di vittoria e orchestrazione generale.
* **Tabellone**: Gestione dell'array di caselle e logica matematica degli indici.
* **Dadi**: Generazione numeri casuali e gestione dei lanci "doppi".
* **Main**: Punto di ingresso (entry-point) dell'applicazione.

### 👨‍💼 **Davide Mallardo** (Project Manager)
*Ruolo: Logica di Interazione ed Entità*
* **Giocatore**: Gestione budget, movimento, proprietà e stato di prigionia.
* **Casella (e derivate)**: Logica astratta e specifica (Terreno, Stazione, Prigione, Imprevisti). Include acquisto, affitti e costruzione case.
* **Banca**: Gestione transazioni e passaggi di proprietà.
* **Mazzo e Carta**: Gestione della pesca e degli effetti delle carte "Imprevisti" e "Probabilità".

> 🎨 **Grafica (JavaFX):** Sviluppata congiuntamente da entrambi i membri del team.

---

## 🏗️ Architettura del Progetto

Il sistema è basato su una struttura a oggetti che interagiscono tramite la classe `Partita`.

### 📂 Classi Principali

* **`Partita`**: Contiene le istanze di Tabellone, Banca, Dadi e Giocatori. I suoi metodi principali sono:
    * `iniziaPartita()`: Setup iniziale.
    * `eseguiTurno()`: Gestisce il ciclo di vita del turno (movimento, azione casella, fine turno).
* **`Tabellone`**: Gestisce l'array di 40 caselle e calcola la posizione futura tramite aritmetica modulare.
* **`Giocatore`**: Mantiene lo stato dell'utente (soldi, posizione, proprietà). Include metodi come `paga()`, `incassa()` e `haSerieCompleta()` per la costruzione.

### 📍 Le Caselle
La classe astratta `Casella` viene estesa per gestire comportamenti specifici:
* 🏠 **Casella_terreno**: Acquisto, rendita e costruzione case.
* 🚂 **Casella_stazione**: Rendita basata sul numero di stazioni possedute (25, 50, 100, 200).
* ❓ **Casella_carta**: Pesca dal mazzo Imprevisti/Probabilità ed esecuzione effetto.
* 👮 **Casella_prigione / VaiPrigione**: Gestione dello stato di detenzione (solo transito o prigionia effettiva).

---

## 🔄 Flusso di Esecuzione (Game Loop)

1.  **Preparazione**:
    * Creazione del Tabellone e configurazione proprietà.
    * Assegnazione budget iniziale ai giocatori e posizionamento sul "Via".
    * Mescolamento mazzi.

2.  **Ciclo di Gioco**:
    * **Verifica Prigione**: Il giocatore tenta di uscire o salta il turno.
    * **Movimento**: Lancio dadi e aggiornamento posizione `(posAttuale + passi) % dimensione`.
    * **Azione**: Esecuzione logica della casella di atterraggio (Paga affitto, Compra, Pesca carta, ecc.).
    * **Gestione Doppi**: Se i dadi sono uguali, il giocatore ripete il turno (salvo prigione).
    * **Fallimento**: Se `soldi < 0`, le proprietà tornano alla banca e il giocatore viene rimosso.

3.  **Vittoria**: Il gioco termina quando rimane un solo giocatore attivo.

---

## 📊 Documentazione UML

Per visualizzare il diagramma delle classi e le relazioni strutturali del progetto, consultare il seguente link:
🔗 [Visualizza Diagramma UML su Drive](https://drive.google.com/file/d/1R93ekOG4wqf_dXXFgu5zhafmqX2WmkWR/view?usp=sharing)

---

## 🛠️ Requisiti

* **Java Development Kit (JDK)**: Versione 8 o superiore.
* **JavaFX**: Per l'esecuzione dell'interfaccia grafica.