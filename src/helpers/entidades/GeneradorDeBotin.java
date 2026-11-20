package helpers.entidades;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.entidades.enemigos.DropStats;
import model.entidades.enemigos.Enemigo;
import model.objetos.EquipablesFactory;
import model.objetos.Item;
import model.objetos.ItemsConsumiblesFactory;
import model.objetos.TipoItemConsumible;
import model.objetos.TipoRareza;

public class GeneradorDeBotin {
    private static final Random RANDOM = new Random();
    private static final int PROBABILIDAD_EQUIPABLE = 30;

    public static List<Item> SortearBotin(List<Enemigo> enemigosDerrotados, boolean primeraVictoria) {
        List<Item> recompensas = new ArrayList<>();
        if (primeraVictoria) {
            recompensas.add(EquipablesFactory.crearArma(TipoRareza.COMUN));
            recompensas.add(EquipablesFactory.crearArmadura(TipoRareza.COMUN));
        }
        recompensas.addAll(SortearBotin(enemigosDerrotados));
        return recompensas;
    }

    public static List<Item> SortearBotin(List<Enemigo> enemigosDerrotados) {
        List<Item> recompensas = new ArrayList<>();
        for (Enemigo enemigo : enemigosDerrotados) {
            DropStats drop = enemigo.GetProbabilidadDeDrop();
            int resultado = RANDOM.nextInt(100);

            if (resultado < drop.getProbabilidadDeDrop()) {
                TipoRareza rareza = sortearRareza(drop.getProbabilidadItemComun(), drop.getProbabilidadItemRaro());
                recompensas.add(sortearObjeto(rareza));
            }
        }
        return recompensas;
    }

    private static Item sortearObjeto(TipoRareza rareza) {
        if (RANDOM.nextInt(100) < PROBABILIDAD_EQUIPABLE) {
            return RANDOM.nextBoolean()
                    ? EquipablesFactory.crearArma(rareza)
                    : EquipablesFactory.crearArmadura(rareza);
        }
        return ItemsConsumiblesFactory.CrearItemConsumible(rareza, sortearTipo());
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

        if (resultado < 70) {
            return TipoItemConsumible.POCION_VIDA;
        }
        return TipoItemConsumible.POCION_MANA;
    }
}
