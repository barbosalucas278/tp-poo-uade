package rpg.view.pantallas;

import rpg.controller.GestorPartida;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
import java.io.File;
import java.net.URL;

public class PantallaGuardado extends JFrame {

    private JButton btnSlot1;
    private JButton btnSlot2;
    private JButton btnSlot3;
    private JButton btnVolver;
    private boolean esModoGuardar; // true = Guardar, false = Cargar

    public PantallaGuardado(boolean esModoGuardar) {
        this.esModoGuardar = esModoGuardar;

        // 1. Configuración básica de la ventana
        String modoTexto = esModoGuardar ? "Guardar Partida" : "Cargar Partida";
        setTitle("RPG por Turnos - " + modoTexto);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 2. Panel con la imagen de fondo de UI
        JPanel panelMenu = new PanelConFondo("fondo_guardado.jpg");
        panelMenu.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // 3. Título superior
        JLabel lblTitulo = new JLabel(modoTexto.toUpperCase());
        lblTitulo.setFont(new Font("Georgia", Font.BOLD, 32));
        lblTitulo.setForeground(new Color(212, 175, 55)); // Dorado
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 40, 10);
        panelMenu.add(lblTitulo, gbc);

        // 4. Dimensiones para los botones de Slot
        Dimension tamanoSlot = new Dimension(350, 50);

        // 5. Instanciar los 3 Slots verificando si existe el archivo
        btnSlot1 = new BotonRPG(obtenerTextoSlot("slot1"));
        btnSlot1.setPreferredSize(tamanoSlot);
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        panelMenu.add(btnSlot1, gbc);

        btnSlot2 = new BotonRPG(obtenerTextoSlot("slot2"));
        btnSlot2.setPreferredSize(tamanoSlot);
        gbc.gridy = 2;
        panelMenu.add(btnSlot2, gbc);

        btnSlot3 = new BotonRPG(obtenerTextoSlot("slot3"));
        btnSlot3.setPreferredSize(tamanoSlot);
        gbc.gridy = 3;
        panelMenu.add(btnSlot3, gbc);

        // 6. Botón Volver
        btnVolver = new BotonRPG("VOLVER AL MENÚ");
        btnVolver.setPreferredSize(new Dimension(200, 45));
        gbc.gridy = 4;
        gbc.insets = new Insets(30, 10, 10, 10);
        panelMenu.add(btnVolver, gbc);

        add(panelMenu);
        inicializarEventos();
    }

    private String obtenerTextoSlot(String nombreSlot) {
        File archivo = new File("partidas/" + nombreSlot + ".dat");
        if (archivo.exists()) {
            return "SLOT: " + nombreSlot.toUpperCase() + " (Partida Guardada)";
        } else {
            return "SLOT: " + nombreSlot.toUpperCase() + " [VACÍO]";
        }
    }

    private void inicializarEventos() {
        btnSlot1.addActionListener(e -> procesarAccionSlot("slot1"));
        btnSlot2.addActionListener(e -> procesarAccionSlot("slot2"));
        btnSlot3.addActionListener(e -> procesarAccionSlot("slot3"));

        btnVolver.addActionListener(e -> {
            // Abrir el menú principal y cerrar esta pantalla
            PantallaInicio inicio = new PantallaInicio();
            inicio.setVisible(true);
            this.dispose();
        });
    }

    private void procesarAccionSlot(String slot) {
        if (esModoGuardar) {
            boolean exito = GestorPartida.getInstance().guardarPartidaActual(slot);
            if (exito) {
                JOptionPane.showMessageDialog(this, "Partida guardada correctamente en " + slot, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                actualizarTextoBotones();
            } else {
                JOptionPane.showMessageDialog(this, "No hay partida activa para guardar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            boolean exito = GestorPartida.getInstance().cargarPartidaExistente(slot);
            if (exito) {
                JOptionPane.showMessageDialog(this, "Partida cargada correctamente desde " + slot, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                // Próximamente: Transición a PantallaBatalla o PantallaMapa
            } else {
                JOptionPane.showMessageDialog(this, "El slot seleccionado está vacío.", "Atención", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void actualizarTextoBotones() {
        btnSlot1.setText(obtenerTextoSlot("slot1"));
        btnSlot2.setText(obtenerTextoSlot("slot2"));
        btnSlot3.setText(obtenerTextoSlot("slot3"));
    }

    // =========================================================================
    // BOTÓN CON ESTILO RPG
    // =========================================================================
    private class BotonRPG extends JButton {
        public BotonRPG(String texto) {
            super(texto);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setFont(new Font("Georgia", Font.BOLD, 14));
            setForeground(new Color(230, 230, 230));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color colorFondo = getModel().isRollover() ? new Color(50, 40, 30, 230) : new Color(20, 15, 10, 200);
            Color colorBorde = getModel().isRollover() ? new Color(255, 215, 0) : new Color(160, 120, 40);

            g2.setColor(colorFondo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

            g2.setColor(colorBorde);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 11, 11);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =========================================================================
    // PANEL CON FONDO
    // =========================================================================
    private class PanelConFondo extends JPanel {
        private Image imagenFondo;

        public PanelConFondo(String nombreImagen) {
            URL imgUrl = getClass().getResource("/resources/images/UI/" + nombreImagen);
            if (imgUrl != null) {
                this.imagenFondo = new ImageIcon(imgUrl).getImage();
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