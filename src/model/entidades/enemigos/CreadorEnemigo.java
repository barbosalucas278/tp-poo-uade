package model.entidades.enemigos;

interface CreadorEnemigo {
    Enemigo crear(String nombre, int vida, int recursoMax, int ataque,
            int defensa, int velocidad, int nivel, int experiencia);
}
