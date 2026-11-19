package model.entidades.personajes;
/**
 * Personaje que lanza hechizos y usa mana como recurso.
 * <p>Su habilidad especial es Bola de Fuego: hace el 130% del ataque
 * y le suma el intelecto, que crece al subir de nivel.</p>
 */
public class Mago extends Personaje {
    private static final String NOMBRE_RECURSO = "MANA";
    private int intelecto; // Daño extra de la Bola de Fuego. Empieza en 5 y sube 2 por nivel
    private final int costoHabilidad = 50;

    /**
    * @param nombre         Nombre del personaje
    * @param vidaMax        Vida maxima
    * @param recursoMax     Mana maxima
    * @param ataqueBase     Ataque inicial
    * @param defensaBase    Defensa inicial
    * @param velocidad      Energia que gana por tick de combate   
    * @param rutaImagen     Ruta de la imagen de que muestra en pantalla 
    */
    public Mago(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad,
            String rutaImagen) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, rutaImagen, NOMBRE_RECURSO);
        this.intelecto = 5;
    }

    @Override
    public int usarHabilidadEspecial() {
        return bolaDeFuego();
    }

    @Override
    public String getNombreHabilidad() {
        return "Bola de fuego";
    }

    /** Calcula el daño de Bola de fuego. */
    private int bolaDeFuego() {
        int danio = (int) Math.round(atacar() * 1.30) + intelecto;
        return danio;
    }

    @Override
    public void aumentarEstadisticasDeClase() {
        this.ataqueBase += 2;
        this.defensaBase += 1;
        this.intelecto += 2;
        this.vidaMax += 5;
    }

    @Override
    public String getNombreRecurso() {
        return NOMBRE_RECURSO;
    }

    @Override
    public boolean usaMana() {
        return true;
    }

    @Override
    public TipoPersonaje getTipo() {
        return TipoPersonaje.MAGO;
    }

    @Override
    public int getCostoHabilidad() {
        return costoHabilidad;
    }

}
