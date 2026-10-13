package model.DTOs;

import model.entidades.personajes.TipoPersonaje;

/**
 * PersonajeDTO
 */
public class PersonajeDTO {
    public String nombre;
    public String clase;
    public String rutaImagen;

    public String getNombre() {
        return nombre;
    }

    public TipoPersonaje getTipoPersonaje() {
        return TipoPersonaje.valueOf(clase.toUpperCase());
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public PersonajeDTO(String nombre, String clase, String rutaImagen) {
        this.nombre = nombre;
        this.clase = clase;
        this.rutaImagen = rutaImagen;
    }
}
