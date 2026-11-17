package model.entidades.personajes;

import model.entidades.Entidad;

public class Curador extends Personaje {
    private static final String NOMBRE_RECURSO = "MANA";
    private int voluntad;

    public Curador(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad,
            String rutaImagen) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, rutaImagen, NOMBRE_RECURSO);
        this.voluntad = 15;
    }

    @Override
    public void usarHabilidadEspecial(Entidad objetivo) {
        validarObjetivoHabilidad(objetivo);
        validarCostoHabilidad();
        if (objetivo.getVidaActual() >= objetivo.getVidaMax()) {
            throw new IllegalArgumentException("El aliado ya tiene la vida completa.");
        }
        objetivo.recibirSanacion(30);
        consumirRecursos(getCostoHabilidadEspecial());
    }

    @Override
    public String getNombreHabilidadEspecial() {
        return "Curación";
    }

    @Override
    public int getCostoHabilidadEspecial() {
        return 25;
    }

    @Override
    public void validarObjetivoHabilidad(Entidad objetivo) {
        validarObjetivoVivo(objetivo);
        if (!(objetivo instanceof Personaje)) {
            throw new IllegalArgumentException("La curación requiere un personaje aliado.");
        }
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
        voluntad += 4;
        vidaMax += 10;
    }
}
