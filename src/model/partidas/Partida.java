package model.partidas;

import java.util.ArrayList;
import java.util.List;
import model.entidades.personajes.Personaje;
import model.objetos.Inventario;

/**
 * Partida
 */
public class Partida {

    private String nombre;
    private List<Personaje> party;
    private Inventario inventarioGlobal;
    private List<Mapa> mapas;

    public Partida(String nombre, List<Personaje> party, Mapa mapa) {
        this.nombre = nombre;
        this.party = party;
        this.inventarioGlobal = new Inventario();
        this.mapas = new ArrayList<Mapa>();
        this.mapas.add(mapa);
    }

    public void agregarPersonaje(Personaje personaje) {
        party.add(personaje);
    }

    public void removerPersonaje(Personaje personaje) {
        party.remove(personaje);
    }

    public Inventario getInventarioGlobal() {
        return inventarioGlobal;
    }

    public Mapa getMapaActual() {
        Mapa mapaActual = null;
        for (Mapa nivel : mapas) {
            if (!nivel.isCompletado()) {
                mapaActual = nivel;
                break;
            }
        }
        return mapaActual;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Personaje> getParty() {
        return party;
    }

}
