package model.objetos;

import java.util.ArrayList;
import java.util.List;

import model.entidades.personajes.Personaje;

/**
 * Inventario
 */
public class Inventario {
    private List<Item> items;
    private int capacidadMax = 20;

    public Inventario() {
        this.items = new ArrayList<>();
    }

    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

    public List<ItemEquipable> getEquipables() {
        List<ItemEquipable> equipables = new ArrayList<>();
        for (Item item : items) {
            if (item instanceof ItemEquipable) {
                equipables.add((ItemEquipable) item);
            }
        }
        return equipables;
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
        if (item == null || !items.contains(item)) {
            return;
        }
        TipoObjeto tipo = item.getTipo();
        if (personaje == null || tipo == null) {
            throw new IllegalArgumentException("Hace falta un personaje y un tipo de equipamiento válido.");
        }

        ItemEquipable anterior = personaje.getItemEquipado(tipo);
        // El objeto que se equipa libera el lugar para el anterior.
        items.remove(item);
        if (anterior != null) {
            anterior.removerBonus(personaje);
            items.add(anterior);
        }
        item.aplicarBonus(personaje);
        personaje.equiparItem(tipo, item);
    }

    public void desequiparItem(ItemEquipable item, Personaje personaje) {
        if (item == null || personaje == null || personaje.getItemEquipado(item.getTipo()) != item) {
            throw new IllegalArgumentException("El personaje no tiene equipado ese objeto.");
        }
        if (items.size() >= capacidadMax) {
            throw new IllegalStateException("El inventario está lleno.");
        }

        item.removerBonus(personaje);
        personaje.desequiparItem(item.getTipo());
        items.add(item);
    }

    public int getCapacidadMax() {
        return capacidadMax;
    }

}
