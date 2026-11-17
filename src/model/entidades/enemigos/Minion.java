package model.entidades.enemigos;

import java.util.List;

import model.entidades.personajes.Personaje;

/**
 * Enemigo más débil del juego (Slime).
 * <p>Su habilidad especial es Escupitajo Ácido: hace el 150% de su ataque
 * a un personaje. Es la habilidad más barata, así que la puede usar seguido.</p>
 */
public class Minion extends Enemigo {
    private static final int DROP = 40; // % de chance de soltar un objeto al morir
    private static final int DROP_COMUN = 75; // % de que el objeto sea común
    private static final int DROP_RARO = 23; // % de que el objeto sea raro (el 2% restante es épico)
    private static final int COSTO_ESCUPITAJO = 10; // Recurso que gasta para usar Escupitajo Ácido

    /**
     * @param nombre      Nombre del enemigo
     * @param vida        Vida máxima
     * @param recursoMax  Recurso máximo para usar la habilidad
     * @param ataque      Ataque base
     * @param defensa     Defensa base
     * @param velocidad   Energía que gana por tick de combate
     * @param nivel       Nivel del enemigo
     * @param experiencia Experiencia que da al ser derrotado
     */
    public Minion(String nombre, int vida, int recursoMax, int ataque, int defensa, int velocidad, int nivel,
            int experiencia) {
        DropStats dropStats = new DropStats(DROP, DROP_COMUN, DROP_RARO);
        super(nombre, vida, recursoMax, ataque, defensa, velocidad, nivel, experiencia, dropStats);
    }

    @Override
    protected int getCostoHabilidad() {
        return COSTO_ESCUPITAJO;
    }

    /**
     * Escupitajo Ácido: le escupe a un personaje y descuenta el recurso.
     * El daño es el 150% del ataque y se le resta la defensa del personaje.
     *
     * @param objetivo        Personaje que recibe el escupitajo
     * @param personajesVivos No se usa: la habilidad pega a un solo personaje
     */
    @Override
    public void usarHabilidadEspecial(Personaje objetivo, List<Personaje> personajesVivos) {
        consumirRecursos(COSTO_ESCUPITAJO);
        int danio = (int) Math.round(atacar() * 1.50);
        objetivo.recibirDanio(danio);
    }

    @Override
    public TipoEnemigo getTipo() {
        return TipoEnemigo.SLIME;
    }
}
