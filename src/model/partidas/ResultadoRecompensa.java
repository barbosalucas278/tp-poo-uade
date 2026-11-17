package model.partidas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.objetos.Item;

// Resultado inmutable de aplicar las recompensas de una victoria.
public final class ResultadoRecompensa {
    private final int experienciaOtorgada;
    private final List<Item> objetosObtenidos;
    private final List<Item> objetosIncorporados;
    private final List<Item> objetosDescartados;

    public ResultadoRecompensa(int experienciaOtorgada, List<Item> objetosObtenidos,
            List<Item> objetosIncorporados, List<Item> objetosDescartados) {
        this.experienciaOtorgada = experienciaOtorgada;
        this.objetosObtenidos = copia(objetosObtenidos);
        this.objetosIncorporados = copia(objetosIncorporados);
        this.objetosDescartados = copia(objetosDescartados);
    }

    public int getExperienciaOtorgada() {
        return experienciaOtorgada;
    }

    public List<Item> getObjetosObtenidos() {
        return objetosObtenidos;
    }

    public List<Item> getObjetosIncorporados() {
        return objetosIncorporados;
    }

    public List<Item> getObjetosDescartados() {
        return objetosDescartados;
    }

    private static List<Item> copia(List<Item> objetos) {
        return Collections.unmodifiableList(new ArrayList<>(objetos));
    }
}
