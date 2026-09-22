package rpg.view.pantallas;

import rpg.view.componentes.BotonRPG;
import rpg.view.componentes.PanelConFondo;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class PantallaInicio extends JFrame {

    private JButton btnJugar;
    private JButton btnCargar;
    private JButton btnSalir;

    public PantallaInicio() {
        setTitle("CHRONICLES OF UADE");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panelMenu = new PanelConFondo("fondo_inicio.jpg");
        panelMenu.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // 1. TÍTULO DEL JUEGO
        JLabel lblTitulo = new JLabel("CHRONICLES OF UADE");
        lblTitulo.setFont(new Font("Georgia", Font.BOLD, 36));
        lblTitulo.setForeground(new Color(212, 175, 55));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 10, 60, 10);
        panelMenu.add(lblTitulo, gbc);

        // 2. CONFIGURACIÓN DE BOTONES
        Dimension tamanoBoton = new Dimension(220, 50);

        btnJugar = new BotonRPG("JUGAR");
        btnJugar.setPreferredSize(tamanoBoton);
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 10, 15, 10);
        panelMenu.add(btnJugar, gbc);

        btnCargar = new BotonRPG("CARGAR PARTIDA");
        btnCargar.setPreferredSize(tamanoBoton);
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        panelMenu.add(btnCargar, gbc);

        btnSalir = new BotonRPG("SALIR");
        btnSalir.setPreferredSize(tamanoBoton);
        gbc.gridy = 3;
        panelMenu.add(btnSalir, gbc);

        add(panelMenu);
        inicializarEventos();
    }

    private void inicializarEventos() {
        btnJugar.addActionListener(e -> {
            PantallaCreacionPersonaje pantallaCreacion = new PantallaCreacionPersonaje();
            pantallaCreacion.setVisible(true);
            dispose();
        });

        btnCargar.addActionListener(e -> {
            PantallaGuardado pantallaCargar = new PantallaGuardado(false);
            pantallaCargar.setVisible(true);
            dispose();
        });

        btnSalir.addActionListener(e -> System.exit(0));
    }
}