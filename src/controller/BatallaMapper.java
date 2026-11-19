package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import helpers.archivos.ResourcesHelper;
import model.DTOs.CombatienteDTO;
import model.DTOs.ItemInventarioDTO;
import model.DTOs.MapaDTO;
import model.entidades.Entidad;
import model.entidades.enemigos.Enemigo;
import model.entidades.personajes.Personaje;
import model.objetos.Item;
import model.objetos.ItemConsumible;
import model.partidas.Mapa;
import model.partidas.Nivel;

class BatallaMapper {


    static MapaDTO aMapaDTO(Mapa mapa) {
        Nivel nivel = mapa == null ? null : mapa.getNivelActual();
        if (nivel == null) {
            return null;
        }
        return new MapaDTO(mapa.getNumeroMapa(), nivel.getNumeroNivel(), mapa.getNiveles().size());
    }
    
    static List<CombatienteDTO> aPartyDTO(List<Personaje> personajes) {
        List<CombatienteDTO> party = new ArrayList<>();
        for (Personaje personaje : personajes) {
            party.add(aCombatiente(personaje, personaje.getRutaImagen(),
                    personaje.getNombreRecurso(), personaje.getNombreHabilidad()));
        }
        return party;
    }

    static List<CombatienteDTO> aEnemigosDTO(List<Enemigo> enemigos) {
        List<CombatienteDTO> resultado = new ArrayList<>();
        for (Enemigo enemigo : enemigos) {
            resultado.add(aCombatiente(enemigo, ResourcesHelper.getRutaImagenEnemigo(enemigo.getTipo()), "VIGOR", null));
        }
        return Collections.unmodifiableList(resultado);
    }

    static List<ItemInventarioDTO> aItemsDTO(List<Item> items) {
        List<ItemInventarioDTO> resultado = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            resultado.add(new ItemInventarioDTO(i, item.getNombre(), item.getDescripcion(), item.getRareza(),
                    item instanceof ItemConsumible));
        }
        return resultado;
    }

    private static CombatienteDTO aCombatiente(Entidad entidad, String rutaImagen, String nombreRecurso,
            String nombreHabilidad) {
        return new CombatienteDTO(entidad.getNombre(), entidad.getClass().getSimpleName(), rutaImagen,
                entidad.getVidaActual(), entidad.getVidaMax(), nombreRecurso,
                entidad.getRecursoActual(), entidad.getRecursoMax(), entidad.getEnergiaActual(), nombreHabilidad);
    }
}
