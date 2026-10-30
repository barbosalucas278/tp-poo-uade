package controller;

import java.util.ArrayList;
import java.util.List;

import model.DTOs.PersonajeDTO;
import model.entidades.personajes.Personaje;
import model.entidades.personajes.PersonajeFactory;
import model.partidas.GestorPartida;
import model.partidas.Partida;

public class CreacionPartyController {
    private static final int CANTIDAD_SLOTS = 4;

    private GestorPartida gestorPartida;
    private PersonajeDTO[] partyDTO;

    public CreacionPartyController() {
        gestorPartida = GestorPartida.getInstance();
        this.partyDTO = new PersonajeDTO[CANTIDAD_SLOTS];
    }

    // Si el slot ya tenía un personaje, se reemplaza
    public void agregarPersonaje(int slotSeleccionado, PersonajeDTO personaje) {
        if (slotSeleccionado >= 0 && slotSeleccionado < partyDTO.length) {
            partyDTO[slotSeleccionado] = personaje;
        }
    }

    public void removerPersonaje(int slotSeleccionado) {
        if (slotSeleccionado >= 0 && slotSeleccionado < partyDTO.length) {
            partyDTO[slotSeleccionado] = null;
        }
    }

    public List<PersonajeDTO> getParty() {
        List<PersonajeDTO> party = new ArrayList<>();
        for (PersonajeDTO personajeDTO : partyDTO) {
            if (personajeDTO != null) {
                party.add(personajeDTO);
            }
        }
        return party;
    }

    public void iniciarJuego() {
        List<Personaje> party = new ArrayList<>();
        for (PersonajeDTO personajeDTO : getParty()) {
            party.add(PersonajeFactory.crearPersonaje(personajeDTO.getNombre(), personajeDTO.getTipoPersonaje(),
                    personajeDTO.getRutaImagen()));
        }
        Partida partida = gestorPartida.nuevaPartida("Nombre de Partida", party);
        if(partida != null) {
            gestorPartida.iniciarPartida(partida.getNombre());
        }
    }
}
