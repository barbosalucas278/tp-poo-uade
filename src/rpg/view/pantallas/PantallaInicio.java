package rpg.view.pantallas;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class PantallaInicio extends JFrame {

    private JButton btnJugar;
    private JButton btnCargar;
    private JButton btnSalir;

    public PantallaInicio() {
        setTitle("RPG por Turnos - Menú Principal");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panelMenu = new PanelConFondo("fondo_inicio.jpg");
        panelMenu.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // 1. TÍTULO DEL JUEGO CON ESTILO FANTASÍA
        JLabel lblTitulo = new JLabel("CHRONICLES OF UADE");
        lblTitulo.setFont(new Font("Georgia", Font.BOLD, 36));
        lblTitulo.setForeground(new Color(212, 175, 55)); // Color Dorado
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 10, 60, 10); // Margen inferior amplio antes de los botones
        panelMenu.add(lblTitulo, gbc);

        // 2. CONFIGURACIÓN Y UBICACIÓN DE BOTONES
        Dimension tamanoBoton = new Dimension(220, 50);

        btnJugar = new BotonRPG("JUGAR");
        btnJugar.setPreferredSize(tamanoBoton);
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 10, 15, 10);
        panelMenu.add(btnJugar, gbc);

        btnCargar = new BotonRPG("CARGAR PARTIDA");
        btnCargar.setPreferredSize(tamanoBoton);
        gbc.gridy = 2;
        panelMenu.add(btnCargar, gbc);

        btnSalir = new BotonRPG("SALIR");
        btnSalir.setPreferredSize(tamanoBoton);
        gbc.gridy = 3;
        panelMenu.add(btnSalir, gbc);

        add(panelMenu);
        inicializarEventos();
    }

    private void inicializarEventos() {
        btnJugar.addActionListener(e -> System.out.println("Iniciar nueva partida..."));
        
        btnCargar.addActionListener(e -> {
            PantallaGuardado pantallaCargar = new PantallaGuardado(false); 
            pantallaCargar.setVisible(true);
            this.dispose(); 
        });        
        btnSalir.addActionListener(e -> System.exit(0));
    }

    // =========================================================================
    // BOTÓN CON ESTILO RPG / MEDIEVAL
    // =========================================================================
    private class BotonRPG extends JButton {

        public BotonRPG(String texto) {
            super(texto);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setFont(new Font("Georgia", Font.BOLD, 15));
            setForeground(new Color(230, 230, 230)); // Texto blanco hueso
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // Colores según interacciones del mouse
            Color colorFondo;
            Color colorBorde;

            if (getModel().isPressed()) {
                colorFondo = new Color(30, 20, 10, 220);  // Furia oscura al presionar
                colorBorde = new Color(180, 140, 40);
            } else if (getModel().isRollover()) {
                colorFondo = new Color(50, 40, 30, 230);  // Resplandor al pasar el cursor
                colorBorde = new Color(255, 215, 0);       // Dorado brillante (Gold)
                setForeground(new Color(255, 215, 0));
            } else {
                colorFondo = new Color(20, 15, 10, 200);  // Negro/Marrón oscuro semi-transparente
                colorBorde = new Color(160, 120, 40);      // Dorado opaco
                setForeground(new Color(220, 220, 220));
            }

            // Dibujar fondo semi-transparente redondeado
            g2.setColor(colorFondo);
            g2.fillRoundRect(0, 0, width, height, 15, 15);

            // Dibujar borde dorado doble o resaltado
            g2.setColor(colorBorde);
            g2.drawRoundRect(0, 0, width - 1, height - 1, 15, 15);
            g2.drawRoundRect(2, 2, width - 5, height - 5, 11, 11); // Segundo borde interno

            g2.dispose();
            super.paintComponent(g);
        }
    }

  
    // PANEL CON FONDO
    private class PanelConFondo extends JPanel {
        private Image imagenFondo;

        public PanelConFondo(String nombreImagen) {
            String rutaRelativa = "/resources/images/UI/" + nombreImagen;
            URL imgUrl = getClass().getResource(rutaRelativa);
            
            if (imgUrl != null) {
                this.imagenFondo = new ImageIcon(imgUrl).getImage();
            } else {
                System.err.println("No se pudo encontrar la imagen en la ruta: " + rutaRelativa);
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
}