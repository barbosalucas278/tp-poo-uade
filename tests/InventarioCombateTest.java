import java.util.ArrayList;
import java.util.List;

import controller.BatallaController;
import model.Combate.EstadoCombate;
import model.Combate.MotorCombate;
import model.DTOs.ItemInventarioDTO;
import model.entidades.Entidad;
import model.entidades.enemigos.DropStats;
import model.entidades.enemigos.Enemigo;
import model.entidades.enemigos.EnemigoFactory;
import model.entidades.enemigos.Elite;
import model.entidades.enemigos.Estandar;
import model.entidades.enemigos.Jefe;
import model.entidades.enemigos.Minion;
import model.entidades.enemigos.TipoEnemigo;
import model.entidades.personajes.Arquero;
import model.entidades.personajes.Asesino;
import model.entidades.personajes.Curador;
import model.entidades.personajes.Guerrero;
import model.entidades.personajes.Invocador;
import model.entidades.personajes.Mago;
import model.entidades.personajes.Personaje;
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
import model.partidas.Nivel;
import model.partidas.Partida;

/** Pruebas sin dependencias: compilar junto a src y ejecutar InventarioCombateTest. */
public class InventarioCombateTest {
    private static int verificaciones;

    public static void main(String[] args) {
        curarAlActorConsumeSuAccion();
        pocionSoloAfectaAlActor();
        restaurarManaRespetaMaximo();
        usosInvalidosConservanObjetoYTurno();
        soloActuaElPersonajeEnTurno();
        accionesOfensivasRechazanAliadosComoObjetivos();
        noSePuedeReutilizarUnObjetoConsumido();
        ataqueSigueFinalizandoElTurno();
        defensaSoloSePermiteAlActorActual();
        motorControlaCostoDeHabilidadesDeTodasLasClases();
        motorControlaCostoDeHabilidadesDeEnemigos();
        motorResuelveTurnoEnemigoYDetectaDerrota();
        motorRechazaAccionesConElCombateTerminado();
        motorNoAvanzaEnergiaTrasLaDerrota();
        regeneracionDeRecursoEsResponsabilidadDelPersonaje();
        controllerValidaYExponeElInventario();
        controllerAplicaRecompensasUnaSolaVez();
        factoryConservaRarezaYTipo();
        System.out.println("OK: " + verificaciones + " verificaciones de inventario y acciones de combate.");
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
        Escenario escenario = new Escenario(new Mago("Actor", 100, 100, 20, 5, 10, ""));
        escenario.actor.setRecursoActual(90);
        PocionMana pocion = new PocionMana("Maná", "Restaura", 50);
        escenario.inventario.agregarItem(pocion);

        escenario.motor.usarItem(escenario.actor, pocion, escenario.inventario);

        verificar(escenario.actor.getRecursoActual() == 100, "El maná del actor debe respetar el máximo.");
        verificar(escenario.actor.getEnergiaActual() == 0, "El mago debe consumir su propio turno.");
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

        Mago externo = new Mago("Externo", 100, 100, 10, 1, 1, "");
        externo.setVidaActual(50);
        rechazarSinConsumir(escenario, externo, vida);
        escenario.actor.setVidaActual(50);
        rechazarSinConsumir(escenario, escenario.actor, new PocionVida("Ajena", "", 25));

        Escenario escenarioMago = new Escenario(new Mago("Actor", 100, 100, 20, 5, 10, ""));
        PocionMana manaCompleto = new PocionMana("Maná", "Restaura", 25);
        escenarioMago.inventario.agregarItem(manaCompleto);
        rechazarSinConsumir(escenarioMago, escenarioMago.actor, manaCompleto);
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

    private static void accionesOfensivasRechazanAliadosComoObjetivos() {
        Escenario escenario = new Escenario();
        int vidaAliado = escenario.aliado.getVidaActual();
        esperarRechazo(() -> escenario.motor.ataque(escenario.actor, escenario.aliado));
        verificar(escenario.aliado.getVidaActual() == vidaAliado,
                "Un ataque básico no debe dañar a un aliado.");
        verificar(escenario.motor.getActorActual() == escenario.actor,
                "El ataque a un aliado debe conservar el turno.");

        Escenario escenarioMago = new Escenario(new Mago("Mago", 100, 100, 20, 5, 10, ""));
        int manaMago = escenarioMago.actor.getRecursoActual();
        int vidaAliadoMago = escenarioMago.aliado.getVidaActual();
        esperarRechazo(() -> escenarioMago.motor.usarHabilidadEspecial(
                escenarioMago.actor, escenarioMago.aliado));
        verificar(escenarioMago.actor.getRecursoActual() == manaMago,
                "Una habilidad contra un aliado no debe gastar recursos.");
        verificar(escenarioMago.aliado.getVidaActual() == vidaAliadoMago,
                "Una habilidad ofensiva no debe dañar a un aliado.");

        Personaje personaje = nuevoGuerrero();
        Enemigo enemigo = EnemigoFactory.crearEnemigo(TipoEnemigo.SLIME, 1);
        Enemigo aliadoEnemigo = EnemigoFactory.crearEnemigo(TipoEnemigo.SLIME, 1);
        MotorCombate motorEnemigo = new MotorCombate(List.of(personaje), List.of(enemigo, aliadoEnemigo));
        enemigo.acumularEnergia(100);
        motorEnemigo.avanzarEnergia();
        int vidaAliadoEnemigo = aliadoEnemigo.getVidaActual();
        esperarRechazo(() -> motorEnemigo.ataque(enemigo, aliadoEnemigo));
        verificar(aliadoEnemigo.getVidaActual() == vidaAliadoEnemigo,
                "Un enemigo tampoco debe poder atacar a otro enemigo.");
        verificar(motorEnemigo.getActorActual() == enemigo,
                "El ataque de un enemigo a un aliado debe conservar el turno.");
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

    private static void defensaSoloSePermiteAlActorActual() {
        Escenario escenario = new Escenario();
        esperarRechazo(() -> escenario.motor.defensa(escenario.aliado));
        esperarRechazo(() -> escenario.motor.defensa(null));
        verificar(escenario.motor.getActorActual() == escenario.actor,
                "Una defensa fuera de turno no debe finalizar la acción actual.");
        verificar(escenario.actor.getEnergiaActual() == 100,
                "Una defensa fuera de turno no debe reiniciar la energía del actor.");
        escenario.aliado.recibirDanio(20);
        verificar(escenario.aliado.getVidaActual() == 85,
                "Una defensa fuera de turno no debe aumentar la defensa del aliado.");

        escenario.motor.defensa(escenario.actor);
        verificar(escenario.motor.getActorActual() == null,
                "La defensa del actor actual debe finalizar su turno.");
        verificar(escenario.actor.getEnergiaActual() == 0,
                "La defensa válida debe reiniciar la energía del actor.");
        escenario.actor.recibirDanio(20);
        verificar(escenario.actor.getVidaActual() == 95, "El motor debe aplicar la defensa antes de terminar el turno.");

        escenario.actor.acumularEnergia(100);
        escenario.motor.avanzarEnergia();
        esperarRechazo(() -> escenario.motor.ataque(escenario.actor, null));
        escenario.actor.recibirDanio(20);
        verificar(escenario.actor.getVidaActual() == 90, "Un ataque inválido debe conservar la defensa.");
        escenario.motor.ataque(escenario.actor, escenario.enemigo);
        escenario.actor.recibirDanio(20);
        verificar(escenario.actor.getVidaActual() == 75, "El ataque válido debe quitar la defensa.");
    }

    private static void motorControlaCostoDeHabilidadesDeTodasLasClases() {
        Personaje[] personajes = {
                new Guerrero("Guerrero", 100, 100, 20, 5, 10, ""),
                new Arquero("Arquero", 100, 100, 20, 5, 10, ""),
                new Asesino("Asesino", 100, 100, 20, 5, 10, ""),
                new Curador("Curador", 100, 100, 20, 5, 10, ""),
                new Invocador("Invocador", 100, 100, 20, 5, 10, ""),
                new Mago("Mago", 100, 100, 20, 5, 10, "")
        };

        for (Personaje personaje : personajes) {
            Enemigo enemigo = EnemigoFactory.crearEnemigo(TipoEnemigo.SLIME, 1);
            MotorCombate motor = new MotorCombate(List.of(personaje), List.of(enemigo));
            personaje.setRecursoActual(personaje.getCostoHabilidad() - 1);
            personaje.setVidaActual(50);
            personaje.acumularEnergia(100);
            motor.avanzarEnergia();

            int recursoAnterior = personaje.getRecursoActual();
            int vidaAnterior = personaje.getVidaActual();
            int vidaEnemigo = enemigo.getVidaActual();
            Entidad objetivo = personaje.esHabilidadDeApoyo() ? null : enemigo;
            esperarRechazo(() -> motor.usarHabilidadEspecial(personaje, objetivo));
            verificar(personaje.getRecursoActual() == recursoAnterior,
                    "La habilidad sin recurso suficiente no debe descontar recursos.");
            verificar(personaje.getVidaActual() == vidaAnterior,
                    "La habilidad sin recurso suficiente no debe aplicar sus efectos.");
            verificar(enemigo.getVidaActual() == vidaEnemigo,
                    "La habilidad sin recurso suficiente no debe dañar al objetivo.");
            verificar(motor.getActorActual() == personaje,
                    "La habilidad sin recurso suficiente debe conservar el turno.");

            personaje.setRecursoActual(personaje.getCostoHabilidad());
            motor.usarHabilidadEspecial(personaje, objetivo);
            verificar(personaje.getRecursoActual() == 0,
                    "El motor debe descontar el costo definido por cada clase.");
            verificar(motor.getActorActual() == null, "Una habilidad válida debe finalizar el turno.");
        }
    }

    private static void motorControlaCostoDeHabilidadesDeEnemigos() {
        Enemigo[] enemigos = {
                new Minion("Minion", 100, 100, 20, 5, 10, 1, 1),
                new Jefe("Jefe", 100, 100, 20, 5, 10, 1, 1),
                new Elite("Elite", 100, 100, 20, 5, 10, 1, 1),
                new Estandar("Estandar", 100, 100, 20, 5, 10, 1, 1)
        };

        for (Enemigo enemigo : enemigos) {
            Personaje objetivo = nuevoGuerrero();
            MotorCombate motor = new MotorCombate(List.of(objetivo), List.of(enemigo));
            enemigo.setRecursoActual(enemigo.getCostoHabilidad() - 1);
            enemigo.acumularEnergia(100);
            motor.avanzarEnergia();

            int recursoAnterior = enemigo.getRecursoActual();
            int vidaObjetivo = objetivo.getVidaActual();
            List<Personaje> personajesVivos = List.of(objetivo);
            esperarRechazo(() -> motor.habilidadEnemigo(enemigo, objetivo, personajesVivos));
            verificar(enemigo.getRecursoActual() == recursoAnterior,
                    "La habilidad enemiga sin recurso suficiente no debe descontar recursos.");
            verificar(objetivo.getVidaActual() == vidaObjetivo,
                    "La habilidad enemiga sin recurso suficiente no debe aplicar daño.");
            verificar(motor.getActorActual() == enemigo,
                    "La habilidad enemiga sin recurso suficiente debe conservar el turno.");

            enemigo.setRecursoActual(enemigo.getCostoHabilidad());
            motor.habilidadEnemigo(enemigo, objetivo, personajesVivos);
            verificar(enemigo.getRecursoActual() == 0,
                    "El motor debe descontar el costo definido por cada enemigo.");
            verificar(motor.getActorActual() == null, "Una habilidad enemiga válida debe finalizar el turno.");
        }
    }

    private static void motorResuelveTurnoEnemigoYDetectaDerrota() {
        Guerrero personaje = nuevoGuerrero();
        Enemigo enemigo = new Minion("Slime", 100, 100, 25, 5, 10, 1, 1);
        enemigo.setRecursoActual(0);
        enemigo.acumularEnergia(100);
        MotorCombate motor = new MotorCombate(List.of(personaje), List.of(enemigo));

        verificar(motor.getEstado() == null, "El combate debe seguir mientras ambos equipos estén en pie.");
        motor.avanzarCombate();
        verificar(personaje.getVidaActual() == 80, "El motor debe resolver el ataque del enemigo automáticamente.");
        verificar(enemigo.getRecursoActual() == 10 && motor.getActorActual() == null,
                "El ataque normal debe recuperar recurso y finalizar el turno enemigo.");
        personaje.setVidaActual(0);
        verificar(motor.getEstado() == EstadoCombate.DERROTA, "El motor debe detectar la derrota de la party.");
    }

    private static void motorRechazaAccionesConElCombateTerminado() {
        Curador curador = new Curador("Curador", 100, 100, 20, 5, 10, "");
        Escenario escenario = new Escenario(curador);
        curador.setVidaActual(50);
        escenario.aliado.setVidaActual(50);
        PocionVida pocion = new PocionVida("Vida", "Cura", 25);
        escenario.inventario.agregarItem(pocion);
        escenario.enemigo.setVidaActual(0);

        verificar(escenario.motor.getEstado() == EstadoCombate.VICTORIA, "La batalla ya está ganada.");
        esperarRechazo(() -> escenario.motor.validarUsoItem(curador, pocion));
        rechazarSinConsumir(escenario, curador, pocion);
        esperarRechazo(() -> escenario.motor.usarHabilidadEspecial(curador, null));
        esperarRechazo(() -> escenario.motor.defensa(curador));
        verificar(curador.getRecursoActual() == 100 && escenario.aliado.getVidaActual() == 50,
                "Una habilidad rechazada no debe gastar recursos ni curar al grupo.");
        curador.recibirDanio(20);
        verificar(curador.getVidaActual() == 35, "La defensa rechazada no debe cambiar la postura.");
    }

    private static void motorNoAvanzaEnergiaTrasLaDerrota() {
        Guerrero personaje = nuevoGuerrero();
        personaje.setVidaActual(0);
        Enemigo enemigo = new Minion("Slime", 100, 100, 25, 5, 10, 1, 1);
        enemigo.acumularEnergia(90);
        MotorCombate motor = new MotorCombate(List.of(personaje), List.of(enemigo));

        motor.avanzarEnergia();

        verificar(motor.getEstado() == EstadoCombate.DERROTA, "La batalla ya está perdida.");
        verificar(motor.getActorActual() == null && enemigo.getEnergiaActual() == 90,
                "El motor no debe acumular energía ni asignar turnos después de la derrota.");
    }

    private static void regeneracionDeRecursoEsResponsabilidadDelPersonaje() {
        Mago mago = new Mago("Mago", 100, 100, 20, 5, 10, "");
        Enemigo enemigo = EnemigoFactory.crearEnemigo(TipoEnemigo.SLIME, 1);
        mago.setRecursoActual(10);
        enemigo.setRecursoActual(10);
        MotorCombate motor = new MotorCombate(List.of(mago), List.of(enemigo));

        motor.avanzarEnergia();

        verificar(mago.getRecursoActual() == 13, "El personaje debe regenerar su recurso durante el tick.");
        verificar(enemigo.getRecursoActual() == 10, "La regeneración del personaje no debe afectar al enemigo.");
    }

    private static void controllerValidaYExponeElInventario() {
        Guerrero actor = nuevoGuerrero();
        Mago aliado = new Mago("Aliado", 100, 100, 20, 5, 5, "");
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

    private static void controllerAplicaRecompensasUnaSolaVez() {
        Guerrero actor = nuevoGuerrero();
        Guerrero aliado = nuevoGuerrero();
        Partida partida = GestorPartida.getInstance().nuevaPartida("Prueba recompensas",
                new ArrayList<>(List.of(actor, aliado)));
        Nivel nivel = partida.getMapaActual().getNivelActual();
        Enemigo enemigo = new Minion("Slime", 1, 100, 10, 0, 10, 1, 25) {
            @Override
            public DropStats GetProbabilidadDeDrop() {
                return new DropStats(100, 100, 0);
            }
        };
        nivel.getEnemigos().clear();
        nivel.getEnemigos().add(enemigo);
        BatallaController controller = new BatallaController();

        esperarRechazo(() -> controller.continuarAlSiguienteNivel());
        actor.acumularEnergia(100);
        controller.avanzarCombate();
        verificar(controller.resolverAtaque(0, 0), "El controlador debe delegar el ataque de la selección.");
        verificar(controller.verificarEstadoCombate() == EstadoCombate.VICTORIA, "Debe detectarse la victoria.");
        verificar(controller.continuarAlSiguienteNivel() && nivel.isCompletado(),
                "La victoria debe completar el nivel y dejar disponible el siguiente.");
        verificar(controller.getMapaActual().nivelActual == nivel.getNumeroNivel() + 1,
                "El DTO debe mostrar el nuevo nivel después del avance.");

        Nivel siguienteNivel = partida.getMapaActual().getNivelActual();
        for (Enemigo enemigoSiguiente : siguienteNivel.getEnemigos()) {
            enemigoSiguiente.setVidaActual(0);
        }
        esperarRechazo(() -> controller.continuarAlSiguienteNivel());
        verificar(!siguienteNivel.isCompletado(), "La confirmación anterior no debe completar otro nivel.");
        verificar(actor.getExperienciaActual() == 25 && aliado.getExperienciaActual() == 25,
                "Cada personaje debe recibir la experiencia de la victoria una sola vez.");
        verificar(controller.getCantidadItemsInventario() == 1, "El inventario debe recibir el botín una sola vez.");
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

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
        verificaciones++;
    }

    private static final class Escenario {
        private final Personaje actor;
        private final Mago aliado = new Mago("Aliado", 100, 100, 20, 5, 5, "");
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
}
