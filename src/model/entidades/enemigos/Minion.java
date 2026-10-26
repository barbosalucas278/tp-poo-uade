package model.entidades.enemigos;

/**
 * Minion
 */
public class Minion extends Enemigo {
    public Minion(String nombre, int vida, int recursoMax, int ataque, int defensa, int velocidad, int nivel, int experiencia) {
        super(nombre, vida, recursoMax, ataque, defensa, velocidad, nivel, experiencia);
    }

    @Override
    public TipoEnemigo getTipo() {
        return TipoEnemigo.SLIME;
    }
}
