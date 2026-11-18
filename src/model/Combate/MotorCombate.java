package model.Combate;

import java.util.ArrayList;
import java.util.List;

import model.entidades.Entidad;
import model.entidades.enemigos.Enemigo;
import model.entidades.personajes.Personaje;
import model.objetos.Inventario;
import model.objetos.ItemConsumible;

public class MotorCombate {
    // Maná que recupera cada personaje vivo cada vez que se acumula energía
    private static final int REGENERACION_MANA_POR_TICK = 3;

    private final List<Entidad> participantes = new ArrayList<>();
    private Entidad actorActual;

    public MotorCombate(List<Personaje> party, List<Enemigo> enemigos) {
        participantes.addAll(party);
        participantes.addAll(enemigos);
    }

    public void avanzarEnergia() {
        if (actorActual != null) {
            return; // Esperamos que termine su acción.
        }

        // Primero atendemos a quienes ya tenían energía suficiente.
        actorActual = buscarActorListo();
        if (actorActual != null) {
            return;
        }

        for (Entidad entidad : participantes) {
            if (entidad.estaVivo()) {
                entidad.acumularEnergia(entidad.getVelocidad());
                // Los personajes recuperan maná de a poco, mucho más lento que la energía
                if (entidad instanceof Personaje) {
                    entidad.generarRecursos(REGENERACION_MANA_POR_TICK);
                }
            }
        }

        actorActual = buscarActorListo();
    }

    private Entidad buscarActorListo() {
        for (Entidad entidad : participantes) {
            if (entidad.estaVivo() && entidad.listaParaLaAccion()) {
                return entidad;
            }
        }
        return null;
    }

    public Entidad getActorActual() {
        return actorActual;
    }

    public void ataque(Entidad atacante, Entidad defensor) {
        if (atacante != actorActual) {
            throw new IllegalStateException("No es el turno del atacante.");
        }
        int danio = atacante.atacar();
        defensor.recibirDanio(danio);
        finalizarAccion(atacante);
    }

       // Ejecuta la habilidad especial del personaje que tiene el turno PERSONAJE
    public void usarHabilidadEspecial(Personaje atacante, Entidad objetivo) {
        // Solo puede actuar el personaje al que le toca
        if (atacante != actorActual) {
            throw new IllegalStateException("No es el turno del personaje.");
        }

        // Habilidad de apoyo (Curador): cura a todos los personajes vivos del grupo
        if (atacante.esHabilidadDeApoyo()) {
            int curacion = atacante.usarHabilidadEspecial();
            for (Entidad entidad : participantes) {
                if (entidad instanceof Personaje && entidad.estaVivo()) {
                    entidad.recibirSanacion(curacion);
                }
            }
            finalizarAccion(atacante);
            return;
        }

        // Habilidad de daño: necesita un objetivo válido y vivo
        if (objetivo == null || !participantes.contains(objetivo) || !objetivo.estaVivo()) {
            throw new IllegalArgumentException("Elegí un objetivo válido que siga en combate.");
        }

        // Cada clase calcula su propio daño (polimorfismo) y el objetivo lo recibe
        int danio = atacante.usarHabilidadEspecial();
        objetivo.recibirDanio(danio);
        finalizarAccion(atacante);
    }

       // Ejecuta la habilidad especial del enemigo que tiene el turno ENEMIGO
    public void habilidadEnemigo(Enemigo enemigo, Personaje objetivo, List<Personaje> personajesVivos) {
        // Solo puede actuar el enemigo al que le toca
        if (enemigo != actorActual) {
            throw new IllegalStateException("No es el turno del enemigo.");
        }

        // Habilidad de área (Jefe): golpea a todos los personajes vivos del grupo
        if (enemigo.habilidadAfectaATodos()) {
            int danio = enemigo.usarHabilidadEspecial();
            for (Personaje personaje : personajesVivos) {
                aplicarDanioHabilidad(enemigo, personaje, danio);
            }
            finalizarAccion(enemigo);
            return;
        }

        // Habilidad a un solo objetivo: necesita un personaje válido y vivo
        if (objetivo == null || !participantes.contains(objetivo) || !objetivo.estaVivo()) {
            throw new IllegalArgumentException("El enemigo necesita un objetivo válido que siga en combate.");
        }

        // Cada enemigo calcula su propio daño (polimorfismo) y el objetivo lo recibe
        int danio = enemigo.usarHabilidadEspecial();
        aplicarDanioHabilidad(enemigo, objetivo, danio);
        finalizarAccion(enemigo);
    }

    // Aplica el daño de la habilidad: si el enemigo ignora la defensa, resta la vida directo
    private void aplicarDanioHabilidad(Enemigo enemigo, Personaje objetivo, int danio) {
        if (enemigo.habilidadIgnoraDefensa()) {
            objetivo.setVidaActual(objetivo.getVidaActual() - danio);
        } else {
            objetivo.recibirDanio(danio);
        }
    }

    public void defensa(Entidad personaje) {

        finalizarAccion(personaje);
    }

    /** Usar una poción sobre sí mismo consume la acción del personaje en turno. */
    public void usarItem(Personaje actor, ItemConsumible item, Inventario inventario) {
        if (actor == null || actor != actorActual || !actor.estaVivo()) {
            throw new IllegalStateException("Esperá el turno del personaje para usar un objeto.");
        }
        if (!participantes.contains(actor)) {
            throw new IllegalArgumentException("El personaje debe pertenecer a esta batalla.");
        }
        if (inventario == null || !inventario.consumirItem(item, actor)) {
            throw new IllegalArgumentException("El objeto ya no está en el inventario.");
        }
        finalizarAccion(actor);
    }

    private void finalizarAccion(Entidad actor) {
        actor.reiniciarEnergia();
        actorActual = null;
    }
}
