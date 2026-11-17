package model.DTOs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Datos preparados por el controller para mostrar el resultado de una victoria.
public final class ResultadoRecompensaDTO {
    private final int experienciaOtorgada;
    private final List<String> objetosObtenidos;
    private final List<String> objetosIncorporados;
    private final List<String> objetosDescartados;

    public ResultadoRecompensaDTO(int experienciaOtorgada, List<String> objetosObtenidos,
            List<String> objetosIncorporados, List<String> objetosDescartados) {
        this.experienciaOtorgada = experienciaOtorgada;
        this.objetosObtenidos = copia(objetosObtenidos);
        this.objetosIncorporados = copia(objetosIncorporados);
        this.objetosDescartados = copia(objetosDescartados);
    }

    public int getExperienciaOtorgada() {
        return experienciaOtorgada;
    }

    public List<String> getObjetosObtenidos() {
        return objetosObtenidos;
    }

    public List<String> getObjetosIncorporados() {
        return objetosIncorporados;
    }

    public List<String> getObjetosDescartados() {
        return objetosDescartados;
    }

    private static List<String> copia(List<String> valores) {
        return Collections.unmodifiableList(new ArrayList<>(valores));
    }
}
