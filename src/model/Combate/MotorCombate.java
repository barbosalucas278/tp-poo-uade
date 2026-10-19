package model.Combate;

import java.util.ArrayList;
import java.util.List;

import model.entidades.Entidad;
import model.entidades.enemigos.Enemigo;
import model.entidades.personajes.Personaje;

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

    // TODO: resolver ataque y finalizar la acción.
    public void ataque(Entidad atacante, Entidad defensor) {
        if (atacante != actorActual) {
            throw new IllegalStateException("No es el turno del atacante.");
        }
        int danio = atacante.atacar();
        defensor.recibirDanio(danio);
        atacante.reiniciarEnergia();
        actorActual = null;
    }
    // TODO: detectar victoria o derrota.
    // TODO: definir prioridad cuando varios estén listos.
}