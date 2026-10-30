package model.DTOs;

import model.objetos.TipoRareza;

/** Datos de solo lectura de un objeto y su posición en el inventario. */
public final class ItemInventarioDTO {
    private final int indice;
    private final String nombre;
    private final String descripcion;
    private final TipoRareza rareza;
    private final boolean consumible;

    public ItemInventarioDTO(int indice, String nombre, String descripcion, TipoRareza rareza, boolean consumible) {
        this.indice = indice;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.rareza = rareza;
        this.consumible = consumible;
    }

    public int getIndice() {
        return indice;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public TipoRareza getRareza() {
        return rareza;
    }

    public boolean isConsumible() {
        return consumible;
    }
}
