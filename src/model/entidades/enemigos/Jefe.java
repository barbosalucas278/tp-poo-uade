package model.entidades.enemigos;

/**
 * Jefe
 */
public class Jefe extends Enemigo {
    private static final int DROP = 100;
    private static final int DROP_COMUN = 15;
    private static final int DROP_RARO = 55;

    public Jefe(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad, int nivel,
            int experienciaOtorgada) {
        DropStats dropStats = new DropStats(DROP, DROP_COMUN, DROP_RARO);
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, nivel, experienciaOtorgada, dropStats);
    }

    public void faseFuria() {
        this.setVelocidad(this.getVelocidad() * 2);
    }

    public void usarHabilidadEspecial() {
        // Implementación de la habilidad especial del jefe
    }

    @Override
    public TipoEnemigo getTipo() {
        return TipoEnemigo.DRAGON;
    }
}
