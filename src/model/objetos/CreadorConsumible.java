package model.objetos;

interface CreadorConsumible {
    ItemConsumible crear(int cantidad, TipoRareza rareza);
}
