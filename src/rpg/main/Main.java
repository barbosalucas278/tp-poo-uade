package rpg.main;

import javax.swing.SwingUtilities;
import rpg.view.pantallas.PantallaInicio;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                PantallaInicio menu = new PantallaInicio();
                menu.setVisible(true);
            }
        });
    }
}