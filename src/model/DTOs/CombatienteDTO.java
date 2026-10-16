package model.DTOs;

/** Datos de solo lectura necesarios para representar un combatiente. */
public final class CombatienteDTO {
    private final String nombre;
    private final String clase;
    private final String rutaImagen;
    private final int vidaActual;
    private final int vidaMax;
    private final String nombreRecurso;
    private final int recursoActual;
    private final int recursoMax;
    private final int energiaActual;

    public CombatienteDTO(String nombre, String clase, String rutaImagen,
            int vidaActual, int vidaMax, String nombreRecurso,
            int recursoActual, int recursoMax, int energiaActual) {
        this.nombre = nombre;
        this.clase = clase;
        this.rutaImagen = rutaImagen;
        this.vidaActual = vidaActual;
        this.vidaMax = vidaMax;
        this.nombreRecurso = nombreRecurso;
        this.recursoActual = recursoActual;
        this.recursoMax = recursoMax;
        this.energiaActual = energiaActual;
    }

    public String getNombre() {
        return nombre;
    }

    public String getClase() {
        return clase;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public int getVidaActual() {
        return vidaActual;
    }

    public int getVidaMax() {
        return vidaMax;
    }

    public String getNombreRecurso() {
        return nombreRecurso;
    }

    public int getRecursoActual() {
        return recursoActual;
    }

    public int getRecursoMax() {
        return recursoMax;
    }

    public int getEnergiaActual() {
        return energiaActual;
    }

    public int getEnergiaMax() {
        return 100;
    }
}
