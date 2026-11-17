package model.entidades.enemigos;

import java.util.ArrayList;
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
        return objetivosValidos(party).get(0);
    }

    public void ejecutarAccion(List<Personaje> party) {
        atacar(seleccionarObjetivo(party));
    }

    public void atacar(Personaje objetivo) {
        if (objetivo == null || !objetivo.estaVivo()) {
            throw new IllegalArgumentException("El enemigo sólo puede atacar a un personaje vivo.");
        }
        objetivo.recibirDanio(this.ataqueBase);
    }

    protected List<Personaje> objetivosValidos(List<Personaje> party) {
        if (party == null) {
            throw new IllegalArgumentException("La lista de objetivos no puede ser nula.");
        }
        List<Personaje> vivos = new ArrayList<>();
        for (Personaje personaje : party) {
            if (personaje != null && personaje.estaVivo()) {
                vivos.add(personaje);
            }
        }
        if (vivos.isEmpty()) {
            throw new IllegalStateException("No quedan personajes vivos para atacar.");
        }
        return vivos;
    }

    public int getExperienciaOtorgada() {
        return experienciaOtorgada;
    }

    public DropStats GetProbabilidadDeDrop() {
        return this.dropStats;
    }
}
