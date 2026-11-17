package model.entidades.enemigos;

import java.util.List;

import model.entidades.personajes.Personaje;

/**
 * Jefe final de cada mapa (Dragón).
 * <p>Su habilidad especial es Aliento de Fuego: le hace el 60% de su ataque
 * a todo el grupo. Usa la habilidad más seguido que los otros enemigos y,
 * cuando le queda la mitad de la vida, entra en furia y duplica su velocidad.</p>
 */
public class Jefe extends Enemigo {
    private static final int DROP = 100; // % de chance de soltar un objeto al morir (siempre suelta)
    private static final int DROP_COMUN = 15; // % de que el objeto sea común
    private static final int DROP_RARO = 55; // % de que el objeto sea raro (el 30% restante es épico)
    private static final int COSTO_ALIENTO_DE_FUEGO = 50; // Recurso que gasta para usar Aliento de Fuego
    private boolean furiaActivada = false; // Para que la furia se active una sola vez por combate

    /**
     * @param nombre              Nombre del enemigo
     * @param vidaMax             Vida máxima
     * @param recursoMax          Recurso máximo para usar la habilidad
     * @param ataqueBase          Ataque base
     * @param defensaBase         Defensa base
     * @param velocidad           Energía que gana por tick de combate
     * @param nivel               Nivel del enemigo
     * @param experienciaOtorgada Experiencia que da al ser derrotado
     */
    public Jefe(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad, int nivel,
            int experienciaOtorgada) {
        DropStats dropStats = new DropStats(DROP, DROP_COMUN, DROP_RARO);
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, nivel, experienciaOtorgada, dropStats);
    }

    /** Duplica la velocidad: el jefe empieza a actuar el doble de seguido. */
    public void faseFuria() {
        this.setVelocidad(this.getVelocidad() * 2);
    }

    /** Si al empezar su turno tiene la mitad de la vida o menos, entra en furia (solo la primera vez). */
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

    /** 50% en vez del 30% que usan los demás enemigos. */
    @Override
    protected int getProbabilidadHabilidad() {
        return 50;
    }

    /**
     * Aliento de Fuego: quema a todos los personajes vivos y descuenta el recurso.
     * El daño es el 60% del ataque y a cada personaje se le resta su defensa.
     *
     * @param objetivo        No se usa: la habilidad pega a todo el grupo
     * @param personajesVivos Personajes que reciben el daño
     */
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
