package model.entidades.personajes;

/** Adaptador legado: las partidas antiguas se migran a Arquero en el factory. */
@Deprecated
public class Asesino extends Arquero {
    public Asesino(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad,
            String rutaImagen) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, rutaImagen);
    }

    @Override
    public TipoPersonaje getTipo() {
        return TipoPersonaje.ASESINO;
    }
}
