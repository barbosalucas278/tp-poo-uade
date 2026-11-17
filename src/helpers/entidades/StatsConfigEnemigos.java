package helpers.entidades;

/**
 * Clase de configuración/DTO que almacena las estadísticas base de un enemigo.
 * <p>Orden estándar de los atributos:</p>
 * <ol>
 *   <li><b>nombre</b>: Identificador o nombre del enemigo.</li>
 *   <li><b>vida</b>: Puntos de salud máximos (HP).</li>
 *   <li><b>recursoMax</b>: Cantidad máxima de maná o energía.</li>
 *   <li><b>ataque</b>: Daño base físico/mágico.</li>
 *   <li><b>defensa</b>: Reducción de daño recibido.</li>
 *   <li><b>velocidad</b>: Determina el orden de turno.</li>
 *   <li><b>experiencia</b>: XP otorgada al ser derrotado.</li>
 * </ol>
 */
public class StatsConfigEnemigos {
    private String nombre;
    private int vida;
    private int recursoMax;
    private int ataque;
    private int defensa;
    private int velocidad;
    private int experiencia;


    /**
     * Construye una nueva configuración de estadísticas para un enemigo.
     *
     * @param nombre      1. Nombre del enemigo
     * @param vida        2. Puntos de vida iniciales/máximos
     * @param recursoMax  3. Recurso máximo (maná/energía)
     * @param ataque      4. Valor de ataque
     * @param defensa     5. Valor de defensa
     * @param velocidad   6. Velocidad de acción/turno
     * @param experiencia 7. Experiencia que otorga al morir
     */

    public StatsConfigEnemigos(String nombre, int vida, int recursoMax, int ataque, int defensa, int velocidad, int experiencia) {
        this.nombre = nombre;
        this.vida = vida;
        this.recursoMax = recursoMax;
        this.ataque = ataque;
        this.defensa = defensa;
        this.velocidad = velocidad;
        this.experiencia = experiencia;
    }

    
    public String getNombre() {
        return nombre;
    }

    public int getVida() {
        return vida;
    }

    public int getRecursoMax() {
        return recursoMax;
    }

    public int getAtaque() {
        return ataque;
    }

    public int getDefensa() {
        return defensa;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public int getExperiencia() {
        return experiencia;
    }
    
}
