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

    public Elite(String nombre, int vida, int energia, int ataque, int defensa, int velocidad, int nivel,
            int experiencia) {
        DropStats dropStats = new DropStats(DROP, DROP_COMUN, DROP_RARO);
        super(nombre, vida, energia, ataque, defensa, velocidad, nivel, experiencia, dropStats);
    }

    @Override
    public TipoEnemigo getTipo() {
        return TipoEnemigo.ORCO;
    }

    @Override
    public Personaje seleccionarObjetivo(List<Personaje> party) {
        return objetivosValidos(party).stream()
                .max((primero, segundo) -> Integer.compare(primero.getAtaqueBase(), segundo.getAtaqueBase()))
                .get();
    }
}
