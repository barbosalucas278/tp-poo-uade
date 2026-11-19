package model.entidades.personajes;

interface CreadorPersonaje {
    Personaje crear(String nombre, int vida, int recursoMax, int ataque,
            int defensa, int velocidad, String rutaImagen);
}
