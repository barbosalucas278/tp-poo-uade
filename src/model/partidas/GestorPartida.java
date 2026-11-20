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

    private static final int MAPA_INICIAL = 1;
    private static final int NIVEL_INICIAL = 1;
    private static GestorPartida instance;
    public Partida partidaActual;

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
        Partida.validarCantidadMinimaPersonajes(party);
        Partida nuevaPartida = new Partida(nombrePartida, party, generarNuevoMapa(MAPA_INICIAL));
        this.partidaActual = nuevaPartida;
        return this.partidaActual;
    }

    /** Otorga las recompensas de la victoria y devuelve si queda otro nivel por jugar. */
    public boolean completarNivel(Nivel nivel) {
        Mapa mapa = partidaActual == null ? null : partidaActual.getMapaActual();
        if (nivel == null || mapa == null || nivel != mapa.getNivelActual()
                || nivel.getEnemigos().stream().anyMatch(Enemigo::estaVivo)) {
            throw new IllegalStateException("El nivel solo se puede completar después de una victoria.");
        }

        List<Enemigo> enemigos = nivel.getEnemigos();
        int experienciaGanada = CalculadoraDeExperiencia.CalcularExperiencia(enemigos);
        boolean primeraVictoria = mapa.getNumeroMapa() == MAPA_INICIAL && nivel.getNumeroNivel() == NIVEL_INICIAL;
        List<Item> itemsGanados = GeneradorDeBotin.SortearBotin(enemigos, primeraVictoria);
        otorgarExperiencia(experienciaGanada);
        partidaActual.setInventarioGlobal(itemsGanados);
        nivel.setCompletado(true);
        if (mapa.getNivelActual() == null) {
            avanzarMapa();
        }
        return hayNivelPendiente();
    }

    /** Avanza al siguiente mapa cuando se completaron todos los niveles del actual. */
    public void avanzarMapa() {
        Mapa mapaActual = partidaActual == null ? null : partidaActual.getMapaActual();
        if (mapaActual == null) {
            return;
        }
        if (mapaActual.getNivelActual() != null) {
            throw new IllegalStateException("Completá todos los niveles antes de avanzar de mapa.");
        }

        Mapa nuevoMapa = generarNuevoMapa(mapaActual.getNumeroMapa() + 1);
        mapaActual.setCompletado(true);
        partidaActual.agregarMapa(nuevoMapa);
    }

    private Mapa generarNuevoMapa(int numeroMapa) {
        Random random = new Random();
        List<Nivel> niveles = new ArrayList<>();

        int cantidadNiveles = random.nextInt(8) + 3;

        for (int numeroNivel = NIVEL_INICIAL; numeroNivel <= cantidadNiveles; numeroNivel++) {
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
}
