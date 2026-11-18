package controller;

import model.partidas.GestorPartida;

/**
 * Controlador de la pantalla de guardado y carga.
 * Hace de intermediario entre la vista y GestorPartida.
 */
public class GuardadoController {

    private GestorPartida gestorPartida;

    public GuardadoController() {
        gestorPartida = GestorPartida.getInstance();
    }

    /**
     * Indica si el slot tiene una partida guardada.
     * @param slot nombre del slot (por ejemplo "slot1")
     * @return true si existe una partida guardada en el slot
     */
    public boolean existePartidaGuardada(String slot) {
        return gestorPartida.existePartidaGuardada(slot);
    }

    /**
     * Guarda la partida actual en el slot.
     * @param slot nombre del slot donde se guarda
     * @return true si se guardó; false si no hay una partida activa
     */
    public boolean guardarPartida(String slot) {
        return gestorPartida.guardarPartidaActual(slot);
    }

    /**
     * Carga la partida guardada en el slot y la deja como partida actual.
     * @param slot nombre del slot que se carga
     * @return true si se cargó; false si el archivo está dañado
     */
    public boolean cargarPartida(String slot) {
        return gestorPartida.cargarPartidaExistente(slot);
    }

    /**
     * Indica si a la partida actual le quedan niveles por jugar.
     * @return true si hay al menos un nivel sin completar
     */
    public boolean hayNivelPendiente() {
        return gestorPartida.hayNivelPendiente();
    }
}
