package Progetto.src;

class Carta {
    private String descrizione;
    private int idEffetto;
    private int valore;

    public Carta(String descrizione, int idEffetto, int valore) {
        this.descrizione = descrizione;
        this.idEffetto = idEffetto;
        this.valore = valore;
    }

    public String getDescrizione() {
        return this.descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public int getIdEffetto() {
        return this.idEffetto;
    }

    public void setIdEffetto(int idEffetto) {
        this.idEffetto = idEffetto;
    }

    public int getValore() {
        return this.valore;
    }

    public void setValore(int valore) {
        this.valore = valore;
    }

    

    
    

}