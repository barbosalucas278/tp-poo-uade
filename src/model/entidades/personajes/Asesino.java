package model.entidades.personajes;

public class Asesino extends Personaje {
    private static final String NOMBRE_RECURSO = "VIGOR";
    // Vigor que gasta cada vez que usa Daga Mortal
    private static final int COSTO_DAGA_MORTAL = 40;
    // Probabilidad (en %) de hacer golpe crítico; sube al subir de nivel
    private int destreza;

    public int getDestreza() {
        return destreza;
    }

    public Asesino(String nombre, int vidaMax, int recursoMax, int ataqueBase, int defensaBase, int velocidad,
            String rutaImagen) {
        super(nombre, vidaMax, recursoMax, ataqueBase, defensaBase, velocidad, rutaImagen, NOMBRE_RECURSO);
        this.destreza = 15;
    }

    // Habilidad especial del Asesino: devuelve el daño que hace Daga Mortal
    @Override
    public int usarHabilidadEspecial() {
        return dagaMortal();
    }

    // Ataque con daga: 140 % del ataque, con chance de crítico que duplica el daño
    private int dagaMortal() {
        // Si no tiene vigor suficiente, no puede usarla y no pierde el turno
        if (getRecursoActual() < COSTO_DAGA_MORTAL) {
            throw new IllegalStateException("No hay suficiente vigor para usar Daga Mortal.");
        }
        int danio = (int) Math.round(atacar() * 1.40);
        // Sortea un número de 0 a 100: si es menor que la destreza, es crítico
        if (Math.random() * 100 < destreza) {
            danio *= 2;
        }
        consumirRecursos(COSTO_DAGA_MORTAL);
        return danio;
    }

    @Override
    public String getNombreRecurso() {
        return NOMBRE_RECURSO;
    }

    @Override
    public TipoPersonaje getTipo() {
        return TipoPersonaje.ASESINO;
    }

    @Override
    public void aumentarEstadisticasDeClase() {
        this.ataqueBase += 2;
        this.defensaBase += 1;
        this.destreza += 4;
        this.vidaMax += 10;
    }
}