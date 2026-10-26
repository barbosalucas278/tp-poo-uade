package helpers.archivos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import model.DTOs.PartidaGuardadaDTO;
import model.DTOs.PartidaGuardadaDTO.EnemigoGuardado;
import model.DTOs.PartidaGuardadaDTO.ItemGuardado;
import model.DTOs.PartidaGuardadaDTO.MapaGuardado;
import model.DTOs.PartidaGuardadaDTO.NivelGuardado;
import model.DTOs.PartidaGuardadaDTO.PersonajeGuardado;
import model.entidades.enemigos.TipoEnemigo;
import model.entidades.personajes.TipoPersonaje;
import model.objetos.TipoObjeto;

/**
 * Guarda y carga partidas en archivos de texto (partidas/slotN.txt).
 *
 * Cada línea es un registro con los campos separados por "|". El primer campo
 * indica el tipo de registro. Los NIVEL pertenecen al último MAPA leído y los
 * ENEMIGO al último NIVEL leído:
 *
 * PARTIDA|nombre
 * PERSONAJE|tipo|nombre|rutaImagen|vida|recurso|ataque|defensa|velocidad|nivel|xp|xpSiguienteNivel
 * ITEM|nombre|descripcion|tipo|bonusAtaque|bonusDefensa
 * MAPA|numero|completado
 * NIVEL|numero|completado
 * ENEMIGO|tipo|nivel|vida
 */
public class GestorArchivos {
    private static final String CARPETA = "partidas";
    private static final String SEPARADOR = "|";

    private static Path ruta(String slot) {
        return Paths.get(CARPETA, slot + ".txt");
    }

    public static boolean existe(String slot) {
        return Files.exists(ruta(slot));
    }

    public static boolean guardar(PartidaGuardadaDTO partida, String slot) {
        try {
            Files.createDirectories(Paths.get(CARPETA));
            try (BufferedWriter writer = Files.newBufferedWriter(ruta(slot), StandardCharsets.UTF_8)) {
                escribir(writer, partida);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static PartidaGuardadaDTO cargar(String slot) {
        if (!existe(slot)) {
            return null;
        }
        try (BufferedReader reader = Files.newBufferedReader(ruta(slot), StandardCharsets.UTF_8)) {
            return leer(reader);
        } catch (Exception e) { // IOException, o NumberFormatException/IllegalArgumentException si el archivo está corrupto
            e.printStackTrace();
            return null;
        }
    }

    private static void escribir(BufferedWriter writer, PartidaGuardadaDTO partida) throws IOException {
        escribirLinea(writer, "PARTIDA", partida.nombre);

        for (PersonajeGuardado p : partida.party) {
            escribirLinea(writer, "PERSONAJE", p.tipo, p.nombre, p.rutaImagen, p.vidaActual, p.recursoActual,
                    p.ataqueBase, p.defensaBase, p.velocidad, p.nivel, p.experienciaActual,
                    p.experienciaSiguienteNivel);
        }

        for (ItemGuardado item : partida.inventario) {
            escribirLinea(writer, "ITEM", item.nombre, item.descripcion, item.tipo, item.bonusAtaque,
                    item.bonusDefensa);
        }

        for (MapaGuardado mapa : partida.mapas) {
            escribirLinea(writer, "MAPA", mapa.numeroMapa, mapa.completado);
            for (NivelGuardado nivel : mapa.niveles) {
                escribirLinea(writer, "NIVEL", nivel.numeroNivel, nivel.completado);
                for (EnemigoGuardado enemigo : nivel.enemigos) {
                    escribirLinea(writer, "ENEMIGO", enemigo.tipo, enemigo.nivel, enemigo.vidaActual);
                }
            }
        }
    }

    private static PartidaGuardadaDTO leer(BufferedReader reader) throws IOException {
        PartidaGuardadaDTO partida = new PartidaGuardadaDTO();
        MapaGuardado mapaActual = null;
        NivelGuardado nivelActual = null;

        String linea;
        while ((linea = reader.readLine()) != null) {
            if (linea.isBlank()) {
                continue;
            }
            String[] campos = linea.split(Pattern.quote(SEPARADOR), -1);

            switch (campos[0]) {
                case "PARTIDA":
                    partida.nombre = campos[1];
                    break;

                case "PERSONAJE":
                    PersonajeGuardado personaje = new PersonajeGuardado();
                    personaje.tipo = TipoPersonaje.valueOf(campos[1]);
                    personaje.nombre = campos[2];
                    personaje.rutaImagen = campos[3];
                    personaje.vidaActual = Integer.parseInt(campos[4]);
                    personaje.recursoActual = Integer.parseInt(campos[5]);
                    personaje.ataqueBase = Integer.parseInt(campos[6]);
                    personaje.defensaBase = Integer.parseInt(campos[7]);
                    personaje.velocidad = Integer.parseInt(campos[8]);
                    personaje.nivel = Integer.parseInt(campos[9]);
                    personaje.experienciaActual = Integer.parseInt(campos[10]);
                    personaje.experienciaSiguienteNivel = Integer.parseInt(campos[11]);
                    partida.party.add(personaje);
                    break;

                case "ITEM":
                    ItemGuardado item = new ItemGuardado();
                    item.nombre = campos[1];
                    item.descripcion = campos[2];
                    item.tipo = TipoObjeto.valueOf(campos[3]);
                    item.bonusAtaque = Integer.parseInt(campos[4]);
                    item.bonusDefensa = Integer.parseInt(campos[5]);
                    partida.inventario.add(item);
                    break;

                case "MAPA":
                    mapaActual = new MapaGuardado();
                    mapaActual.numeroMapa = Integer.parseInt(campos[1]);
                    mapaActual.completado = Boolean.parseBoolean(campos[2]);
                    partida.mapas.add(mapaActual);
                    nivelActual = null;
                    break;

                case "NIVEL":
                    if (mapaActual == null) {
                        throw new IllegalArgumentException("NIVEL sin un MAPA previo");
                    }
                    nivelActual = new NivelGuardado();
                    nivelActual.numeroNivel = Integer.parseInt(campos[1]);
                    nivelActual.completado = Boolean.parseBoolean(campos[2]);
                    mapaActual.niveles.add(nivelActual);
                    break;

                case "ENEMIGO":
                    if (nivelActual == null) {
                        throw new IllegalArgumentException("ENEMIGO sin un NIVEL previo");
                    }
                    EnemigoGuardado enemigo = new EnemigoGuardado();
                    enemigo.tipo = TipoEnemigo.valueOf(campos[1]);
                    enemigo.nivel = Integer.parseInt(campos[2]);
                    enemigo.vidaActual = Integer.parseInt(campos[3]);
                    nivelActual.enemigos.add(enemigo);
                    break;

                default:
                    throw new IllegalArgumentException("Línea desconocida: " + linea);
            }
        }

        if (partida.nombre == null || partida.mapas.isEmpty()) {
            throw new IllegalArgumentException("Archivo de partida incompleto");
        }
        return partida;
    }

    private static void escribirLinea(BufferedWriter writer, Object... campos) throws IOException {
        List<String> textos = new ArrayList<>();
        for (Object campo : campos) {
            textos.add(limpiar(campo));
        }
        writer.write(String.join(SEPARADOR, textos));
        writer.newLine();
    }

    // El separador o un salto de línea dentro de un texto (ej. un nombre) romperían el formato
    private static String limpiar(Object campo) {
        if (campo == null) {
            return "";
        }
        return campo.toString().replace(SEPARADOR, " ").replace("\r", " ").replace("\n", " ");
    }
}
