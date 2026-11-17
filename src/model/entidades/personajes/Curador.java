package model.entidades.personajes;

public class Curador extends Personaje {
    private static final String NOMBRE_RECURSO = "FE";
    // Fe que gasta cada vez que usa Sanación
    private static final int COSTO_SANACION = 50;
    // Poder de curación; sube al subir de nivel
    private int fe;

    public Curador(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad,
            String rutaImagen) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, rutaImagen, NOMBRE_RECURSO);
        this.fe = 15;
    }

    // Indica que su habilidad ayuda al grupo en vez de dañar a un enemigo
    @Override
    public boolean esHabilidadDeApoyo() {
        return true;
    }

    // Habilidad especial del Curador: devuelve cuánta vida cura Sanación
    @Override
    public int usarHabilidadEspecial() {
        return sanacion();
    }

    // Calcula la curación (el doble de su fe); el motor se la aplica a cada aliado vivo
    private int sanacion() {
        // Si no tiene fe suficiente, no puede usarla y no pierde el turno
        if (getRecursoActual() < COSTO_SANACION) {
            throw new IllegalStateException("No hay suficiente fe para usar Sanación.");
        }
        int curacion = fe * 2;
        consumirRecursos(COSTO_SANACION);
        return curacion;
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