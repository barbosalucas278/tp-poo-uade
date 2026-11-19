package model.entidades.personajes;

import java.util.HashMap;
import java.util.Map;

import helpers.entidades.EstadisticasBasePersonajes;
import helpers.entidades.StatsBaseConfigPerosnajes;

public class PersonajeFactory {
    private static final Map<TipoPersonaje, CreadorPersonaje> CREADORES = cargarCreadores();

    private static Map<TipoPersonaje, CreadorPersonaje> cargarCreadores() {
        Map<TipoPersonaje, CreadorPersonaje> creadores = new HashMap<>();
        creadores.put(TipoPersonaje.GUERRERO, new CreadorGuerrero());
        creadores.put(TipoPersonaje.MAGO, new CreadorMago());
        creadores.put(TipoPersonaje.CURADOR, new CreadorCurador());
        creadores.put(TipoPersonaje.ARQUERO, new CreadorArquero());
        creadores.put(TipoPersonaje.ASESINO, new CreadorAsesino());
        creadores.put(TipoPersonaje.INVOCADOR, new CreadorInvocador());
        return creadores;
    }

    public static Personaje crearPersonaje(String nombre, TipoPersonaje tipo, String rutaImagen) {
        CreadorPersonaje creador = CREADORES.get(tipo);
        if (creador == null) {
            throw new IllegalArgumentException("Tipo de personaje inválido");
        }

        StatsBaseConfigPerosnajes stats = EstadisticasBasePersonajes.obtener(tipo);
        return creador.crear(nombre, stats.getVida(), stats.getRecursoMax(), stats.getAtaque(),
                stats.getDefensa(), stats.getVelocidad(), rutaImagen);
    }
}
