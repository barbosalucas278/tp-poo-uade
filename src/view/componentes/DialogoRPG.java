package view.componentes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

// Reemplazo de JOptionPane con la estética del juego (fondo oscuro, marco dorado, tipografía Georgia)
public class DialogoRPG extends JDialog {

    private static final Color COLOR_FONDO = new Color(20, 15, 10);
    private static final Color COLOR_BORDE = new Color(160, 120, 40);
    private static final Color COLOR_TITULO = new Color(212, 175, 55);
    private static final Color COLOR_TEXTO = new Color(230, 230, 230);

    private boolean confirmado = false;

    private DialogoRPG(Component padre, String titulo, String mensaje, boolean conCancelar) {
        super(obtenerVentana(padre), titulo, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);

        JPanel panel = new PanelDialogo();
        panel.setLayout(new BorderLayout(0, 18));
        panel.setBorder(BorderFactory.createEmptyBorder(22, 30, 20, 30));

        JLabel lblTitulo = new JLabel(titulo.toUpperCase(), SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Georgia", Font.BOLD, 20));
        lblTitulo.setForeground(COLOR_TITULO);

        JLabel lblMensaje = new JLabel("<html><div style='text-align:center; width:280px'>" + mensaje + "</div></html>",
                SwingConstants.CENTER);
        lblMensaje.setFont(new Font("Georgia", Font.PLAIN, 15));
        lblMensaje.setForeground(COLOR_TEXTO);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panelBotones.setOpaque(false);

        BotonRPG btnAceptar = new BotonRPG(conCancelar ? "SÍ" : "ACEPTAR");
        btnAceptar.setPreferredSize(new Dimension(130, 40));
        btnAceptar.addActionListener(e -> {
            confirmado = true;
            dispose();
        });
        panelBotones.add(btnAceptar);

        if (conCancelar) {
            BotonRPG btnCancelar = new BotonRPG("NO");
            btnCancelar.setPreferredSize(new Dimension(130, 40));
            btnCancelar.addActionListener(e -> dispose());
            panelBotones.add(btnCancelar);
        }

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblMensaje, BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);

        // ESC cierra el diálogo sin confirmar
        panel.registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().setDefaultButton(btnAceptar);

        setContentPane(panel);
        pack();
        setLocationRelativeTo(padre);
    }

    private static Window obtenerVentana(Component padre) {
        if (padre == null || padre instanceof Window) {
            return (Window) padre;
        }
        return SwingUtilities.getWindowAncestor(padre);
    }

    public static void mostrarAviso(Component padre, String titulo, String mensaje) {
        new DialogoRPG(padre, titulo, mensaje, false).setVisible(true);
    }

    public static boolean confirmar(Component padre, String titulo, String mensaje) {
        DialogoRPG dialogo = new DialogoRPG(padre, titulo, mensaje, true);
        dialogo.setVisible(true);
        return dialogo.confirmado;
    }

    private static class PanelDialogo extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(COLOR_FONDO);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Borde dorado doble, igual que BotonRPG
            g2.setColor(COLOR_BORDE);
            g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            g2.setColor(COLOR_TITULO);
            g2.drawRect(4, 4, getWidth() - 9, getHeight() - 9);

            g2.dispose();
        }
    }
}
