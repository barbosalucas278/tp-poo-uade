import java.util.ArrayList;
import java.util.List;

import controller.BatallaController;
import model.Combate.MotorCombate;
import model.DTOs.ItemInventarioDTO;
import model.entidades.enemigos.Enemigo;
import model.entidades.enemigos.EnemigoFactory;
import model.entidades.enemigos.TipoEnemigo;
import model.entidades.personajes.Guerrero;
import model.entidades.personajes.Hechicero;
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
        noSePuedeReutilizarUnObjetoConsumido();
        ataqueSigueFinalizandoElTurno();
        controllerValidaYExponeElInventario();
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
}
