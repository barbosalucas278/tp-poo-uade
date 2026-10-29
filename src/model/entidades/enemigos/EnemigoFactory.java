package model.entidades.enemigos;

import helpers.entidades.EstadisticasEnemigos;
import helpers.entidades.StatsConfigEnemigos;

/**
 * EnemigoFactory
 */
public class EnemigoFactory {
    public static Enemigo crearEnemigo(TipoEnemigo tipo, int nivel) {
        int aumento = nivel - 1;
        switch (tipo) {
            case TipoEnemigo.SLIME:
                StatsConfigEnemigos stats = EstadisticasEnemigos.obtener(TipoEnemigo.SLIME);
                
                return new Minion(
                        stats.getNombre(),
                        stats.getVida() + aumento * 10,
                        stats.getRecursoMax(),
                        stats.getAtaque() + aumento * 2,
                        stats.getDefensa() + aumento,
                        stats.getVelocidad(),
                        nivel,
                        stats.getExperiencia() + aumento * 5);

            case TipoEnemigo.ESQUELETO:
                stats = EstadisticasEnemigos.obtener(TipoEnemigo.ESQUELETO);
                return new Estandar(
                        stats.getNombre(),
                        stats.getVida() + aumento * 15,
                        stats.getRecursoMax(),
                        stats.getAtaque() + aumento * 3,
                        stats.getDefensa() + aumento,
                        stats.getVelocidad(),
                        nivel,
                        stats.getExperiencia() + aumento * 10);

            case TipoEnemigo.ORCO:
                stats = EstadisticasEnemigos.obtener(TipoEnemigo.ORCO);
                return new Elite(
                        stats.getNombre(),
                        stats.getVida() + aumento * 25,
                        stats.getRecursoMax(),
                        stats.getAtaque() + aumento * 4,
                        stats.getDefensa() + aumento * 2,
                        stats.getVelocidad(),
                        nivel,
                        stats.getExperiencia() + aumento * 20);

            case TipoEnemigo.DRAGON:
                stats = EstadisticasEnemigos.obtener(TipoEnemigo.DRAGON);
                return new Jefe(
                        stats.getNombre(),
                        stats.getVida() + aumento * 60,
                        stats.getRecursoMax(),
                        stats.getAtaque() + aumento * 5,
                        stats.getDefensa() + aumento * 2,
                        stats.getVelocidad(),
                        nivel,
                        stats.getExperiencia() + aumento * 50);
            default:
                throw new IllegalArgumentException("Tipo de enemigo inválido");
        }
    }
}
