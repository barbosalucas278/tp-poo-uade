package rpg.view.componentes;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;

public class PanelConFondo extends JPanel {
    private Image imagenFondo;

    public PanelConFondo(String nombreImagen) {
        String rutaAbsoluta = "/resources/images/UI/" + nombreImagen;
        URL imgUrl = getClass().getResource(rutaAbsoluta);
        
        if (imgUrl != null) {
            this.imagenFondo = new ImageIcon(imgUrl).getImage();
        } else {
            System.err.println("No se pudo encontrar la imagen en la ruta: " + rutaAbsoluta);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagenFondo != null) {
            g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        }
    }
}