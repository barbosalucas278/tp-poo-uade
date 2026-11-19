package model.entidades.enemigos;

import helpers.entidades.StatsConfigEnemigos;

class ConfiguracionCreacion {
    private final CreadorEnemigo creador;
    private final int vidaPorNivel;
    private final int ataquePorNivel;
    private final int defensaPorNivel;
    private final int experienciaPorNivel;

    ConfiguracionCreacion(CreadorEnemigo creador, int vidaPorNivel, int ataquePorNivel,
            int defensaPorNivel, int experienciaPorNivel) {
        this.creador = creador;
        this.vidaPorNivel = vidaPorNivel;
        this.ataquePorNivel = ataquePorNivel;
        this.defensaPorNivel = defensaPorNivel;
        this.experienciaPorNivel = experienciaPorNivel;
    }

    Enemigo crear(StatsConfigEnemigos stats, int nivel) {
        int aumento = nivel - 1;
        return creador.crear(stats.getNombre(), stats.getVida() + aumento * vidaPorNivel,
                stats.getRecursoMax(), stats.getAtaque() + aumento * ataquePorNivel,
                stats.getDefensa() + aumento * defensaPorNivel, stats.getVelocidad(),
                nivel, stats.getExperiencia() + aumento * experienciaPorNivel);
    }
}
