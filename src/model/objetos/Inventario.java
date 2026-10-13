package model.objetos;

import java.util.List;

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
        return items;
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

    public void consumirItem(ItemConsumible item, Personaje personaje) {
        if (items.contains(item)) {
            item.aplicarEfecto(personaje);
            items.remove(item);
        }
    }

    public void equiparItem(ItemEquipable item, Personaje personaje) {
        if (items.contains(item)) {
            item.aplicarBonus(personaje);
            items.remove(item);
        }
    }

    public void desequiparItem(ItemEquipable item, Personaje personaje) {
        item.removerBonus(personaje);
        items.add(item);
    }

    public int getCapacidadMax() {
        return capacidadMax;
    }

}
