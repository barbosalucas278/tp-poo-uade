package model.entidades.personajes;
/**
 * MAGO
 * 
 */
public class Mago extends Personaje {
    private static final String NOMBRE_RECURSO = "MANA";
    private static final int COSTO_BOLA_DE_FUEGO = 50;
    private int intelecto;

    /**
    * 
    * @param nombre
    * @param vidaMax
    * @param recursoMax
    * @param ataqueBase
    * @param defensaBase
    * @param velocidad
    * @param rutaImagen
    */
    public Mago(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad,
            String rutaImagen) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, rutaImagen, NOMBRE_RECURSO);
        this.intelecto = 15;
    }

    @Override
    public int usarHabilidadEspecial() {
        return bolaDeFuego();
    }

    private int bolaDeFuego() {
        if (getRecursoActual() < COSTO_BOLA_DE_FUEGO) {
            throw new IllegalStateException("No hay suficiente maná para lanzar Bola de fuego.");
        }

        int danio = (int) Math.round(atacar() * 1.30);
        consumirRecursos(COSTO_BOLA_DE_FUEGO);
        return danio;
    }

    @Override
    public void aumentarEstadisticasDeClase() {
        this.ataqueBase += 2;
        this.defensaBase += 1;
        this.intelecto += 4;
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

    public int getIntelecto() {
        return intelecto;
    }
}
