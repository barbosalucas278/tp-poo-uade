package model.entidades.enemigos;

/**
 * Minion
 */
public class Minion extends Enemigo {
    private static final int DROP = 40;
    private static final int DROP_COMUN = 75;
    private static final int DROP_RARO = 23;
    // Recurso que gasta cada vez que usa Escupitajo Ácido
    private static final int COSTO_ESCUPITAJO = 10;

    public Minion(String nombre, int vida, int recursoMax, int ataque, int defensa, int velocidad, int nivel,
            int experiencia) {
        DropStats dropStats = new DropStats(DROP, DROP_COMUN, DROP_RARO);
        super(nombre, vida, recursoMax, ataque, defensa, velocidad, nivel, experiencia, dropStats);
    }

    // Habilidad especial del Minion: devuelve el daño que hace Escupitajo Ácido
    @Override
    public int usarHabilidadEspecial() {
        return escupitajoAcido();
    }

    // Le escupe a un personaje: 150 % de su ataque
    private int escupitajoAcido() {
        // Si no tiene recurso suficiente, no puede usarla
        if (getRecursoActual() < COSTO_ESCUPITAJO) {
            throw new IllegalStateException("No hay suficiente recurso para usar Escupitajo Ácido.");
        }
        int danio = (int) Math.round(atacar() * 1.50);
        consumirRecursos(COSTO_ESCUPITAJO);
        return danio;
    }

    @Override
    protected int getCostoHabilidad() {
        return COSTO_ESCUPITAJO;
    }

    @Override
    public TipoEnemigo getTipo() {
        return TipoEnemigo.SLIME;
    }
}
