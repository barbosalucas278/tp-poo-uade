package model.entidades.personajes;

public class Invocador extends Personaje {
    private static final String NOMBRE_RECURSO = "ESENCIA";
    private int espiritu;

    public Invocador(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad,
            String rutaImagen) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, rutaImagen, NOMBRE_RECURSO);
        this.espiritu = 15;
    }

    @Override
    public void usarHabilidadEspecial() {
        invocarCriatura();
    }

    private void invocarCriatura() {
    }

    @Override
    public String getNombreRecurso() {
        return NOMBRE_RECURSO;
    }

    @Override
    public TipoPersonaje getTipo() {
        return TipoPersonaje.INVOCADOR;
    }

    @Override
    public void aumentarEstadisticasDeClase() {
        ataqueBase += 2;
        defensaBase += 1;
        espiritu += 4;
        vidaMax += 5;
    }
}
