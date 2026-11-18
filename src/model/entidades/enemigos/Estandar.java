package model.entidades.enemigos;

/**
 * Esqueleto
 */
public class Estandar extends Enemigo {
    private static final int DROP = 25;
    private static final int DROP_COMUN = 90;
    private static final int DROP_RARO = 10;
    // Recurso que gasta cada vez que usa Golpe Perforante
    private static final int COSTO_GOLPE_PERFORANTE = 20;

    public Estandar(String nombre, int vida, int energia, int ataque, int defensa, int velocidad, int nivel,
            int experiencia) {
        DropStats dropStats = new DropStats(DROP, DROP_COMUN, DROP_RARO);
        super(nombre, vida, energia, ataque, defensa, velocidad, nivel, experiencia, dropStats);
    }

    // Indica que su habilidad atraviesa la defensa del personaje
    @Override
    public boolean habilidadIgnoraDefensa() {
        return true;
    }

    // Habilidad especial del Estandar: devuelve el daño que hace Golpe Perforante
    @Override
    public int usarHabilidadEspecial() {
        return golpePerforante();
    }

    // Golpe que atraviesa la armadura: 120 % de su ataque (el motor lo aplica sin descontar defensa)
    private int golpePerforante() {
        // Si no tiene recurso suficiente, no puede usarla
        if (getRecursoActual() < COSTO_GOLPE_PERFORANTE) {
            throw new IllegalStateException("No hay suficiente recurso para usar Golpe Perforante.");
        }
        int danio = (int) Math.round(atacar() * 1.20);
        consumirRecursos(COSTO_GOLPE_PERFORANTE);
        return danio;
    }

    @Override
    protected int getCostoHabilidad() {
        return COSTO_GOLPE_PERFORANTE;
    }

    @Override
    public TipoEnemigo getTipo() {
        return TipoEnemigo.ESQUELETO;
    }
}