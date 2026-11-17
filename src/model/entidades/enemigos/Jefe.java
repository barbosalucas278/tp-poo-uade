package model.entidades.enemigos;

import java.util.List;
import model.entidades.personajes.Personaje;

public class Jefe extends Enemigo {
    private static final int DROP = 100;
    private static final int DROP_COMUN = 15;
    private static final int DROP_RARO = 55;
    private boolean furiaActivada;

    public Jefe(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad, int nivel,
            int experienciaOtorgada) {
        DropStats dropStats = new DropStats(DROP, DROP_COMUN, DROP_RARO);
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, nivel, experienciaOtorgada, dropStats);
    }

    public void faseFuria() {
        if (!furiaActivada) {
            setVelocidad(getVelocidad() * 2);
            furiaActivada = true;
        }
    }

    @Override
    public void ejecutarAccion(List<Personaje> party) {
        objetivosValidos(party);
        if (getVidaActual() <= getVidaMax() / 2) {
            faseFuria();
        }
        atacar(seleccionarObjetivo(party));
    }

    @Override
    public Personaje seleccionarObjetivo(List<Personaje> party) {
        return objetivosValidos(party).stream()
                .min((primero, segundo) -> Integer.compare(primero.getVidaActual(), segundo.getVidaActual()))
                .get();
    }

    @Override
    public TipoEnemigo getTipo() {
        return TipoEnemigo.DRAGON;
    }
}
