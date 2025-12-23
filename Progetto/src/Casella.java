package Progetto.src;
abstract class Casella {
    protected String nome;
    protected int id; //indica la posizione sul tabellone (0-39)

    public Casella(String nome, int id){
        this.nome = nome;
        this.id = id;
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
