package Files;
public class Dadi {
    // Variabili per memorizzare i valori dei due dadi
    private int valore1;
    private int valore2;

    public Dadi() {
        // Inizializza i dadi a 0
        valore1 = 0;
        valore2 = 0;
    }// Lancia i dadi generando due numeri casuali da 1 a 6
    public void lancia() {
        // Math.random() genera un numero tra 0.0 e 0.99
        // Moltiplicando per 6 e aggiungendo 1 otteniamo un numero da 1 a 6
        valore1 = (int)(Math.random() * 6) + 1;
        valore2 = (int)(Math.random() * 6) + 1;
        
        System.out.println("Dadi lanciati: " + valore1 + " - " + valore2);
    }// Restituisce la somma dei due dadi per il movimento
    public int getTotale() {
        return valore1 + valore2;
    }

    // Controlla se è uscito un doppio (per tirare di nuovo o uscire di prigione)
    public boolean isDoppio() {
        return valore1 == valore2;
    }
}