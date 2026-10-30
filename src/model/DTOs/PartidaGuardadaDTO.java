package model.DTOs;

import java.util.ArrayList;
import java.util.List;
import model.entidades.enemigos.TipoEnemigo;
import model.entidades.personajes.TipoPersonaje;
import model.objetos.TipoItemConsumible;
import model.objetos.TipoObjeto;
import model.objetos.TipoRareza;

/**
 * Datos de una partida tal como se guardan en el archivo TXT.
 */
public class PartidaGuardadaDTO {
    public String nombre;
    public List<PersonajeGuardado> party = new ArrayList<>();
    public List<ItemGuardado> inventario = new ArrayList<>();
    public List<ConsumibleGuardado> consumibles = new ArrayList<>();
    public List<MapaGuardado> mapas = new ArrayList<>();

    public static class PersonajeGuardado {
        public TipoPersonaje tipo;
        public String nombre;
        public String rutaImagen;
        public int vidaActual;
        public int recursoActual;
        public int ataqueBase;
        public int defensaBase;
        public int velocidad;
        public int nivel;
        public int experienciaActual;
        public int experienciaSiguienteNivel;
    }

    public static class ItemGuardado {
        public String nombre;
        public String descripcion;
        public TipoObjeto tipo;
        public int bonusAtaque;
        public int bonusDefensa;
    }

    public static class ConsumibleGuardado {
        public TipoItemConsumible tipo;
        public TipoRareza rareza;
    }

    public static class MapaGuardado {
        public int numeroMapa;
        public boolean completado;
        public List<NivelGuardado> niveles = new ArrayList<>();
    }

    public static class NivelGuardado {
        public int numeroNivel;
        public boolean completado;
        public List<EnemigoGuardado> enemigos = new ArrayList<>();
    }

    public static class EnemigoGuardado {
        public TipoEnemigo tipo;
        public int nivel;
        public int vidaActual;
    }
}
