package helpers.entidades;

import java.util.Map;

import model.entidades.personajes.TipoPersonaje;


public class EstadisticasBasePersonajes {
        private static final Map<TipoPersonaje, StatsBaseConfigPerosnajes> CONFIGURACIONES = Map.of(
            TipoPersonaje.GUERRERO, new StatsBaseConfigPerosnajes(100, 50, 20, 15, 10),
            TipoPersonaje.HECHICERO, new StatsBaseConfigPerosnajes(80, 100, 25, 5, 15),
            TipoPersonaje.ASESINO, new StatsBaseConfigPerosnajes(90, 60, 30, 10, 20)
        );

    public static StatsBaseConfigPerosnajes obtener(TipoPersonaje tipo) {
        return CONFIGURACIONES.get(tipo);
    }
}
