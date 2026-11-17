package model.entidades.personajes;


/**
 * Personake de combate cuerpo a cuerpo que usa furia como recurso.
 * <p>Su habilidad especial es Embestida: Hace el ataque normal y le suma
 * la fuerza, que cree al subir de nibel. Es la clase que mas vida gana por nivel</p>
 */
public class Guerrero extends Personaje {
    private static final String NOMBRE_RECURSO = "FURIA";
    private static final int COSTO_EMBESTIDA = 30; // Furia que gasta cada vez que usa Embestida
    private int fuerza;  // Daño extra que suma en la Embestida; sube al subir de nivel

    public int getFuerza() {
        return fuerza;
    }

    /**
     * @param nombre         Nombre del personaje
     * @param vidaMax        Vida maxima
     * @param recursoMax     Mana maxima
     * @param ataqueBase     Ataque inicial
     * @param defensaBase    Defensa inicial
     * @param velocidad      Energia que gana por tick de combate   
     * @param rutaImagen     Ruta de la imagen de que muestra en pantalla
     */
    public Guerrero(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad,
            String rutaImagen) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, rutaImagen, NOMBRE_RECURSO);
        this.fuerza = 15;
    }

    
    @Override
    public int usarHabilidadEspecial() {
        return embestida(); 
    }

    /**
     * Carga contra el enemigo y descuenta la furia
     * 
     * @return Daño a aplicar al enemigo
     * @throws IllegalStateException Si no hay furia suficiente no se pierde el turno
     */
    private int embestida() {
        if (getRecursoActual() < COSTO_EMBESTIDA) { // Si no tiene furia suficiente, no puede usarla y no pierde el turno
            throw new IllegalStateException("No hay suficiente furia para usar Embestida.");
        }
        int danio = atacar() + fuerza;
        consumirRecursos(COSTO_EMBESTIDA);
        return danio;
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
        this.ataqueBase += 2;
        this.defensaBase += 1;
        this.fuerza += 4;
        this.vidaMax += 20;
    }
}