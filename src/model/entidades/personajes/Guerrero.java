package model.entidades.personajes;

public class Guerrero extends Personaje {
    private static final String NOMBRE_RECURSO = "FURIA";
    // Furia que gasta cada vez que usa Embestida
    private static final int COSTO_EMBESTIDA = 30;
    // Daño extra que suma en la Embestida; sube al subir de nivel
    private int fuerza;

    public int getFuerza() {
        return fuerza;
    }

    public Guerrero(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad,
            String rutaImagen) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, rutaImagen, NOMBRE_RECURSO);
        this.fuerza = 15;
    }

    // Habilidad especial del Guerrero: devuelve el daño que hace Embestida
    @Override
    public int usarHabilidadEspecial() {
        return embestida();
    }

    // Carga contra el enemigo: suma su fuerza al ataque normal
    private int embestida() {
        // Si no tiene furia suficiente, no puede usarla y no pierde el turno
        if (getRecursoActual() < COSTO_EMBESTIDA) {
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