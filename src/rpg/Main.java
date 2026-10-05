package rpg;

import javax.swing.SwingUtilities;

import view.pantallas.PantallaInicio;

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