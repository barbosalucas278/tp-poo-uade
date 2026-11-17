package model.entidades.personajes;

public class Invocador extends Personaje {
    private static final String NOMBRE_RECURSO = "ESENCIA";
    // Esencia que gasta cada vez que invoca a su animal
    private static final int COSTO_ALIANZA_ANIMAL = 60;
    // Poder del animal invocado; sube al subir de nivel
    private int espiritu;

    public Invocador(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad,
            String rutaImagen) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, rutaImagen, NOMBRE_RECURSO);
        this.espiritu = 15;
    }

    // Habilidad especial del Invocador: devuelve el daño que hace Alianza Animal
    @Override
    public int usarHabilidadEspecial() {
        return alianzaAnimal();
    }

    // Invoca un animal que ataca: 110 % del ataque más el espíritu del invocador
    private int alianzaAnimal() {
        // Si no tiene esencia suficiente, no puede usarla y no pierde el turno
        if (getRecursoActual() < COSTO_ALIANZA_ANIMAL) {
            throw new IllegalStateException("No hay suficiente esencia para usar Alianza Animal.");
        }
        int danio = (int) Math.round(atacar() * 1.10) + espiritu;
        consumirRecursos(COSTO_ALIANZA_ANIMAL);
        return danio;
    }

    @Override
    public String getNombreRecurso() {
        return NOMBRE_RECURSO;
    }

    @Override
    public TipoPersonaje getTipo() {
        return TipoPersonaje.INVOCADOR;
    }

    @Override
    public void aumentarEstadisticasDeClase() {
        ataqueBase += 2;
        defensaBase += 1;
        espiritu += 4;
        vidaMax += 5;
    }
}