package model.entidades.personajes;

public class Asesino extends Personaje {
    private static final String NOMBRE_RECURSO = "VIGOR";
    private int destreza;

    public int getDestreza() {
        return destreza;
    }

    public Asesino(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad,
            String rutaImagen) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, rutaImagen, NOMBRE_RECURSO);
        this.destreza = 15;
    }

    @Override
    public void usarHabilidadEspecial() {
        this.sigilo();
    }

    private void sigilo() {
        // Implementación de la habilidad especial del Asesino
    }

    @Override
    public String getNombreRecurso() {
        return NOMBRE_RECURSO;
    }

    @Override
    public TipoPersonaje getTipo() {
        return TipoPersonaje.ASESINO;
    }

    @Override
    public void aumentarEstadisticasDeClase() {
        this.ataqueBase += 2;
        this.defensaBase += 1;
        this.destreza += 4;
        this.vidaMax += 10;
    }
}
