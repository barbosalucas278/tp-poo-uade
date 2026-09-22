package rpg.view.pantallas;

import rpg.view.componentes.BarraEstadoRPG;
import rpg.view.componentes.BotonRPG;
import rpg.view.componentes.CampoTextoRPG;
import rpg.view.componentes.PanelConFondo;
import rpg.view.componentes.PanelMarcoDorado;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.net.URL;

public class PantallaCreacionPersonaje extends JFrame {

    private JButton[] btnSlots = new JButton[4];
    private int slotSeleccionado = 0;

    private JProgressBar barVida, barAtaque, barDefensa;
    private JLabel lblVidaVal, lblAtaqueVal, lblDefensaVal;
    private int vida = 10, ataque = 8, defensa = 6;
    private String claseSeleccionada = "Hechicero";

    private JLabel lblAvatar;
    private JTextField txtNombre;
    private JButton btnDado, btnCrear, btnVolver;

    public PantallaCreacionPersonaje() {
        setTitle("Chronicles of UADE - Creación de Personaje");
        setSize(850, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        PanelConFondo panelPrincipal = new PanelConFondo("fondo_inicio.jpg");
        panelPrincipal.setLayout(new BorderLayout(15, 15));

        JLabel lblTitulo = new JLabel("CREACIÓN DE PERSONAJE", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Georgia", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(212, 175, 55));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        JPanel panelContenido = new JPanel(new GridLayout(1, 3, 15, 0));
        panelContenido.setOpaque(false);

        panelContenido.add(crearPanelSlots());
        panelContenido.add(crearPanelCentral());
        panelContenido.add(crearPanelDerecho());

        panelPrincipal.add(panelContenido, BorderLayout.CENTER);
        add(panelPrincipal);
    }

    private JPanel crearPanelSlots() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 0, 15));
        panel.setOpaque(false);

        for (int i = 0; i < 4; i++) {
            btnSlots[i] = new BotonRPG((i + 1) + "  [VACÍO]  +");
            btnSlots[i].setFont(new Font("Georgia", Font.BOLD, 16));
            final int index = i;
            btnSlots[i].addActionListener(e -> slotSeleccionado = index);
            panel.add(btnSlots[i]);
        }
        return panel;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 15));
        panel.setOpaque(false);

        JPanel panelAtributos = new PanelMarcoDorado("ATRIBUTOS");
        panelAtributos.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        barVida = new BarraEstadoRPG(Color.RED, 20, vida);
        lblVidaVal = new JLabel(vida + "/20", SwingConstants.RIGHT);
        lblVidaVal.setForeground(Color.WHITE);
        agregarFilaAtributo(panelAtributos, gbc, 0, "VIDA", barVida, lblVidaVal);

        barAtaque = new BarraEstadoRPG(Color.BLUE, 15, ataque);
        lblAtaqueVal = new JLabel(ataque + "/15", SwingConstants.RIGHT);
        lblAtaqueVal.setForeground(Color.WHITE);
        agregarFilaAtributo(panelAtributos, gbc, 1, "ATAQUE", barAtaque, lblAtaqueVal);

        barDefensa = new BarraEstadoRPG(Color.GREEN, 12, defensa);
        lblDefensaVal = new JLabel(defensa + "/12", SwingConstants.RIGHT);
        lblDefensaVal.setForeground(Color.WHITE);
        agregarFilaAtributo(panelAtributos, gbc, 2, "DEFENSA", barDefensa, lblDefensaVal);

        panel.add(panelAtributos);

        JPanel panelClase = new PanelMarcoDorado("CLASE");
        panelClase.setLayout(new GridLayout(1, 3, 10, 10));

        JButton btnAsesino = new BotonRPG("⚔");
        JButton btnGuerrero = new BotonRPG("🛡");
        JButton btnHechicero = new BotonRPG("🪄");

        btnAsesino.addActionListener(e -> claseSeleccionada = "Asesino");
        btnGuerrero.addActionListener(e -> claseSeleccionada = "Guerrero");
        btnHechicero.addActionListener(e -> claseSeleccionada = "Hechicero");

        panelClase.add(btnAsesino);
        panelClase.add(btnGuerrero);
        panelClase.add(btnHechicero);

        panel.add(panelClase);
        return panel;
    }

    private void agregarFilaAtributo(JPanel panel, GridBagConstraints gbc, int fila, String nombre, JProgressBar barra, JLabel lblVal) {
        gbc.gridy = fila * 2;
        gbc.gridx = 0;
        JLabel lbl = new JLabel(nombre);
        lbl.setForeground(new Color(212, 175, 55));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        panel.add(lblVal, gbc);

        gbc.gridy = fila * 2 + 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(barra, gbc);
        gbc.gridwidth = 1;
    }

    private JPanel crearPanelDerecho() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JPanel panelAvatar = new PanelMarcoDorado("");
        panelAvatar.setLayout(new BorderLayout());

        JButton btnIzq = new BotonRPG("<");
        JButton btnDer = new BotonRPG(">");
        btnIzq.setPreferredSize(new Dimension(45, 45));
        btnDer.setPreferredSize(new Dimension(45, 45));

        lblAvatar = new JLabel("", SwingConstants.CENTER);
        URL imgUrl = getClass().getResource("/resources/images/personajes/mago.png");
        if (imgUrl != null) {
            lblAvatar.setIcon(new ImageIcon(imgUrl));
        } else {
            lblAvatar.setText("[ AVATAR ]");
            lblAvatar.setForeground(Color.WHITE);
        }

        panelAvatar.add(btnIzq, BorderLayout.WEST);
        panelAvatar.add(lblAvatar, BorderLayout.CENTER);
        panelAvatar.add(btnDer, BorderLayout.EAST);

        txtNombre = new CampoTextoRPG("[Escribe tu nombre]");
        panelAvatar.add(txtNombre, BorderLayout.SOUTH);
        panel.add(panelAvatar, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new GridLayout(1, 2, 10, 0));
        panelInferior.setOpaque(false);

        btnVolver = new BotonRPG("VOLVER");
        btnVolver.addActionListener(e -> {
            PantallaInicio inicio = new PantallaInicio();
            inicio.setVisible(true);
            dispose();
        });

        btnCrear = new BotonRPG("CREAR");

        panelInferior.add(btnVolver);
        panelInferior.add(btnCrear);

        panel.add(panelInferior, BorderLayout.SOUTH);
        return panel;
    }
}