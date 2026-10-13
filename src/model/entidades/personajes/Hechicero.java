package model.entidades.personajes;

/**
 * Hechicero
 */
public class Hechicero extends Personaje {
    private int intelecto;

    public Hechicero(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad,
            String rutaImagen) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, rutaImagen);
        this.intelecto = 15;
    }

    @Override
    public void usarHabilidadEspecial() {
        this.meditar();
    }

    private void meditar() {
        // Implementación de la habilidad especial del Hechicero
    }
}
