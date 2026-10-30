package model.Combate;

import java.util.ArrayList;
import java.util.List;

import model.entidades.Entidad;
import model.entidades.enemigos.Enemigo;
import model.entidades.personajes.Personaje;
import model.objetos.Inventario;
import model.objetos.ItemConsumible;

public class MotorCombate {
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
