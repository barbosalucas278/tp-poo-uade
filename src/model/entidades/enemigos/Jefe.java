package model.entidades.enemigos;

/**
 * Jefe
 */
public class Jefe extends Enemigo{

    public Jefe(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad, int nivel,
            int experienciaOtorgada) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, nivel, experienciaOtorgada);
    }

    public void faseFuria(){
        this.setVelocidad(this.getVelocidad() * 2);
    }

    public void usarHabilidadEspecial() {
        // Implementación de la habilidad especial del jefe
    }
}
