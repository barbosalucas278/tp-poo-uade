package controller;

import java.util.ArrayList;
import java.util.List;

import model.DTOs.PersonajeDTO;
import model.entidades.personajes.Personaje;
import model.entidades.personajes.PersonajeFactory;
import model.partidas.GestorPartida;
import model.partidas.Partida;

public class CreacionPartyController {
    private GestorPartida gestorPartida;
    private List<PersonajeDTO> partyDTO;

    public CreacionPartyController() {
        gestorPartida = GestorPartida.getInstance();
        this.partyDTO = new ArrayList<>();
    }

    public void agregarPersonaje(PersonajeDTO personaje) {
        partyDTO.add(personaje);
    }

    public void removerPersonaje(int slotSeleccionado) {
        if (slotSeleccionado >= 0 && slotSeleccionado < partyDTO.size()) {
            partyDTO.remove(slotSeleccionado);
        }
    }

    public List<PersonajeDTO> getParty() {
        return partyDTO;
    }

    public void iniciarJuego() {
        List<Personaje> party = new ArrayList<>();
        for (PersonajeDTO personajeDTO : partyDTO) {
            party.add(PersonajeFactory.crearPersonaje(personajeDTO.getNombre(), personajeDTO.getTipoPersonaje(),
                    personajeDTO.getRutaImagen()));
        }
        Partida partida = gestorPartida.nuevaPartida("Nombre de Partida", party);
        if(partida != null) {
            gestorPartida.iniciarPartida(partida.getNombre());
        }
    }
}
