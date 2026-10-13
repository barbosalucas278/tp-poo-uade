package model.entidades.personajes;

public class Asesino extends Personaje {
    private int destreza;

    public Asesino(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad,
            String rutaImagen) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, rutaImagen);
        this.destreza = 15;
    }

    @Override
    public void usarHabilidadEspecial() {
        this.sigilo();
    }

    private void sigilo() {
        // Implementación de la habilidad especial del Asesino
    }
}
