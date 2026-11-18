package model.entidades.enemigos;

/**
 * Jefe
 */
public class Jefe extends Enemigo {
    private static final int DROP = 100;
    private static final int DROP_COMUN = 15;
    private static final int DROP_RARO = 55;
    private static final int COSTO_ALIENTO_DE_FUEGO = 50; // Recurso que gasta cada vez que usa Aliento de Fuego
    private boolean furiaActivada = false; // Para que la fase de furia se active una sola vez por combate

    public Jefe(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad, int nivel,
            int experienciaOtorgada) {
        DropStats dropStats = new DropStats(DROP, DROP_COMUN, DROP_RARO);
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, nivel, experienciaOtorgada, dropStats);
    }

    // Duplica la velocidad: el jefe empieza a actuar el doble de seguido
    public void faseFuria() {
        this.setVelocidad(this.getVelocidad() * 2);
    }

    // Al empezar su turno, si tiene la mitad de vida o menos, entra en furia (solo la primera vez)
    @Override
    public void alIniciarTurno() {
        if (!furiaActivada && getVidaActual() <= getVidaMax() / 2) {
            faseFuria();
            furiaActivada = true;
        }
    }

    // Indica que su habilidad golpea a todo el grupo en vez de a un solo personaje
    @Override
    public boolean habilidadAfectaATodos() {
        return true;
    }

    // Habilidad especial del Jefe: devuelve el daño que hace Aliento de Fuego a cada personaje
    @Override
    public int usarHabilidadEspecial() {
        return alientoDeFuego();
    }

    // Quema a todo el grupo: 60 % de su ataque a cada personaje vivo (el motor lo aplica a todos)
    private int alientoDeFuego() {
        return (int) Math.round(atacar() * 0.60);
    }

    @Override
    public int getCostoHabilidad() {
        return COSTO_ALIENTO_DE_FUEGO;
    }

    // El jefe usa su habilidad más seguido que los demás enemigos
    @Override
    protected int getProbabilidadHabilidad() {
        return 50;
    }

    @Override
    public TipoEnemigo getTipo() {
        return TipoEnemigo.DRAGON;
    }
}