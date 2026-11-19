package model.objetos;

import java.util.HashMap;
import java.util.Map;

public class ItemsConsumiblesFactory {
    private static final Map<TipoItemConsumible, CreadorConsumible> CREADORES = cargarCreadores();
    private static final Map<TipoRareza, Integer> CANTIDADES_POR_RAREZA = cargarCantidadesPorRareza();

    private static Map<TipoItemConsumible, CreadorConsumible> cargarCreadores() {
        Map<TipoItemConsumible, CreadorConsumible> creadores = new HashMap<>();
        creadores.put(TipoItemConsumible.POCION_MANA, new CreadorPocionMana());
        creadores.put(TipoItemConsumible.POCION_VIDA, new CreadorPocionVida());
        return creadores;
    }

    private static Map<TipoRareza, Integer> cargarCantidadesPorRareza() {
        Map<TipoRareza, Integer> cantidades = new HashMap<>();
        cantidades.put(TipoRareza.COMUN, 25);
        cantidades.put(TipoRareza.RARO, 50);
        cantidades.put(TipoRareza.EPICO, 100);
        return cantidades;
    }

    public static ItemConsumible CrearItemConsumible(TipoRareza rareza, TipoItemConsumible tipo) {
        int cantidadRestaurada = cantidadDeRecursoSegunRareza(rareza);
        CreadorConsumible creador = CREADORES.get(tipo);
        if (creador == null) {
            throw new IllegalArgumentException("Tipo de consumible inválido");
        }

        return creador.crear(cantidadRestaurada, rareza);
    }

    public static int cantidadDeRecursoSegunRareza(TipoRareza rareza) {
        Integer cantidad = CANTIDADES_POR_RAREZA.get(rareza);
        if (cantidad == null) {
            throw new IllegalArgumentException("Rareza de consumible inválida");
        }
        return cantidad;
    }
}
