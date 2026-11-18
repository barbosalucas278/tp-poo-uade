package model.entidades.personajes;

/**
 * Personaje de ataque a distancia que usa concentración como recurso.
 * <p>Su habilidad especial es Disparo Certero: hace el 150% del ataque
 * y le suma la puntería, que crece al subir de nivel.</p>
 */
public class Arquero extends Personaje {
    private static final String NOMBRE_RECURSO = "CONCENTRACION";
    private final int costoHabilidad = 60;
    private int punteria;

    /**
     * @param nombre         Nombre del personaje
     * @param vidaMax        Vida maxima
     * @param recursoMax     Mana maxima
     * @param ataqueBase     Ataque inicial
     * @param defensaBase    Defensa inicial
     * @param velocidad      Energia que gana por tick de combate   
     * @param rutaImagen     Ruta de la imagen de que muestra en pantalla
     */
    public Arquero(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad,
            String rutaImagen) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, rutaImagen, NOMBRE_RECURSO);
        this.punteria = 15;
    }

    @Override
    public int usarHabilidadEspecial() {
        return disparoCertero();
    }

    /** Calcula el daño de Disparo Certero. */
    private int disparoCertero() {
        return (int) Math.round(atacar() * 1.50) + punteria;
    }

    @Override
    public int getCostoHabilidad() {
        return costoHabilidad;
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
