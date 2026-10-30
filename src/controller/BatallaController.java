package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import helpers.archivos.ResourcesHelper;
import helpers.entidades.CalculadoraDeExperiencia;
import helpers.entidades.GeneradorDeBotin;
import model.Combate.MotorCombate;
import model.DTOs.CombatienteDTO;
import model.DTOs.ItemInventarioDTO;
import model.DTOs.MapaDTO;
import model.entidades.Entidad;
import model.entidades.enemigos.Enemigo;
import model.entidades.personajes.Personaje;
import model.objetos.Item;
import model.objetos.ItemConsumible;
import model.partidas.GestorPartida;
import model.partidas.Mapa;
import model.partidas.Nivel;
import model.partidas.Partida;

public class BatallaController {
    private final GestorPartida gestorPartida;
    private final MotorCombate motorCombate;

    public BatallaController() {
        this.gestorPartida = GestorPartida.getInstance();
        Partida partidaActual = this.gestorPartida.getPartidaActual();
        this.motorCombate = new MotorCombate(partidaActual.getParty(),
                partidaActual.getMapaActual().getNivelActual().getEnemigos());
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
            enemigos.add(crearCombatiente(enemigo,
                    ResourcesHelper.getRutaImagenEnemigo(enemigo.getTipo()), "VIGOR"));
        }
        return Collections.unmodifiableList(enemigos);
    }

    public int getCantidadItemsInventario() {
        Partida partida = gestorPartida.getPartidaActual();
        return partida == null ? 0 : partida.getInventarioGlobal().getItems().size();
    }

    public int getCapacidadInventario() {
        Partida partida = gestorPartida.getPartidaActual();
        return partida == null ? 0 : partida.getInventarioGlobal().getCapacidadMax();
    }

    public List<ItemInventarioDTO> getItemsInventario() {
        Partida partida = gestorPartida.getPartidaActual();

        List<ItemInventarioDTO> resultado = new ArrayList<>();
        List<Item> items = partida.getInventarioGlobal().getItems();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            resultado.add(new ItemInventarioDTO(i, item.getNombre(), item.getDescripcion(), item.getRareza(),
                    item instanceof ItemConsumible));
        }
        return resultado;
    }

    /** Devuelve null si el uso es válido, o el motivo para mostrar en la vista. */
    public String getMotivoNoPuedeUsarItem(int indicePersonaje, int indiceItem) {
        try {
            validarUsoItem(indicePersonaje, indiceItem);
            return null;
        } catch (IllegalArgumentException | IllegalStateException e) {
            return e.getMessage();
        }
    }

    public void resolverUsoItem(int indicePersonaje, int indiceItem) {
        ItemConsumible item = validarUsoItem(indicePersonaje, indiceItem);
        Partida partida = gestorPartida.getPartidaActual();
        motorCombate.usarItem(partida.getParty().get(indicePersonaje), item, partida.getInventarioGlobal());
    }

    private ItemConsumible validarUsoItem(int indicePersonaje, int indiceItem) {
        Partida partida = gestorPartida.getPartidaActual();
        if (partida == null || verificarEstadoCombate() != null || getMapaActual() == null) {
            throw new IllegalStateException("No hay un combate activo para usar objetos.");
        }
        if (indicePersonaje < 0 || indicePersonaje != getIndicePersonajeEnTurno()) {
            throw new IllegalStateException("Esperá el turno de un personaje para abrir el inventario.");
        }
        List<Item> items = partida.getInventarioGlobal().getItems();
        if (indiceItem < 0 || indiceItem >= items.size()) {
            throw new IllegalArgumentException("Elegí un objeto del inventario.");
        }
        if (!(items.get(indiceItem) instanceof ItemConsumible)) {
            throw new IllegalArgumentException("Los equipables no se pueden usar como consumibles en batalla.");
        }
        ItemConsumible consumible = (ItemConsumible) items.get(indiceItem);
        consumible.validarUso(partida.getParty().get(indicePersonaje));
        return consumible;
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

    // TODO: exponer habilidades.
    public void avanzarCombate() {
        motorCombate.avanzarEnergia();

        Entidad actor = motorCombate.getActorActual();

        if (!(actor instanceof Enemigo)) {
            return; // Todavía no hay actor o esperamos al jugador.
        }

        Enemigo enemigo = (Enemigo) actor;

        List<Personaje> personajesVivos = new ArrayList<>();

        for (Personaje personaje : gestorPartida.getPartidaActual().getParty()) {
            if (personaje.estaVivo()) {
                personajesVivos.add(personaje);
            }
        }

        if (personajesVivos.isEmpty()) {
            return;
        }

        Personaje objetivo = enemigo.seleccionarObjetivo(personajesVivos);

        motorCombate.ataque(enemigo, objetivo);
    }

    public int getIndicePersonajeEnTurno() {
        Entidad actor = motorCombate.getActorActual();
        if (!(actor instanceof Personaje) || !actor.estaVivo()) {
            return -1;
        }
        return gestorPartida.getPartidaActual().getParty().indexOf(actor);
    }

    public boolean resolverAtaque(int indicePersonaje, int indiceEnemigo) {
        List<Personaje> party = gestorPartida.getPartidaActual().getParty();
        Mapa mapa = obtenerMapaActual();
        Nivel nivel = mapa == null ? null : mapa.getNivelActual();
        if (nivel == null || indicePersonaje < 0 || indicePersonaje >= party.size()
                || indiceEnemigo < 0 || indiceEnemigo >= nivel.getEnemigos().size()) {
            return false;
        }

        Personaje personaje = party.get(indicePersonaje);
        Enemigo enemigo = nivel.getEnemigos().get(indiceEnemigo);
        personaje.resetEstado();
        if (indicePersonaje != getIndicePersonajeEnTurno() || !enemigo.estaVivo()) {
            return false;
        }
        motorCombate.ataque(personaje, enemigo);
        return true;
    }

    public EstadoCombate verificarEstadoCombate() {
        Partida partida = gestorPartida.getPartidaActual();
        Mapa mapa = obtenerMapaActual();
        Nivel nivel = mapa == null ? null : mapa.getNivelActual();
        EstadoCombate estado = null;
        if (nivel == null) {
            // TOOD: Evaluar throw an exception
            return null;
        }

        boolean todosEnemigosDerrotados = nivel.getEnemigos().stream().allMatch(enemigo -> !enemigo.estaVivo());
        boolean todosPersonajesDerrotados = partida.getParty().stream().allMatch(personaje -> !personaje.estaVivo());

        if (todosEnemigosDerrotados) {
            estado = EstadoCombate.VICTORIA;
        } else if (todosPersonajesDerrotados) {
            estado = EstadoCombate.DERROTA;
        }
        return estado;
    }

    public boolean continuarAlSiguienteNivel() {
        Mapa mapa = obtenerMapaActual();
        Nivel nivel = mapa == null ? null : mapa.getNivelActual();
        if (nivel == null || verificarEstadoCombate() != EstadoCombate.VICTORIA) {
            throw new IllegalStateException("El nivel solo se puede completar después de una victoria.");
        }

        int experienciaGanada = CalculadoraDeExperiencia.CalcularExperiencia(nivel.getEnemigos());
        List<Item> itemsGanados = GeneradorDeBotin.SortearBotin(nivel.getEnemigos());
        gestorPartida.otorgarExperiencia(experienciaGanada);
        gestorPartida.getPartidaActual().setInventarioGlobal(itemsGanados);
        nivel.setCompletado(true);
        if (mapa.getNivelActual() == null) {
            gestorPartida.avanzarMapa();
        }
        return getMapaActual() != null;
    }

    public void resolverDefensa(int indicePersonaje) {
        List<Personaje> party = gestorPartida.getPartidaActual().getParty();

        Personaje personaje = party.get(indicePersonaje);

        personaje.defenderse();
        motorCombate.defensa(personaje);
    }
}
