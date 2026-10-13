package model.partidas;

import java.util.List;

import model.entidades.enemigos.Enemigo;

/**
 * Nivel
 */
public class Nivel {
    private List<Enemigo> enemigos;

    public Nivel(List<Enemigo> enemigos) {
        this.enemigos = enemigos;
    }

    public List<Enemigo> getEnemigos() {
        return enemigos;
    }
    
}
