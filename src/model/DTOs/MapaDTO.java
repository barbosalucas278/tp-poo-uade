package model.DTOs;

/**
 * MapaDTO
 */
public class MapaDTO {
    public int numeroMapa;
    public int nivelActual;
    public int totalNiveles;

    public MapaDTO(int numeroMapa, int nivelActual, int totalNiveles) {
        this.numeroMapa = numeroMapa;
        this.nivelActual = nivelActual;
        this.totalNiveles = totalNiveles;
    }

}
