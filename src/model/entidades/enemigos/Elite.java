package model.entidades.enemigos;

/**
 * Orco
 */
public class Elite extends Enemigo {
    private static final int DROP = 70;
    private static final int DROP_COMUN = 45;
    private static final int DROP_RARO = 45;
    private static final int COSTO_GOLPE_BRUTAL = 25; // Recurso que gasta cada vez que usa Golpe Brutal

    public Elite(String nombre, int vida, int energia, int ataque, int defensa, int velocidad, int nivel,
            int experiencia) {
        DropStats dropStats = new DropStats(DROP, DROP_COMUN, DROP_RARO);
        super(nombre, vida, energia, ataque, defensa, velocidad, nivel, experiencia, dropStats);
    }

    // Habilidad especial del Elite: devuelve el daño que hace Golpe Brutal
    @Override
    public int usarHabilidadEspecial() {
        return golpeBrutal();
    }

    // Un golpe muy fuerte a un personaje: 180 % de su ataque
    private int golpeBrutal() {
        return (int) Math.round(atacar() * 1.80);
    }

    @Override
    public int getCostoHabilidad() {
        return COSTO_GOLPE_BRUTAL;
    }

    @Override
    public TipoEnemigo getTipo() {
        return TipoEnemigo.ORCO;
    }
}