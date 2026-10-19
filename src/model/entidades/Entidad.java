package model.entidades;

/**
 * Entidad
 */
public abstract class Entidad {
    protected String nombre;
    protected int vidaMax;
    protected int vidaActual;
    protected int recursoMax;
    protected int recursoActual;
    protected int ataqueBase;
    protected int defensaBase;
    protected int energiaActual;
    protected int velocidad;
    protected boolean estaDefendiendose = false;

    public Entidad(String nombre, int vidaMax, int recursoMax, int ataqueBase,
            int defensaBase, int velocidad) {
        this.nombre = nombre;
        this.vidaMax = vidaMax;
        this.vidaActual = vidaMax;
        this.recursoMax = recursoMax;
        this.recursoActual = recursoMax;
        this.ataqueBase = ataqueBase;
        this.defensaBase = defensaBase;
        this.energiaActual = 0;
        this.velocidad = velocidad;
    }

    public String getNombre() {
        return nombre;
    }

    public int getVidaMax() {
        return vidaMax;
    }

    public int getVidaActual() {
        return vidaActual;
    }

    public int getRecursoMax() {
        return recursoMax;
    }

    public int getRecursoActual() {
        return recursoActual;
    }

    public int getEnergiaActual() {
        return energiaActual;
    }

    public int getAtaqueBase() {
        return ataqueBase;
    }

    public void setAtaqueBase(int ataqueBase) {
        this.ataqueBase = ataqueBase;
    }

    public int getDefensaBase() {
        return defensaBase;
    }

    public void setDefensaBase(int defensaBase) {
        this.defensaBase = defensaBase;
    }

    public int atacar(){
        return this.ataqueBase;
    }

    public void recibirDanio(int danio) {
        int danioRecibido = danio - this.defensaBase;
        if (danioRecibido < 0) {
            danioRecibido = 0;
        }
        this.vidaActual -= danioRecibido;
        if (this.vidaActual < 0) {
            this.vidaActual = 0;
        }
    }

    public void recibirSanacion(int cantidad) {
        this.vidaActual += cantidad;
        if (this.vidaActual > this.vidaMax) {
            this.vidaActual = this.vidaMax;
        }
    }

    public void acumularEnergia(int cantidad) {
        this.energiaActual += cantidad;
        if (this.energiaActual > 100) {
            this.energiaActual = 100;
        }
    }

    public void reiniciarEnergia() {
        this.energiaActual = 0;
    }

    public void consumirRecursos(int cantidad) {
        this.recursoActual -= cantidad;
        if (this.recursoActual < 0) {
            this.recursoActual = 0;
        }
    }

    public void generarRecursos(int cantidad) {
        this.recursoActual += cantidad;
        if (this.recursoActual > this.recursoMax) {
            this.recursoActual = this.recursoMax;
        }
    }

    public boolean listaParaLaAccion() {
        return this.energiaActual >= 100;
    }

    public boolean estaVivo() {
        return this.vidaActual > 0;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }
}
