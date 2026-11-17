package helpers.archivos;

import model.entidades.enemigos.TipoEnemigo;

public class ResourcesHelper {
    public static String getRutaImagenPersonaje(String clase, String genero) {
        if (clase == null || genero == null) {
            return null;
        }

        boolean mujer = "Mujer".equals(genero);
        switch (clase) {
            case "Arquero":
                return mujer
                        ? "/resources/images/personajes/ArqueraMujer.jpg"
                        : "/resources/images/personajes/ArqueroHombre.jpg";
            case "Asesino":
                return mujer
                        ? "/resources/images/personajes/AsesinaMujer.jpg"
                        : "/resources/images/personajes/AsesinoHombre.jpg";
            case "Curador":
                return mujer
                        ? "/resources/images/personajes/CuradoraMujer.jpg"
                        : "/resources/images/personajes/CuradorHombre.jpg";
            case "Guerrero":
                return mujer
                        ? "/resources/images/personajes/GuerreroMujer.jpg"
                        : "/resources/images/personajes/GuerreroHombre.jpg";
            case "Invocador":
                return mujer
                        ? "/resources/images/personajes/InvocadoraMujer.jpg"
                        : "/resources/images/personajes/InvocadorHombre.jpg";
            case "Mago":
                return mujer
                        ? "/resources/images/personajes/MagaLesviana.jpg"
                        : "/resources/images/personajes/MagoLesviano.jpg";
            
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
                return "/resources/images/enemigos/Esqueleton.png";
            case ORCO:
                return "/resources/images/enemigos/OrcoBinarie.png";
            case SLIME:
                return "/resources/images/enemigos/SlimeEnojadito.png";
            case DRAGON:
                return "/resources/images/enemigos/DragonaMilica.png";
            default:
                return null;
        }
    }
}
