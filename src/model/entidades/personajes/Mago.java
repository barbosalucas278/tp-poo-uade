package model.entidades.personajes;

import model.entidades.Entidad;

public class Mago extends Personaje {
    private static final String NOMBRE_RECURSO = "MANA";
    private int intelecto;

    public Mago(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad,
            String rutaImagen) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, rutaImagen, NOMBRE_RECURSO);
        this.intelecto = 15;
    }

    @Override
    public void usarHabilidadEspecial(Entidad objetivo) {
        validarObjetivoEnemigo(objetivo);
        validarCostoHabilidad();
        objetivo.recibirDanio(ataqueBase + 25);
        consumirRecursos(getCostoHabilidadEspecial());
    }

    @Override
    public String getNombreHabilidadEspecial() {
        return "Bola de fuego";
    }

    @Override
    public int getCostoHabilidadEspecial() {
        return 30;
    }

    @Override
    public void validarObjetivoHabilidad(Entidad objetivo) {
        validarObjetivoEnemigo(objetivo);
    }

    @Override
    public String getNombreRecurso() {
        return NOMBRE_RECURSO;
    }

    @Override
    public TipoPersonaje getTipo() {
        return TipoPersonaje.MAGO;
    }

    @Override
    public void aumentarEstadisticasDeClase() {
        ataqueBase += 2;
        defensaBase += 1;
        intelecto += 4;
        vidaMax += 5;
    }
}
