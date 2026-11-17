package helpers.entidades;

import java.util.Map;
import model.entidades.personajes.TipoPersonaje;

/**
 * 
 */

public class EstadisticasBasePersonajes {
        private static final Map<TipoPersonaje, StatsBaseConfigPerosnajes> CONFIGURACIONES = Map.of(
            TipoPersonaje.ASESINO, new StatsBaseConfigPerosnajes(125, 75, 32, 7, 40),
            TipoPersonaje.ARQUERO, new StatsBaseConfigPerosnajes(145, 80, 35, 8, 32),
            TipoPersonaje.CURADOR, new StatsBaseConfigPerosnajes(155, 120, 25, 11, 25),
            TipoPersonaje.GUERRERO, new StatsBaseConfigPerosnajes(200, 55, 30, 15, 20),
            TipoPersonaje.INVOCADOR, new StatsBaseConfigPerosnajes(135, 120, 33, 8, 26),
            TipoPersonaje.MAGO, new StatsBaseConfigPerosnajes(110, 110, 40, 5, 28)
        );

    public static StatsBaseConfigPerosnajes obtener(TipoPersonaje tipo) {
        return CONFIGURACIONES.get(tipo);
    }
}
