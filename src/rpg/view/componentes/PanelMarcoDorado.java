package rpg.view.componentes;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;

public class PanelMarcoDorado extends JPanel {

    public PanelMarcoDorado(String titulo) {
        setOpaque(true);
        setBackground(new Color(15, 12, 10, 210));
        
        if (titulo != null && !titulo.isEmpty()) {
            setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(160, 120, 40), 1),
                    titulo,
                    0, 0,
                    new Font("Georgia", Font.BOLD, 14),
                    new Color(212, 175, 55)
            ));
        } else {
            setBorder(BorderFactory.createLineBorder(new Color(160, 120, 40), 1));
        }
    }
}