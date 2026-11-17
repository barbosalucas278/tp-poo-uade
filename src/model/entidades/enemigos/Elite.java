package model.entidades.enemigos;

import java.util.List;

import model.entidades.personajes.Personaje;

/**
 * Enemigo fuerte del juego (Orco).
 * <p>Su habilidad especial es Golpe Brutal: hace el 180% de su ataque
 * a un personaje. Es el golpe individual más fuerte de los enemigos.</p>
 */
public class Elite extends Enemigo {
    private static final int DROP = 70; // % de chance de soltar un objeto al morir
    private static final int DROP_COMUN = 45; // % de que el objeto sea común
    private static final int DROP_RARO = 45; // % de que el objeto sea raro (el 10% restante es épico)
    private static final int COSTO_GOLPE_BRUTAL = 25; // Recurso que gasta para usar Golpe Brutal

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
    public Elite(String nombre, int vida, int energia, int ataque, int defensa, int velocidad, int nivel,
            int experiencia) {
        DropStats dropStats = new DropStats(DROP, DROP_COMUN, DROP_RARO);
        super(nombre, vida, energia, ataque, defensa, velocidad, nivel, experiencia, dropStats);
    }

    @Override
    protected int getCostoHabilidad() {
        return COSTO_GOLPE_BRUTAL;
    }

    /**
     * Golpe Brutal: un golpe muy fuerte a un personaje que descuenta el recurso.
     * El daño es el 180% del ataque y se le resta la defensa del personaje.
     *
     * @param objetivo        Personaje que recibe el golpe
     * @param personajesVivos No se usa: la habilidad pega a un solo personaje
     */
    @Override
    public void usarHabilidadEspecial(Personaje objetivo, List<Personaje> personajesVivos) {
        consumirRecursos(COSTO_GOLPE_BRUTAL);
        int danio = (int) Math.round(atacar() * 1.80);
        objetivo.recibirDanio(danio);
    }

    @Override
    public TipoEnemigo getTipo() {
        return TipoEnemigo.ORCO;
    }
}
