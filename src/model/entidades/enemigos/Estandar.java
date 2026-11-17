package model.entidades.enemigos;

import java.util.List;

import model.entidades.personajes.Personaje;

/**
 * Esqueleto
 */
public class Estandar extends Enemigo {
    private static final int DROP = 25;
    private static final int DROP_COMUN = 90;
    private static final int DROP_RARO = 10;
    // Recurso que gasta en Golpe Perforante
    private static final int COSTO_GOLPE_PERFORANTE = 20;

    public Estandar(String nombre, int vida, int energia, int ataque, int defensa, int velocidad, int nivel,
            int experiencia) {
        DropStats dropStats = new DropStats(DROP, DROP_COMUN, DROP_RARO);
        super(nombre, vida, energia, ataque, defensa, velocidad, nivel, experiencia, dropStats);
    }

    @Override
    protected int getCostoHabilidad() {
        return COSTO_GOLPE_PERFORANTE;
    }

    // Golpe Perforante: 120 % de su ataque e ignora la defensa del personaje
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