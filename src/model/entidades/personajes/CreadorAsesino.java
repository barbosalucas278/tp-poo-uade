package model.entidades.personajes;

class CreadorAsesino implements CreadorPersonaje {
    @Override
    public Personaje crear(String nombre, int vida, int recursoMax, int ataque,
            int defensa, int velocidad, String rutaImagen) {
        return new Asesino(nombre, vida, recursoMax, ataque, defensa, velocidad, rutaImagen);
    }
}
