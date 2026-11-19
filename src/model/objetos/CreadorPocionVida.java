package model.objetos;

class CreadorPocionVida implements CreadorConsumible {
    @Override
    public ItemConsumible crear(int cantidad, TipoRareza rareza) {
        return new PocionVida("Poción de vida", "Restaura " + cantidad + " de vida.", cantidad, rareza);
    }
}
