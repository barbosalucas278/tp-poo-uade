package model.partidas;

import java.util.ArrayList;
import java.util.List;
import model.entidades.personajes.Personaje;
import model.objetos.Inventario;
import model.objetos.Item;

/**
 * Representa una partida en curso dentro del juego.
 * <p>Gestiona el nombre de la sesión, el equipo de personajes activos (mínimo {@value #CANTIDAD_MINIMA_PERSONAJES}),
 * el inventario compartido y la progresión de mapas.</p>
 */
public class Partida {

    public static final int CANTIDAD_MINIMA_PERSONAJES = 2;

    private String nombre;
    private List<Personaje> party;
    private Inventario inventarioGlobal;
    private List<Mapa> mapas;

    /**
     * Crea una nueva partida inicializando el inventario global y agregando el mapa inicial.
     *
     * @param nombre Nombre identificador de la partida.
     * @param party  Lista inicial de personajes (debe contener al menos 2 personajes no nulos).
     * @param mapa   Mapa inicial donde comienza la aventura.
     */
    public Partida(String nombre, List<Personaje> party, Mapa mapa) {
        validarCantidadMinimaPersonajes(party);
        this.nombre = nombre;
        this.party = new ArrayList<>(party);
        this.inventarioGlobal = new Inventario();
        this.mapas = new ArrayList<Mapa>();
        this.mapas.add(mapa);
    }

    /**
     * Valida que la lista de personajes cumpla con el mínimo requerido y no contenga referencias nulas.
     *
     * @param party Lista de personajes a verificar.
     */
    static void validarCantidadMinimaPersonajes(List<Personaje> party) {
        if (party == null || party.size() < CANTIDAD_MINIMA_PERSONAJES) {
            throw new IllegalArgumentException("La party debe tener al menos dos personajes.");
        }
        for (Personaje personaje : party) {
            validarPersonaje(personaje);
        }
    }

    private static void validarPersonaje(Personaje personaje) {
        if (personaje == null) {
            throw new IllegalArgumentException("La party no puede tener personajes nulos.");
        }
    }

    /**
     * Agrega un personaje a la party actual.
     *
     * @param personaje Personaje a incorporar al equipo.
     */
    public void agregarPersonaje(Personaje personaje) {
        validarPersonaje(personaje);
        party.add(personaje);
    }

    /**
     * Remueve un personaje existente de la party.
     *
     * @param personaje Personaje a quitar del equipo.
     * @throws IllegalStateException si la party quedaría por debajo del mínimo.
     */
    public void removerPersonaje(Personaje personaje) {
        if (party.size() <= CANTIDAD_MINIMA_PERSONAJES && party.contains(personaje)) {
            throw new IllegalStateException("La party debe tener al menos dos personajes.");
        }
        party.remove(personaje);
    }

    /**
     * Agrega un nuevo mapa a la lista de mapas de la partida.
     *
     * @param mapa Mapa a registrar.
     */
    public void agregarMapa(Mapa mapa) {
        mapas.add(mapa);
    }

    public Inventario getInventarioGlobal() {
        return inventarioGlobal;
    }

    public void setInventarioGlobal(List<Item> items) {
        for (Item item : items) {
            this.inventarioGlobal.agregarItem(item);
        }
    }

    public Mapa getMapaActual() {
        Mapa mapaActual = null;
        for (Mapa nivel : mapas) {
            if (!nivel.isCompletado()) {
                mapaActual = nivel;
                break;
            }
        }
        return mapaActual;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Personaje> getParty() {
        return new ArrayList<>(party);
    }

    public List<Mapa> getMapas() {
        return new ArrayList<>(mapas);
    }

}
