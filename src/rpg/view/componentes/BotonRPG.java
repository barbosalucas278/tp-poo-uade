package rpg.view.componentes;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class BotonRPG extends JButton {

    public BotonRPG(String texto) {
        super(texto);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setFont(new Font("Georgia", Font.BOLD, 15));
        setForeground(new Color(230, 230, 230));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        Color colorFondo;
        Color colorBorde;

        if (getModel().isPressed()) {
            colorFondo = new Color(30, 20, 10, 220);
            colorBorde = new Color(180, 140, 40);
        } else if (getModel().isRollover()) {
            colorFondo = new Color(50, 40, 30, 230);
            colorBorde = new Color(255, 215, 0);
            setForeground(new Color(255, 215, 0));
        } else {
            colorFondo = new Color(20, 15, 10, 200);
            colorBorde = new Color(160, 120, 40);
            setForeground(new Color(220, 220, 220));
        }

        g2.setColor(colorFondo);
        g2.fillRoundRect(0, 0, width, height, 15, 15);

        g2.setColor(colorBorde);
        g2.drawRoundRect(0, 0, width - 1, height - 1, 15, 15);
        g2.drawRoundRect(2, 2, width - 5, height - 5, 11, 11);

        g2.dispose();
        super.paintComponent(g);
    }
}