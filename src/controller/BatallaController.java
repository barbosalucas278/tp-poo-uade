package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import helpers.archivos.ResourcesHelper;
import model.Combate.MotorCombate;
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

    private Mapa obtenerMapaActual() {
        Partida partida = gestorPartida.getPartidaActual();
        return partida == null ? null : partida.getMapaActual();
    }

    private CombatienteDTO crearCombatiente(Entidad entidad, String rutaImagen, String nombreRecurso) {
        return new CombatienteDTO(entidad.getNombre(), entidad.getClass().getSimpleName(), rutaImagen,
                entidad.getVidaActual(), entidad.getVidaMax(), nombreRecurso,
                entidad.getRecursoActual(), entidad.getRecursoMax(), entidad.getEnergiaActual());
    }

    // TODO: incorporar el motor de combate y exponer acciones, habilidades y uso
    // del inventario.
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

    public void resolverAtaque(int indicePersonaje, int indiceEnemigo) {
        List<Personaje> party = gestorPartida.getPartidaActual().getParty();
        Mapa mapa = obtenerMapaActual();
        Nivel nivel = mapa == null ? null : mapa.getNivelActual();
        if (nivel == null || indicePersonaje < 0 || indicePersonaje >= party.size()
                || indiceEnemigo < 0 || indiceEnemigo >= nivel.getEnemigos().size()) {
            return;
        }

        Personaje personaje = party.get(indicePersonaje);
        Enemigo enemigo = nivel.getEnemigos().get(indiceEnemigo);
        motorCombate.ataque(personaje, enemigo);
    }

    public EstadoCombate verificarEstadoCombate() {
        Partida partida = gestorPartida.getPartidaActual();
        Mapa mapa = obtenerMapaActual();
        Nivel nivel = mapa == null ? null : mapa.getNivelActual();
        EstadoCombate estado = null;
        if (nivel == null) {
            //TOOD: Evaluar throw an exception
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

    /** Confirma la victoria y devuelve si hay otro nivel disponible para preparar. */
    public boolean continuarAlSiguienteNivel() {
        Mapa mapa = obtenerMapaActual();
        Nivel nivel = mapa == null ? null : mapa.getNivelActual();
        if (nivel == null || verificarEstadoCombate() != EstadoCombate.VICTORIA) {
            throw new IllegalStateException("El nivel solo se puede completar después de una victoria.");
        }

        nivel.setCompletado(true);
        if (mapa.getNivelActual() == null) {
            mapa.setCompletado(true);
            // TODO(progreso): generar/desbloquear el próximo mapa al completar el actual.
        }
        return getMapaActual() != null;
    }
}
