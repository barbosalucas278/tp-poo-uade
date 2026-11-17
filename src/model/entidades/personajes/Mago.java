package model.entidades.personajes;
/**
 * Personaje que lanza hechizos y usa mana como recurso.
 * <p>Su habilidad especial es Bola de Fuego: hace el 130% del ataque
 * y le suma el intelecto, que crece al subir de nivel.</p>
 */
public class Mago extends Personaje {
    private static final String NOMBRE_RECURSO = "MANA";
    private static final int COSTO_BOLA_DE_FUEGO = 50; // Mana que gasta para usar la bola de Fuego
    private int intelecto; // Daño extra de la Bola de Fuego. Empieza en 5 y sube 2 por nivel

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

    /**
     * Calcula el daño de Bola de fuego y descuenta el mana
     * 
     * @return Daño a aplicar al enemigo+
     * @throws IllegalStateException si no hay maná suficiente el turno no se pierde
     */
    private int bolaDeFuego() {
        if (getRecursoActual() < COSTO_BOLA_DE_FUEGO) {
            throw new IllegalStateException("No hay suficiente maná para lanzar Bola de fuego.");
        }

        int danio = (int) Math.round(atacar() * 1.30) + intelecto;
        consumirRecursos(COSTO_BOLA_DE_FUEGO);
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
    public TipoPersonaje getTipo() {
        return TipoPersonaje.MAGO;
    }

}
