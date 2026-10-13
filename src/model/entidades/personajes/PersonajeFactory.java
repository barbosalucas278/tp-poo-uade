package model.entidades.personajes;

import helpers.entidades.EstadisticasBasePersonajes;
import helpers.entidades.StatsBaseConfigPerosnajes;

public class PersonajeFactory {
        public static Personaje crearPersonaje(String nombre, TipoPersonaje tipo, String rutaImagen) {

        switch (tipo) {
            case TipoPersonaje.GUERRERO:
                StatsBaseConfigPerosnajes stats = EstadisticasBasePersonajes.obtener(TipoPersonaje.GUERRERO);
                
                return new Guerrero(
                        nombre,
                        stats.getVida(),
                        stats.getRecursoMax(),
                        stats.getAtaque(),
                        stats.getDefensa(),
                        stats.getVelocidad(),
                        rutaImagen);

            case TipoPersonaje.HECHICERO:
                stats = EstadisticasBasePersonajes.obtener(TipoPersonaje.HECHICERO);
                return new Hechicero(
                        nombre,
                        stats.getVida(),
                        stats.getRecursoMax(),
                        stats.getAtaque(),
                        stats.getDefensa(),
                        stats.getVelocidad(),
                        rutaImagen);

            case TipoPersonaje.ASESINO:
                stats = EstadisticasBasePersonajes.obtener(TipoPersonaje.ASESINO);
                return new Asesino(
                        nombre,
                        stats.getVida(),
                        stats.getRecursoMax(),
                        stats.getAtaque(),
                        stats.getDefensa(),
                        stats.getVelocidad(),
                        rutaImagen);

            default:
                throw new IllegalArgumentException("Tipo de personaje inválido");
        }
    }

}
