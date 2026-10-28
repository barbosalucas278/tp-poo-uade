package helpers.archivos;

import model.entidades.enemigos.TipoEnemigo;

public class ResourcesHelper {
    public static String getRutaImagenPersonaje(String clase, String genero) {
        if (clase == null || genero == null) {
            return null;
        }

        boolean mujer = "Mujer".equals(genero);
        switch (clase) {
            case "Maga":
            case "Hechicera":
            case "Hechicero":
                return mujer
                        ? "/resources/images/personajes/MagaLesviana.png"
                        : "/resources/images/personajes/MagoLesviano.png";
            case "Asesino":
                return mujer
                        ? "/resources/images/personajes/AsesinaMujer.png"
                        : "/resources/images/personajes/AsesinoHombre.png";
            case "Guerrero":
                return mujer
                        ? "/resources/images/personajes/GuerreroMujer.png"
                        : "/resources/images/personajes/GuerreroHombre.png";
            default:
                return null;
        }
    }

    public static String getRutaImagenEnemigo(TipoEnemigo tipo) {
        if (tipo == null) {
            return null;
        }

        switch (tipo) {
            case ESQUELETO:
                return "/resources/images/enemigos/Esqueleto.png";
            case ORCO:
                return "/resources/images/enemigos/OrcoBinarie.png";
            case SLIME:
                return "/resources/images/enemigos/SlimeEnojadito.png";
            case DRAGON:
            default:
                return null;
        }
    }
}
