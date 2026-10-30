package model.entidades.enemigos;

/**
 * Minion
 */
public class Minion extends Enemigo {
    private static final int DROP = 40;
    private static final int DROP_COMUN = 75;
    private static final int DROP_RARO = 23;

    public Minion(String nombre, int vida, int recursoMax, int ataque, int defensa, int velocidad, int nivel,
            int experiencia) {
        DropStats dropStats = new DropStats(DROP, DROP_COMUN, DROP_RARO);
        super(nombre, vida, recursoMax, ataque, defensa, velocidad, nivel, experiencia, dropStats);
    }

    @Override
    public TipoEnemigo getTipo() {
        return TipoEnemigo.SLIME;
    }
}
