package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import helpers.archivos.ResourcesHelper;
import model.Combate.EstadoCombate;
import model.Combate.MotorCombate;
import model.DTOs.CombatienteDTO;
import model.DTOs.ItemInventarioDTO;
import model.DTOs.MapaDTO;
import model.DTOs.ResultadoRecompensaDTO;
import model.entidades.Entidad;
import model.entidades.enemigos.Enemigo;
import model.entidades.personajes.Personaje;
import model.objetos.Item;
import model.objetos.ItemConsumible;
import model.objetos.ItemEquipable;
import model.objetos.TipoObjeto;
import model.partidas.GestorPartida;
import model.partidas.Mapa;
import model.partidas.Nivel;
import model.partidas.Partida;
import model.partidas.ResultadoRecompensa;

public class BatallaController {
    private static final Logger LOGGER = Logger.getLogger(BatallaController.class.getName());
    private final GestorPartida gestorPartida;
    private MotorCombate motorCombate;
    private ResultadoRecompensa resultadoVictoriaPendiente;
    private EstadoCombate ultimoEstadoLogueado;

    public BatallaController() {
        this.gestorPartida = GestorPartida.getInstance();
        Partida partidaActual = this.gestorPartida.getPartidaActual();
        this.motorCombate = crearMotorCombate();
        LOGGER.info("Inicio de batalla: " + (partidaActual == null ? "sin partida" : partidaActual.getNombre()));
    }

    private MotorCombate crearMotorCombate() {
        Partida partidaActual = gestorPartida.getPartidaActual();
        return new MotorCombate(partidaActual.getParty(),
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
        Partida partida = gestorPartida.getPartidaActual();
        validarIndicePersonaje(partida, indicePersonaje);
        ItemConsumible item = obtenerConsumiblePorIndice(partida, indiceItem);
        motorCombate.usarItem(partida.getParty().get(indicePersonaje), item, partida.getInventarioGlobal());
    }

    public String getMotivoNoPuedeEquiparItem(int indicePersonaje, int indiceItem) {
        try {
            obtenerEquipableValido(indicePersonaje, indiceItem);
            return null;
        } catch (IllegalArgumentException | IllegalStateException e) {
            return e.getMessage();
        }
    }

    public void resolverEquiparItem(int indicePersonaje, int indiceItem) {
        Partida partida = gestorPartida.getPartidaActual();
        validarIndicePersonaje(partida, indicePersonaje);
        ItemEquipable item = obtenerEquipablePorIndice(partida, indiceItem);
        motorCombate.equiparItem(partida.getParty().get(indicePersonaje), item, partida);
    }

    public String getMotivoNoPuedeDesequiparItem(int indicePersonaje, TipoObjeto tipo) {
        try {
            obtenerEquipadoValido(indicePersonaje, tipo);
            return null;
        } catch (IllegalArgumentException | IllegalStateException e) {
            return e.getMessage();
        }
    }

    public void resolverDesequiparItem(int indicePersonaje, TipoObjeto tipo) {
        Partida partida = gestorPartida.getPartidaActual();
        validarIndicePersonaje(partida, indicePersonaje);
        ItemEquipable item = obtenerEquipadoPorTipo(partida, indicePersonaje, tipo);
        motorCombate.desequiparItem(partida.getParty().get(indicePersonaje), item, partida);
    }

    public List<TipoObjeto> getSlotsEquipados(int indicePersonaje) {
        Partida partida = gestorPartida.getPartidaActual();
        if (partida == null || indicePersonaje < 0 || indicePersonaje >= partida.getParty().size()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(partida.getParty().get(indicePersonaje).getEquipamiento().keySet());
    }

    private ItemEquipable obtenerEquipableValido(int indicePersonaje, int indiceItem) {
        validarCambioEquipamiento(indicePersonaje);
        return obtenerEquipablePorIndice(gestorPartida.getPartidaActual(), indiceItem);
    }

    private ItemEquipable obtenerEquipablePorIndice(Partida partida, int indiceItem) {
        List<Item> items = partida.getInventarioGlobal().getItems();
        if (indiceItem < 0 || indiceItem >= items.size() || !(items.get(indiceItem) instanceof ItemEquipable)) {
            throw new IllegalArgumentException("Elegí un equipable disponible.");
        }
        return (ItemEquipable) items.get(indiceItem);
    }

    private ItemEquipable obtenerEquipadoValido(int indicePersonaje, TipoObjeto tipo) {
        validarCambioEquipamiento(indicePersonaje);
        return obtenerEquipadoPorTipo(gestorPartida.getPartidaActual(), indicePersonaje, tipo);
    }

    private ItemEquipable obtenerEquipadoPorTipo(Partida partida, int indicePersonaje, TipoObjeto tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Elegí un slot válido.");
        }
        Personaje personaje = partida.getParty().get(indicePersonaje);
        ItemEquipable item = personaje.getEquipamiento().get(tipo);
        if (item == null) {
            throw new IllegalArgumentException("No hay un objeto equipado en ese slot.");
        }
        return item;
    }

    private void validarCambioEquipamiento(int indicePersonaje) {
        Partida partida = gestorPartida.getPartidaActual();
        if (partida == null || motorCombate.getEstadoCombate() != EstadoCombate.EN_CURSO
                || getMapaActual() == null) {
            throw new IllegalStateException("No hay un combate activo para cambiar el equipamiento.");
        }
        validarIndicePersonaje(partida, indicePersonaje);
        motorCombate.validarActorDeTurno(partida.getParty().get(indicePersonaje));
    }

    private void validarIndicePersonaje(Partida partida, int indicePersonaje) {
        if (partida == null || indicePersonaje < 0 || indicePersonaje >= partida.getParty().size()) {
            throw new IllegalArgumentException("Elegí un personaje válido.");
        }
    }

    private ItemConsumible obtenerConsumiblePorIndice(Partida partida, int indiceItem) {
        if (partida == null) {
            throw new IllegalStateException("No hay una partida activa.");
        }
        List<Item> items = partida.getInventarioGlobal().getItems();
        if (indiceItem < 0 || indiceItem >= items.size()) {
            throw new IllegalArgumentException("Elegí un objeto del inventario.");
        }
        if (!(items.get(indiceItem) instanceof ItemConsumible)) {
            throw new IllegalArgumentException("Los equipables no se pueden usar como consumibles en batalla.");
        }
        return (ItemConsumible) items.get(indiceItem);
    }

    public String getMotivoNoPuedeUsarHabilidad(int indicePersonaje, boolean objetivoAliado, int indiceObjetivo) {
        try {
            validarHabilidad(indicePersonaje, objetivoAliado, indiceObjetivo);
            return null;
        } catch (IllegalArgumentException | IllegalStateException e) {
            return e.getMessage();
        }
    }

    public void resolverHabilidad(int indicePersonaje, boolean objetivoAliado, int indiceObjetivo) {
        Partida partida = gestorPartida.getPartidaActual();
        validarIndicePersonaje(partida, indicePersonaje);
        Entidad objetivo = obtenerObjetivoHabilidad(partida, objetivoAliado, indiceObjetivo);
        motorCombate.habilidad(partida.getParty().get(indicePersonaje), objetivo);
    }

    public String getNombreHabilidad(int indicePersonaje) {
        List<Personaje> party = gestorPartida.getPartidaActual().getParty();
        if (indicePersonaje < 0 || indicePersonaje >= party.size()) {
            throw new IllegalArgumentException("Personaje inválido.");
        }
        return party.get(indicePersonaje).getNombreHabilidadEspecial();
    }

    private Entidad validarHabilidad(int indicePersonaje, boolean objetivoAliado, int indiceObjetivo) {
        Partida partida = gestorPartida.getPartidaActual();
        if (partida == null || verificarEstadoCombate() != EstadoCombate.EN_CURSO || getMapaActual() == null) {
            throw new IllegalStateException("No hay un combate activo para usar habilidades.");
        }
        List<Personaje> party = partida.getParty();
        if (indicePersonaje < 0 || indicePersonaje >= party.size()
                || indicePersonaje != getIndicePersonajeEnTurno()) {
            throw new IllegalStateException("Esperá el turno de un personaje para usar su habilidad.");
        }
        Entidad objetivo = obtenerObjetivoHabilidad(partida, objetivoAliado, indiceObjetivo);
        Personaje actor = party.get(indicePersonaje);
        motorCombate.validarHabilidad(actor, objetivo);
        return objetivo;
    }

    private Entidad obtenerObjetivoHabilidad(Partida partida, boolean objetivoAliado, int indiceObjetivo) {
        Entidad objetivo;
        List<Personaje> party = partida.getParty();
        if (objetivoAliado) {
            if (indiceObjetivo < 0 || indiceObjetivo >= party.size()) {
                throw new IllegalArgumentException("Elegí un aliado válido.");
            }
            objetivo = party.get(indiceObjetivo);
        } else {
            Nivel nivel = obtenerMapaActual().getNivelActual();
            if (indiceObjetivo < 0 || indiceObjetivo >= nivel.getEnemigos().size()) {
                throw new IllegalArgumentException("Elegí un enemigo válido.");
            }
            objetivo = nivel.getEnemigos().get(indiceObjetivo);
        }
        return objetivo;
    }

    private ItemConsumible validarUsoItem(int indicePersonaje, int indiceItem) {
        Partida partida = gestorPartida.getPartidaActual();
        if (partida == null || verificarEstadoCombate() != EstadoCombate.EN_CURSO || getMapaActual() == null) {
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

    public void avanzarCombate() {
        resultadoVictoriaPendiente = null;
        try {
        motorCombate.avanzarEnergia();

        Entidad actor = motorCombate.getActorActual();

        if (!(actor instanceof Enemigo)) {
            return; // Todavía no hay actor o esperamos al jugador.
        }

        Enemigo enemigo = (Enemigo) actor;

        motorCombate.ejecutarTurnoEnemigo(enemigo);
        } catch (RuntimeException excepcion) {
            LOGGER.log(Level.SEVERE, "Excepción al avanzar el combate", excepcion);
            throw excepcion;
        }
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
        try {
            motorCombate.ataque(personaje, enemigo);
            return true;
        } catch (IllegalArgumentException | IllegalStateException e) {
            return false;
        }
    }

    public EstadoCombate verificarEstadoCombate() {
        EstadoCombate estado = motorCombate.getEstadoCombate();
        if (estado != EstadoCombate.EN_CURSO && estado != ultimoEstadoLogueado) {
            ultimoEstadoLogueado = estado;
            LOGGER.info("Fin de batalla: " + estado);
        }
        return estado;
    }

    public boolean continuarAlSiguienteNivel() {
        if (resultadoVictoriaPendiente != null) {
            throw new IllegalStateException("La victoria ya fue procesada; iniciá el siguiente encuentro.");
        }
        Mapa mapa = obtenerMapaActual();
        Nivel nivel = mapa == null ? null : mapa.getNivelActual();
        if (nivel == null || verificarEstadoCombate() != EstadoCombate.VICTORIA) {
            throw new IllegalStateException("El nivel solo se puede completar después de una victoria.");
        }
        prepararResultadoVictoria();
        return getMapaActual() != null;
    }

    public ResultadoRecompensaDTO prepararResultadoVictoria() {
        if (resultadoVictoriaPendiente == null) {
            if (verificarEstadoCombate() != EstadoCombate.VICTORIA) {
                throw new IllegalStateException("Sólo se pueden preparar recompensas después de una victoria.");
            }
            resultadoVictoriaPendiente = gestorPartida.completarNivelConVictoria();
            motorCombate = crearMotorCombate();
        }
        return convertirResultado(resultadoVictoriaPendiente);
    }

    private ResultadoRecompensaDTO convertirResultado(ResultadoRecompensa resultado) {
        return new ResultadoRecompensaDTO(resultado.getExperienciaOtorgada(),
                nombres(resultado.getObjetosObtenidos()), nombres(resultado.getObjetosIncorporados()),
                nombres(resultado.getObjetosDescartados()));
    }

    private List<String> nombres(List<Item> objetos) {
        List<String> nombres = new ArrayList<>();
        for (Item objeto : objetos) {
            nombres.add(objeto.getNombre());
        }
        return nombres;
    }

    public void resolverDefensa(int indicePersonaje) {
        List<Personaje> party = gestorPartida.getPartidaActual().getParty();
        if (indicePersonaje < 0 || indicePersonaje >= party.size()) {
            throw new IllegalArgumentException("Elegí un personaje válido.");
        }

        Personaje personaje = party.get(indicePersonaje);
        motorCombate.defensa(personaje);
    }
}
