package model.entidades.personajes;

/**
 * Hechicero
 */
public class Hechicero extends Personaje {
    private static final String NOMBRE_RECURSO = "MANA";
    private int intelecto;

    public int getIntelecto() {
        return intelecto;
    }

    public Hechicero(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad,
            String rutaImagen) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, rutaImagen, NOMBRE_RECURSO);
        this.intelecto = 15;
    }

    @Override
    public void usarHabilidadEspecial() {
        this.meditar();
    }

    private void meditar() {
        // Implementación de la habilidad especial del Hechicero
    }

    @Override
    public String getNombreRecurso() {
        return NOMBRE_RECURSO;
    }
}
