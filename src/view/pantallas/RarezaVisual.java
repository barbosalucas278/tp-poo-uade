package view.pantallas;

import model.objetos.TipoRareza;

final class RarezaVisual {
    private RarezaVisual() {
    }

    static String nombre(TipoRareza rareza) {
        if (rareza == null) {
            return "Sin rareza";
        }
        if (rareza == TipoRareza.RARO) {
            return "Raro";
        }
        if (rareza == TipoRareza.EPICO) {
            return "Épico";
        }
        return "Común";
    }
}
