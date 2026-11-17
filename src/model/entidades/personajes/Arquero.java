package model.entidades.personajes;

public class Arquero extends Personaje {
    private static final String NOMBRE_RECURSO = "CONCENTRACION";
    private int punteria;

    public Arquero(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad,
            String rutaImagen) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, rutaImagen, NOMBRE_RECURSO);
        this.punteria = 15;
    }

    @Override
    public void usarHabilidadEspecial() {
        disparoCertero();
    }

    private void disparoCertero() {
    }

    @Override
    public String getNombreRecurso() {
        return NOMBRE_RECURSO;
    }

    @Override
    public TipoPersonaje getTipo() {
        return TipoPersonaje.ARQUERO;
    }

    @Override
    public void aumentarEstadisticasDeClase() {
        ataqueBase += 3;
        defensaBase += 1;
        punteria += 4;
        vidaMax += 10;
    }
}
