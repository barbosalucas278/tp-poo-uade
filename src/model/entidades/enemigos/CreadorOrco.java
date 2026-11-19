package model.entidades.enemigos;

class CreadorOrco implements CreadorEnemigo {
    @Override
    public Enemigo crear(String nombre, int vida, int recursoMax, int ataque,
            int defensa, int velocidad, int nivel, int experiencia) {
        return new Elite(nombre, vida, recursoMax, ataque, defensa, velocidad, nivel, experiencia);
    }
}
