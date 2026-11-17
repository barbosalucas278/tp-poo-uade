package tools;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import helpers.archivos.GestorArchivos;
import model.DTOs.PartidaGuardadaDTO;
import model.DTOs.PartidaGuardadaDTO.ConsumibleGuardado;
import model.DTOs.PartidaGuardadaDTO.EnemigoGuardado;
import model.DTOs.PartidaGuardadaDTO.EquipamientoGuardado;
import model.DTOs.PartidaGuardadaDTO.ItemGuardado;
import model.DTOs.PartidaGuardadaDTO.MapaGuardado;
import model.DTOs.PartidaGuardadaDTO.NivelGuardado;
import model.DTOs.PartidaGuardadaDTO.PersonajeGuardado;
import model.entidades.enemigos.TipoEnemigo;
import model.entidades.personajes.TipoPersonaje;
import model.objetos.TipoItemConsumible;
import model.objetos.TipoObjeto;
import model.objetos.TipoRareza;
import model.persistencia.PartidaMapper;
import model.partidas.Partida;

/**
 * Genera una partida de prueba fuera de los slots de la aplicación y verifica
 * el ciclo equipar, reemplazar, desequipar y guardar/cargar.
 *
 * Ejecutar desde la raíz del proyecto:
 *   javac -encoding UTF-8 -d out\\fixture src\\... tests\\tools\\PrepararPartidaEquipamiento.java
 *   java -cp out\\fixture tools.PrepararPartidaEquipamiento
 */
public final class PrepararPartidaEquipamiento {
    private static final Path ARCHIVO = Paths.get("tests", "tools", "partida-equipamiento-prueba.txt");
    private static final Path ARCHIVO_FINAL = Paths.get("tests", "tools",
            "partida-equipamiento-verificada.txt");

    private PrepararPartidaEquipamiento() {
    }

    public static void main(String[] args) throws IOException {
        boolean force = args.length > 0 && "--force".equals(args[0]);
        if (args.length > 1 || (args.length == 1 && !force)) {
            throw new IllegalArgumentException("Uso: PrepararPartidaEquipamiento [--force]");
        }
        if ((Files.exists(ARCHIVO) || Files.exists(ARCHIVO_FINAL)) && !force) {
            throw new IllegalStateException("Ya existe un fixture de equipamiento."
                    + ". No se sobrescribe; usar --force para regenerar el fixture.");
        }
        PartidaGuardadaDTO snapshot = crearSnapshotInicial();
        if (!GestorArchivos.guardarEnArchivo(snapshot, ARCHIVO)) {
            throw new IllegalStateException("No se pudo escribir " + ARCHIVO);
        }

        verificarCiclo(snapshot);
        System.out.println("Fixture creado y verificado: " + ARCHIVO);
        System.out.println("Fixture equipado creado: " + ARCHIVO_FINAL);
        System.out.println("Para cargarlo en la aplicación, copiarlo sólo a un slot vacío (slot1, slot2 o slot3).");
    }

    private static PartidaGuardadaDTO crearSnapshotInicial() {
        PartidaGuardadaDTO snapshot = new PartidaGuardadaDTO();
        snapshot.nombre = "Prueba equipamiento reproducible";

        snapshot.party.add(personaje(TipoPersonaje.GUERRERO, "Guerrero de prueba", 200, 50, 40, 15, 20));
        snapshot.party.add(personaje(TipoPersonaje.MAGO, "Mago de apoyo", 100, 100, 35, 5, 30));

        snapshot.inventario.add(equipable("Espada de prueba", "Arma para equipar inicialmente", 7, 0));
        snapshot.inventario.add(equipable("Hacha de prueba", "Arma para reemplazar la espada", 11, 0));

        ConsumibleGuardado pocion = new ConsumibleGuardado();
        pocion.tipo = TipoItemConsumible.POCION_MANA;
        pocion.rareza = TipoRareza.COMUN;
        snapshot.consumibles.add(pocion);

        MapaGuardado mapa = new MapaGuardado();
        mapa.numeroMapa = 1;
        mapa.completado = false;
        NivelGuardado nivel = new NivelGuardado();
        nivel.numeroNivel = 1;
        nivel.completado = false;
        EnemigoGuardado enemigo = new EnemigoGuardado();
        enemigo.tipo = TipoEnemigo.SLIME;
        enemigo.nivel = 1;
        enemigo.vidaActual = 40;
        nivel.enemigos.add(enemigo);
        mapa.niveles.add(nivel);
        snapshot.mapas.add(mapa);
        return snapshot;
    }

    private static PersonajeGuardado personaje(TipoPersonaje tipo, String nombre, int vida, int recurso,
            int ataque, int defensa, int velocidad) {
        PersonajeGuardado personaje = new PersonajeGuardado();
        personaje.tipo = tipo;
        personaje.nombre = nombre;
        personaje.rutaImagen = "";
        personaje.vidaActual = vida;
        personaje.recursoActual = recurso;
        personaje.ataqueBase = ataque;
        personaje.defensaBase = defensa;
        personaje.velocidad = velocidad;
        personaje.nivel = 1;
        personaje.experienciaActual = 0;
        personaje.experienciaSiguienteNivel = 100;
        return personaje;
    }

    private static ItemGuardado equipable(String nombre, String descripcion, int ataque, int defensa) {
        ItemGuardado item = new ItemGuardado();
        item.nombre = nombre;
        item.descripcion = descripcion;
        item.tipo = TipoObjeto.ARMA;
        item.bonusAtaque = ataque;
        item.bonusDefensa = defensa;
        return item;
    }

    private static void verificarCiclo(PartidaGuardadaDTO snapshot) throws IOException {
        Partida partida = PartidaMapper.desdeSnapshot(GestorArchivos.cargarArchivo(ARCHIVO));
        var guerrero = partida.getParty().get(0);
        var espada = partida.getInventarioGlobal().getItems().stream()
                .filter(item -> item.getNombre().equals("Espada de prueba"))
                .map(item -> (model.objetos.ItemEquipable) item).findFirst().orElseThrow();
        var hacha = partida.getInventarioGlobal().getItems().stream()
                .filter(item -> item.getNombre().equals("Hacha de prueba"))
                .map(item -> (model.objetos.ItemEquipable) item).findFirst().orElseThrow();

        verificar(guerrero.getAtaqueBase() == 40 && guerrero.getDefensaBase() == 15,
                "estadísticas iniciales");
        verificar(partida.getInventarioGlobal().getItems().size() == 3, "inventario inicial con poción");

        partida.equiparItem(guerrero, espada);
        verificar(guerrero.getAtaqueBase() == 47 && partida.getInventarioGlobal().getItems().size() == 2,
                "equipar aplica +7 una vez");

        partida.equiparItem(guerrero, hacha);
        verificar(guerrero.getAtaqueBase() == 51 && partida.getInventarioGlobal().getItems().size() == 2,
                "reemplazar deja sólo el arma nueva disponible");
        verificar(partida.getInventarioGlobal().getItems().contains(espada), "la espada vuelve al inventario");

        if (!GestorArchivos.guardarEnArchivo(PartidaMapper.aSnapshot(partida), ARCHIVO_FINAL)) {
            throw new IllegalStateException("No se pudo escribir " + ARCHIVO_FINAL);
        }

        Path temporal = Files.createTempFile(Paths.get("tests", "tools"),
                "partida-equipamiento-ciclo-", ".txt");
        if (!GestorArchivos.guardarEnArchivo(PartidaMapper.aSnapshot(partida), temporal)) {
            throw new IllegalStateException("No se pudo verificar el guardado temporal");
        }
        Partida cargada = PartidaMapper.desdeSnapshot(GestorArchivos.cargarArchivo(temporal));
        var guerreroCargado = cargada.getParty().get(0);
        verificar(guerreroCargado.getAtaqueBase() == 51 && guerreroCargado.getDefensaBase() == 15,
                "cargar conserva estadísticas sin duplicar bonus");
        verificar(guerreroCargado.getEquipamiento().get(TipoObjeto.ARMA).getNombre().equals("Hacha de prueba"),
                "cargar conserva el arma equipada");

        var hachaCargada = guerreroCargado.getEquipamiento().get(TipoObjeto.ARMA);
        cargada.desequiparItem(guerreroCargado, hachaCargada);
        verificar(guerreroCargado.getAtaqueBase() == 40 && cargada.getInventarioGlobal().getItems().size() == 3,
                "desequipar retira el bonus y devuelve el objeto");

        if (!temporal.toFile().delete()) {
            throw new IllegalStateException("No se pudo eliminar el archivo temporal " + temporal);
        }
        if (snapshot.equipamiento.size() != 0) {
            throw new IllegalStateException("El fixture inicial no debe arrancar con equipamiento");
        }
    }

    private static void verificar(boolean condicion, String caso) {
        if (!condicion) {
            throw new IllegalStateException("Falló la verificación: " + caso);
        }
    }
}
