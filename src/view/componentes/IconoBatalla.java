package view.componentes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;

/** Iconos provisionales independientes de fuentes y del sistema operativo. */
public class IconoBatalla implements Icon {
    public enum Tipo { ATACAR, DEFENDER, HABILIDAD, INVENTARIO }

    private final Tipo tipo;
    private final int tamano;

    public IconoBatalla(Tipo tipo, int tamano) {
        this.tipo = tipo;
        this.tamano = tamano;
    }

    @Override
    public int getIconWidth() { return tamano; }

    @Override
    public int getIconHeight() { return tamano; }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(x, y);
        g2.scale(tamano / 40.0, tamano / 40.0);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(c.isEnabled() ? new Color(212, 175, 85) : new Color(101, 98, 84));
        switch (tipo) {
            case ATACAR:
                g2.drawPolygon(new int[] {11, 29, 34, 32, 14}, new int[] {27, 7, 5, 12, 30}, 5);
                g2.drawLine(8, 25, 17, 33);
                g2.drawLine(12, 29, 6, 36);
                break;
            case DEFENDER:
                g2.drawPolygon(new int[] {20, 33, 30, 25, 20, 15, 10, 7},
                        new int[] {4, 9, 25, 32, 36, 32, 25, 9}, 8);
                g2.drawLine(20, 8, 20, 30);
                break;
            case HABILIDAD:
                g2.drawPolygon(new int[] {23, 12, 20, 17, 30, 23},
                        new int[] {3, 23, 23, 37, 16, 16}, 6);
                break;
            case INVENTARIO:
                g2.drawRoundRect(5, 11, 30, 23, 6, 6);
                g2.drawLine(5, 21, 35, 21);
                g2.drawRect(17, 18, 6, 8);
                g2.drawLine(11, 12, 11, 33);
                g2.drawLine(29, 12, 29, 33);
                break;
        }
        g2.dispose();
    }
}
