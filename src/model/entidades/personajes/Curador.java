package model.entidades.personajes;

/**
 * Personaje de apoyo que usa fe como recurso
 * <p>Su habilidad especial es Sanacion: en vez de dañar a un enemigo, 
 * cura a todos los aliados vivos el doble de su poder de curacion que 
 * crece al subir de nivel</p>
 */
public class Curador extends Personaje {
    private static final String NOMBRE_RECURSO = "FE";
    private final int costoHabilidad = 50;
    private int fe; // Poder de curación; sube al subir de nivel

    /**
     * @param nombre         Nombre del personaje
     * @param vidaMax        Vida maxima
     * @param recursoMax     Mana maxima
     * @param ataqueBase     Ataque inicial
     * @param defensaBase    Defensa inicial
     * @param velocidad      Energia que gana por tick de combate   
     * @param rutaImagen     Ruta de la imagen de que muestra en pantalla
     */
    public Curador(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad,
            String rutaImagen) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, rutaImagen, NOMBRE_RECURSO);
        this.fe = 15;
    }

    /** Sanacion cura al grupo, asi que no hace falta elegir un enemigo */
    @Override
    public boolean esHabilidadDeApoyo() {
        return true;
    }

    @Override
    public int usarHabilidadEspecial() {
        return sanacion();
    }

    /** Calcula la curación; el motor la aplica a cada aliado vivo. */
    private int sanacion() {
        return fe * 2;
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