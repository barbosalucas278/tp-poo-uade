package model.entidades.personajes;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import model.entidades.Entidad;
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
        this.equipamiento.put(tipo, item);
    }

    public void desequiparItem(TipoObjeto tipo) {
        this.equipamiento.remove(tipo);
    }

    /** @return el objeto equipado en ese lugar (ARMA o ARMADURA), o null si no tiene nada */
    public ItemEquipable getItemEquipado(TipoObjeto tipo) {
        return this.equipamiento.get(tipo);
    }

    /** @return una copia de la lista de objetos que tiene equipados */
    public List<ItemEquipable> getItemsEquipados() {
        return new ArrayList<>(this.equipamiento.values());
    }

    public void restaurarProgreso(int nivel, int xpActual, int xpSiguiente) {
        this.nivel = nivel;
        this.experienciaActual = xpActual;
        this.experienciaSiguientenivel = xpSiguiente;
    }

    public abstract int usarHabilidadEspecial();
        // Indica si la habilidad especial ayuda al grupo en lugar de dañar a un enemigo.
        // Por defecto es false; el Curador la sobrescribe y devuelve true.
        public boolean esHabilidadDeApoyo() {
         return false;
    }

    public abstract String getNombreRecurso();

    public abstract TipoPersonaje getTipo();


}
