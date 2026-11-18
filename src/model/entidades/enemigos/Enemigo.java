package model.entidades.enemigos;

import model.entidades.Entidad;
import model.entidades.personajes.Personaje;

public abstract class Enemigo extends Entidad {
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

    // Nombre de la habilidad especial (para mostrarlo en pantalla)
    public abstract String getNombreHabilidad();

    // Recurso que cuesta usar la habilidad
    public abstract int getCostoHabilidad();

    // Cuánto multiplica su ataque la habilidad (1.5 = 150 % del ataque)
    public abstract double getMultiplicadorHabilidad();

    // Si la habilidad golpea a todo el grupo; por defecto golpea a uno solo
    public boolean habilidadAfectaATodos() {
        return false;
    }

    // Si la habilidad ignora la defensa del personaje; por defecto no la ignora
    public boolean habilidadIgnoraDefensa() {
        return false;
    }

    // Probabilidad (en %) de usar la habilidad cuando le alcanza el recurso
    public int getProbabilidadHabilidad() {
        return 30;
    }

    // Indica si tiene recurso suficiente para usar la habilidad
    public boolean puedeUsarHabilidad() {
        return getRecursoActual() >= getCostoHabilidad();
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