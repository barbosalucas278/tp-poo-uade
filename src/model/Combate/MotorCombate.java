package model.Combate;

import java.util.ArrayList;
import java.util.List;

import model.entidades.Entidad;
import model.entidades.enemigos.Enemigo;
import model.entidades.personajes.Personaje;
import model.objetos.Inventario;
import model.objetos.ItemConsumible;

/**
 * Coordina los turnos y la resolución de acciones durante un combate.
 */
public class MotorCombate {
    private final List<Personaje> personajes;
    private final List<Enemigo> enemigos;
    private final List<Entidad> participantes = new ArrayList<>();
    private Entidad actorActual;

    /**
     * Crea el motor con copias de los equipos y reúne a sus integrantes para
     * administrar el orden de los turnos.
     *
     * @param party personajes que participan en el combate
     * @param enemigos enemigos que participan en el combate
     */
    public MotorCombate(List<Personaje> party, List<Enemigo> enemigos) {
        this.personajes = new ArrayList<>(party);
        this.enemigos = new ArrayList<>(enemigos);

        participantes.addAll(this.personajes);
        participantes.addAll(this.enemigos);
    }

    /**
     * Si no hay un actor actual, busca uno listo; si no lo encuentra, avanza
     * la energía de los participantes vivos y vuelve a buscar. Los personajes
     * también regeneran su recurso durante el tick.
     */
    public void avanzarEnergia() {
        if (actorActual != null) {
            return; // Esperamos que termine su acción.
        }

        actorActual = buscarActorListo(); // Primero atendemos a quienes ya tenían energía suficiente.
        if (actorActual != null) {
            return;
        }

        for (Entidad entidad : participantes) {
            if (entidad.estaVivo()) {
                entidad.acumularEnergia(entidad.getVelocidad());
                if (entidad instanceof Personaje) {
                    ((Personaje) entidad).regenerarRecurso();
                }
            }
        }

        actorActual = buscarActorListo();
    }

    /**
     * Busca al primer participante vivo que haya acumulado energía suficiente.
     */
    private Entidad buscarActorListo() {
        for (Entidad entidad : participantes) {
            if (entidad.estaVivo() && entidad.listaParaLaAccion()) {
                return entidad;
            }
        }
        return null;
    }

    /**
     * Devuelve quién tiene el turno actualmente.
     */
    public Entidad getActorActual() {
        return actorActual;
    }

    /**
     * Ejecuta un ataque básico y finaliza el turno del atacante.
     *
     * @param atacante entidad que realiza el ataque
     * @param defensor entidad que recibe el daño
     * @throws IllegalStateException si el atacante no tiene el turno
     * @throws IllegalArgumentException si el defensor no es un objetivo hostil válido y vivo                            
     */
    public void ataque(Entidad atacante, Entidad defensor) {
        if (atacante != actorActual) {
            throw new IllegalStateException("No es el turno del atacante.");
        }
        if (defensor == null || !defensor.estaVivo() || !esObjetivoHostil(atacante, defensor)) {
            throw new IllegalArgumentException("El defensor debe ser un participante vivo del equipo contrario.");
        }
        int danio = atacante.atacar();
        defensor.recibirDanio(danio);
        finalizarAccion(atacante);
    }

    /**
     * Ejecuta la habilidad especial del personaje actual. Las habilidades de
     * apoyo curan a los personajes vivos; las demás requieren un objetivo vivo
     * del equipo enemigo que participe del combate.
     *
     * @param atacante personaje que usa la habilidad
     * @param objetivo entidad objetivo de una habilidad de daño; puede ser {@code null} para una habilidad de apoyo           
     * @throws IllegalStateException si el personaje no tiene el turno o no cuenta con el recurso necesario    
     * @throws IllegalArgumentException si el objetivo de daño no es válido
     */
    public void usarHabilidadEspecial(Personaje atacante, Entidad objetivo) {
        if (atacante != actorActual) { // Solo puede actuar el personaje al que le toca
            throw new IllegalStateException("No es el turno del personaje."); 
        }

        // Habilidad de apoyo (Curador): cura a todos los personajes vivos del grupo
        if (atacante.esHabilidadDeApoyo()) {
            validarCostoHabilidad(atacante);
            int curacion = atacante.usarHabilidadEspecial();
            pagarCostoHabilidad(atacante);
            for (Entidad entidad : participantes) {
                if (entidad instanceof Personaje && entidad.estaVivo()) {
                    entidad.recibirSanacion(curacion);
                }
            }
            finalizarAccion(atacante);
            return;
        }

        // Habilidad de daño: necesita un objetivo hostil, válido y vivo
        if (objetivo == null || !objetivo.estaVivo() || !esObjetivoHostil(atacante, objetivo)) {
            throw new IllegalArgumentException("Elegí un enemigo válido que siga en combate.");
        }

        validarCostoHabilidad(atacante);

        int danio = atacante.usarHabilidadEspecial();
        pagarCostoHabilidad(atacante);
        objetivo.recibirDanio(danio);
        finalizarAccion(atacante);
    }

    /**
     * Comprueba que el personaje tenga el recurso requerido por su habilidad.
     *
     * @param atacante personaje que intenta usar la habilidad
     * @throws IllegalStateException si no tiene recurso suficiente
     */
    private void validarCostoHabilidad(Entidad entidad) {
        if (entidad.getRecursoActual() < entidad.getCostoHabilidad()) {
            throw new IllegalStateException("No hay suficiente recurso para usar la habilidad especial.");
        }
    }

    /**
     * Descuenta del personaje el costo de su habilidad especial.
     *
     * @param atacante personaje que paga el costo
     */
    private void pagarCostoHabilidad(Entidad entidad) {
        entidad.consumirRecursos(entidad.getCostoHabilidad());
    }

    /**
     * Ejecuta la habilidad especial del enemigo actual. Puede aplicarla a todos
     * los personajes vivos o a un único objetivo, según el tipo de habilidad.
     *
     * @param enemigo enemigo que usa la habilidad
     * @param objetivo personaje objetivo para una habilidad individual
     * @param personajesVivos personajes vivos afectados por una habilidad de área
     * @throws IllegalStateException si el enemigo no tiene el turno o no cuenta
     *                               con el recurso necesario
     * @throws IllegalArgumentException si el objetivo individual no es válido
     */
    public void habilidadEnemigo(Enemigo enemigo, Personaje objetivo, List<Personaje> personajesVivos) {
        // Solo puede actuar el enemigo al que le toca
        if (enemigo != actorActual) {
            throw new IllegalStateException("No es el turno del enemigo.");
        }

        // Habilidad de área (Jefe): golpea a todos los personajes vivos del grupo
        if (enemigo.habilidadAfectaATodos()) {
            validarCostoHabilidad(enemigo);
            int danio = enemigo.usarHabilidadEspecial();
            pagarCostoHabilidad(enemigo);
            for (Personaje personaje : personajesVivos) {
                aplicarDanioHabilidad(enemigo, personaje, danio);
            }
            finalizarAccion(enemigo);
            return;
        }

        // Habilidad a un solo objetivo: necesita un personaje del grupo, válido y vivo
        if (objetivo == null || !objetivo.estaVivo() || !esObjetivoHostil(enemigo, objetivo)) {
            throw new IllegalArgumentException("El enemigo necesita un objetivo válido que siga en combate.");
        }

        validarCostoHabilidad(enemigo);

        // Cada enemigo calcula su propio daño (polimorfismo) y el objetivo lo recibe
        int danio = enemigo.usarHabilidadEspecial();
        pagarCostoHabilidad(enemigo);
        aplicarDanioHabilidad(enemigo, objetivo, danio);
        finalizarAccion(enemigo);
    }

    /**
     * Comprueba que el objetivo pertenezca al equipo contrario del atacante
     * y que sea uno de los participantes de este combate.
     *
     * @param atacante entidad que realiza la acción ofensiva
     * @param objetivo entidad que recibiría el efecto
     * @return {@code true} si el objetivo es del equipo contrario y participa
     */
    private boolean esObjetivoHostil(Entidad atacante, Entidad objetivo) {
        if (atacante instanceof Personaje) {
            return objetivo instanceof Enemigo && enemigos.contains(objetivo);
        }
        if (atacante instanceof Enemigo) {
            return objetivo instanceof Personaje && personajes.contains(objetivo);
        }
        return false;
    }

    /**
     * Aplica el daño de una habilidad enemiga, respetando si esta ignora la
     * defensa del personaje.
     *
     * @param enemigo enemigo que ejecutó la habilidad
     * @param objetivo personaje que recibe el daño
     * @param danio cantidad de daño calculada por la habilidad
     */
    private void aplicarDanioHabilidad(Enemigo enemigo, Personaje objetivo, int danio) {
        if (enemigo.habilidadIgnoraDefensa()) {
            objetivo.setVidaActual(objetivo.getVidaActual() - danio);
        } else {
            objetivo.recibirDanio(danio);
        }
    }

    /**
     * Finaliza la acción defensiva del actor actual.
     *
     * @param personaje entidad que realiza la defensa
     * @throws IllegalStateException si la entidad no está viva o no tiene el turno
     */
    public void defensa(Entidad personaje) {
        if (personaje == null || personaje != actorActual || !personaje.estaVivo()) {
            throw new IllegalStateException("Esperá el turno de la entidad para defenderte.");
        }
        finalizarAccion(personaje);
    }

    /**
     * Usa un objeto consumible sobre el personaje actual y finaliza su turno.
     *
     * @param actor personaje que usa el objeto
     * @param item objeto consumible que se quiere usar
     * @param inventario inventario del que se consume el objeto
     * @throws IllegalStateException si el personaje no tiene el turno o no está vivo
     * @throws IllegalArgumentException si el personaje no participa en el combate
     *                                  o el objeto no está disponible
     */
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

    /**
     * Reinicia la energía del actor y deja el combate sin un turno activo.
     *
     * @param actor entidad cuya acción acaba de terminar
     */
    private void finalizarAccion(Entidad actor) {
        actor.reiniciarEnergia();
        actorActual = null;
    }
}
