package model.entidades.personajes;

public class Curador extends Personaje {
    private static final String NOMBRE_RECURSO = "FE";
    private int fe;

    public Curador(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad,
            String rutaImagen) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, rutaImagen, NOMBRE_RECURSO);
        this.fe = 15;
    }

    @Override
    public int usarHabilidadEspecial() {
        sanarAliado();
        return 0;
    }

    private void sanarAliado() {
    }

    @Override
    public String getNombreRecurso() {
        return NOMBRE_RECURSO;
    }

    @Override
    public TipoPersonaje getTipo() {
        return TipoPersonaje.CURADOR;
    }

    @Override
    public void aumentarEstadisticasDeClase() {
        ataqueBase += 1;
        defensaBase += 1;
        fe += 4;
        vidaMax += 15;
    }
}
