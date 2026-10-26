package model.entidades.enemigos;

/**
 * Orco
 */
public class Elite extends Enemigo {
    public Elite(String nombre, int vida, int energia, int ataque, int defensa, int velocidad, int nivel, int experiencia) {
        super(nombre, vida, energia, ataque, defensa, velocidad, nivel, experiencia);
    }

    @Override
    public TipoEnemigo getTipo() {
        return TipoEnemigo.ORCO;
    }
}
