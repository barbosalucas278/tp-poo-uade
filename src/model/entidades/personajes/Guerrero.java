package model.entidades.personajes;

public class Guerrero extends Personaje {
    private int fuerza;


    public Guerrero(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad,
            String rutaImagen) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, rutaImagen);
        this.fuerza = 15;
    }

    @Override
    public void usarHabilidadEspecial() {
        this.GritoDeGuerra();
    }

    private void GritoDeGuerra() {
        // Implementación de la habilidad especial del Guerrero
    }
}
