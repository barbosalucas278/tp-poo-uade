package model.entidades.personajes;

import java.util.EnumMap;
import java.util.Collections;
import java.util.Map;

import model.entidades.Entidad;
import model.entidades.enemigos.Enemigo;
import model.objetos.ItemEquipable;
import model.objetos.TipoObjeto;

public abstract class Personaje extends Entidad {
    protected int nivel;
    protected int experienciaActual;
    protected int experienciaSiguientenivel;
    protected String nombreRecurso;
    protected String rutaImagen;
    protected EnumMap<TipoObjeto, ItemEquipable> equipamiento;

    public Personaje(String nombre, int vidaMax, int recursoMax, int ataqueBase,
            int defensaBase, int velocidad,
            String rutaImagen, String nombreRecurso) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad);
        this.nivel = 1;
        this.experienciaActual = 0;
        this.experienciaSiguientenivel = 100;
        this.rutaImagen = rutaImagen;
        this.nombreRecurso = nombreRecurso;
        this.equipamiento = new EnumMap<>(TipoObjeto.class);
    }

    public void ganarExperiencia(int experiencia) {
        this.experienciaActual += experiencia;
        while (this.experienciaActual >= this.experienciaSiguientenivel) {
            this.subirDeNivel();
        }
    }

    public void subirDeNivel() {
        boolean teniaVidaLlena = this.vidaActual == this.vidaMax;
        this.nivel++;
        this.experienciaActual -= this.experienciaSiguientenivel;
        this.experienciaSiguientenivel *= 1.5;
        this.aumentarEstadisticasDeClase();
        if (teniaVidaLlena) {
            this.vidaActual = this.vidaMax;
        }
    }

    public abstract void aumentarEstadisticasDeClase();

    public int getNivel() {
        return nivel;
    }

    public int getExperienciaActual() {
        return experienciaActual;
    }

    public int getExperienciaSiguienteNivel() {
        return experienciaSiguientenivel;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void equiparItem(TipoObjeto tipo, ItemEquipable item) {
        if (tipo == null || item == null || item.getTipo() != tipo) {
            throw new IllegalArgumentException("El objeto no es compatible con el slot.");
        }
        ItemEquipable anterior = this.equipamiento.get(tipo);
        if (anterior != null) {
            anterior.removerBonus(this);
        }
        item.aplicarBonus(this);
        this.equipamiento.put(tipo, item);
    }

    public ItemEquipable desequiparItem(TipoObjeto tipo) {
        ItemEquipable anterior = this.equipamiento.remove(tipo);
        if (anterior != null) {
            anterior.removerBonus(this);
        }
        return anterior;
    }

    public Map<TipoObjeto, ItemEquipable> getEquipamiento() {
        return Collections.unmodifiableMap(equipamiento);
    }

    /** Asigna durante la carga sin volver a sumar bonificaciones ya persistidas. */
    public void restaurarEquipamiento(ItemEquipable item) {
        if (item == null || item.getTipo() == null || equipamiento.containsKey(item.getTipo())) {
            throw new IllegalArgumentException("El equipamiento restaurado no es válido.");
        }
        equipamiento.put(item.getTipo(), item);
    }

    public void restaurarProgreso(int nivel, int xpActual, int xpSiguiente) {
        this.nivel = nivel;
        this.experienciaActual = xpActual;
        this.experienciaSiguientenivel = xpSiguiente;
    }

    public abstract void usarHabilidadEspecial(Entidad objetivo);

    public abstract String getNombreHabilidadEspecial();

    public abstract int getCostoHabilidadEspecial();

    public abstract void validarObjetivoHabilidad(Entidad objetivo);

    public void validarHabilidadDisponible() {
        validarCostoHabilidad();
    }

    protected void validarObjetivoVivo(Entidad objetivo) {
        if (objetivo == null || !objetivo.estaVivo()) {
            throw new IllegalArgumentException("El objetivo debe estar vivo.");
        }
    }

    protected void validarCostoHabilidad() {
        if (recursoActual < getCostoHabilidadEspecial()) {
            throw new IllegalArgumentException("No hay recurso suficiente para usar la habilidad.");
        }
    }

    protected void validarObjetivoEnemigo(Entidad objetivo) {
        validarObjetivoVivo(objetivo);
        if (!(objetivo instanceof Enemigo)) {
            throw new IllegalArgumentException("La habilidad requiere un enemigo como objetivo.");
        }
    }

    public abstract String getNombreRecurso();

    public abstract TipoPersonaje getTipo();


}
