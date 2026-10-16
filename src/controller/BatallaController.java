package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.DTOs.CombatienteDTO;
import model.DTOs.MapaDTO;
import model.entidades.Entidad;
import model.entidades.enemigos.Enemigo;
import model.entidades.personajes.Personaje;
import model.partidas.GestorPartida;
import model.partidas.Mapa;
import model.partidas.Nivel;
import model.partidas.Partida;

public class BatallaController {
    private final GestorPartida gestorPartida;

    public BatallaController() {
        this.gestorPartida = GestorPartida.getInstance();
    }

    public MapaDTO getMapaActual() {
        Mapa mapa = obtenerMapaActual();
        Nivel nivel = mapa == null ? null : mapa.getNivelActual();
        if (nivel == null) {
            return null;
        }
        return new MapaDTO(mapa.getNumeroMapa(), nivel.getNumeroNivel(), mapa.getNiveles().size());
    }

    public List<CombatienteDTO> getPartyActual() {
        Partida partida = gestorPartida.getPartidaActual();
        if (partida == null) {
            return Collections.emptyList();
        }
        List<CombatienteDTO> party = new ArrayList<>();
        for (Personaje personaje : partida.getParty()) {
            party.add(crearCombatiente(personaje, personaje.getRutaImagen(), personaje.getNombreRecurso()));
        }
        return party;
    }

    public List<CombatienteDTO> getEnemigosActuales() {
        Mapa mapa = obtenerMapaActual();
        Nivel nivel = mapa == null ? null : mapa.getNivelActual();
        if (nivel == null) {
            return Collections.emptyList();
        }
        List<CombatienteDTO> enemigos = new ArrayList<>();
        for (Enemigo enemigo : nivel.getEnemigos()) {
            enemigos.add(crearCombatiente(enemigo, null, "VIGOR"));
        }
        return Collections.unmodifiableList(enemigos);
    }

    public int getCantidadItemsInventario() {
        Partida partida = gestorPartida.getPartidaActual();
        return partida == null ? 0 : partida.getInventarioGlobal().getItems().size();
    }

    private Mapa obtenerMapaActual() {
        Partida partida = gestorPartida.getPartidaActual();
        return partida == null ? null : partida.getMapaActual();
    }

    private CombatienteDTO crearCombatiente(Entidad entidad, String rutaImagen, String nombreRecurso) {
        return new CombatienteDTO(entidad.getNombre(), entidad.getClass().getSimpleName(), rutaImagen,
                entidad.getVidaActual(), entidad.getVidaMax(), nombreRecurso,
                entidad.getRecursoActual(), entidad.getRecursoMax(), entidad.getEnergiaActual());
    }

    // TODO: incorporar el motor de combate y exponer acciones, habilidades y uso del inventario.
}
