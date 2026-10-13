package model.partidas;

import java.util.List;

/**
 * Mapa
 */
public class Mapa {
    private List<Nivel> niveles;

    public Mapa(List<Nivel> niveles) {
        this.niveles = niveles;
    }
    public List<Nivel> getNiveles() {
        return niveles;
    }
    public Nivel getNivel(int index) {
        return niveles.get(index);
    }

}
