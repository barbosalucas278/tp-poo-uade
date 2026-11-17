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

            case TipoPersonaje.MAGO:
                stats = EstadisticasBasePersonajes.obtener(TipoPersonaje.MAGO);
                return new Mago(
                        nombre,
                        stats.getVida(),
                        stats.getRecursoMax(),
                        stats.getAtaque(),
                        stats.getDefensa(),
                        stats.getVelocidad(),
                        rutaImagen);

            case TipoPersonaje.CURADOR:
                stats = EstadisticasBasePersonajes.obtener(TipoPersonaje.CURADOR);
                return new Curador(
                        nombre,
                        stats.getVida(),
                        stats.getRecursoMax(),
                        stats.getAtaque(),
                        stats.getDefensa(),
                        stats.getVelocidad(),
                        rutaImagen);

            case TipoPersonaje.ARQUERO:
                stats = EstadisticasBasePersonajes.obtener(TipoPersonaje.ARQUERO);
                return new Arquero(
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

            case TipoPersonaje.INVOCADOR:
                stats = EstadisticasBasePersonajes.obtener(TipoPersonaje.INVOCADOR);
                return new Invocador(
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
