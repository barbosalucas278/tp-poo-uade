package model.objetos;

class EquipoPorRareza {
    private final String variante;
    private final int bonusAtaque;
    private final int bonusDefensa;

    EquipoPorRareza(String variante, int bonusAtaque, int bonusDefensa) {
        this.variante = variante;
        this.bonusAtaque = bonusAtaque;
        this.bonusDefensa = bonusDefensa;
    }

    String getVariante() {
        return variante;
    }

    int getBonusAtaque() {
        return bonusAtaque;
    }

    int getBonusDefensa() {
        return bonusDefensa;
    }
}
