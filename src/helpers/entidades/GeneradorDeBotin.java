package helpers.entidades;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.entidades.enemigos.DropStats;
import model.entidades.enemigos.Enemigo;
import model.entidades.enemigos.TipoEnemigo;
import model.objetos.Item;
import model.objetos.ItemsConsumiblesFactory;
import model.objetos.TipoItemConsumible;
import model.objetos.TipoRareza;

public class GeneradorDeBotin {
    private static final Random RANDOM = new Random();

    public static List<Item> SortearBotin(List<Enemigo> enemigosDerrotados) {
        List<Item> recompensas = new ArrayList<>();
        for (Enemigo enemigo : enemigosDerrotados) {
            DropStats drop = enemigo.GetProbabilidadDeDrop();
            int resultado = RANDOM.nextInt(100);

            if (resultado < drop.getProbabilidadDeDrop()) {
                TipoRareza rareza = sortearRareza(drop.getProbabilidadItemComun(), drop.getProbabilidadItemRaro());
                TipoItemConsumible tipo = sortearTipo();
                recompensas.add(ItemsConsumiblesFactory.CrearItemConsumible(rareza, tipo));
            }
        }
        return recompensas;
    }

    private static TipoRareza sortearRareza(int porcentajeComun, int porcentajeRaro) {
        int resultado = RANDOM.nextInt(100);

        if (resultado < porcentajeComun) {
            return TipoRareza.COMUN;
        }
        if (resultado < porcentajeComun + porcentajeRaro) {
            return TipoRareza.RARO;
        }
        return TipoRareza.EPICO;
    }

    private static TipoItemConsumible sortearTipo() {
        int resultado = RANDOM.nextInt(100);

        if (resultado < 50) {
            return TipoItemConsumible.POCION_MANA;
        }
        return TipoItemConsumible.POCION_VIDA;
    }
}
