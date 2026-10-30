package model.objetos;

/**
 * Item
 */
public abstract class Item {
    protected String nombre;
    protected String descripcion;
    protected TipoRareza rareza;

    public Item(String nombre, String descripcion) {
        this(nombre, descripcion, TipoRareza.COMUN);
    }

    public Item(String nombre, String descripcion, TipoRareza rareza) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.rareza = rareza;
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

}
