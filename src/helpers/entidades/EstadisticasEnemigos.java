package helpers.entidades;

import java.util.Map;

import model.entidades.enemigos.TipoEnemigo;

public class EstadisticasEnemigos {
    private static final Map<TipoEnemigo, StatsConfigEnemigos> CONFIGURACIONES = Map.of(
            TipoEnemigo.SLIME, new StatsConfigEnemigos("Slime", 40, 20, 8, 2, 4, 10),
            TipoEnemigo.ESQUELETO, new StatsConfigEnemigos("Esqueleto", 70, 40, 15, 4, 15, 20),
            TipoEnemigo.ORCO, new StatsConfigEnemigos("Orc", 140, 50, 25, 10, 6, 30),
            TipoEnemigo.DRAGON, new StatsConfigEnemigos("Dragon", 500, 100, 40, 15, 10, 100 )
        );

    public static StatsConfigEnemigos obtener(TipoEnemigo tipo) {
        return CONFIGURACIONES.get(tipo);
    }
}
