import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;

import controller.BatallaController;
import model.Combate.EstadoCombate;
import model.Combate.MotorCombate;
import model.DTOs.ItemInventarioDTO;
import model.DTOs.ResultadoRecompensaDTO;
import model.entidades.personajes.Arquero;
import model.entidades.personajes.Curador;
import model.entidades.enemigos.Enemigo;
import model.entidades.enemigos.EnemigoFactory;
import model.entidades.enemigos.Elite;
import model.entidades.enemigos.Estandar;
import model.entidades.enemigos.Jefe;
import model.entidades.enemigos.Minion;
import model.entidades.enemigos.TipoEnemigo;
import model.entidades.personajes.Guerrero;
import model.entidades.personajes.Hechicero;
import model.entidades.personajes.Mago;
import model.entidades.personajes.Personaje;
import model.entidades.personajes.TipoPersonaje;
import model.objetos.Inventario;
import model.objetos.ItemConsumible;
import model.objetos.ItemEquipable;
import model.objetos.ItemsConsumiblesFactory;
import model.objetos.PocionMana;
import model.objetos.PocionVida;
import model.objetos.TipoItemConsumible;
import model.objetos.TipoObjeto;
import model.objetos.TipoRareza;
import model.partidas.GestorPartida;
import model.partidas.Mapa;
import model.partidas.Nivel;
import model.partidas.Partida;
import model.persistencia.PartidaMapper;
import helpers.archivos.GestorArchivos;

/** Pruebas sin dependencias: compilar junto a src y ejecutar InventarioCombateTest. */
public class InventarioCombateTest {
    private static final int ESCENARIOS_EJECUTADOS = 33;
    private static int verificaciones;

    public static void main(String[] args) {
        curarAlActorConsumeSuAccion();
        pocionSoloAfectaAlActor();
        restaurarManaRespetaMaximo();
        usosInvalidosConservanObjetoYTurno();
        soloActuaElPersonajeEnTurno();
        noSePuedeReutilizarUnObjetoConsumido();
        ataqueSigueFinalizandoElTurno();
        ataqueRechazaObjetivosInvalidos();
        ataqueRechazaBandosYTurnos();
        defensaSoloAceptaAlPersonajeEnTurno();
        combateTerminadoNoAvanzaNiActua();
        estadoCombateEsUnicoYExplicito();
        partidaProtegeElMinimoDePersonajes();
        controllerReconstruyeMotorAlAvanzarNivel();
        primerEncuentroPermiteActuarAlEnemigoAntesDeLaVictoria();
        flujoNormalAvanzaDespuesDeCadaAccionYLosEnemigosActuan();
        habilidadesValidasConsumenRecursoYTurno();
        habilidadesInvalidasConservanEstado();
        persistenciaConservaClasesYMigraTiposAntiguos();
        comportamientosEnemigosSonDeterministas();
        enemigosConsumenUnTurnoYRespetanElFinDelCombate();
        furiaDelJefeSeActivaUnaSolaVez();
        controllerValidaYExponeElInventario();
        factoryConservaRarezaYTipo();
        equipamientoValidaAsignacionYBonificaciones();
        persistenciaRealConservaEquipamientoSinDuplicarBonos();
        controllerGestionaEquipamientoComoAccion();
        coleccionesDePartidaMapaYNivelEstanProtegidas();
        recompensasSeOtorganUnaVezYBotinLlenoNoRompeLaPartida();
        derrotaNoOtorgaRecompensas();
        progresionPermiteSubirVariosNiveles();
        transicionaEntreMapasYConservaElGuardado();
        resultadoDeVictoriaReflejaBotinReal();
        System.out.println("OK: " + ESCENARIOS_EJECUTADOS + " escenarios; " + verificaciones
                + " verificaciones dinámicas de inventario y acciones de combate.");
    }

    private static void curarAlActorConsumeSuAccion() {
        Escenario escenario = new Escenario();
        escenario.actor.setVidaActual(90);
        PocionVida pocion = new PocionVida("Vida", "Cura", 25);
        escenario.inventario.agregarItem(pocion);

        escenario.motor.usarItem(escenario.actor, pocion, escenario.inventario);

        verificar(escenario.actor.getVidaActual() == 100, "La curación debe respetar el máximo.");
        verificar(escenario.inventario.getItems().isEmpty(), "Debe consumirse una poción.");
        verificar(escenario.actor.getEnergiaActual() == 0, "Debe agotarse la energía del actor.");
        verificar(escenario.motor.getActorActual() == null, "Debe finalizar la acción.");
    }

    private static void pocionSoloAfectaAlActor() {
        Escenario escenario = new Escenario();
        escenario.actor.setVidaActual(30);
        escenario.aliado.setVidaActual(20);
        escenario.aliado.acumularEnergia(75);
        PocionVida pocion = new PocionVida("Vida", "Cura", 25);
        escenario.inventario.agregarItem(pocion);

        escenario.motor.usarItem(escenario.actor, pocion, escenario.inventario);

        verificar(escenario.actor.getVidaActual() == 55, "La poción solo debe curar al personaje en turno.");
        verificar(escenario.aliado.getVidaActual() == 20, "La poción no debe curar a otro aliado.");
        verificar(escenario.aliado.getEnergiaActual() == 75, "El aliado debe conservar su energía.");
        verificar(escenario.actor.getEnergiaActual() == 0, "Quien usa el objeto debe gastar su acción.");
    }

    private static void restaurarManaRespetaMaximo() {
        Escenario escenario = new Escenario(new Hechicero("Actor", 100, 100, 20, 5, 10, ""));
        escenario.actor.setRecursoActual(90);
        PocionMana pocion = new PocionMana("Maná", "Restaura", 50);
        escenario.inventario.agregarItem(pocion);

        escenario.motor.usarItem(escenario.actor, pocion, escenario.inventario);

        verificar(escenario.actor.getRecursoActual() == 100, "El maná del actor debe respetar el máximo.");
        verificar(escenario.actor.getEnergiaActual() == 0, "El hechicero debe consumir su propio turno.");
        verificar(escenario.inventario.getItems().isEmpty(), "La poción de maná debe consumirse.");
        verificar(escenario.motor.getActorActual() == null, "El uso de maná también consume la acción.");
    }

    private static void usosInvalidosConservanObjetoYTurno() {
        Escenario escenario = new Escenario();
        PocionVida vida = new PocionVida("Vida", "Cura", 25);
        PocionMana mana = new PocionMana("Maná", "Restaura", 25);
        escenario.inventario.agregarItem(vida);
        escenario.inventario.agregarItem(mana);

        rechazarSinConsumir(escenario, escenario.actor, vida);
        rechazarSinConsumir(escenario, escenario.actor, mana);
        rechazarSinConsumir(escenario, escenario.aliado, mana);
        escenario.actor.setVidaActual(0);
        rechazarSinConsumir(escenario, escenario.actor, vida);
        verificar(escenario.actor.getVidaActual() == 0, "Un personaje caído no puede consumir una poción.");

        Hechicero externo = new Hechicero("Externo", 100, 100, 10, 1, 1, "");
        externo.setVidaActual(50);
        rechazarSinConsumir(escenario, externo, vida);
        escenario.actor.setVidaActual(50);
        rechazarSinConsumir(escenario, escenario.actor, new PocionVida("Ajena", "", 25));

        Escenario hechicero = new Escenario(new Hechicero("Actor", 100, 100, 20, 5, 10, ""));
        PocionMana manaCompleto = new PocionMana("Maná", "Restaura", 25);
        hechicero.inventario.agregarItem(manaCompleto);
        rechazarSinConsumir(hechicero, hechicero.actor, manaCompleto);
    }

    private static void soloActuaElPersonajeEnTurno() {
        Escenario escenario = new Escenario();
        escenario.actor.setVidaActual(50);
        PocionVida vida = new PocionVida("Vida", "Cura", 25);
        escenario.inventario.agregarItem(vida);
        rechazarSinConsumir(escenario, escenario.aliado, vida);

        MotorCombate sinTurno = new MotorCombate(List.of(escenario.actor), List.of(escenario.enemigo));
        esperarRechazo(() -> sinTurno.usarItem(escenario.actor, vida, escenario.inventario));
        verificar(escenario.inventario.getItems().contains(vida), "Fuera de turno debe conservarse la poción.");

        escenario.actor.reiniciarEnergia();
        escenario.enemigo.acumularEnergia(100);
        MotorCombate turnoEnemigo = new MotorCombate(List.of(escenario.actor), List.of(escenario.enemigo));
        turnoEnemigo.avanzarEnergia();
        esperarRechazo(() -> turnoEnemigo.usarItem(escenario.actor, vida, escenario.inventario));
        verificar(turnoEnemigo.getActorActual() == escenario.enemigo, "Debe conservarse el turno enemigo.");
        verificar(escenario.actor.getVidaActual() == 50, "Un uso fuera de turno no debe curar.");
    }

    private static void noSePuedeReutilizarUnObjetoConsumido() {
        Escenario escenario = new Escenario();
        escenario.actor.setVidaActual(20);
        PocionVida vida = new PocionVida("Vida", "Cura", 25);
        escenario.inventario.agregarItem(vida);
        escenario.motor.usarItem(escenario.actor, vida, escenario.inventario);
        esperarRechazo(() -> escenario.motor.usarItem(escenario.actor, vida, escenario.inventario));
        verificar(escenario.actor.getVidaActual() == 45, "El segundo intento no debe volver a aplicar el efecto.");

        escenario.actor.acumularEnergia(100);
        escenario.motor.avanzarEnergia();
        rechazarSinConsumir(escenario, escenario.actor, vida);
    }

    private static void ataqueSigueFinalizandoElTurno() {
        Escenario escenario = new Escenario();
        int vidaAnterior = escenario.enemigo.getVidaActual();
        escenario.motor.ataque(escenario.actor, escenario.enemigo);
        verificar(escenario.enemigo.getVidaActual() < vidaAnterior, "El ataque debe seguir aplicando daño.");
        verificar(escenario.actor.getEnergiaActual() == 0 && escenario.motor.getActorActual() == null,
                "El ataque debe seguir consumiendo la acción.");
    }

    private static void ataqueRechazaObjetivosInvalidos() {
        Escenario escenario = new Escenario();
        Enemigo externo = EnemigoFactory.crearEnemigo(TipoEnemigo.ORCO, 1);
        int energia = escenario.actor.getEnergiaActual();
        int vida = escenario.actor.getVidaActual();

        esperarIllegalArgument(() -> escenario.motor.ataque(escenario.actor, externo));
        esperarIllegalArgument(() -> escenario.motor.ataque(escenario.actor, null));
        Enemigo otroEnemigo = EnemigoFactory.crearEnemigo(TipoEnemigo.ORCO, 1);
        MotorCombate motorConOtroEnemigo = new MotorCombate(List.of(escenario.actor, escenario.aliado),
                List.of(escenario.enemigo, otroEnemigo));
        motorConOtroEnemigo.avanzarEnergia();
        escenario.enemigo.setVidaActual(0);
        esperarIllegalArgument(() -> motorConOtroEnemigo.ataque(escenario.actor, escenario.enemigo));

        verificar(motorConOtroEnemigo.getActorActual() == escenario.actor,
                "Un objetivo inválido debe conservar el turno.");
        verificar(escenario.actor.getEnergiaActual() == energia && escenario.actor.getVidaActual() == vida,
                "Un ataque rechazado no debe modificar al atacante.");
    }

    private static void ataqueRechazaBandosYTurnos() {
        Escenario escenario = new Escenario();
        int vidaActor = escenario.actor.getVidaActual();
        int vidaAliado = escenario.aliado.getVidaActual();
        int vidaEnemigo = escenario.enemigo.getVidaActual();

        esperarIllegalArgument(() -> escenario.motor.ataque(escenario.actor, escenario.aliado));
        esperarIllegalArgument(() -> escenario.motor.ataque(escenario.actor, escenario.actor));
        verificar(escenario.actor.getVidaActual() == vidaActor && escenario.aliado.getVidaActual() == vidaAliado
                && escenario.enemigo.getVidaActual() == vidaEnemigo,
                "Un ataque rechazado no debe cambiar vidas de ningún participante.");
        int vidaAntesDeProbarDefensa = escenario.actor.getVidaActual();
        escenario.actor.recibirDanio(20);
        verificar(escenario.actor.getVidaActual() == vidaAntesDeProbarDefensa - 15,
                "Un ataque rechazado no debe cambiar el estado defensivo.");

        escenario.actor.reiniciarEnergia();
        escenario.aliado.reiniciarEnergia();
        escenario.enemigo.acumularEnergia(100);
        MotorCombate motorEnemigo = new MotorCombate(List.of(escenario.actor, escenario.aliado),
                List.of(escenario.enemigo));
        motorEnemigo.avanzarEnergia();
        esperarIllegalState(() -> motorEnemigo.ataque(escenario.actor, escenario.enemigo));
        verificar(escenario.actor.getEnergiaActual() == 0 && motorEnemigo.getActorActual() == escenario.enemigo,
                "Un ataque fuera de turno debe conservar el actor actual y la energía.");
        int vidaEnemigoAntes = escenario.enemigo.getVidaActual();
        esperarIllegalArgument(() -> motorEnemigo.ataque(escenario.enemigo, escenario.enemigo));
        verificar(escenario.enemigo.getVidaActual() == vidaEnemigoAntes
                && motorEnemigo.getActorActual() == escenario.enemigo,
                "Un enemigo no puede atacar a otro enemigo ni perder su turno.");
    }

    private static void defensaSoloAceptaAlPersonajeEnTurno() {
        Escenario escenario = new Escenario();
        esperarIllegalState(() -> escenario.motor.defensa(escenario.aliado));
        verificar(escenario.motor.getActorActual() == escenario.actor,
                "Una defensa fuera de turno debe conservar el actor actual.");
        escenario.aliado.recibirDanio(20);
        verificar(escenario.aliado.getVidaActual() == 85,
                "Una defensa rechazada no debe modificar al aliado.");

        escenario.motor.defensa(escenario.actor);
        int vidaAntesDeDefender = escenario.actor.getVidaActual();
        escenario.actor.recibirDanio(20);
        verificar(escenario.actor.getVidaActual() == vidaAntesDeDefender - 5,
                "La defensa válida debe modificar el estado defensivo.");
        verificar(escenario.motor.getActorActual() == null && escenario.actor.getEnergiaActual() == 0,
                "La defensa válida debe consumir el turno.");
    }

    private static void combateTerminadoNoAvanzaNiActua() {
        Escenario escenario = new Escenario();
        escenario.enemigo.setVidaActual(0);
        int vida = escenario.actor.getVidaActual();
        int energia = escenario.actor.getEnergiaActual();
        escenario.actor.resetEstado();

        escenario.motor.avanzarEnergia();
        verificar(escenario.motor.getActorActual() == null && escenario.actor.getEnergiaActual() == energia,
                "Un combate terminado no debe avanzar energía ni seleccionar actor.");
        esperarIllegalState(() -> escenario.motor.ataque(escenario.actor, escenario.enemigo));
        esperarIllegalState(() -> escenario.motor.defensa(escenario.actor));
        esperarIllegalState(() -> escenario.motor.usarItem(escenario.actor,
                new PocionVida("Vida", "", 10), new Inventario()));
        verificar(escenario.actor.getVidaActual() == vida && escenario.actor.getEnergiaActual() == energia,
                "Las acciones rechazadas al terminar no deben modificar el estado.");
    }

    private static void estadoCombateEsUnicoYExplicito() {
        Guerrero actor = new Guerrero("Estado actor", 100, 50, 20, 1, 10, "");
        Enemigo enemigo = EnemigoFactory.crearEnemigo(TipoEnemigo.SLIME, 1);
        MotorCombate enCurso = new MotorCombate(List.of(actor), List.of(enemigo));
        verificar(enCurso.getEstadoCombate() == EstadoCombate.EN_CURSO,
                "Un combate con participantes vivos debe estar en curso.");

        enemigo.setVidaActual(0);
        verificar(enCurso.getEstadoCombate() == EstadoCombate.VICTORIA,
                "Sin enemigos vivos el estado debe ser victoria.");

        Guerrero derrotado = new Guerrero("Estado derrotado", 100, 50, 20, 1, 10, "");
        derrotado.setVidaActual(0);
        Enemigo enemigoVivo = EnemigoFactory.crearEnemigo(TipoEnemigo.SLIME, 1);
        MotorCombate derrota = new MotorCombate(List.of(derrotado), List.of(enemigoVivo));
        verificar(derrota.getEstadoCombate() == EstadoCombate.DERROTA,
                "Sin personajes vivos el estado debe ser derrota.");
    }

    private static void partidaProtegeElMinimoDePersonajes() {
        Enemigo enemigo = EnemigoFactory.crearEnemigo(TipoEnemigo.SLIME, 1);
        Mapa mapa = new Mapa(1, List.of(new Nivel(1, List.of(enemigo))));
        esperarIllegalArgument(() -> new Partida("Party nula", null, mapa));
        esperarIllegalArgument(() -> new Partida("Inválida", List.of(nuevoGuerrero()), mapa));

        Personaje repetido = nuevoGuerrero();
        esperarIllegalArgument(() -> new Partida("Repetida", List.of(repetido, repetido), mapa));
        ArrayList<Personaje> partyConNulo = new ArrayList<>();
        partyConNulo.add(repetido);
        partyConNulo.add(null);
        esperarIllegalArgument(() -> new Partida("Nulo", partyConNulo, mapa));
        esperarIllegalArgument(() -> new Partida("Cinco", List.of(repetido,
                new Hechicero("2", 100, 100, 1, 1, 1, ""),
                new Hechicero("3", 100, 100, 1, 1, 1, ""),
                new Hechicero("4", 100, 100, 1, 1, 1, ""),
                new Hechicero("5", 100, 100, 1, 1, 1, "")), mapa));

        Personaje primero = nuevoGuerrero();
        Personaje segundo = new Hechicero("Segundo", 100, 100, 20, 5, 5, "");
        ArrayList<Personaje> listaOriginal = new ArrayList<>(List.of(primero, segundo));
        Partida partida = new Partida("Válida", listaOriginal, mapa);
        listaOriginal.clear();
        verificar(partida.getParty().size() == 2,
                "Modificar la lista original no debe modificar la party.");
        esperarIllegalState(() -> partida.removerPersonaje(partida.getParty().get(0)));
        esperarUnsupported(() -> partida.getParty().clear());
        esperarIllegalArgument(() -> partida.agregarPersonaje(null));
        esperarIllegalArgument(() -> partida.agregarPersonaje(primero));
        partida.agregarPersonaje(new Hechicero("Mismo nombre", 100, 100, 1, 1, 1, ""));
        partida.agregarPersonaje(new Hechicero("Otro", 100, 100, 1, 1, 1, ""));
        esperarIllegalState(() -> partida.agregarPersonaje(new Hechicero("Sobra", 100, 100, 1, 1, 1, "")));
        verificar(partida.getParty().size() == 4,
                "La party debe conservar el rango de dos a cuatro personajes.");
    }

    private static void controllerReconstruyeMotorAlAvanzarNivel() {
        Personaje actor = nuevoGuerrero();
        Personaje aliado = new Hechicero("Aliado nivel", 100, 100, 20, 5, 5, "");
        Partida partida = GestorPartida.getInstance().nuevaPartida("Transición",
                new ArrayList<>(List.of(actor, aliado)));
        BatallaController controller = new BatallaController();
        for (Enemigo enemigo : partida.getMapaActual().getNivelActual().getEnemigos()) {
            enemigo.setVidaActual(0);
        }
        int vidaAntes = actor.getVidaActual();
        int vidaAliadoAntes = aliado.getVidaActual();
        controller.continuarAlSiguienteNivel();
        List<Enemigo> nuevosEnemigos = partida.getMapaActual().getNivelActual().getEnemigos();
        Enemigo nuevoEnemigo = nuevosEnemigos.get(0);
        nuevoEnemigo.acumularEnergia(100);
        controller.avanzarCombate();
        verificar(partida.getMapaActual().getNivelActual().getEnemigos().size() == nuevosEnemigos.size()
                && partida.getMapaActual().getNivelActual().getEnemigos().get(0) == nuevosEnemigos.get(0),
                "La partida debe conservar el nivel nuevo como combate actual.");
        verificar(actor.getVidaActual() < vidaAntes || aliado.getVidaActual() < vidaAliadoAntes,
                "El controller debe ejecutar acciones de los enemigos del nuevo nivel.");
    }

    // Reproduce el recorrido de la vista: avanzar por tick y resolver sólo cuando hay turno jugador.
    private static void flujoNormalAvanzaDespuesDeCadaAccionYLosEnemigosActuan() {
        Guerrero actor = new Guerrero("Jugador flujo", 1000, 50, 1, 0, 20, "");
        Mago aliado = new Mago("Aliado flujo", 1000, 100, 1, 0, 5, "");
        Partida partida = GestorPartida.getInstance().nuevaPartida("Flujo normal",
                new ArrayList<>(List.of(actor, aliado)));
        BatallaController controller = new BatallaController();
        List<Enemigo> enemigos = partida.getMapaActual().getNivelActual().getEnemigos();
        for (Enemigo enemigo : enemigos) {
            enemigo.setVidaActual(1000);
            enemigo.setAtaqueBase(20);
        }

        int accionesJugador = 0;
        int vidaInicial = actor.getVidaActual();
        for (int tick = 0; tick < 80
                && controller.verificarEstadoCombate() == EstadoCombate.EN_CURSO; tick++) {
            controller.avanzarCombate();
            int indice = controller.getIndicePersonajeEnTurno();
            if (indice >= 0) {
                verificar(controller.resolverAtaque(indice, primerEnemigoVivo(enemigos)),
                        "El flujo debe continuar después de cada acción del jugador.");
                accionesJugador++;
            }
        }
        verificar(accionesJugador > 0, "El avance normal debe entregar al menos un turno al jugador.");
        verificar(actor.getVidaActual() < vidaInicial,
                "Un enemigo vivo con velocidad positiva debe actuar y producir daño en el flujo normal.");

        Guerrero derrotado = new Guerrero("Derrotado", 10, 50, 1, 0, 1, "");
        Mago aliadoDerrotado = new Mago("Aliado derrotado", 10, 100, 1, 0, 1, "");
        Partida partidaDerrota = GestorPartida.getInstance().nuevaPartida("Derrota flujo",
                new ArrayList<>(List.of(derrotado, aliadoDerrotado)));
        BatallaController controllerDerrota = new BatallaController();
        for (Enemigo enemigoDerrota : partidaDerrota.getMapaActual().getNivelActual().getEnemigos()) {
            enemigoDerrota.setVelocidad(100);
            enemigoDerrota.setAtaqueBase(100);
        }
        for (int tick = 0; tick < 10
                && controllerDerrota.verificarEstadoCombate() == EstadoCombate.EN_CURSO; tick++) {
            controllerDerrota.avanzarCombate();
        }
        verificar(controllerDerrota.verificarEstadoCombate() == EstadoCombate.DERROTA,
                "Varios turnos enemigos deben actualizar vida y alcanzar la derrota.");
    }

    private static int primerEnemigoVivo(List<Enemigo> enemigos) {
        for (int i = 0; i < enemigos.size(); i++) {
            if (enemigos.get(i).estaVivo()) {
                return i;
            }
        }
        return -1;
    }

    // Regresión del caso manual: el slime inicial no debe morir antes de conseguir turno.
    private static void primerEncuentroPermiteActuarAlEnemigoAntesDeLaVictoria() {
        Guerrero jugador = new Guerrero("Jugador inicial", 200, 50, 40, 0, 20, "");
        Mago aliado = new Mago("Aliado inicial", 100, 100, 1, 0, 1, "");
        Enemigo slime = EnemigoFactory.crearEnemigo(TipoEnemigo.SLIME, 1);
        MotorCombate motor = new MotorCombate(List.of(jugador, aliado), List.of(slime));
        int vidaInicial = jugador.getVidaActual();
        int accionesEnemigas = 0;
        for (int tick = 0; tick < 12 && slime.estaVivo(); tick++) {
            motor.avanzarEnergia();
            if (motor.getActorActual() instanceof Personaje) {
                motor.ataque(motor.getActorActual(), slime);
            } else if (motor.getActorActual() instanceof Enemigo) {
                motor.ejecutarTurnoEnemigo((Enemigo) motor.getActorActual());
                accionesEnemigas++;
            }
        }
        verificar(accionesEnemigas > 0,
                "El slime inicial debe alcanzar su turno antes de que el jugador lo derrote.");
        verificar(jugador.getVidaActual() < vidaInicial,
                "La acción del slime debe reflejar daño recibido en el modelo.");
    }

    private static void habilidadesValidasConsumenRecursoYTurno() {
        verificarHabilidadDeAtaque(new Guerrero("Guerrero", 100, 50, 20, 1, 10, ""), 20,
                "Golpe poderoso");
        verificarHabilidadDeAtaque(new Mago("Mago", 100, 100, 20, 1, 10, ""), 30,
                "Bola de fuego");
        verificarHabilidadDeAtaque(new Arquero("Arquero", 100, 60, 20, 1, 10, ""), 20,
                "Disparo certero");

        Curador curador = new Curador("Curador", 100, 100, 20, 1, 10, "");
        Guerrero aliado = new Guerrero("Aliado", 100, 50, 20, 1, 5, "");
        aliado.setVidaActual(50);
        Enemigo enemigo = EnemigoFactory.crearEnemigo(TipoEnemigo.SLIME, 1);
        MotorCombate motor = new MotorCombate(List.of(curador, aliado), List.of(enemigo));
        curador.acumularEnergia(100);
        motor.avanzarEnergia();
        motor.habilidad(curador, aliado);
        verificar(aliado.getVidaActual() == 80 && curador.getRecursoActual() == 75,
                "La curación debe restaurar 30 y consumir 25 de maná.");
        verificar(curador.getEnergiaActual() == 0 && motor.getActorActual() == null,
                "La curación válida debe consumir un único turno.");
    }

    private static void verificarHabilidadDeAtaque(Personaje personaje, int costo, String nombre) {
        Enemigo enemigo = EnemigoFactory.crearEnemigo(TipoEnemigo.SLIME, 1);
        MotorCombate motor = new MotorCombate(List.of(personaje), List.of(enemigo));
        personaje.acumularEnergia(100);
        motor.avanzarEnergia();
        int vidaAntes = enemigo.getVidaActual();
        int recursoAntes = personaje.getRecursoActual();
        motor.habilidad(personaje, enemigo);
        verificar(enemigo.getVidaActual() < vidaAntes,
                nombre + " debe producir daño sobre el enemigo.");
        verificar(personaje.getRecursoActual() == recursoAntes - costo,
                nombre + " debe consumir su costo.");
        verificar(personaje.getEnergiaActual() == 0 && motor.getActorActual() == null,
                nombre + " debe consumir un único turno.");
    }

    private static void habilidadesInvalidasConservanEstado() {
        Mago mago = new Mago("Mago", 100, 100, 20, 1, 10, "");
        Enemigo enemigo = EnemigoFactory.crearEnemigo(TipoEnemigo.SLIME, 1);
        MotorCombate motorMago = new MotorCombate(List.of(mago), List.of(enemigo));
        mago.acumularEnergia(100);
        motorMago.avanzarEnergia();
        mago.setRecursoActual(0);
        int vidaEnemigo = enemigo.getVidaActual();
        esperarIllegalArgument(() -> motorMago.habilidad(mago, enemigo));
        verificar(mago.getRecursoActual() == 0 && mago.getEnergiaActual() == 100
                && motorMago.getActorActual() == mago && enemigo.getVidaActual() == vidaEnemigo,
                "El maná insuficiente no debe modificar estado ni turno.");

        Guerrero guerrero = new Guerrero("Guerrero", 100, 50, 20, 1, 10, "");
        Guerrero aliado = new Guerrero("Aliado", 100, 50, 20, 1, 5, "");
        MotorCombate motorGuerrero = new MotorCombate(List.of(guerrero, aliado), List.of(enemigo));
        guerrero.acumularEnergia(100);
        motorGuerrero.avanzarEnergia();
        int recursoGuerrero = guerrero.getRecursoActual();
        esperarIllegalArgument(() -> motorGuerrero.habilidad(guerrero, aliado));
        verificar(guerrero.getRecursoActual() == recursoGuerrero && guerrero.getEnergiaActual() == 100
                && motorGuerrero.getActorActual() == guerrero,
                "Un objetivo aliado inválido no debe consumir recurso ni turno.");

        Curador curador = new Curador("Curador", 100, 100, 20, 1, 10, "");
        MotorCombate motorCurador = new MotorCombate(List.of(curador), List.of(enemigo));
        curador.acumularEnergia(100);
        motorCurador.avanzarEnergia();
        int recursoCurador = curador.getRecursoActual();
        esperarIllegalArgument(() -> motorCurador.habilidad(curador, enemigo));
        verificar(curador.getRecursoActual() == recursoCurador && curador.getEnergiaActual() == 100
                && motorCurador.getActorActual() == curador,
                "El curador no debe sanar enemigos ni consumir su turno.");
    }

    private static void persistenciaConservaClasesYMigraTiposAntiguos() {
        List<Personaje> party = new ArrayList<>(List.of(
                new Guerrero("Guerrero", 100, 50, 20, 1, 10, ""),
                new Mago("Mago", 100, 100, 20, 1, 10, ""),
                new Arquero("Arquero", 100, 60, 20, 1, 10, ""),
                new Curador("Curador", 100, 100, 20, 1, 10, "")));
        Enemigo enemigo = EnemigoFactory.crearEnemigo(TipoEnemigo.SLIME, 1);
        Partida partida = new Partida("Clases nuevas", party,
                new Mapa(1, List.of(new Nivel(1, List.of(enemigo)))));
        Partida restaurada = PartidaMapper.desdeSnapshot(PartidaMapper.aSnapshot(partida));
        verificar(restaurada.getParty().get(0).getTipo() == TipoPersonaje.GUERRERO
                && restaurada.getParty().get(1).getTipo() == TipoPersonaje.MAGO
                && restaurada.getParty().get(2).getTipo() == TipoPersonaje.ARQUERO
                && restaurada.getParty().get(3).getTipo() == TipoPersonaje.CURADOR,
                "La persistencia debe conservar las cuatro clases nuevas.");

        List<Personaje> antiguos = new ArrayList<>(List.of(
                new Hechicero("Legado mago", 100, 100, 20, 1, 10, ""),
                new model.entidades.personajes.Asesino("Legado arquero", 100, 60, 20, 1, 10, "")));
        Partida partidaAntigua = new Partida("Legado", antiguos,
                new Mapa(1, List.of(new Nivel(1, List.of(EnemigoFactory.crearEnemigo(TipoEnemigo.SLIME, 1))))));
        Partida migrada = PartidaMapper.desdeSnapshot(PartidaMapper.aSnapshot(partidaAntigua));
        verificar(migrada.getParty().get(0).getTipo() == TipoPersonaje.MAGO
                && migrada.getParty().get(1).getTipo() == TipoPersonaje.ARQUERO,
                "HECHICERO y ASESINO deben migrar explícitamente a MAGO y ARQUERO.");
    }

    private static void comportamientosEnemigosSonDeterministas() {
        Guerrero primero = new Guerrero("Primero", 100, 50, 10, 1, 1, "");
        Mago segundo = new Mago("Segundo", 100, 100, 50, 1, 1, "");
        segundo.setVidaActual(40);
        List<Personaje> objetivos = List.of(primero, segundo);

        verificar(new Minion("Minion", 100, 0, 10, 0, 1, 1, 1).seleccionarObjetivo(objetivos) == primero,
                "El minion debe elegir el primer objetivo vivo.");
        verificar(new Estandar("Estandar", 100, 0, 10, 0, 1, 1, 1).seleccionarObjetivo(objetivos) == segundo,
                "El estandar debe elegir al personaje más herido.");
        verificar(new Elite("Elite", 100, 0, 10, 0, 1, 1, 1).seleccionarObjetivo(objetivos) == segundo,
                "El elite debe elegir al personaje con mayor ataque.");
        verificar(new Jefe("Jefe", 100, 0, 10, 0, 1, 1, 1).seleccionarObjetivo(objetivos) == segundo,
                "El jefe debe elegir al personaje más herido.");

        primero.setVidaActual(0);
        verificar(new Minion("Minion vivo", 100, 0, 10, 0, 1, 1, 1).seleccionarObjetivo(objetivos) == segundo,
                "Los enemigos deben ignorar objetivos derrotados.");
        esperarIllegalState(() -> new Minion("Sin objetivos", 100, 0, 10, 0, 1, 1, 1)
                .seleccionarObjetivo(List.of()));
    }

    private static void enemigosConsumenUnTurnoYRespetanElFinDelCombate() {
        Guerrero objetivo = new Guerrero("Objetivo", 100, 50, 10, 0, 1, "");
        Minion minion = new Minion("Minion", 100, 0, 20, 0, 1, 1, 1);
        MotorCombate motor = new MotorCombate(List.of(objetivo), List.of(minion));
        minion.acumularEnergia(100);
        motor.avanzarEnergia();
        int vidaAntes = objetivo.getVidaActual();
        motor.ejecutarTurnoEnemigo(minion);
        verificar(objetivo.getVidaActual() < vidaAntes,
                "El enemigo debe ejecutar una acción sobre un objetivo vivo.");
        verificar(minion.getEnergiaActual() == 0 && motor.getActorActual() == null,
                "La acción enemiga debe consumir un único turno.");

        objetivo.setVidaActual(0);
        motor.avanzarEnergia();
        verificar(motor.getActorActual() == null,
                "El motor no debe seleccionar actores cuando terminó el combate.");
        esperarIllegalState(() -> motor.ejecutarTurnoEnemigo(minion));
        verificar(minion.getEnergiaActual() == 0 && objetivo.getVidaActual() == 0,
                "Un enemigo no debe actuar después de finalizar el combate.");
    }

    private static void furiaDelJefeSeActivaUnaSolaVez() {
        Jefe jefe = new Jefe("Jefe", 100, 0, 10, 0, 5, 1, 1);
        Guerrero objetivo = nuevoGuerrero();
        jefe.setVidaActual(49);
        jefe.ejecutarAccion(List.of(objetivo));
        verificar(jefe.getVelocidad() == 10,
                "La furia debe activarse al llegar a la mitad o menos de la vida máxima.");
        jefe.setVidaActual(100);
        jefe.setVidaActual(49);
        jefe.ejecutarAccion(List.of(objetivo));
        verificar(jefe.getVelocidad() == 10,
                "La furia no debe volver a duplicarse después de recuperar y perder vida.");
    }

    private static void controllerValidaYExponeElInventario() {
        Guerrero actor = nuevoGuerrero();
        Hechicero aliado = new Hechicero("Aliado", 100, 100, 20, 5, 5, "");
        aliado.setVidaActual(20);
        actor.setVidaActual(50);
        actor.acumularEnergia(100);
        Partida partida = GestorPartida.getInstance().nuevaPartida("Prueba inventario",
                new ArrayList<>(List.of(actor, aliado)));
        PocionVida pocion = new PocionVida("Vida rara", "Cura", 25, TipoRareza.RARO);
        partida.getInventarioGlobal().agregarItem(new ItemEquipable("Espada", "Arma", TipoObjeto.ARMA, 5, 0));
        partida.getInventarioGlobal().agregarItem(pocion);
        BatallaController controller = new BatallaController();

        verificar(controller.getMotivoNoPuedeUsarItem(0, 1) != null, "El controller debe rechazar el uso sin turno.");
        controller.avanzarCombate();
        List<ItemInventarioDTO> items = controller.getItemsInventario();
        verificar(items.size() == 2 && !items.get(0).isConsumible() && items.get(1).isConsumible(),
                "La vista debe poder distinguir equipables de consumibles.");
        verificar(items.get(1).getIndice() == 1 && items.get(1).getRareza() == TipoRareza.RARO,
                "El DTO debe conservar posición y rareza.");
        esperarRechazo(() -> controller.resolverUsoItem(0, 0));
        esperarRechazo(() -> controller.resolverUsoItem(0, 8));
        esperarRechazo(() -> controller.resolverUsoItem(8, 1));
        esperarRechazo(() -> controller.resolverUsoItem(1, 1));
        verificar(controller.getMotivoNoPuedeUsarItem(1, 1) != null, "Otro aliado no puede usar el objeto en este turno.");
        verificar(controller.getMotivoNoPuedeUsarItem(0, 1) == null, "El uso válido debe estar disponible.");
        controller.resolverUsoItem(0, 1);
        verificar(actor.getVidaActual() == 75 && controller.getCantidadItemsInventario() == 1,
                "El controller debe aplicar la poción y conservar el equipable.");
        verificar(controller.getIndicePersonajeEnTurno() == -1 && actor.getEnergiaActual() == 0,
                "La vista debe observar que terminó el turno.");
        verificar(aliado.getVidaActual() == 20, "El controller no debe aplicar la poción a otro aliado.");
        esperarRechazo(() -> controller.resolverUsoItem(0, 0));

        actor.acumularEnergia(100);
        controller.avanzarCombate();
        partida.getInventarioGlobal().agregarItem(new PocionVida("Otra", "", 25));
        for (Enemigo enemigo : partida.getMapaActual().getNivelActual().getEnemigos()) {
            enemigo.setVidaActual(0);
        }
        esperarRechazo(() -> controller.resolverUsoItem(0, 1));
        verificar(partida.getInventarioGlobal().getItems().size() == 2,
                "Un combate terminado no debe permitir consumir objetos.");
    }

    private static void factoryConservaRarezaYTipo() {
        for (TipoRareza rareza : TipoRareza.values()) {
            ItemConsumible vida = ItemsConsumiblesFactory.CrearItemConsumible(rareza, TipoItemConsumible.POCION_VIDA);
            ItemConsumible mana = ItemsConsumiblesFactory.CrearItemConsumible(rareza, TipoItemConsumible.POCION_MANA);
            verificar(vida instanceof PocionVida && vida.getNombre().contains("vida"), "La poción de vida debe identificarse bien.");
            verificar(mana instanceof PocionMana && mana.getNombre().contains("maná"), "La poción de maná debe identificarse bien.");
            verificar(vida.getRareza() == rareza && mana.getRareza() == rareza, "La rareza debe llegar al inventario.");
        }
    }

    private static void equipamientoValidaAsignacionYBonificaciones() {
        Guerrero guerrero = nuevoGuerrero();
        Mago otroPersonaje = new Mago("Otro", 100, 100, 20, 5, 10, "");
        Partida partida = new Partida("Equipamiento", new ArrayList<>(List.of(guerrero, otroPersonaje)),
                new Mapa(1, List.of(new Nivel(1, List.of(EnemigoFactory.crearEnemigo(TipoEnemigo.SLIME, 1))))));
        ItemEquipable espada = new ItemEquipable("Espada", "", TipoObjeto.ARMA, 7, 0);
        ItemEquipable reemplazo = new ItemEquipable("Hacha", "", TipoObjeto.ARMA, 11, 0);
        ItemEquipable armadura = new ItemEquipable("Armadura", "", TipoObjeto.ARMADURA, 0, 6);
        partida.getInventarioGlobal().agregarItem(espada);
        partida.getInventarioGlobal().agregarItem(reemplazo);
        partida.getInventarioGlobal().agregarItem(armadura);

        esperarIllegalArgument(() -> guerrero.equiparItem(TipoObjeto.ARMADURA, espada));
        partida.equiparItem(guerrero, espada);
        verificar(guerrero.getAtaqueBase() == 27 && !partida.getInventarioGlobal().getItems().contains(espada),
                "Equipar debe aplicar una sola vez el bonus y retirar el objeto disponible.");
        esperarIllegalArgument(() -> partida.equiparItem(guerrero, espada));
        esperarIllegalArgument(() -> partida.equiparItem(otroPersonaje, espada));
        partida.equiparItem(guerrero, reemplazo);
        verificar(guerrero.getAtaqueBase() == 31 && partida.getInventarioGlobal().getItems().contains(espada),
                "Reemplazar debe retirar el bonus anterior y devolver el objeto anterior.");
        partida.desequiparItem(guerrero, reemplazo);
        verificar(guerrero.getAtaqueBase() == 20 && partida.getInventarioGlobal().getItems().contains(reemplazo),
                "Desequipar debe retirar exactamente una vez el bonus y devolver el objeto.");
        esperarIllegalArgument(() -> partida.desequiparItem(guerrero, reemplazo));
        esperarUnsupported(() -> partida.getInventarioGlobal().getItems().clear());
        esperarIllegalArgument(() -> partida.equiparItem(new Guerrero("Externo", 100, 50, 20, 5, 10, ""), armadura));

        partida.equiparItem(guerrero, armadura);
        int espacios = partida.getInventarioGlobal().getCapacidadMax()
                - partida.getInventarioGlobal().getItems().size();
        for (int i = 0; i < espacios; i++) {
            verificar(partida.getInventarioGlobal().agregarItem(new ItemEquipable("Relleno" + i, "",
                    TipoObjeto.ARMA, 0, 0)), "Debe poder llenar el inventario de prueba.");
        }
        int defensa = guerrero.getDefensaBase();
        esperarIllegalState(() -> partida.desequiparItem(guerrero, armadura));
        verificar(guerrero.getDefensaBase() == defensa && guerrero.getEquipamiento().get(TipoObjeto.ARMADURA) == armadura,
                "Desequipar con inventario lleno no debe modificar estado ni estadísticas.");
    }

    private static void persistenciaRealConservaEquipamientoSinDuplicarBonos() {
        Guerrero guerrero = nuevoGuerrero();
        Mago mago = new Mago("Mago", 100, 100, 20, 5, 10, "");
        Partida partida = new Partida("Persistencia equipamiento", new ArrayList<>(List.of(guerrero, mago)),
                new Mapa(1, List.of(new Nivel(1, List.of(EnemigoFactory.crearEnemigo(TipoEnemigo.SLIME, 1))))));
        ItemEquipable espada = new ItemEquipable("Espada", "", TipoObjeto.ARMA, 7, 0);
        partida.getInventarioGlobal().agregarItem(espada);
        partida.equiparItem(guerrero, espada);
        guerrero.ganarExperiencia(100);
        int ataqueGuardado = guerrero.getAtaqueBase();
        Path archivo = null;
        try {
            archivo = Files.createTempFile("tp-poo-equipamiento-", ".txt");
            verificar(GestorArchivos.guardarEnArchivo(PartidaMapper.aSnapshot(partida), archivo),
                    "Debe guardar el snapshot en un archivo temporal real.");
            Partida restaurada = PartidaMapper.desdeSnapshot(GestorArchivos.cargarArchivo(archivo));
            Personaje restaurado = restaurada.getParty().get(0);
            verificar(restaurado.getEquipamiento().containsKey(TipoObjeto.ARMA)
                    && restaurado.getAtaqueBase() == ataqueGuardado
                    && restaurada.getInventarioGlobal().getItems().isEmpty(),
                    "La carga debe restaurar slot, objeto y estadísticas sin duplicar bonus.");
            restaurada.desequiparItem(restaurado, restaurado.getEquipamiento().get(TipoObjeto.ARMA));
            verificar(restaurado.getAtaqueBase() == ataqueGuardado - 7
                    && restaurada.getInventarioGlobal().getItems().size() == 1,
                    "Después de cargar, desequipar debe retirar una sola vez la bonificación.");
            Partida repetida = PartidaMapper.desdeSnapshot(PartidaMapper.aSnapshot(restaurada));
            verificar(repetida.getParty().get(0).getAtaqueBase() == ataqueGuardado - 7,
                    "Guardar y cargar después de desequipar no debe acumular bonus.");
        } catch (Exception e) {
            throw new AssertionError("Falló la persistencia real temporal: " + e.getMessage(), e);
        } finally {
            if (archivo != null) {
                try {
                    Files.deleteIfExists(archivo);
                } catch (Exception e) {
                    throw new AssertionError("No se pudo limpiar el archivo temporal de prueba.", e);
                }
            }
        }
    }

    private static void controllerGestionaEquipamientoComoAccion() {
        Guerrero actor = nuevoGuerrero();
        Mago aliado = new Mago("Aliado", 100, 100, 20, 5, 5, "");
        actor.acumularEnergia(100);
        Partida partida = GestorPartida.getInstance().nuevaPartida("Controller equipamiento",
                new ArrayList<>(List.of(actor, aliado)));
        ItemEquipable espada = new ItemEquipable("Espada", "", TipoObjeto.ARMA, 5, 0);
        partida.getInventarioGlobal().agregarItem(espada);
        BatallaController controller = new BatallaController();
        controller.avanzarCombate();
        verificar(controller.getMotivoNoPuedeEquiparItem(0, 0) == null,
                "El controller debe habilitar equipar el objeto disponible en el turno.");
        controller.resolverEquiparItem(0, 0);
        verificar(actor.getEquipamiento().get(TipoObjeto.ARMA) == espada && actor.getEnergiaActual() == 0,
                "Equipar desde el controller debe asignar el objeto y consumir un turno.");
        actor.acumularEnergia(100);
        controller.avanzarCombate();
        controller.resolverDesequiparItem(0, TipoObjeto.ARMA);
        verificar(actor.getEquipamiento().isEmpty() && partida.getInventarioGlobal().getItems().contains(espada),
                "Desequipar desde el controller debe devolver el objeto al inventario.");
    }

    private static void coleccionesDePartidaMapaYNivelEstanProtegidas() {
        Enemigo enemigo = EnemigoFactory.crearEnemigo(TipoEnemigo.SLIME, 1);
        ArrayList<Enemigo> enemigosOriginales = new ArrayList<>(List.of(enemigo));
        Nivel nivel = new Nivel(1, enemigosOriginales);
        enemigosOriginales.clear();
        verificar(nivel.getEnemigos().size() == 1, "Nivel debe copiar la lista recibida.");
        esperarUnsupported(() -> nivel.getEnemigos().clear());

        ArrayList<Nivel> nivelesOriginales = new ArrayList<>(List.of(nivel));
        Mapa mapa = new Mapa(1, nivelesOriginales);
        nivelesOriginales.clear();
        verificar(mapa.getNiveles().size() == 1, "Mapa debe copiar la lista recibida.");
        esperarUnsupported(() -> mapa.getNiveles().clear());

        Partida partida = new Partida("Colecciones", List.of(nuevoGuerrero(),
                new Mago("Mago", 100, 100, 20, 1, 1, "")), mapa);
        esperarUnsupported(() -> partida.getMapas().clear());
    }

    private static void recompensasSeOtorganUnaVezYBotinLlenoNoRompeLaPartida() {
        Guerrero actor = nuevoGuerrero();
        Mago aliado = new Mago("Aliado", 100, 100, 20, 1, 1, "");
        actor.acumularEnergia(100);
        Partida partida = GestorPartida.getInstance().nuevaPartida("Recompensas",
                new ArrayList<>(List.of(actor, aliado)));
        while (partida.getInventarioGlobal().getItems().size() < partida.getInventarioGlobal().getCapacidadMax()) {
            partida.getInventarioGlobal().agregarItem(new ItemEquipable("Relleno", "", TipoObjeto.ARMA, 0, 0));
        }
        int experienciaAntes = actor.getExperienciaActual();
        int objetosAntes = partida.getInventarioGlobal().getItems().size();
        for (Enemigo enemigo : partida.getMapaActual().getNivelActual().getEnemigos()) {
            enemigo.setVidaActual(0);
        }
        BatallaController controller = new BatallaController();
        controller.continuarAlSiguienteNivel();
        int experienciaDespues = actor.getExperienciaActual();
        verificar(experienciaDespues != experienciaAntes,
                "Una victoria debe otorgar experiencia aunque el inventario esté lleno.");
        verificar(partida.getInventarioGlobal().getItems().size() == objetosAntes,
                "El botín sin capacidad debe descartarse sin perder objetos previos.");
        int experienciaEstable = actor.getExperienciaActual();
        esperarIllegalState(controller::continuarAlSiguienteNivel);
        verificar(actor.getExperienciaActual() == experienciaEstable
                && partida.getInventarioGlobal().getItems().size() == objetosAntes,
                "Repetir continuar no debe volver a otorgar recompensas.");
    }

    private static void derrotaNoOtorgaRecompensas() {
        Guerrero actor = nuevoGuerrero();
        Mago aliado = new Mago("Aliado", 100, 100, 20, 1, 1, "");
        Partida partida = GestorPartida.getInstance().nuevaPartida("Derrota",
                new ArrayList<>(List.of(actor, aliado)));
        actor.setVidaActual(0);
        aliado.setVidaActual(0);
        int experiencia = actor.getExperienciaActual();
        int objetos = partida.getInventarioGlobal().getItems().size();
        BatallaController controller = new BatallaController();
        verificar(controller.verificarEstadoCombate() == EstadoCombate.DERROTA,
                "La party derrotada debe informar derrota.");
        esperarIllegalState(controller::continuarAlSiguienteNivel);
        verificar(actor.getExperienciaActual() == experiencia
                && partida.getInventarioGlobal().getItems().size() == objetos,
                "La derrota no debe otorgar experiencia ni botín.");
    }

    private static void progresionPermiteSubirVariosNiveles() {
        Guerrero guerrero = nuevoGuerrero();
        guerrero.ganarExperiencia(250);
        verificar(guerrero.getNivel() == 3 && guerrero.getExperienciaActual() == 0,
                "La experiencia suficiente debe permitir subir más de un nivel.");
    }

    private static void transicionaEntreMapasYConservaElGuardado() {
        Guerrero actor = nuevoGuerrero();
        Mago aliado = new Mago("Aliado", 100, 100, 20, 1, 1, "");
        Partida partida = GestorPartida.getInstance().nuevaPartida("Mapas",
                new ArrayList<>(List.of(actor, aliado)));
        BatallaController controller = new BatallaController();
        int mapaInicial = partida.getMapaActual().getNumeroMapa();
        int intentos = 0;
        while (partida.getMapaActual().getNumeroMapa() == mapaInicial && intentos++ < 12) {
            for (Enemigo enemigo : partida.getMapaActual().getNivelActual().getEnemigos()) {
                enemigo.setVidaActual(0);
            }
            controller.continuarAlSiguienteNivel();
            controller = new BatallaController();
        }
        verificar(partida.getMapaActual().getNumeroMapa() > mapaInicial,
                "Completar el último nivel debe crear y seleccionar el mapa siguiente.");
        verificar(partida.getMapaActual().getNivelActual() != null,
                "El mapa siguiente debe tener un nivel actual.");
        Path archivo = null;
        try {
            archivo = Files.createTempFile("tp-poo-mapa-", ".txt");
            verificar(GestorArchivos.guardarEnArchivo(PartidaMapper.aSnapshot(partida), archivo),
                    "Debe guardar la progresión de mapa en un archivo temporal.");
            Partida restaurada = PartidaMapper.desdeSnapshot(GestorArchivos.cargarArchivo(archivo));
            verificar(restaurada.getMapaActual().getNumeroMapa() == partida.getMapaActual().getNumeroMapa()
                    && restaurada.getMapaActual().getNivelActual() != null,
                    "La carga debe conservar el mapa y nivel pendientes después de avanzar.");
        } catch (Exception e) {
            throw new AssertionError("Falló el guardado de progresión de mapa.", e);
        } finally {
            if (archivo != null) {
                try {
                    Files.deleteIfExists(archivo);
                } catch (Exception e) {
                    throw new AssertionError("No se pudo limpiar el archivo temporal de mapas.", e);
                }
            }
        }
    }

    private static void resultadoDeVictoriaReflejaBotinReal() {
        EscenarioJefe espacio = crearEscenarioJefe(0);
        ResultadoRecompensaDTO resultadoEspacio = espacio.controller.prepararResultadoVictoria();
        verificar(resultadoEspacio.getObjetosObtenidos().size() == 1
                && resultadoEspacio.getObjetosIncorporados().size() == 1
                && resultadoEspacio.getObjetosDescartados().isEmpty(),
                "El resultado debe reflejar el botín real incorporado con espacio disponible.");
        Path archivo = null;
        try {
            archivo = Files.createTempFile("tp-poo-resultado-", ".txt");
            verificar(GestorArchivos.guardarEnArchivo(PartidaMapper.aSnapshot(espacio.partida), archivo),
                    "Guardar desde el resultado debe persistir las recompensas ya aplicadas.");
            Partida restaurada = PartidaMapper.desdeSnapshot(GestorArchivos.cargarArchivo(archivo));
            verificar(restaurada.getInventarioGlobal().getItems().size()
                    == espacio.partida.getInventarioGlobal().getItems().size(),
                    "Cargar desde el resultado no debe perder ni duplicar el botín incorporado.");
        } catch (Exception e) {
            throw new AssertionError("Falló guardar/cargar desde el resultado.", e);
        } finally {
            if (archivo != null) {
                try {
                    Files.deleteIfExists(archivo);
                } catch (Exception e) {
                    throw new AssertionError("No se pudo limpiar el archivo temporal del resultado.", e);
                }
            }
        }

        EscenarioJefe parcial = crearEscenarioJefe(parcialObjetos());
        ResultadoRecompensaDTO resultadoParcial = parcial.controller.prepararResultadoVictoria();
        verificar(resultadoParcial.getObjetosObtenidos().size() == 1
                && resultadoParcial.getObjetosIncorporados().size() == 1
                && resultadoParcial.getObjetosDescartados().isEmpty(),
                "El resultado debe reflejar el botín real incorporado con inventario parcialmente lleno.");

        EscenarioJefe lleno = crearEscenarioJefe(20);
        ResultadoRecompensaDTO resultadoLleno = lleno.controller.prepararResultadoVictoria();
        verificar(resultadoLleno.getObjetosObtenidos().size() == 1
                && resultadoLleno.getObjetosIncorporados().isEmpty()
                && resultadoLleno.getObjetosDescartados().size() == 1
                && lleno.partida.getInventarioGlobal().getItems().size() == 20,
                "El resultado debe informar el botín descartado con inventario lleno.");
    }

    private static int parcialObjetos() {
        return 19;
    }

    private static EscenarioJefe crearEscenarioJefe(int objetosIniciales) {
        Guerrero actor = nuevoGuerrero();
        Mago aliado = new Mago("Aliado", 100, 100, 20, 1, 1, "");
        Partida partida = GestorPartida.getInstance().nuevaPartida("Resultado jefe",
                new ArrayList<>(List.of(actor, aliado)));
        BatallaController controller = new BatallaController();
        while (partida.getMapaActual().getNivelActual().getEnemigos().get(0).getTipo() != TipoEnemigo.DRAGON) {
            for (Enemigo enemigo : partida.getMapaActual().getNivelActual().getEnemigos()) {
                enemigo.setVidaActual(0);
            }
            controller.continuarAlSiguienteNivel();
            controller = new BatallaController();
        }
        while (partida.getInventarioGlobal().getItems().size() < objetosIniciales) {
            partida.getInventarioGlobal().agregarItem(new ItemEquipable("Relleno", "", TipoObjeto.ARMA, 0, 0));
        }
        for (Enemigo enemigo : partida.getMapaActual().getNivelActual().getEnemigos()) {
            enemigo.setVidaActual(0);
        }
        return new EscenarioJefe(partida, controller);
    }

    private static void rechazarSinConsumir(Escenario escenario, Personaje actor, ItemConsumible item) {
        int cantidad = escenario.inventario.getItems().size();
        int vida = actor.getVidaActual();
        int recurso = actor.getRecursoActual();
        esperarRechazo(() -> escenario.motor.usarItem(actor, item, escenario.inventario));
        verificar(escenario.inventario.getItems().size() == cantidad, "El uso inválido no debe retirar objetos.");
        verificar(escenario.motor.getActorActual() == escenario.actor && escenario.actor.getEnergiaActual() == 100,
                "El uso inválido debe conservar la acción del actor.");
        verificar(actor.getVidaActual() == vida && actor.getRecursoActual() == recurso,
                "El uso inválido no debe cambiar al personaje.");
    }

    private static Guerrero nuevoGuerrero() {
        return new Guerrero("Actor", 100, 50, 20, 5, 10, "");
    }

    private static void esperarRechazo(Runnable accion) {
        try {
            accion.run();
        } catch (IllegalArgumentException | IllegalStateException esperado) {
            verificaciones++;
            return;
        }
        throw new AssertionError("Se esperaba rechazar un uso inválido.");
    }

    private static void esperarIllegalArgument(Runnable accion) {
        try {
            accion.run();
        } catch (IllegalArgumentException esperado) {
            verificaciones++;
            return;
        }
        throw new AssertionError("Se esperaba IllegalArgumentException.");
    }

    private static void esperarIllegalState(Runnable accion) {
        try {
            accion.run();
        } catch (IllegalStateException esperado) {
            verificaciones++;
            return;
        }
        throw new AssertionError("Se esperaba IllegalStateException.");
    }

    private static void esperarUnsupported(Runnable accion) {
        try {
            accion.run();
        } catch (UnsupportedOperationException esperado) {
            verificaciones++;
            return;
        }
        throw new AssertionError("Se esperaba UnsupportedOperationException.");
    }

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
        verificaciones++;
    }

    private static final class Escenario {
        private final Personaje actor;
        private final Hechicero aliado = new Hechicero("Aliado", 100, 100, 20, 5, 5, "");
        private final Enemigo enemigo = EnemigoFactory.crearEnemigo(TipoEnemigo.SLIME, 1);
        private final Inventario inventario = new Inventario();
        private final MotorCombate motor;

        private Escenario() {
            this(nuevoGuerrero());
        }

        private Escenario(Personaje actor) {
            this.actor = actor;
            motor = new MotorCombate(List.of(actor, aliado), List.of(enemigo));
            actor.acumularEnergia(100);
            motor.avanzarEnergia();
        }
    }

    private static final class EscenarioJefe {
        private final Partida partida;
        private final BatallaController controller;

        private EscenarioJefe(Partida partida, BatallaController controller) {
            this.partida = partida;
            this.controller = controller;
        }
    }
}
