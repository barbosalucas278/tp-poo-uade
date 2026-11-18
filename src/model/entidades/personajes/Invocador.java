package model.entidades.personajes;


/**
 * Personaje que invoca animales y usa esencia como recurso.
 * <p>Su habilidad especial es Alianza Animal: Hace el 110% del ataque
 * y le suma el espiritu, que crece al subir de nivel</p>.
 */
public class Invocador extends Personaje {
    private static final String NOMBRE_RECURSO = "ESENCIA";
    private final int costoHabilidad = 60;
    private int espiritu; // Poder del animal invocado; sube al subir de nivel

    /**
     * @param nombre            Nombre del personaje
     * @param vidaMax           Vida maxima
     * @param recursoMax        Esencia maxima
     * @param ataqueBase        Ataque inicial
     * @param defensaBase       Defensa inicial
     * @param velocidad         Energia que gana por tick de combate
     * @param rutaImagen        Ruta de la imagen que se muestra en pantalla
     */
    public Invocador(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad,
            String rutaImagen) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, rutaImagen, NOMBRE_RECURSO);
        this.espiritu = 15;
    }

    
    @Override
    public int usarHabilidadEspecial() {
        return alianzaAnimal(); // Habilidad especial del Invocador: devuelve el daño que hace Alianza Animal
    }

   /** Calcula el daño del animal invocado. */
    private int alianzaAnimal() {
        return (int) Math.round(atacar() * 1.10) + espiritu;  // El animal ataca con el 110 % más el espíritu del invocador
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
        return TipoPersonaje.INVOCADOR;
    }

    @Override
    public void aumentarEstadisticasDeClase() {
        ataqueBase += 2;
        defensaBase += 1;
        espiritu += 4;
        vidaMax += 5;
    }
}