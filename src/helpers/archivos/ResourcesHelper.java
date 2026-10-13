package helpers.archivos;

public class ResourcesHelper {
    public static String getRutaImagenPersonaje(String clase, String genero) {
        if (clase.equals("Maga")) {
            return genero.equals("Mujer") ? "/resources/images/personajes/MagaLesviana.jpeg" : "/resources/images/personajes/MagoLesviano.jpeg";
        } else if (clase.equals("Asesino")) {
            return genero.equals("Mujer") ? "/resources/images/personajes/AsesinaMujer.jpg" : "/resources/images/personajes/AsesinoHombre.jpg";
        } else if (clase.equals("Guerrero")) {
            return genero.equals("Mujer") ? "/resources/images/personajes/GuerreroMujer.jpg" : "/resources/images/personajes/GuerreroHombre.jpg";
        }
        return null;
    }
}
