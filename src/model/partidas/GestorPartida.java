package model.partidas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import helpers.archivos.GestorArchivos;
import helpers.entidades.CalculadoraDeExperiencia;
import helpers.entidades.GeneradorDeBotin;
import model.DTOs.PartidaGuardadaDTO;
import model.entidades.enemigos.Enemigo;
import model.entidades.enemigos.EnemigoFactory;
import model.entidades.enemigos.TipoEnemigo;
import model.entidades.personajes.Personaje;
import model.objetos.Item;
import model.persistencia.PartidaMapper;

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

    public void avanzarMapa() {
        if (partidaActual != null) {
            Mapa mapaActual = partidaActual.getMapaActual();
            if (mapaActual != null) {
                mapaActual.setCompletado(true);
                int numeroMapaSiguiente = mapaActual.getNumeroMapa() + 1;
                Mapa nuevoMapa = generarNuevoMapa(numeroMapaSiguiente);
                partidaActual.agregarMapa(nuevoMapa);
            }
        }
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
    	if (partidaActual == null) {
            return false;
        }
        return GestorArchivos.guardar(PartidaMapper.aSnapshot(partidaActual), nombreSlot);
    }

    public boolean cargarPartidaExistente(String nombreSlot) {
    	PartidaGuardadaDTO snapshot = GestorArchivos.cargar(nombreSlot);
        if (snapshot == null) {
            return false;
        }
        this.partidaActual = PartidaMapper.desdeSnapshot(snapshot);
        return true;
    }

    public boolean existePartidaGuardada(String nombreSlot) {
        return GestorArchivos.existe(nombreSlot);
    }

    /** Indica si la partida actual tiene un nivel sin completar para seguir jugando. */
    public boolean hayNivelPendiente() {
        Mapa mapa = partidaActual == null ? null : partidaActual.getMapaActual();
        return mapa != null && mapa.getNivelActual() != null;
    }

    public void otorgarExperiencia(int exp){
        for (Personaje personaje : this.partidaActual.getParty()) {
            personaje.ganarExperiencia(exp);
        }
    }

    // Completa exactamente el nivel victorioso y aplica sus recompensas una sola vez.
    public ResultadoRecompensa completarNivelConVictoria() {
        if (partidaActual == null || partidaActual.getMapaActual() == null
                || partidaActual.getMapaActual().getNivelActual() == null) {
            throw new IllegalStateException("No hay un nivel pendiente para completar.");
        }
        Mapa mapa = partidaActual.getMapaActual();
        Nivel nivel = mapa.getNivelActual();
        if (nivel.getEnemigos().stream().anyMatch(Enemigo::estaVivo)) {
            throw new IllegalStateException("El nivel sólo se puede completar después de una victoria.");
        }

        int experiencia = CalculadoraDeExperiencia.CalcularExperiencia(nivel.getEnemigos());
        otorgarExperiencia(experiencia);
        List<Item> objetosObtenidos = GeneradorDeBotin.SortearBotin(nivel.getEnemigos());
        List<Item> objetosIncorporados = new ArrayList<>();
        List<Item> objetosDescartados = new ArrayList<>();
        for (Item item : objetosObtenidos) {
            // Si el inventario está lleno, se descarta sólo ese botín; no se pierde lo anterior.
            if (partidaActual.getInventarioGlobal().agregarItem(item)) {
                objetosIncorporados.add(item);
            } else {
                objetosDescartados.add(item);
            }
        }
        nivel.setCompletado(true);
        if (mapa.getNivelActual() == null) {
            avanzarMapa();
        }
        return new ResultadoRecompensa(experiencia, objetosObtenidos, objetosIncorporados, objetosDescartados);
    }
}
