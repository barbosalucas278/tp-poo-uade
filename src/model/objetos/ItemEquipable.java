package model.objetos;

import model.entidades.personajes.Personaje;

/**
 * ItemEquipable
 */
public class ItemEquipable extends Item {
    private TipoObjeto tipo;
    private int bonusAtaque;
    private int bonusDefensa;

    public ItemEquipable(String nombre, String descripcion, TipoObjeto tipo, int bonusAtaque, int bonusDefensa) {
        super(nombre, descripcion);
        this.tipo = tipo;
        this.bonusAtaque = bonusAtaque;
        this.bonusDefensa = bonusDefensa;
    }

    public void aplicarBonus(Personaje personaje) {
        if (tipo == TipoObjeto.ARMA) {
            personaje.setAtaqueBase(personaje.getAtaqueBase() + bonusAtaque);
        } else if (tipo == TipoObjeto.ARMADURA) {
            personaje.setDefensaBase(personaje.getDefensaBase() + bonusDefensa);
        }
    }

    public void removerBonus(Personaje personaje) {
        if (tipo == TipoObjeto.ARMA) {
            personaje.setAtaqueBase(personaje.getAtaqueBase() - bonusAtaque);
        } else if (tipo == TipoObjeto.ARMADURA) {
            personaje.setDefensaBase(personaje.getDefensaBase() - bonusDefensa);
        }
    }

    public TipoObjeto getTipo() {
        return tipo;
    }
}
