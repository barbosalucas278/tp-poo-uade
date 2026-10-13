package controller;

import model.DTOs.MapaDTO;
import model.partidas.GestorPartida;
import model.partidas.Partida;

public class BatallaController {
    private GestorPartida gestorPartida;
    private Partida partidaActual;

    public BatallaController() {
        this.gestorPartida = GestorPartida.getInstance();
        this.partidaActual = gestorPartida.getPartidaActual();
    }

    public MapaDTO getMapaActual() {
        MapaDTO mapaDTO = null;
        if (partidaActual != null) {
            mapaDTO = new MapaDTO(
                    partidaActual.getMapaActual().getNumeroMapa(),
                    partidaActual.getMapaActual().getNivelActual().getNumeroNivel(),
                    partidaActual.getMapaActual().getNiveles().size());
        }
        return mapaDTO;

    }

    

}
