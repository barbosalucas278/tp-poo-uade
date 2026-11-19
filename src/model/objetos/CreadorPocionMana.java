package model.objetos;

class CreadorPocionMana implements CreadorConsumible {
    @Override
    public ItemConsumible crear(int cantidad, TipoRareza rareza) {
        return new PocionMana("Poción de maná", "Restaura " + cantidad + " de maná.", cantidad, rareza);
    }
}
