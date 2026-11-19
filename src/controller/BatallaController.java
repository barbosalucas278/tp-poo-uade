package controller;

import java.util.Collections;
import java.util.List;

import model.Combate.EstadoCombate;
import model.Combate.MotorCombate;
import model.DTOs.CombatienteDTO;
import model.DTOs.ItemInventarioDTO;
import model.DTOs.MapaDTO;
import model.entidades.Entidad;
import model.entidades.enemigos.Enemigo;
import model.entidades.personajes.Personaje;
import model.objetos.Item;
import model.partidas.GestorPartida;
import model.partidas.Mapa;
import model.partidas.Nivel;
import model.partidas.Partida;

public class BatallaController {
    private final GestorPartida gestorPartida;
    private final Partida partida;
    private final Nivel nivelCombate;
    private final MotorCombate motorCombate;

    public BatallaController() {
        gestorPartida = GestorPartida.getInstance();
        partida = gestorPartida.getPartidaActual();
        nivelCombate = obtenerNivelActual();
        motorCombate = nivelCombate == null ? null : new MotorCombate(partida.getParty(), nivelCombate.getEnemigos());
    }

    public MapaDTO getMapaActual() {
        return BatallaMapper.aMapaDTO(obtenerMapaActual());
    }

    public List<CombatienteDTO> getPartyActual() {
        return partida == null ? Collections.emptyList() : BatallaMapper.aPartyDTO(partida.getParty());
    }

    public List<CombatienteDTO> getEnemigosActuales() {
        Nivel nivel = obtenerNivelActual();
        return nivel == null ? Collections.emptyList() : BatallaMapper.aEnemigosDTO(nivel.getEnemigos());
    }

    public int getCantidadItemsInventario() {
        return partida == null ? 0 : partida.getInventarioGlobal().getItems().size();
    }

    public int getCapacidadInventario() {
        return partida == null ? 0 : partida.getInventarioGlobal().getCapacidadMax();
    }

    public List<ItemInventarioDTO> getItemsInventario() {
        return partida == null ? Collections.emptyList() : BatallaMapper.aItemsDTO(partida.getInventarioGlobal().getItems());
    }

    public void avanzarCombate() {
        if (hayCombateActual()) {
            motorCombate.avanzarCombate();
        }
    }

    public EstadoCombate verificarEstadoCombate() {
        return hayCombateActual() ? motorCombate.getEstado() : null;
    }

    public int getIndicePersonajeEnTurno() {
        Entidad actor = hayCombateActual() ? motorCombate.getActorActual() : null;
        if (!(actor instanceof Personaje) || !actor.estaVivo()) {
            return -1;
        }
        return partida.getParty().indexOf(actor);
    }

    public boolean resolverAtaque(int indicePersonaje, int indiceEnemigo) {
        if (!hayCombateActual()) {
            return false;
        }
        Personaje personaje = seleccionar(partida.getParty(), indicePersonaje);
        Enemigo enemigo = seleccionar(nivelCombate.getEnemigos(), indiceEnemigo);
        try {
            motorCombate.ataque(personaje, enemigo);
            return true;
        } catch (IllegalArgumentException | IllegalStateException e) {
            return false;
        }
    }

    public void resolverDefensa(int indicePersonaje) {
        if (!hayCombateActual()) {
            throw new IllegalStateException("No hay un combate activo para defenderse.");
        }
        motorCombate.defensa(seleccionar(partida.getParty(), indicePersonaje));
    }

    public void resolverHabilidadEspecial(int indicePersonaje, int indiceEnemigo) {
        if (!hayCombateActual() || motorCombate.getEstado() != null) {
            throw new IllegalStateException("No hay un combate activo para usar habilidades.");
        }
        Personaje personaje = seleccionar(partida.getParty(), indicePersonaje);
        if (personaje == null) {
            throw new IllegalArgumentException("Seleccioná un personaje.");
        }
        Enemigo enemigo = seleccionar(nivelCombate.getEnemigos(), indiceEnemigo);
        motorCombate.usarHabilidadEspecial(personaje, enemigo);
    }

    /** Devuelve null si el uso es válido, o el motivo para mostrar en la vista. */
    public String getMotivoNoPuedeUsarItem(int indicePersonaje, int indiceItem) {
        try {
            Item item = seleccionarItem(indiceItem);
            Personaje personaje = seleccionar(partida.getParty(), indicePersonaje);
            motorCombate.validarUsoItem(personaje, item);
            return null;
        } catch (IllegalArgumentException | IllegalStateException e) {
            return e.getMessage();
        }
    }

    public void resolverUsoItem(int indicePersonaje, int indiceItem) {
        Item item = seleccionarItem(indiceItem);
        Personaje personaje = seleccionar(partida.getParty(), indicePersonaje);
        motorCombate.usarItem(personaje, item, partida.getInventarioGlobal());
    }

    public boolean continuarAlSiguienteNivel() {
        return gestorPartida.completarNivel(nivelCombate);
    }

    private Item seleccionarItem(int indiceItem) {
        if (!hayCombateActual() || motorCombate.getEstado() != null) {
            throw new IllegalStateException("No hay un combate activo para usar objetos.");
        }
        Item item = seleccionar(partida.getInventarioGlobal().getItems(), indiceItem);
        if (item == null) {
            throw new IllegalArgumentException("Elegí un objeto del inventario.");
        }
        return item;
    }

    private boolean hayCombateActual() {
        return nivelCombate != null && partida == gestorPartida.getPartidaActual() && nivelCombate == obtenerNivelActual();
    }

    private Mapa obtenerMapaActual() {
        return partida == null ? null : partida.getMapaActual();
    }

    private Nivel obtenerNivelActual() {
        Mapa mapa = obtenerMapaActual();
        return mapa == null ? null : mapa.getNivelActual();
    }

    private static <T> T seleccionar(List<T> opciones, int indice) {
        return indice < 0 || indice >= opciones.size() ? null : opciones.get(indice);
    }
}
