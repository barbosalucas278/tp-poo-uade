package model.partidas;

import java.util.List;

import model.entidades.enemigos.Enemigo;

/**
 * Nivel
 */
public class Nivel {
    private int numeroNivel;
    private List<Enemigo> enemigos;
    private boolean completado;

    public Nivel(int numeroNivel, List<Enemigo> enemigos) {
        this.numeroNivel = numeroNivel;
        this.enemigos = enemigos;
        this.completado = false;
    }

    public int getNumeroNivel() {
        return numeroNivel;
    }

    public List<Enemigo> getEnemigos() {
        return enemigos;
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }
}