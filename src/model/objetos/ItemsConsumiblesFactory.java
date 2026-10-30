package model.objetos;

public class ItemsConsumiblesFactory {
    public static ItemConsumible CrearItemConsumible(TipoRareza rareza, TipoItemConsumible tipo) {
        int cantidadRestaurada = cantidadDeRecursoSegunRareza(rareza);
        switch (tipo) {
            case POCION_MANA:
                return new PocionMana("Poción de maná", "Restaura " + cantidadRestaurada + " de maná.",
                        cantidadRestaurada, rareza);
            case POCION_VIDA:
                return new PocionVida("Poción de vida", "Restaura " + cantidadRestaurada + " de vida.",
                        cantidadRestaurada, rareza);
            default:
                throw new IllegalArgumentException("Tipo de personaje inválido");
        }
    }

    public static int cantidadDeRecursoSegunRareza(TipoRareza rareza) {
        switch (rareza) {
            case COMUN:
                return 25;
            case RARO:
                return 50;
            case EPICO:
                return 100;
            default:
                throw new IllegalArgumentException("Tipo de personaje inválido");

        }
    }
}
