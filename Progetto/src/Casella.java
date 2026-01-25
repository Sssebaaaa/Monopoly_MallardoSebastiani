package Progetto.src;
abstract class Casella {
    protected String nome;
    protected int id; // posizione sul tabellone (0-39)
    protected Casella next;
    protected Casella prev;

    public Casella(String nome, int id){
        this.nome = nome;
        this.id = id;
    }

    public Casella getNext() { 
        return next; 
    }

    public Casella getPrev() { 
        return prev; 
    }

    public void setNext(Casella next) { 
        this.next = next; 
    }
    
    public void setPrev(Casella prev) { 
        this.prev = prev; 
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public abstract void azione(Giocatore g, Partita p);
    

    
}
