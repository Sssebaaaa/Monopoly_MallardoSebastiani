package Progetto.src;
public class Dadi {
    // Valori dei due dadi
    private int valore1;
    private int valore2;

    public Dadi() {
        // Inizializza a 0
        valore1 = 0;
        valore2 = 0;
    }// Lancia i dadi generando due numeri casuali da 1 a 6
    public void lancia() {
        // Genera due valori casuali da 1 a 6
        valore1 = (int)(Math.random() * 6) + 1;
        valore2 = (int)(Math.random() * 6) + 1;
        
        System.out.println("Dadi lanciati: " + valore1 + " - " + valore2);
    }// Somma dei due dadi
    public int getTotale() {
        return valore1 + valore2;
    }

    // True se è uscito un doppio
    public boolean isDoppio() {
        return valore1 == valore2;
    }
}