package model.entidades.personajes;

import model.entidades.Entidad;

public class Arquero extends Personaje {
    private static final String NOMBRE_RECURSO = "VIGOR";
    private int destreza;

    public Arquero(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad,
            String rutaImagen) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, rutaImagen, NOMBRE_RECURSO);
        this.destreza = 15;
    }

    @Override
    public void usarHabilidadEspecial(Entidad objetivo) {
        validarObjetivoEnemigo(objetivo);
        validarCostoHabilidad();
        objetivo.recibirDanio(ataqueBase + 15);
        consumirRecursos(getCostoHabilidadEspecial());
    }

    @Override
    public String getNombreHabilidadEspecial() {
        return "Disparo certero";
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
        return TipoPersonaje.ARQUERO;
    }

    @Override
    public void aumentarEstadisticasDeClase() {
        ataqueBase += 2;
        defensaBase += 1;
        destreza += 4;
        vidaMax += 10;
    }
}
