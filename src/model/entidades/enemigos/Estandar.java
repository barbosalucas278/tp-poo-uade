package model.entidades.enemigos;

/**
 * Esqueleto
 */
public class Estandar extends Enemigo {
    private static final int DROP = 25;
    private static final int DROP_COMUN = 90;
    private static final int DROP_RARO = 10;

    public Estandar(String nombre, int vida, int energia, int ataque, int defensa, int velocidad, int nivel,
            int experiencia) {
        DropStats dropStats = new DropStats(DROP, DROP_COMUN, DROP_RARO);
        super(nombre, vida, energia, ataque, defensa, velocidad, nivel, experiencia, dropStats);
    }

    @Override
    public TipoEnemigo getTipo() {
        return TipoEnemigo.ESQUELETO;
    }
}
