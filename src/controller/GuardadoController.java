package controller;

import model.partidas.GestorPartida;

// Coordina las operaciones de guardado que solicita la pantalla de slots.
public class GuardadoController {
    public enum EstadoCarga {
        SLOT_VACIO,
        ERROR,
        PARTIDA_COMPLETADA,
        OK
    }

    private final GestorPartida gestorPartida;

    public GuardadoController() {
        this.gestorPartida = GestorPartida.getInstance();
    }

    public boolean existeSlot(String slot) {
        return gestorPartida.existePartidaGuardada(slot);
    }

    public boolean guardarPartidaActual(String slot) {
        return gestorPartida.guardarPartidaActual(slot);
    }

    public EstadoCarga cargarPartida(String slot) {
        if (!gestorPartida.existePartidaGuardada(slot)) {
            return EstadoCarga.SLOT_VACIO;
        }
        if (!gestorPartida.cargarPartidaExistente(slot)) {
            return EstadoCarga.ERROR;
        }
        return gestorPartida.hayNivelPendiente() ? EstadoCarga.OK : EstadoCarga.PARTIDA_COMPLETADA;
    }
}
