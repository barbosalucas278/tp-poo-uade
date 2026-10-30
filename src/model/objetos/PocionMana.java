package model.objetos;

import model.entidades.personajes.Hechicero;
import model.entidades.personajes.Personaje;

public class PocionMana extends ItemConsumible {
    private int _cantidadDeManaRestaurada;
    public PocionMana(String nombre, String descripcion, int cantidadDeManaRestaurada) {
        this(nombre, descripcion, cantidadDeManaRestaurada, TipoRareza.COMUN);
    }

    public PocionMana(String nombre, String descripcion, int cantidadDeManaRestaurada, TipoRareza rareza) {
        super(nombre, descripcion, rareza);
        if (cantidadDeManaRestaurada <= 0) {
            throw new IllegalArgumentException("La poción debe restaurar una cantidad positiva de maná.");
        }
        this._cantidadDeManaRestaurada = cantidadDeManaRestaurada;
    }

    @Override
    public void validarUso(Personaje personaje) {
        super.validarUso(personaje);
        if (!(personaje instanceof Hechicero)) {
            throw new IllegalArgumentException("La poción de maná solo se puede usar sobre un hechicero.");
        }
        if (personaje.getRecursoActual() >= personaje.getRecursoMax()) {
            throw new IllegalArgumentException("El personaje ya tiene el maná completo.");
        }
    }

    @Override
    public void aplicarEfecto(Personaje personaje) {
        validarUso(personaje);
        personaje.generarRecursos(_cantidadDeManaRestaurada);
    }

    @Override
    public TipoItemConsumible getTipo() {
        return TipoItemConsumible.POCION_MANA;
    }

}
