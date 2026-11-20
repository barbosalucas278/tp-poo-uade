package controller;

import java.util.ArrayList;
import java.util.List;

import model.DTOs.EquipableDTO;
import model.entidades.personajes.Personaje;
import model.objetos.Inventario;
import model.objetos.ItemEquipable;
import model.objetos.TipoObjeto;
import model.partidas.GestorPartida;
import model.partidas.Partida;

/** Conecta la pantalla de equipamiento de prebatalla con la partida actual. */
public class EquipamientoController {
    private final Partida partida;

    public EquipamientoController() {
        partida = GestorPartida.getInstance().getPartidaActual();
        if (partida == null) {
            throw new IllegalStateException("Hace falta una partida para administrar el equipamiento.");
        }
    }

    public List<String> getNombresPersonajes() {
        List<String> nombres = new ArrayList<>();
        for (Personaje personaje : partida.getParty()) {
            nombres.add(personaje.getNombre());
        }
        return nombres;
    }

    public List<EquipableDTO> getEquipables() {
        List<EquipableDTO> equipables = new ArrayList<>();
        for (ItemEquipable item : partida.getInventarioGlobal().getEquipables()) {
            equipables.add(aDTO(item));
        }
        return equipables;
    }

    public EquipableDTO getEquipado(int indicePersonaje, TipoObjeto tipo) {
        ItemEquipable item = seleccionarPersonaje(indicePersonaje).getItemEquipado(tipo);
        return item == null ? null : aDTO(item);
    }

    public int getAtaque(int indicePersonaje) {
        return seleccionarPersonaje(indicePersonaje).getAtaqueBase();
    }

    public int getDefensa(int indicePersonaje) {
        return seleccionarPersonaje(indicePersonaje).getDefensaBase();
    }

    public void equipar(int indicePersonaje, int indiceEquipable) {
        Personaje personaje = seleccionarPersonaje(indicePersonaje);
        Inventario inventario = partida.getInventarioGlobal();
        List<ItemEquipable> equipables = inventario.getEquipables();
        if (indiceEquipable < 0 || indiceEquipable >= equipables.size()) {
            throw new IllegalArgumentException("Elegí un objeto del inventario.");
        }
        inventario.equiparItem(equipables.get(indiceEquipable), personaje);
    }

    public void desequipar(int indicePersonaje, TipoObjeto tipo) {
        Personaje personaje = seleccionarPersonaje(indicePersonaje);
        partida.getInventarioGlobal().desequiparItem(personaje.getItemEquipado(tipo), personaje);
    }

    private Personaje seleccionarPersonaje(int indicePersonaje) {
        List<Personaje> party = partida.getParty();
        if (indicePersonaje < 0 || indicePersonaje >= party.size()) {
            throw new IllegalArgumentException("Elegí un personaje.");
        }
        return party.get(indicePersonaje);
    }

    private EquipableDTO aDTO(ItemEquipable item) {
        return new EquipableDTO(item.getNombre(), item.getTipo(), item.getBonusAtaque(), item.getBonusDefensa(),
                item.getRareza());
    }
}
