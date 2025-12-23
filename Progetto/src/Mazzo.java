package Progetto.src;

class Mazzo {
    private Carta[] carte;

    public Mazzo() {
        this.carte = new Carta[16];
    }

    public void mescola(){
        for(int i=0; i<carte.length; i++){
            int random = (int) (Math.random()*carte.length);
            Carta temp = carte[i];
            carte[i] = carte[random];
            carte[random] = temp;
        }
    }

    public Carta pesca(){
        Carta carta = carte[0];
        for(int i=0; i<carte.length-1; i++){
            carte[i] = carte[i+1];
        }
        carte[carte.length-1] = carta;
        return carta;
    }

    public void aggiungiCarta(Carta c) {
        for (int i = 0; i < carte.length; i++) {
            if (carte[i] == null) {
                carte[i] = c;
                return; 
            }
        }
    }

}
