package model.partidas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.entidades.personajes.Personaje;
import model.objetos.Inventario;
import model.objetos.Item;
import model.objetos.ItemEquipable;

/**
 * Partida
 */
public class Partida {
    private static final int MINIMO_PERSONAJES = 2;
    private static final int MAXIMO_PERSONAJES = 4;

    private String nombre;
    private List<Personaje> party;
    private Inventario inventarioGlobal;
    private List<Mapa> mapas;

    public Partida(String nombre, List<Personaje> party, Mapa mapa) {
        validarParty(party);
        if (party.size() < MINIMO_PERSONAJES) {
            throw new IllegalArgumentException("La partida debe tener entre dos y cuatro personajes.");
        }
        if (mapa == null) {
            throw new IllegalArgumentException("La partida debe tener un mapa inicial.");
        }
        this.nombre = nombre;
        this.party = new ArrayList<>(party);
        this.inventarioGlobal = new Inventario();
        this.mapas = new ArrayList<Mapa>();
        this.mapas.add(mapa);
    }

    public void agregarPersonaje(Personaje personaje) {
        if (personaje == null) {
            throw new IllegalArgumentException("No se puede agregar un personaje nulo.");
        }
        if (party.size() >= MAXIMO_PERSONAJES) {
            throw new IllegalStateException("La partida no puede tener más de cuatro personajes.");
        }
        if (party.stream().anyMatch(actual -> actual == personaje)) {
            throw new IllegalArgumentException("No se puede repetir la misma instancia de personaje.");
        }
        party.add(personaje);
    }

    public void removerPersonaje(Personaje personaje) {
        if (party.size() <= MINIMO_PERSONAJES) {
            throw new IllegalStateException("La partida debe conservar al menos dos personajes.");
        }
        party.remove(personaje);
    }

    public Inventario getInventarioGlobal() {
        return inventarioGlobal;
    }

    public void setInventarioGlobal(List<Item> items) {
        if (items == null) {
            throw new IllegalArgumentException("La lista de objetos no puede ser nula.");
        }
        for (Item item : items) {
            this.inventarioGlobal.agregarItem(item);
        }
    }

    public void equiparItem(Personaje personaje, ItemEquipable item) {
        validarPersonajeDeLaPartida(personaje);
        inventarioGlobal.equiparItem(item, personaje);
    }

    public void desequiparItem(Personaje personaje, ItemEquipable item) {
        validarPersonajeDeLaPartida(personaje);
        inventarioGlobal.desequiparItem(item, personaje);
    }

    public void restaurarEquipamiento(Personaje personaje, ItemEquipable item) {
        validarPersonajeDeLaPartida(personaje);
        personaje.restaurarEquipamiento(item);
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
        return Collections.unmodifiableList(party);
    }

    public List<Mapa> getMapas() {
        return Collections.unmodifiableList(mapas);
    }

    public void agregarMapa(Mapa mapa) {
        if (mapa == null) {
            throw new IllegalArgumentException("No se puede agregar un mapa nulo.");
        }
        mapas.add(mapa);
    }

    private static void validarParty(List<Personaje> party) {
        if (party == null) {
            throw new IllegalArgumentException("La party no puede ser nula.");
        }
        if (party.size() > MAXIMO_PERSONAJES) {
            throw new IllegalArgumentException("La partida no puede tener más de cuatro personajes.");
        }
        for (Personaje personaje : party) {
            if (personaje == null) {
                throw new IllegalArgumentException("La party no puede contener personajes nulos.");
            }
        }
        for (int i = 0; i < party.size(); i++) {
            for (int j = i + 1; j < party.size(); j++) {
                if (party.get(i) == party.get(j)) {
                    throw new IllegalArgumentException("La party no puede repetir la misma instancia.");
                }
            }
        }
    }

    private void validarPersonajeDeLaPartida(Personaje personaje) {
        if (personaje == null || party.stream().noneMatch(actual -> actual == personaje)) {
            throw new IllegalArgumentException("El personaje debe pertenecer a la partida.");
        }
    }

}
