package model.entidades.personajes;

public class Guerrero extends Personaje {
    private static final String NOMBRE_RECURSO = "FURIA";
    private int fuerza;


    public int getFuerza() {
        return fuerza;
    }

    public Guerrero(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad,
            String rutaImagen) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, rutaImagen, NOMBRE_RECURSO);
        this.fuerza = 15;
    }

    @Override
    public void usarHabilidadEspecial() {
        this.GritoDeGuerra();
    }

    private void GritoDeGuerra() {
        // Implementación de la habilidad especial del Guerrero
    }

    @Override
    public String getNombreRecurso() {
        return NOMBRE_RECURSO;
    }

    @Override
    public TipoPersonaje getTipo() {
        return TipoPersonaje.GUERRERO;
    }
}
