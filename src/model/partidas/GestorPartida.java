package model.partidas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.entidades.enemigos.Enemigo;
import model.entidades.enemigos.EnemigoFactory;
import model.entidades.enemigos.TipoEnemigo;
import model.entidades.personajes.Personaje;

public class GestorPartida {

    private static GestorPartida instance;
    private Partida partidaActual;
    private final int NIVEL_INICIAL = 1;

    private GestorPartida() {
    }

    public static GestorPartida getInstance() {
        if (instance == null) {
            instance = new GestorPartida();
        }
        return instance;
    }

    public Partida getPartidaActual() {
        return partidaActual;
    }

    public Partida nuevaPartida(String nombrePartida, List<Personaje> party) {
        this.partidaActual = new Partida(nombrePartida, party, generarNuevoMapa(NIVEL_INICIAL));
        return this.partidaActual;
    }

    private Mapa generarNuevoMapa(int numeroMapa) {
        Random random = new Random();
        List<Nivel> niveles = new ArrayList<>();

        int cantidadNiveles = random.nextInt(8) + 3;

        for (int numeroNivel = 1; numeroNivel <= cantidadNiveles; numeroNivel++) {
            List<Enemigo> enemigos = new ArrayList<>();

            int nivelEnemigos = numeroMapa + numeroNivel - 1;

            if (numeroNivel == cantidadNiveles) {
                Enemigo jefe = EnemigoFactory.crearEnemigo(TipoEnemigo.DRAGON, nivelEnemigos);
                enemigos.add(jefe);
            } else {
                int cantidadEnemigos = random.nextInt(3) + 1;

                for (int i = 0; i < cantidadEnemigos; i++) {
                    int tipo = random.nextInt(3);
                    Enemigo enemigo = EnemigoFactory.crearEnemigo(TipoEnemigo.values()[tipo], nivelEnemigos);
                    enemigos.add(enemigo);
                }
            }

            Nivel nivel = new Nivel(numeroNivel, enemigos);
            niveles.add(nivel);
        }

        return new Mapa(numeroMapa, niveles);
    }

    public void iniciarPartida(String nombrePartida) {

    }

    public boolean guardarPartidaActual(String nombreSlot) {
        // if (partidaActual != null) {
        // return GestorArchivos.guardarPartida(partidaActual, nombreSlot);
        // }
        return false;
    }

    public boolean cargarPartidaExistente(String nombreSlot) {
        // Jugador j = GestorArchivos.cargarPartida(nombreSlot);
        // if (j != null) {
        // this.jugadorActual = j;
        // return true;
        // }
        return false;
    }
}