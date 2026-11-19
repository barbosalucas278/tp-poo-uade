package model.objetos;

/** Recibe los ítems del inventario para el guardado. */
public interface RegistroItems {
    void registrarEquipable(ItemEquipable item);

    void registrarConsumible(ItemConsumible item);
}
