package model.objetos;

import model.entidades.personajes.Personaje;

public class PocionVida extends ItemConsumible {
    private int _cantidadDeVidaRestaurada;
    public PocionVida(String nombre, String descripcion, int cantidadDeVidaRestaurada) {
        this(nombre, descripcion, cantidadDeVidaRestaurada, TipoRareza.COMUN);
    }

    public PocionVida(String nombre, String descripcion, int cantidadDeVidaRestaurada, TipoRareza rareza) {
        super(nombre, descripcion, rareza);
        if (cantidadDeVidaRestaurada <= 0) {
            throw new IllegalArgumentException("La poción debe restaurar una cantidad positiva de vida.");
        }
        this._cantidadDeVidaRestaurada = cantidadDeVidaRestaurada;
    }

    @Override
    public void validarUso(Personaje personaje) {
        super.validarUso(personaje);
        if (personaje.getVidaActual() >= personaje.getVidaMax()) {
            throw new IllegalArgumentException("El personaje ya tiene la vida completa.");
        }
    }

    @Override
    public void aplicarEfecto(Personaje personaje) {
        validarUso(personaje);
        personaje.recibirSanacion(this._cantidadDeVidaRestaurada);
    }

    @Override
    public TipoItemConsumible getTipo() {
        return TipoItemConsumible.POCION_VIDA;
    }

}
