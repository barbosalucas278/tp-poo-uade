package model.objetos;

import java.util.List;
import java.util.Collections;

import model.entidades.personajes.Personaje;

/**
 * Inventario
 */
public class Inventario {
    private List<Item> items;
    private int capacidadMax = 20;

    public Inventario() {
        this.items = new java.util.ArrayList<>();
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public boolean agregarItem(Item item) {
        if (items.size() < capacidadMax) {
            items.add(item);
            return true;
        }
        return false;
    }

    public boolean removerItem(Item item) {
        return items.remove(item);
    }

    public boolean consumirItem(ItemConsumible item, Personaje personaje) {
        if (item == null || !items.contains(item)) {
            return false;
        }
        item.validarUso(personaje);
        item.aplicarEfecto(personaje);
        items.remove(item);
        return true;
    }

    public void equiparItem(ItemEquipable item, Personaje personaje) {
        validarEquipamientoDisponible(item, personaje);
        ItemEquipable anterior = personaje.getEquipamiento().get(item.getTipo());
        // La validación se completa antes de modificar estadísticas o colecciones.
        personaje.equiparItem(item.getTipo(), item);
        items.remove(item);
        if (anterior != null) {
            items.add(anterior);
        }
    }

    public void desequiparItem(ItemEquipable item, Personaje personaje) {
        if (item == null || personaje == null || personaje.getEquipamiento().get(item.getTipo()) != item) {
            throw new IllegalArgumentException("El objeto no está equipado en ese personaje.");
        }
        if (items.size() >= capacidadMax) {
            throw new IllegalStateException("No hay espacio en el inventario para desequipar el objeto.");
        }
        personaje.desequiparItem(item.getTipo());
        items.add(item);
    }

    private void validarEquipamientoDisponible(ItemEquipable item, Personaje personaje) {
        if (item == null || personaje == null) {
            throw new IllegalArgumentException("El personaje y el objeto son obligatorios.");
        }
        if (!items.contains(item)) {
            throw new IllegalArgumentException("El objeto no está disponible en el inventario.");
        }
        if (item.getTipo() == null) {
            throw new IllegalArgumentException("El objeto no tiene un slot válido.");
        }
    }

    public int getCapacidadMax() {
        return capacidadMax;
    }

}
