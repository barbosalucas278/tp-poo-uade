package model.entidades.personajes;

import model.entidades.Entidad;

public class Guerrero extends Personaje {
    private static final String NOMBRE_RECURSO = "FURIA";
    private int fuerza;

    public Guerrero(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad,
            String rutaImagen) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, rutaImagen, NOMBRE_RECURSO);
        this.fuerza = 15;
    }

    @Override
    public void usarHabilidadEspecial(Entidad objetivo) {
        validarObjetivoEnemigo(objetivo);
        validarCostoHabilidad();
        objetivo.recibirDanio(ataqueBase + 10);
        consumirRecursos(getCostoHabilidadEspecial());
    }

    @Override
    public String getNombreHabilidadEspecial() {
        return "Golpe poderoso";
    }

    @Override
    public int getCostoHabilidadEspecial() {
        return 20;
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
        return TipoPersonaje.GUERRERO;
    }

    @Override
    public void aumentarEstadisticasDeClase() {
        ataqueBase += 2;
        defensaBase += 1;
        fuerza += 4;
        vidaMax += 20;
    }
}
