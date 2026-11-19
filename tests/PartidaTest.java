import java.util.ArrayList;
import java.util.List;

import model.entidades.personajes.Guerrero;
import model.entidades.personajes.Personaje;
import model.partidas.Mapa;
import model.partidas.Partida;

/** Pruebas de las reglas de la partida sin pasar por la pantalla. */
public class PartidaTest {
    private static int verificaciones;

    public static void main(String[] args) {
        partidaProtegeElEquipoMinimo();
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
