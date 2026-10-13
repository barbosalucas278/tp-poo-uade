package view.pantallas;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import controller.BatallaController;
import model.DTOs.MapaDTO;
import view.componentes.BotonRPG;
import view.componentes.PanelConFondo;
import view.componentes.PanelMarcoDorado;

public class PantallaPreBatalla extends JFrame {

    private static final Color DORADO = new Color(212, 175, 55);
    private static final Color VERDE = new Color(86, 168, 95);
    private static final Color CELESTE = new Color(85, 195, 241);
    private static final Color GRIS = new Color(115, 115, 120);

    private BatallaController controller;
    private JLabel lblMapa;
    private JLabel lblNivel;
    private PanelRecorrido panelRecorrido;
    private BotonRPG btnVolver;
    private BotonRPG btnIniciarBatalla;

    public PantallaPreBatalla() {
        controller = new BatallaController();
        setTitle("Chronicles of UADE - Pre Batalla");
        setSize(980, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panelPrincipal = new PanelConFondo("fondo_inicio.jpg");
        panelPrincipal.setLayout(new BorderLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel panelMapa = new PanelMarcoDorado("");
        panelMapa.setLayout(new BorderLayout(0, 15));
        panelMapa.setBorder(BorderFactory.createCompoundBorder(
                panelMapa.getBorder(),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)));

        panelMapa.add(crearPanelSuperior(), BorderLayout.NORTH);

        panelRecorrido = new PanelRecorrido();
        panelMapa.add(panelRecorrido, BorderLayout.CENTER);
        panelMapa.add(crearPanelAcciones(), BorderLayout.SOUTH);

        panelPrincipal.add(panelMapa, BorderLayout.CENTER);
        setContentPane(panelPrincipal);
        inicializarEventos();

        MapaDTO mapaDTO = controller.getMapaActual();
        actualizarProgreso(mapaDTO.numeroMapa, mapaDTO.nivelActual, mapaDTO.totalNiveles);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 22, 0);

        lblMapa = new JLabel("", SwingConstants.CENTER);
        lblMapa.setFont(new Font("Georgia", Font.BOLD, 32));
        lblMapa.setForeground(DORADO);
        panel.add(lblMapa, gbc);

        JPanel panelNivel = new PanelMarcoDorado("");
        panelNivel.setLayout(new BorderLayout());
        panelNivel.setPreferredSize(new Dimension(280, 60));

        lblNivel = new JLabel("", SwingConstants.CENTER);
        lblNivel.setFont(new Font("Georgia", Font.BOLD, 21));
        lblNivel.setForeground(new Color(230, 230, 230));
        panelNivel.add(lblNivel, BorderLayout.CENTER);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(panelNivel, gbc);
        return panel;
    }

    private JPanel crearPanelAcciones() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        btnVolver = new BotonRPG("VOLVER");
        btnVolver.setPreferredSize(new Dimension(150, 45));
        panel.add(btnVolver, BorderLayout.WEST);

        btnIniciarBatalla = new BotonRPG("INICIAR BATALLA");
        btnIniciarBatalla.setPreferredSize(new Dimension(220, 45));
        panel.add(btnIniciarBatalla, BorderLayout.EAST);
        return panel;
    }

    private void inicializarEventos() {
        btnVolver.addActionListener(e -> {
            PantallaInicio inicio = new PantallaInicio();
            inicio.setVisible(true);
            this.dispose();
        });

        btnIniciarBatalla.addActionListener(e -> {
            PantallaBatalla inicio = new PantallaBatalla();
            inicio.setVisible(true);
            this.dispose();
        });
    }

    /**
     * Actualiza únicamente la representación del progreso. Los números de mapa
     * y nivel comienzan en 1; el controller debe proveer los valores reales.
     */
    public void actualizarProgreso(int numeroMapa, int nivelActual, int totalNiveles) {
        if (numeroMapa < 1 || totalNiveles < 1 || nivelActual < 1 || nivelActual > totalNiveles) {
            throw new IllegalArgumentException("El mapa y los niveles deben ser válidos y comenzar en 1.");
        }

        lblMapa.setText("MAPA " + numeroMapa);
        lblNivel.setText("NIVEL " + nivelActual + " DE " + totalNiveles);
        panelRecorrido.actualizarProgreso(nivelActual, totalNiveles);
    }

    public BotonRPG getBtnVolver() {
        return btnVolver;
    }

    public BotonRPG getBtnIniciarBatalla() {
        return btnIniciarBatalla;
    }

    private static class PanelRecorrido extends JPanel {

        private int nivelActual = 1;
        private int totalNiveles = 1;

        private PanelRecorrido() {
            setOpaque(false);
        }

        private void actualizarProgreso(int nivelActual, int totalNiveles) {
            this.nivelActual = nivelActual;
            this.totalNiveles = totalNiveles;
            getAccessibleContext().setAccessibleName("Recorrido: nivel " + nivelActual + " de " + totalNiveles);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Se muestran hasta tres niveles alrededor del actual. Los puntos
            // indican que el recorrido continúa fuera del tramo visible.
            int cantidad = Math.min(3, totalNiveles);
            int primerNivel = Math.max(1, Math.min(nivelActual - 1, totalNiveles - cantidad + 1));
            int diametro = Math.min(148, Math.min(getHeight() - 70, (getWidth() - 120) / (cantidad * 2 - 1)));
            if (diametro < 40) {
                g2.dispose();
                return;
            }

            int separacion = Math.min(90, diametro * 2 / 3);
            int anchoRecorrido = cantidad * diametro + (cantidad - 1) * separacion;
            int inicioX = (getWidth() - anchoRecorrido) / 2;
            int inicioY = (getHeight() - diametro - 35) / 2;
            int centroY = inicioY + diametro / 2;

            for (int i = 0; i < cantidad - 1; i++) {
                int desdeX = inicioX + i * (diametro + separacion) + diametro + 10;
                int hastaX = desdeX + separacion - 20;
                dibujarFlecha(g2, desdeX, hastaX, centroY);
            }

            for (int i = 0; i < cantidad; i++) {
                dibujarNivel(g2, primerNivel + i, inicioX + i * (diametro + separacion), inicioY, diametro);
            }

            if (primerNivel > 1) {
                dibujarContinuacion(g2, inicioX - 35, centroY);
            }
            if (primerNivel + cantidad - 1 < totalNiveles) {
                dibujarContinuacion(g2, inicioX + anchoRecorrido + 35, centroY);
            }

            g2.dispose();
        }

        private void dibujarFlecha(Graphics2D g2, int desdeX, int hastaX, int y) {
            g2.setColor(new Color(160, 120, 40));
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(desdeX, y, hastaX, y);
            g2.drawLine(hastaX - 10, y - 7, hastaX, y);
            g2.drawLine(hastaX - 10, y + 7, hastaX, y);
        }

        private void dibujarNivel(Graphics2D g2, int numero, int x, int y, int diametro) {
            boolean completado = numero < nivelActual;
            boolean actual = numero == nivelActual;
            Color colorEstado = completado ? VERDE : actual ? CELESTE : GRIS;

            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillOval(x + 3, y + 5, diametro, diametro);
            g2.setColor(completado ? new Color(45, 98, 52)
                    : actual
                            ? new Color(18, 35, 44)
                            : new Color(50, 48, 48));
            g2.fillOval(x, y, diametro, diametro);

            g2.setStroke(new BasicStroke(actual ? 5f : 2f));
            g2.setColor(colorEstado);
            g2.drawOval(x + 3, y + 3, diametro - 6, diametro - 6);
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(new Color(colorEstado.getRed(), colorEstado.getGreen(), colorEstado.getBlue(), 80));
            g2.drawOval(x + 10, y + 10, diametro - 20, diametro - 20);

            g2.setFont(new Font("Georgia", Font.BOLD, diametro / 3));
            g2.setColor(actual ? CELESTE : completado ? new Color(225, 240, 225) : new Color(180, 180, 180));
            FontMetrics fm = g2.getFontMetrics();
            String texto = String.valueOf(numero);
            g2.drawString(texto, x + (diametro - fm.stringWidth(texto)) / 2,
                    y + (diametro - fm.getHeight()) / 2 + fm.getAscent());

            g2.setFont(new Font("Georgia", Font.BOLD, 12));
            g2.setColor(colorEstado);
            String estado = completado ? "COMPLETADO" : actual ? "ACTUAL" : "PENDIENTE";
            g2.drawString(estado, x + (diametro - g2.getFontMetrics().stringWidth(estado)) / 2, y + diametro + 28);
        }

        private void dibujarContinuacion(Graphics2D g2, int centroX, int centroY) {
            g2.setColor(DORADO);
            for (int i = -1; i <= 1; i++) {
                g2.fillOval(centroX + i * 10 - 2, centroY - 2, 4, 4);
            }
        }
    }
}
