package rpg.view.componentes;

import javax.swing.BorderFactory;
import javax.swing.JProgressBar;
import java.awt.Color;
import java.awt.Dimension;

public class BarraEstadoRPG extends JProgressBar {

    public BarraEstadoRPG(Color colorFill, int maximo, int valorInicial) {
        super(0, maximo);
        setValue(valorInicial);
        setForeground(colorFill);
        setBackground(new Color(40, 40, 40));
        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        setPreferredSize(new Dimension(180, 12));
    }
}