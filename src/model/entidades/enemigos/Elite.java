package model.entidades.enemigos;

import java.util.List;

import model.entidades.personajes.Personaje;

/**
 * Orco
 */
public class Elite extends Enemigo {
    private static final int DROP = 70;
    private static final int DROP_COMUN = 45;
    private static final int DROP_RARO = 45;
    // Recurso que gasta en Golpe Brutal
    private static final int COSTO_GOLPE_BRUTAL = 25;

    public Elite(String nombre, int vida, int energia, int ataque, int defensa, int velocidad, int nivel,
            int experiencia) {
        DropStats dropStats = new DropStats(DROP, DROP_COMUN, DROP_RARO);
        super(nombre, vida, energia, ataque, defensa, velocidad, nivel, experiencia, dropStats);
    }

    @Override
    protected int getCostoHabilidad() {
        return COSTO_GOLPE_BRUTAL;
    }

    // Golpe Brutal: un golpe muy fuerte, 180 % de su ataque, a un personaje
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