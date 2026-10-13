package model.objetos;

import model.entidades.personajes.Personaje;

/**
 * ItemConsumible
 */
public abstract class ItemConsumible extends Item {

    public ItemConsumible(String nombre, String descripcion) {
        super(nombre, descripcion);
    }
    
    public abstract void aplicarEfecto(Personaje personaje);
}
