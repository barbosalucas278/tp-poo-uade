package model.DTOs;

import java.util.List;

/**
 * PartidaDTO
 */
public class PartidaDTO {
    public List<PersonajeDTO> personajes;
    public String nombrePartida;
    
    public PartidaDTO(List<PersonajeDTO> personajes, String nombrePartida) {
        this.personajes = personajes;
        this.nombrePartida = nombrePartida;
    }
}
