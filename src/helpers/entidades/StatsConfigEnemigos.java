package helpers.entidades;

/**
 * StatsConfigEnemigos
 */
public class StatsConfigEnemigos {
    private String nombre;
    private int vida;
    private int recursoMax;
    private int ataque;
    private int defensa;
    private int velocidad;
    private int experiencia;
    
    public String getNombre() {
        return nombre;
    }

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

    public int getExperiencia() {
        return experiencia;
    }

    public StatsConfigEnemigos(String nombre, int vida, int recursoMax, int ataque, int defensa, int velocidad, int experiencia) {
        this.nombre = nombre;
        this.vida = vida;
        this.recursoMax = recursoMax;
        this.ataque = ataque;
        this.defensa = defensa;
        this.velocidad = velocidad;
        this.experiencia = experiencia;
    }
    
}
