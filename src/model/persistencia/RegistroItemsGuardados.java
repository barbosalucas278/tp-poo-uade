package model.persistencia;

import model.DTOs.PartidaGuardadaDTO;
import model.DTOs.PartidaGuardadaDTO.ConsumibleGuardado;
import model.DTOs.PartidaGuardadaDTO.ItemGuardado;
import model.objetos.ItemConsumible;
import model.objetos.ItemEquipable;
import model.objetos.RegistroItems;

/** Convierte los ítems a los datos que espera el guardado de la partida. */
class RegistroItemsGuardados implements RegistroItems {
    private final PartidaGuardadaDTO snapshot;

    RegistroItemsGuardados(PartidaGuardadaDTO snapshot) {
        this.snapshot = snapshot;
    }

    @Override
    public void registrarEquipable(ItemEquipable item) {
        snapshot.inventario.add(aItemGuardado(item));
    }

    @Override
    public void registrarConsumible(ItemConsumible item) {
        ConsumibleGuardado consumible = new ConsumibleGuardado();
        consumible.tipo = item.getTipo();
        consumible.rareza = item.getRareza();
        snapshot.consumibles.add(consumible);
    }

    static ItemGuardado aItemGuardado(ItemEquipable equipable) {
        ItemGuardado item = new ItemGuardado();
        item.nombre = equipable.getNombre();
        item.descripcion = equipable.getDescripcion();
        item.tipo = equipable.getTipo();
        item.bonusAtaque = equipable.getBonusAtaque();
        item.bonusDefensa = equipable.getBonusDefensa();
        item.rareza = equipable.getRareza();
        return item;
    }
}
