package helpers.entidades;

public class StatsBaseConfigPerosnajes {
    private int vida;
    private int recursoMax;
    private int ataque;
    private int defensa;
    private int velocidad;
    

    public int getVida() {
        return vida;
    }

    public int getRecursoMax() {
        return recursoMax;
    }

    public int getAtaque() {
        return ataque;
    }

    public int getDefensa() {
        return defensa;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public StatsBaseConfigPerosnajes(int vida, int recursoMax, int ataque, int defensa, int velocidad) {
        this.vida = vida;
        this.recursoMax = recursoMax;
        this.ataque = ataque;
        this.defensa = defensa;
        this.velocidad = velocidad;
    }
    
}
