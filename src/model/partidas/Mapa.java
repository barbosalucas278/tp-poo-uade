package model.partidas;

import java.util.List;

/**
 * Mapa
 */
public class Mapa {
    private int numeroMapa;
    private List<Nivel> niveles;
    private boolean completado;

    public Mapa(int numeroMapa, List<Nivel> niveles) {
        this.numeroMapa = numeroMapa;
        this.niveles = niveles;
        this.completado = false;
    }

    public List<Nivel> getNiveles() {
        return niveles;
    }

    public int getNumeroMapa() {
        return numeroMapa;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }

    public Nivel getNivelActual() {
        Nivel proximoNivel = null;
        for (Nivel nivel : niveles) {
            if (!nivel.isCompletado()) {
                proximoNivel = nivel;
                break;
            }
        }
        return proximoNivel;
    }

    public boolean isCompletado() {
        return completado;
    }
}
