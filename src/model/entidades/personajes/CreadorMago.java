package model.entidades.personajes;

class CreadorMago implements CreadorPersonaje {
    @Override
    public Personaje crear(String nombre, int vida, int recursoMax, int ataque,
            int defensa, int velocidad, String rutaImagen) {
        return new Mago(nombre, vida, recursoMax, ataque, defensa, velocidad, rutaImagen);
    }
}
