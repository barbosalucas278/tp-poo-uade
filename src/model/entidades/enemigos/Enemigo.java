package model.entidades.enemigos;

import java.util.List;

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
