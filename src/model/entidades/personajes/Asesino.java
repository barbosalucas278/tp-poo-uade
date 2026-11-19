package model.entidades.personajes;

/**
 * Personaje ágil que usa vigor como recurso.
 * <p>Su habilidad especial es Daga Mortal: hace el 140% del ataque y puede
 * ser crítica (daño doble). La probabilidad de crítico es la destreza,
 * que crece al subir de nivel.</p>
 */
public class Asesino extends Personaje {
    private static final String NOMBRE_RECURSO = "VIGOR";
    private final int costoHabilidad = 40;
    private int destreza; // Probabilidad de hacer golpe crítico

    /**
     * @param nombre         Nombre del personaje
     * @param vidaMax        Vida maxima
     * @param recursoMax     Mana maxima
     * @param ataqueBase     Ataque inicial
     * @param defensaBase    Defensa inicial
     * @param velocidad      Energia que gana por tick de combate   
     * @param rutaImagen     Ruta de la imagen de que muestra en pantalla
     */
    public Asesino(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad,
            String rutaImagen) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, rutaImagen, NOMBRE_RECURSO);
        this.destreza = 15;
    }

    // Habilidad especial del Asesino: devuelve el daño que hace Daga Mortal
    @Override
    public int usarHabilidadEspecial() {
        return dagaMortal();
    }

    @Override
    public String getNombreHabilidad() {
        return "Daga Mortal";
    }

    /** Ataca con la daga y sortea si es crítico. */
    private int dagaMortal() {
        int danio = (int) Math.round(atacar() * 1.40);
        
        if (Math.random() * 100 < destreza) { // Sortea un número de 0 a 100: si es menor que la destreza, es crítico
            danio *= 2;
        }
        return danio;
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
        return TipoPersonaje.ASESINO;
    }

    @Override
    public void aumentarEstadisticasDeClase() {
        this.ataqueBase += 2;
        this.defensaBase += 1;
        this.destreza += 4;
        this.vidaMax += 10;
    }
}
