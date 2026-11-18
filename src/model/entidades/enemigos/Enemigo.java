package model.entidades.enemigos;

import java.util.List;

import model.entidades.Entidad;
import model.entidades.personajes.Personaje;

public abstract class Enemigo extends Entidad {
    // Recurso que recupera cada vez que hace un ataque normal
    private static final int RECUPERACION_POR_ATAQUE = 10;

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

    // Habilidad especial del enemigo: cada clase devuelve el daño que hace la suya
    public abstract int usarHabilidadEspecial();

    // Recurso que cuesta usar la habilidad (lo usa puedeUsarHabilidad para no tirar excepción)
    protected abstract int getCostoHabilidad();

    // Indica si su habilidad golpea a todo el grupo en vez de a un solo personaje.
    // Por defecto es false; el Jefe la sobrescribe y devuelve true.
    public boolean habilidadAfectaATodos() {
        return false;
    }

    // Indica si su habilidad ignora la defensa del personaje.
    // Por defecto es false; el Estandar la sobrescribe y devuelve true.
    public boolean habilidadIgnoraDefensa() {
        return false;
    }

    // Probabilidad (en %) de usar la habilidad cuando le alcanza el recurso
    protected int getProbabilidadHabilidad() {
        return 30;
    }

    // Indica si tiene recurso suficiente para usar la habilidad
    public boolean puedeUsarHabilidad() {
        return getRecursoActual() >= getCostoHabilidad();
    }

    // Decide si en este turno usa la habilidad: necesita recurso suficiente y que salga el sorteo
    public boolean quiereUsarHabilidad() {
        return puedeUsarHabilidad() && Math.random() * 100 < getProbabilidadHabilidad();
    }

    // Elige al azar a qué personaje vivo va a atacar
    public Personaje seleccionarObjetivo(List<Personaje> personajesVivos) {
        int indice = (int) (Math.random() * personajesVivos.size());
        return personajesVivos.get(indice);
    }

    // Después de un ataque normal recupera un poco de recurso para poder usar la habilidad más adelante
    public void recuperarRecursoPorAtaque() {
        generarRecursos(RECUPERACION_POR_ATAQUE);
    }

    // Se ejecuta al empezar su turno. Por defecto no hace nada; el Jefe lo usa para su fase de furia
    public void alIniciarTurno() {
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