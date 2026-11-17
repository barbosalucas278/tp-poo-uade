package model.partidas;

import java.util.ArrayList;
import java.util.Collections;
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
        if (enemigos == null || enemigos.isEmpty() || enemigos.stream().anyMatch(enemigo -> enemigo == null)) {
            throw new IllegalArgumentException("El nivel debe tener enemigos válidos.");
        }
        this.numeroNivel = numeroNivel;
        this.enemigos = new ArrayList<>(enemigos);
        this.completado = false;
    }

    public int getNumeroNivel() {
        return numeroNivel;
    }

    public List<Enemigo> getEnemigos() {
        return Collections.unmodifiableList(enemigos);
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }
}
