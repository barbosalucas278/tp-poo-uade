package model.entidades.enemigos;

import java.util.List;

import model.entidades.personajes.Personaje;

/**
 * Minion
 */
public class Minion extends Enemigo {
    private static final int DROP = 40;
    private static final int DROP_COMUN = 75;
    private static final int DROP_RARO = 23;
    // Recurso que gasta en Escupitajo Ácido
    private static final int COSTO_ESCUPITAJO = 10;

    public Minion(String nombre, int vida, int recursoMax, int ataque, int defensa, int velocidad, int nivel,
            int experiencia) {
        DropStats dropStats = new DropStats(DROP, DROP_COMUN, DROP_RARO);
        super(nombre, vida, recursoMax, ataque, defensa, velocidad, nivel, experiencia, dropStats);
    }

    @Override
    protected int getCostoHabilidad() {
        return COSTO_ESCUPITAJO;
    }

    // Escupitajo Ácido: le escupe a un personaje y le hace 150 % de su ataque
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