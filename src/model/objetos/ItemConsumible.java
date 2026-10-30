package model.objetos;

import model.entidades.personajes.Personaje;

/**
 * ItemConsumible
 */
public abstract class ItemConsumible extends Item {

    public ItemConsumible(String nombre, String descripcion) {
        super(nombre, descripcion);
    }

    public ItemConsumible(String nombre, String descripcion, TipoRareza rareza) {
        super(nombre, descripcion, rareza);
    }

    /** Un consumible de combate requiere que el personaje esté vivo. */
    public void validarUso(Personaje personaje) {
        if (personaje == null || !personaje.estaVivo()) {
            throw new IllegalArgumentException("El personaje debe estar vivo para usar la poción.");
        }
    }
    
    public abstract void aplicarEfecto(Personaje personaje);
}
