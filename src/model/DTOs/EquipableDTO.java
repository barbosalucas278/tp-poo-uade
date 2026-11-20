package model.DTOs;

import model.objetos.TipoObjeto;
import model.objetos.TipoRareza;

/** Datos de un arma o una armadura que muestra la pantalla de equipamiento. */
public final class EquipableDTO {
    private final String nombre;
    private final TipoObjeto tipo;
    private final int bonusAtaque;
    private final int bonusDefensa;
    private final TipoRareza rareza;

    public EquipableDTO(String nombre, TipoObjeto tipo, int bonusAtaque, int bonusDefensa, TipoRareza rareza) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.bonusAtaque = bonusAtaque;
        this.bonusDefensa = bonusDefensa;
        this.rareza = rareza;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoObjeto getTipo() {
        return tipo;
    }

    public int getBonusAtaque() {
        return bonusAtaque;
    }

    public int getBonusDefensa() {
        return bonusDefensa;
    }

    public TipoRareza getRareza() {
        return rareza;
    }
}
