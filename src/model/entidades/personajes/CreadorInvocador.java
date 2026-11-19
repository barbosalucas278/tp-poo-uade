package model.entidades.personajes;

class CreadorInvocador implements CreadorPersonaje {
    @Override
    public Personaje crear(String nombre, int vida, int recursoMax, int ataque,
            int defensa, int velocidad, String rutaImagen) {
        return new Invocador(nombre, vida, recursoMax, ataque, defensa, velocidad, rutaImagen);
    }
}
