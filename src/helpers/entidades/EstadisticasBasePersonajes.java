package helpers.entidades;

import java.util.Map;
import model.entidades.personajes.TipoPersonaje;


public class EstadisticasBasePersonajes {
        private static final Map<TipoPersonaje, StatsBaseConfigPerosnajes> CONFIGURACIONES = Map.of(
            TipoPersonaje.GUERRERO, new StatsBaseConfigPerosnajes(200, 50, 40, 15, 20),
            TipoPersonaje.HECHICERO, new StatsBaseConfigPerosnajes(100, 100, 35, 5, 30),
            TipoPersonaje.ASESINO, new StatsBaseConfigPerosnajes(150, 60, 50, 10, 40)
        );

    public static StatsBaseConfigPerosnajes obtener(TipoPersonaje tipo) {
        return CONFIGURACIONES.get(tipo);
    }
}
