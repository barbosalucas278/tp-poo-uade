package model.entidades.personajes;

class CreadorCurador implements CreadorPersonaje {
    @Override
    public Personaje crear(String nombre, int vida, int recursoMax, int ataque,
            int defensa, int velocidad, String rutaImagen) {
        return new Curador(nombre, vida, recursoMax, ataque, defensa, velocidad, rutaImagen);
    }
}
