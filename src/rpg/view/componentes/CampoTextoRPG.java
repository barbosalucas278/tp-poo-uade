package rpg.view.componentes;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

public class CampoTextoRPG extends JTextField {

    public CampoTextoRPG(String textoPlaceholder) {
        super(textoPlaceholder);
        setFont(new Font("Georgia", Font.ITALIC, 14));
        setHorizontalAlignment(JTextField.CENTER);
        setBackground(new Color(20, 15, 10, 220));
        setForeground(new Color(212, 175, 55));
        setCaretColor(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(160, 120, 40), 1));
        setPreferredSize(new Dimension(200, 35));
    }
}