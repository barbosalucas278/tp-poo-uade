package model.entidades.enemigos;

import java.util.List;

import model.entidades.Entidad;
import model.entidades.personajes.Personaje;

public abstract class Enemigo extends Entidad {
    // Recurso que recupera el enemigo cada vez que hace un ataque normal
    private static final int RECURSO_POR_ATAQUE = 10;

    protected int nivel;
    protected int experienciaOtorgada;
    protected DropStats dropStats;

    public Enemigo(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad,
            int nivel, int experienciaOtorgada, DropStats drop) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad);
        this.nivel = nivel;
        this.experienciaOtorgada = experienciaOtorgada;
        this.dropStats = drop;
    }

    public int getNivel() {
        return nivel;
    }

    public abstract TipoEnemigo getTipo();

    // Cada tipo de enemigo define su propia habilidad especial.
    // Recibe el personaje elegido y la lista de vivos (por si la habilidad pega a todos).
    public abstract void usarHabilidadEspecial(Personaje objetivo, List<Personaje> personajesVivos);

    // Recurso que cuesta usar la habilidad; cada enemigo define el suyo
    protected abstract int getCostoHabilidad();

    // Probabilidad (en %) de usar la habilidad cuando le alcanza el recurso
    protected int getProbabilidadHabilidad() {
        return 30;
    }

    // Decide en su turno si usa la habilidad: necesita recurso suficiente y que salga el sorteo
    public boolean quiereUsarHabilidad() {
        return getRecursoActual() >= getCostoHabilidad()
                && Math.random() * 100 < getProbabilidadHabilidad();
    }

    // Se ejecuta al empezar su turno. Por defecto no hace nada; el Jefe lo usa para su fase de furia
    public void alIniciarTurno() {
    }

    // Después de un ataque normal recupera un poco de recurso para poder usar la habilidad más adelante
    public void recuperarRecursoPorAtaque() {
        generarRecursos(RECURSO_POR_ATAQUE);
    }

    // Elige al azar a qué personaje vivo atacar
    public Personaje seleccionarObjetivo(List<Personaje> party) {
        int index = (int) (Math.random() * party.size());
        return party.get(index);
    }

    public void atacar(Personaje objetivo) {
        objetivo.recibirDanio(this.ataqueBase);
    }

    public int getExperienciaOtorgada() {
        return experienciaOtorgada;
    }

    public DropStats GetProbabilidadDeDrop() {
        return this.dropStats;
    }
}