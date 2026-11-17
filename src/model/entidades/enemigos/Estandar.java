package model.entidades.enemigos;

import java.util.List;

import model.entidades.personajes.Personaje;

/**
 * Enemigo común del juego (Esqueleto).
 * <p>Su habilidad especial es Golpe Perforante: hace el 120% de su ataque
 * a un personaje e ignora su defensa.</p>
 */
public class Estandar extends Enemigo {
    private static final int DROP = 25; // % de chance de soltar un objeto al morir
    private static final int DROP_COMUN = 90; // % de que el objeto sea común
    private static final int DROP_RARO = 10; // % de que el objeto sea raro (nunca suelta épicos)
    private static final int COSTO_GOLPE_PERFORANTE = 20; // Recurso que gasta para usar Golpe Perforante

    /**
     * @param nombre      Nombre del enemigo
     * @param vida        Vida máxima
     * @param energia     Recurso máximo para usar la habilidad
     * @param ataque      Ataque base
     * @param defensa     Defensa base
     * @param velocidad   Energía que gana por tick de combate
     * @param nivel       Nivel del enemigo
     * @param experiencia Experiencia que da al ser derrotado
     */
    public Estandar(String nombre, int vida, int energia, int ataque, int defensa, int velocidad, int nivel,
            int experiencia) {
        DropStats dropStats = new DropStats(DROP, DROP_COMUN, DROP_RARO);
        super(nombre, vida, energia, ataque, defensa, velocidad, nivel, experiencia, dropStats);
    }

    @Override
    protected int getCostoHabilidad() {
        return COSTO_GOLPE_PERFORANTE;
    }

    /**
     * Golpe Perforante: ataca a un personaje ignorando su defensa y descuenta el recurso.
     * El daño es el 120% del ataque.
     *
     * @param objetivo        Personaje que recibe el golpe
     * @param personajesVivos No se usa: la habilidad pega a un solo personaje
     */
    @Override
    public void usarHabilidadEspecial(Personaje objetivo, List<Personaje> personajesVivos) {
        consumirRecursos(COSTO_GOLPE_PERFORANTE);
        int danio = (int) Math.round(atacar() * 1.20);
        // Resta la vida directamente, sin pasar por recibirDanio (que descuenta la defensa)
        objetivo.setVidaActual(objetivo.getVidaActual() - danio);
    }

    @Override
    public TipoEnemigo getTipo() {
        return TipoEnemigo.ESQUELETO;
    }
}
