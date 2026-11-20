package model.objetos;

import java.util.HashMap;
import java.util.Map;

public class EquipablesFactory {
    private static final Map<TipoRareza, EquipoPorRareza> CONFIGURACIONES = cargarConfiguraciones();

    private static Map<TipoRareza, EquipoPorRareza> cargarConfiguraciones() {
        Map<TipoRareza, EquipoPorRareza> configuraciones = new HashMap<>();
        configuraciones.put(TipoRareza.COMUN, new EquipoPorRareza("de aprendiz", 5, 3));
        configuraciones.put(TipoRareza.RARO, new EquipoPorRareza("de acero", 9, 5));
        configuraciones.put(TipoRareza.EPICO, new EquipoPorRareza("del guardián", 14, 8));
        return configuraciones;
    }

    public static ItemEquipable crearArma(TipoRareza rareza) {
        EquipoPorRareza datos = obtenerConfiguracion(rareza);
        int bonus = datos.getBonusAtaque();
        return new ItemEquipable("Espada " + datos.getVariante(), "Aumenta el ataque en " + bonus + ".",
                TipoObjeto.ARMA, bonus, 0, rareza);
    }

    public static ItemEquipable crearArmadura(TipoRareza rareza) {
        EquipoPorRareza datos = obtenerConfiguracion(rareza);
        int bonus = datos.getBonusDefensa();
        return new ItemEquipable("Armadura " + datos.getVariante(), "Aumenta la defensa en " + bonus + ".",
                TipoObjeto.ARMADURA, 0, bonus, rareza);
    }

    private static EquipoPorRareza obtenerConfiguracion(TipoRareza rareza) {
        EquipoPorRareza configuracion = CONFIGURACIONES.get(rareza);
        if (configuracion == null) {
            throw new IllegalArgumentException("Rareza de equipable inválida.");
        }
        return configuracion;
    }
}
