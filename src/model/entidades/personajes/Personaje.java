package model.entidades.personajes;

import java.util.EnumMap;

import model.entidades.Entidad;
import model.objetos.ItemEquipable;
import model.objetos.TipoObjeto;

public abstract class Personaje extends Entidad {
    protected int nivel;
    protected int experienciaActual;
    protected int experienciaSiguientenivel;
    protected String rutaImagen;
    protected EnumMap<TipoObjeto, ItemEquipable> equipamiento;

    public Personaje(String nombre, int vidaMax, int recursoMax, int ataqueBase,
            int defensaBase, int velocidad,
            String rutaImagen) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad);
        this.nivel = 1;
        this.experienciaActual = 0;
        this.experienciaSiguientenivel = 100;
        this.rutaImagen = rutaImagen;
        this.equipamiento = new EnumMap<>(TipoObjeto.class);
    }

    public void ganarExperiencia(int experiencia) {
        this.experienciaActual += experiencia;
        if (this.experienciaActual >= this.experienciaSiguientenivel) {
            this.nivel++;
            this.experienciaActual -= this.experienciaSiguientenivel;
            this.experienciaSiguientenivel *= 1.5;
        }
    }

    public void equiparItem(TipoObjeto tipo, ItemEquipable item) {
        this.equipamiento.put(tipo, item);
    }

    public void desequiparItem(TipoObjeto tipo) {
        this.equipamiento.remove(tipo);
    }

    public abstract void usarHabilidadEspecial();
}
