package model.entidades.enemigos;

import java.util.HashMap;
import java.util.Map;

import helpers.entidades.EstadisticasEnemigos;

public class EnemigoFactory {
    private static final Map<TipoEnemigo, ConfiguracionCreacion> CONFIGURACIONES = cargarConfiguraciones();

    private static Map<TipoEnemigo, ConfiguracionCreacion> cargarConfiguraciones() {
        Map<TipoEnemigo, ConfiguracionCreacion> configuraciones = new HashMap<>();
        configuraciones.put(TipoEnemigo.SLIME, new ConfiguracionCreacion(new CreadorSlime(), 10, 2, 1, 5));
        configuraciones.put(TipoEnemigo.ESQUELETO, new ConfiguracionCreacion(new CreadorEsqueleto(), 15, 3, 1, 10));
        configuraciones.put(TipoEnemigo.ORCO, new ConfiguracionCreacion(new CreadorOrco(), 25, 4, 2, 20));
        configuraciones.put(TipoEnemigo.DRAGON, new ConfiguracionCreacion(new CreadorDragon(), 60, 5, 2, 50));
        return configuraciones;
    }

    public static Enemigo crearEnemigo(TipoEnemigo tipo, int nivel) {
        ConfiguracionCreacion configuracion = CONFIGURACIONES.get(tipo);
        if (configuracion == null) {
            throw new IllegalArgumentException("Tipo de enemigo inválido");
        }

        return configuracion.crear(EstadisticasEnemigos.obtener(tipo), nivel);
    }
}
