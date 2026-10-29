package helpers.entidades;

import java.util.List;

import model.entidades.enemigos.Enemigo;

public class CalculadoraDeExperiencia {
    public static int CalcularExperiencia(List<Enemigo> listaDeEnemigos){
        int experienciaAcumulada = 0;
        for (Enemigo enemigo : listaDeEnemigos) {
            experienciaAcumulada += enemigo.getExperienciaOtorgada();
        }
        return experienciaAcumulada;
    }
}
