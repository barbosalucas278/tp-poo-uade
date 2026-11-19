import java.util.ArrayList;
import java.util.List;

import model.entidades.enemigos.Enemigo;
import model.entidades.personajes.Guerrero;
import model.entidades.personajes.Personaje;
import model.partidas.GestorPartida;
import model.partidas.Mapa;
import model.partidas.Nivel;
import model.partidas.Partida;

/** Pruebas de las reglas de la partida sin pasar por la pantalla. */
public class PartidaTest {
    private static int verificaciones;

    public static void main(String[] args) {
        partidaProtegeElEquipoMinimo();
        gestorAvanzaMapaSoloAlCompletarLosNiveles();
        System.out.println("OK: " + verificaciones + " verificaciones de partida.");
    }

    private static void partidaProtegeElEquipoMinimo() {
        Personaje primero = nuevoGuerrero("Primero");
        Personaje segundo = nuevoGuerrero("Segundo");
        Personaje tercero = nuevoGuerrero("Tercero");
        Mapa mapa = new Mapa(1, List.of());
        Partida partida = new Partida("Prueba", List.of(primero, segundo), mapa);

        esperarRechazo(() -> partida.removerPersonaje(primero));
        esperarRechazo(() -> partida.agregarPersonaje(null));
        partida.removerPersonaje(tercero);
        verificar(partida.getParty().equals(List.of(primero, segundo)),
                "Los cambios inválidos deben conservar el equipo.");

        partida.agregarPersonaje(tercero);
        partida.removerPersonaje(primero);
        verificar(partida.getParty().equals(List.of(segundo, tercero)),
                "Se puede retirar un personaje si quedan al menos dos.");

        esperarRechazo(() -> new Partida("Incompleta", List.of(primero), mapa));
        List<Personaje> conNulo = new ArrayList<>(List.of(primero));
        conNulo.add(null);
        esperarRechazo(() -> new Partida("Con integrante nulo", conNulo, mapa));
    }

    private static void gestorAvanzaMapaSoloAlCompletarLosNiveles() {
        GestorPartida gestor = GestorPartida.getInstance();
        Partida partida = gestor.nuevaPartida("Progresión", List.of(nuevoGuerrero("Primero"), nuevoGuerrero("Segundo")));
        Mapa mapa = partida.getMapaActual();

        esperarRechazo(gestor::avanzarMapa);
        verificar(partida.getMapas().equals(List.of(mapa)) && !mapa.isCompletado(),
                "Una llamada directa no debe avanzar mientras queden niveles.");

        completarNivel(gestor, mapa.getNivelActual());
        verificar(partida.getMapaActual() == mapa && mapa.getNivelActual() != null,
                "Una victoria intermedia debe dejar la partida en el mismo mapa.");

        for (Nivel nivel : mapa.getNiveles()) {
            if (!nivel.isCompletado()) {
                completarNivel(gestor, nivel);
            }
        }

        verificar(mapa.isCompletado() && partida.getMapas().size() == 2
                && partida.getMapaActual().getNumeroMapa() == mapa.getNumeroMapa() + 1,
                "La última victoria debe habilitar exactamente un mapa nuevo.");
        esperarRechazo(gestor::avanzarMapa);
    }

    private static void completarNivel(GestorPartida gestor, Nivel nivel) {
        for (Enemigo enemigo : nivel.getEnemigos()) {
            enemigo.setVidaActual(0);
        }
        gestor.completarNivel(nivel);
    }

    private static Personaje nuevoGuerrero(String nombre) {
        return new Guerrero(nombre, 100, 50, 20, 5, 10, "");
    }

    private static void esperarRechazo(Runnable operacion) {
        try {
            operacion.run();
        } catch (IllegalArgumentException | IllegalStateException esperado) {
            verificaciones++;
            return;
        }
        throw new AssertionError("Se esperaba rechazar la operación.");
    }

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
        verificaciones++;
    }
}
