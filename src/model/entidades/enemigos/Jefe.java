package model.entidades.enemigos;

import java.util.List;

import model.entidades.personajes.Personaje;

/**
 * Jefe
 */
public class Jefe extends Enemigo {
    private static final int DROP = 100;
    private static final int DROP_COMUN = 15;
    private static final int DROP_RARO = 55;
    // Recurso que gasta en Aliento de Fuego
    private static final int COSTO_ALIENTO_DE_FUEGO = 50;
    // Para que la fase de furia se active una sola vez por combate
    private boolean furiaActivada = false;

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

    @Override
    protected int getCostoHabilidad() {
        return COSTO_ALIENTO_DE_FUEGO;
    }

    // El jefe usa su habilidad más seguido que los demás enemigos
    @Override
    protected int getProbabilidadHabilidad() {
        return 50;
    }

    // Aliento de Fuego: quema a todo el grupo con el 60 % de su ataque
    @Override
    public void usarHabilidadEspecial(Personaje objetivo, List<Personaje> personajesVivos) {
        consumirRecursos(COSTO_ALIENTO_DE_FUEGO);
        int danio = (int) Math.round(atacar() * 0.60);
        for (Personaje personaje : personajesVivos) {
            personaje.recibirDanio(danio);
        }
    }

    @Override
    public TipoEnemigo getTipo() {
        return TipoEnemigo.DRAGON;
    }
}