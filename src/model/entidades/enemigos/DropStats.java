package model.entidades.enemigos;

/**
 * DropStats
 */
public class DropStats {
    private int probabilidadDeDrop;

    public int getProbabilidadDeDrop() {
        return probabilidadDeDrop;
    }

    private int probabilidadItemComun;

    public int getProbabilidadItemComun() {
        return probabilidadItemComun;
    }

    private int probabilidadItemRaro;

    public int getProbabilidadItemRaro() {
        return probabilidadItemRaro;
    }

    public DropStats(int probabilidadDeDrop, int probabilidadItemComun, int probabilidadItemRaro) {
        this.probabilidadDeDrop = probabilidadDeDrop;
        this.probabilidadItemComun = probabilidadItemComun;
        this.probabilidadItemRaro = probabilidadItemRaro;
    }

}
