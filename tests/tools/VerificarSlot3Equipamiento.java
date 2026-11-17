package tools;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import helpers.archivos.GestorArchivos;
import model.DTOs.PartidaGuardadaDTO;
import model.objetos.ItemEquipable;
import model.objetos.TipoObjeto;
import model.persistencia.PartidaMapper;
import model.partidas.Partida;

// Verifica un archivo de partida equipado y el ciclo completo de persistencia.
public final class VerificarSlot3Equipamiento {
    private VerificarSlot3Equipamiento() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException(
                    "Uso: VerificarSlot3Equipamiento <archivo-partida.txt>");
        }

        Path archivo = Paths.get(args[0]);
        PartidaGuardadaDTO snapshot = GestorArchivos.cargarArchivo(archivo);
        if (snapshot == null) {
            throw new IllegalStateException("No se pudo cargar el archivo " + archivo);
        }

        Partida partida = PartidaMapper.desdeSnapshot(snapshot);
        if (partida.getParty().isEmpty()) {
            throw new IllegalStateException("La partida no tiene personajes");
        }
        var personaje = partida.getParty().get(0);
        ItemEquipable equipado = personaje.getEquipamiento().get(TipoObjeto.ARMA);
        verificar(equipado != null, "el primer personaje tiene un arma equipada");

        int ataqueAntes = personaje.getAtaqueBase();
        int defensaAntes = personaje.getDefensaBase();
        String nombreEquipado = equipado.getNombre();
        int inventarioAntes = partida.getInventarioGlobal().getItems().size();

        Path temporal = Files.createTempFile(Paths.get("tests", "tools"),
                "equipamiento-roundtrip-", ".txt");
        try {
            verificar(GestorArchivos.guardarEnArchivo(PartidaMapper.aSnapshot(partida), temporal),
                    "guardado temporal");
            Partida recargada = PartidaMapper.desdeSnapshot(GestorArchivos.cargarArchivo(temporal));
            var personajeRecargado = recargada.getParty().get(0);
            ItemEquipable equipadoRecargado = personajeRecargado.getEquipamiento().get(TipoObjeto.ARMA);
            verificar(personajeRecargado.getAtaqueBase() == ataqueAntes,
                    "carga conserva ataque sin duplicar el bonus");
            verificar(personajeRecargado.getDefensaBase() == defensaAntes,
                    "carga conserva defensa");
            verificar(equipadoRecargado != null && equipadoRecargado.getNombre().equals(nombreEquipado),
                    "carga conserva el arma equipada");
            verificar(recargada.getInventarioGlobal().getItems().size() == inventarioAntes,
                    "carga conserva el inventario");
        } finally {
            Files.deleteIfExists(temporal);
        }

        System.out.println("OK: " + archivo
                + " conserva equipamiento y no duplica bonificaciones al cargar.");
    }

    private static void verificar(boolean condicion, String caso) {
        if (!condicion) {
            throw new IllegalStateException("Falló: " + caso);
        }
    }
}
