package rpg.controller;

import rpg.model.jugador.Jugador;
import rpg.persistence.GestorArchivos;

public class GestorPartida {

    private static GestorPartida instance;
    private Jugador jugadorActual;

    // Constructor privado para aplicar el patrón Singleton
    private GestorPartida() {}

    /**
     * Obtiene la instancia única del GestorPartida.
     */
    public static GestorPartida getInstance() {
        if (instance == null) {
            instance = new GestorPartida();
        }
        return instance;
    }

    /**
     * Guarda el estado del jugador actual en el slot especificado.
     * @param nombreSlot Nombre del archivo de guardado (ej: "slot1")
     * @return true si se guardó correctamente, false en caso contrario
     */
    public boolean guardarPartidaActual(String nombreSlot) {
        if (jugadorActual != null) {
            return GestorArchivos.guardarPartida(jugadorActual, nombreSlot);
        }
        return false;
    }

    /**
     * Carga la partida existente desde un slot y establece al jugador actual.
     * @param nombreSlot Nombre del archivo a cargar (ej: "slot1")
     * @return true si se cargó con éxito, false en caso contrario
     */
    public boolean cargarPartidaExistente(String nombreSlot) {
        Jugador j = GestorArchivos.cargarPartida(nombreSlot);
        if (j != null) {
            this.jugadorActual = j;
            return true;
        }
        return false;
    }

    /**
     * Inicia una nueva partida asignando un nuevo jugador.
     * @param nombreJugador Nombre elegido por el usuario
     */
    public void nuevaPartida(String nombreJugador) {
        this.jugadorActual = new Jugador(nombreJugador);
    }

    // Getters y Setters
    public Jugador getJugadorActual() {
        return jugadorActual;
    }

    public void setJugadorActual(Jugador jugadorActual) {
        this.jugadorActual = jugadorActual;
    }
}