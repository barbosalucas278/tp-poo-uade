package model.Combate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import model.entidades.Entidad;
import model.entidades.enemigos.Enemigo;
import model.entidades.personajes.Personaje;
import model.objetos.Inventario;
import model.objetos.ItemConsumible;
import model.objetos.ItemEquipable;
import model.partidas.Partida;

public class MotorCombate {
    private static final Logger LOGGER = Logger.getLogger(MotorCombate.class.getName());
    private final List<Entidad> participantes = new ArrayList<>();
    private final List<Personaje> personajes;
    private final List<Enemigo> enemigos;
    private Entidad actorActual;

    public MotorCombate(List<Personaje> party, List<Enemigo> enemigos) {
        this.personajes = new ArrayList<>(party);
        this.enemigos = new ArrayList<>(enemigos);
        participantes.addAll(party);
        participantes.addAll(enemigos);
    }

    public void avanzarEnergia() {
        if (combateTerminado()) {
            LOGGER.fine("Combate terminado; no se acumula energía ni se selecciona actor.");
            actorActual = null;
            return;
        }
        if (actorActual != null) {
            LOGGER.fine("Se conserva el actor actual mientras espera su acción: " + actorActual.getNombre());
            return;
        }

        // Primero atendemos a quienes ya tenían energía suficiente.
        actorActual = buscarActorListo();
        if (actorActual != null) {
            return;
        }

        for (Entidad entidad : participantes) {
            if (entidad.estaVivo()) {
                int energiaAntes = entidad.getEnergiaActual();
                entidad.acumularEnergia(entidad.getVelocidad());
                LOGGER.fine("Energía de " + entidad.getNombre() + ": " + energiaAntes + " -> "
                        + entidad.getEnergiaActual() + " (velocidad=" + entidad.getVelocidad() + ")");
            }
        }

        actorActual = buscarActorListo();
    }

    private Entidad buscarActorListo() {
        for (Entidad entidad : participantes) {
            LOGGER.fine("Evaluando actor " + entidad.getNombre() + " vivo=" + entidad.estaVivo()
                    + " energía=" + entidad.getEnergiaActual());
            if (entidad.estaVivo() && entidad.listaParaLaAccion()) {
                LOGGER.info("Actor seleccionado: " + entidad.getNombre() + " ("
                        + entidad.getClass().getSimpleName() + ")");
                return entidad;
            }
        }
        return null;
    }

    public Entidad getActorActual() {
        return actorActual;
    }

    // Devuelve el estado único del combate sin depender de la vista.
    public EstadoCombate getEstadoCombate() {
        boolean hayEnemigosVivos = enemigos.stream().anyMatch(Entidad::estaVivo);
        boolean hayPersonajesVivos = personajes.stream().anyMatch(Entidad::estaVivo);
        if (!hayEnemigosVivos) {
            return EstadoCombate.VICTORIA;
        }
        if (!hayPersonajesVivos) {
            return EstadoCombate.DERROTA;
        }
        return EstadoCombate.EN_CURSO;
    }

    public void ataque(Entidad atacante, Entidad defensor) {
        validarAtaque(atacante, defensor);
        int danio = atacante.atacar();
        atacante.resetEstado();
        int vidaAntes = defensor.getVidaActual();
        defensor.recibirDanio(danio);
        LOGGER.info("Acción ejecutada: ataque; actor=" + atacante.getNombre() + ", objetivo="
                + defensor.getNombre() + ", daño efectivo=" + (vidaAntes - defensor.getVidaActual()));
        finalizarAccion(atacante);
    }

    public void defensa(Entidad personaje) {
        validarCombateActivo();
        if (personaje == null || personaje != actorActual || !personajes.contains(personaje)
                || !personaje.estaVivo()) {
            throw new IllegalStateException("No es el turno del personaje.");
        }
        personaje.defenderse();
        LOGGER.info("Acción ejecutada: defensa; actor=" + personaje.getNombre());
        finalizarAccion(personaje);
    }

    public void habilidad(Personaje actor, Entidad objetivo) {
        validarHabilidad(actor, objetivo);
        int vidaAntes = objetivo.getVidaActual();
        actor.usarHabilidadEspecial(objetivo);
        LOGGER.info("Acción ejecutada: habilidad; actor=" + actor.getNombre() + ", objetivo="
                + objetivo.getNombre() + ", daño/curación efectiva=" + (objetivo.getVidaActual() - vidaAntes));
        finalizarAccion(actor);
    }

    public void ejecutarTurnoEnemigo(Enemigo enemigo) {
        validarCombateActivo();
        if (enemigo == null || enemigo != actorActual || !enemigos.contains(enemigo) || !enemigo.estaVivo()) {
            throw new IllegalStateException("No es el turno del enemigo.");
        }
        List<Personaje> objetivosVivos = new ArrayList<>();
        for (Personaje personaje : personajes) {
            if (personaje.estaVivo()) {
                objetivosVivos.add(personaje);
            }
        }
        Personaje objetivo = enemigo.seleccionarObjetivo(objetivosVivos);
        int[] vidasAntes = new int[personajes.size()];
        for (int i = 0; i < personajes.size(); i++) {
            vidasAntes[i] = personajes.get(i).getVidaActual();
        }
        enemigo.ejecutarAccion(objetivosVivos);
        int danioEfectivo = 0;
        for (int i = 0; i < personajes.size(); i++) {
            int danio = vidasAntes[i] - personajes.get(i).getVidaActual();
            if (personajes.get(i) == objetivo) {
                danioEfectivo = danio;
            }
        }
        LOGGER.info("Acción ejecutada: " + enemigo.getClass().getSimpleName() + "; actor="
                + enemigo.getNombre() + ", objetivo=" + objetivo.getNombre()
                + ", daño efectivo=" + danioEfectivo);
        finalizarAccion(enemigo);
    }

    public void validarHabilidad(Personaje actor, Entidad objetivo) {
        validarCombateActivo();
        if (actor == null || actor != actorActual || !personajes.contains(actor) || !actor.estaVivo()) {
            throw new IllegalStateException("No es el turno del personaje.");
        }
        if (objetivo == null || !participantes.contains(objetivo) || !objetivo.estaVivo()) {
            throw new IllegalArgumentException("El objetivo no pertenece al combate o no está vivo.");
        }
        actor.validarObjetivoHabilidad(objetivo);
        actor.validarHabilidadDisponible();
    }

    private void validarAtaque(Entidad atacante, Entidad defensor) {
        validarCombateActivo();
        if (atacante == null || atacante != actorActual || !participantes.contains(atacante)
                || !atacante.estaVivo()) {
            throw new IllegalStateException("No es el turno del atacante.");
        }
        if (defensor == null || !participantes.contains(defensor) || !defensor.estaVivo()
                || atacante == defensor || esDelMismoBando(atacante, defensor)) {
            throw new IllegalArgumentException("El objetivo del ataque no es válido.");
        }
    }

    private boolean esDelMismoBando(Entidad primero, Entidad segundo) {
        return (personajes.contains(primero) && personajes.contains(segundo))
                || (enemigos.contains(primero) && enemigos.contains(segundo));
    }

    /** Usar una poción sobre sí mismo consume la acción del personaje en turno. */
    public void usarItem(Personaje actor, ItemConsumible item, Inventario inventario) {
        validarCombateActivo();
        if (actor == null || actor != actorActual || !actor.estaVivo()) {
            throw new IllegalStateException("Esperá el turno del personaje para usar un objeto.");
        }
        if (!participantes.contains(actor)) {
            throw new IllegalArgumentException("El personaje debe pertenecer a esta batalla.");
        }
        if (inventario == null || !inventario.consumirItem(item, actor)) {
            throw new IllegalArgumentException("El objeto ya no está en el inventario.");
        }
        LOGGER.info("Acción ejecutada: consumir objeto; actor=" + actor.getNombre() + ", objeto=" + item.getNombre());
        finalizarAccion(actor);
    }

    public void equiparItem(Personaje actor, ItemEquipable item, Partida partida) {
        validarActorDeTurno(actor);
        if (partida == null) {
            throw new IllegalArgumentException("Debe existir una partida para equipar objetos.");
        }
        partida.equiparItem(actor, item);
        LOGGER.info("Acción ejecutada: equipar; actor=" + actor.getNombre() + ", objeto=" + item.getNombre());
        finalizarAccion(actor);
    }

    public void desequiparItem(Personaje actor, ItemEquipable item, Partida partida) {
        validarActorDeTurno(actor);
        if (partida == null) {
            throw new IllegalArgumentException("Debe existir una partida para desequipar objetos.");
        }
        partida.desequiparItem(actor, item);
        LOGGER.info("Acción ejecutada: desequipar; actor=" + actor.getNombre() + ", objeto=" + item.getNombre());
        finalizarAccion(actor);
    }

    public void validarActorDeTurno(Personaje actor) {
        validarCombateActivo();
        if (actor == null || actor != actorActual || !personajes.contains(actor) || !actor.estaVivo()) {
            throw new IllegalStateException("Esperá el turno del personaje para cambiar el equipamiento.");
        }
    }

    private void finalizarAccion(Entidad actor) {
        int energiaAntes = actor.getEnergiaActual();
        actor.reiniciarEnergia();
        LOGGER.fine("Energía de " + actor.getNombre() + " después de la acción: "
                + energiaAntes + " -> " + actor.getEnergiaActual());
        actorActual = null;
    }

    private void validarCombateActivo() {
        if (combateTerminado()) {
            actorActual = null;
            throw new IllegalStateException("El combate ya terminó.");
        }
    }

    private boolean combateTerminado() {
        return getEstadoCombate() != EstadoCombate.EN_CURSO;
    }
}
