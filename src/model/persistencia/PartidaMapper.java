package model.persistencia;

import java.util.ArrayList;
import java.util.List;

import model.DTOs.PartidaGuardadaDTO;
import model.DTOs.PartidaGuardadaDTO.ConsumibleGuardado;
import model.DTOs.PartidaGuardadaDTO.EnemigoGuardado;
import model.DTOs.PartidaGuardadaDTO.ItemGuardado;
import model.DTOs.PartidaGuardadaDTO.MapaGuardado;
import model.DTOs.PartidaGuardadaDTO.NivelGuardado;
import model.DTOs.PartidaGuardadaDTO.PersonajeGuardado;
import model.entidades.enemigos.Enemigo;
import model.entidades.enemigos.EnemigoFactory;
import model.entidades.personajes.Personaje;
import model.entidades.personajes.PersonajeFactory;
import model.objetos.Item;
import model.objetos.ItemConsumible;
import model.objetos.ItemEquipable;
import model.objetos.ItemsConsumiblesFactory;
import model.partidas.Mapa;
import model.partidas.Nivel;
import model.partidas.Partida;

/**
 * Convierte una Partida en PartidaGuardadaDTO (para escribirla a disco) y viceversa.
 */
public class PartidaMapper {

    public static PartidaGuardadaDTO aSnapshot(Partida partida) {
        PartidaGuardadaDTO snapshot = new PartidaGuardadaDTO();
        snapshot.nombre = partida.getNombre();

        for (Personaje personaje : partida.getParty()) {
            PersonajeGuardado p = new PersonajeGuardado();
            p.tipo = personaje.getTipo();
            p.nombre = personaje.getNombre();
            p.rutaImagen = personaje.getRutaImagen();
            p.vidaActual = personaje.getVidaActual();
            p.recursoActual = personaje.getRecursoActual();
            p.ataqueBase = personaje.getAtaqueBase();
            p.defensaBase = personaje.getDefensaBase();
            p.velocidad = personaje.getVelocidad();
            p.nivel = personaje.getNivel();
            p.experienciaActual = personaje.getExperienciaActual();
            p.experienciaSiguienteNivel = personaje.getExperienciaSiguienteNivel();
            snapshot.party.add(p);
        }

        for (Item item : partida.getInventarioGlobal().getItems()) {
            if (item instanceof ItemEquipable) {
                ItemEquipable equipable = (ItemEquipable) item;
                ItemGuardado i = new ItemGuardado();
                i.nombre = equipable.getNombre();
                i.descripcion = equipable.getDescripcion();
                i.tipo = equipable.getTipo();
                i.bonusAtaque = equipable.getBonusAtaque();
                i.bonusDefensa = equipable.getBonusDefensa();
                snapshot.inventario.add(i);
            } else if (item instanceof ItemConsumible) {
                // Alcanza con tipo y rareza: al cargar, ItemsConsumiblesFactory recalcula nombre, descripción y cantidad
                ItemConsumible consumible = (ItemConsumible) item;
                ConsumibleGuardado c = new ConsumibleGuardado();
                c.tipo = consumible.getTipo();
                c.rareza = consumible.getRareza();
                snapshot.consumibles.add(c);
            }
        }

        for (Mapa mapa : partida.getMapas()) {
            MapaGuardado m = new MapaGuardado();
            m.numeroMapa = mapa.getNumeroMapa();
            m.completado = mapa.isCompletado();
            for (Nivel nivel : mapa.getNiveles()) {
                NivelGuardado n = new NivelGuardado();
                n.numeroNivel = nivel.getNumeroNivel();
                n.completado = nivel.isCompletado();
                for (Enemigo enemigo : nivel.getEnemigos()) {
                    EnemigoGuardado e = new EnemigoGuardado();
                    e.tipo = enemigo.getTipo();
                    e.nivel = enemigo.getNivel();
                    e.vidaActual = enemigo.getVidaActual();
                    n.enemigos.add(e);
                }
                m.niveles.add(n);
            }
            snapshot.mapas.add(m);
        }

        return snapshot;
    }

    public static Partida desdeSnapshot(PartidaGuardadaDTO snapshot) {
        List<Personaje> party = new ArrayList<>();
        for (PersonajeGuardado p : snapshot.party) {
            Personaje personaje = PersonajeFactory.crearPersonaje(p.nombre, p.tipo, p.rutaImagen);
            // ataque y defensa ya incluyen los bonus del equipamiento que tenía al guardar
            personaje.setAtaqueBase(p.ataqueBase);
            personaje.setDefensaBase(p.defensaBase);
            personaje.setVelocidad(p.velocidad);
            personaje.setVidaActual(p.vidaActual);
            personaje.setRecursoActual(p.recursoActual);
            personaje.restaurarProgreso(p.nivel, p.experienciaActual, p.experienciaSiguienteNivel);
            party.add(personaje);
        }

        List<Mapa> mapas = new ArrayList<>();
        for (MapaGuardado m : snapshot.mapas) {
            List<Nivel> niveles = new ArrayList<>();
            for (NivelGuardado n : m.niveles) {
                List<Enemigo> enemigos = new ArrayList<>();
                for (EnemigoGuardado e : n.enemigos) {
                    Enemigo enemigo = EnemigoFactory.crearEnemigo(e.tipo, e.nivel);
                    enemigo.setVidaActual(e.vidaActual);
                    enemigos.add(enemigo);
                }
                Nivel nivel = new Nivel(n.numeroNivel, enemigos);
                nivel.setCompletado(n.completado);
                niveles.add(nivel);
            }
            Mapa mapa = new Mapa(m.numeroMapa, niveles);
            mapa.setCompletado(m.completado);
            mapas.add(mapa);
        }

        Partida partida = new Partida(snapshot.nombre, party, mapas.get(0));
        for (int i = 1; i < mapas.size(); i++) {
            partida.agregarMapa(mapas.get(i));
        }

        for (ItemGuardado i : snapshot.inventario) {
            partida.getInventarioGlobal().agregarItem(
                    new ItemEquipable(i.nombre, i.descripcion, i.tipo, i.bonusAtaque, i.bonusDefensa));
        }

        for (ConsumibleGuardado c : snapshot.consumibles) {
            partida.getInventarioGlobal().agregarItem(ItemsConsumiblesFactory.CrearItemConsumible(c.rareza, c.tipo));
        }

        return partida;
    }
}
