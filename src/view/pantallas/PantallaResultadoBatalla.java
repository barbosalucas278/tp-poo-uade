package view.pantallas;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import controller.BatallaController;
import controller.EstadoCombate;
import model.DTOs.CombatienteDTO;
import model.DTOs.MapaDTO;
import view.componentes.BotonRPG;
import view.componentes.PanelConFondo;
import view.componentes.PanelMarcoDorado;

/** Resumen del combate terminado; el controller administra el avance de nivel o mapa. */
public class PantallaResultadoBatalla extends JFrame {

    private static final Color DORADO = new Color(212, 175, 55);
    private static final Color TEXTO = new Color(232, 225, 207);
    private static final Color SECUNDARIO = new Color(180, 173, 156);
    private final BatallaController controller;
    private final EstadoCombate estado;
    private final BotonRPG btnPrincipal = new BotonRPG("");
    private final BotonRPG btnVolverMenu = new BotonRPG("VOLVER AL MENÚ");
    private final BotonRPG btnGuardar = new BotonRPG("GUARDAR PARTIDA");
    private final JLabel lblMensaje = etiqueta("", 14, TEXTO);
    private boolean navegando;
    private boolean victoriaConfirmada;
    private boolean siguienteEncuentroDisponible;

    public PantallaResultadoBatalla(BatallaController controller, EstadoCombate estado) {
        this.controller = Objects.requireNonNull(controller);
        this.estado = Objects.requireNonNull(estado);
        MapaDTO mapa = Objects.requireNonNull(controller.getMapaActual(), "Debe existir un nivel para mostrar su resultado.");
        boolean victoria = estado == EstadoCombate.VICTORIA;
        boolean ultimoNivel = mapa.nivelActual == mapa.totalNiveles;

        setTitle("Chronicles of UADE - Resultado de batalla");
        setSize(980, 640);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel principal = new PanelConFondo("fondo_inicio.jpg");
        principal.setLayout(new BorderLayout());
        principal.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel marco = new PanelMarcoDorado("");
        marco.setLayout(new BorderLayout(0, 12));
        marco.setBorder(BorderFactory.createCompoundBorder(marco.getBorder(),
                BorderFactory.createEmptyBorder(22, 35, 22, 35)));

        JPanel cabecera = new JPanel(new BorderLayout(0, 8));
        cabecera.setOpaque(false);
        cabecera.add(etiqueta("CHRONICLES OF UADE", 13, DORADO), BorderLayout.NORTH);
        cabecera.add(etiqueta("MAPA " + mapa.numeroMapa + "  ·  NIVEL " + mapa.nivelActual
                + " DE " + mapa.totalNiveles, 17, TEXTO), BorderLayout.CENTER);
        marco.add(cabecera, BorderLayout.NORTH);
        marco.add(crearResumen(mapa, victoria, ultimoNivel), BorderLayout.CENTER);
        marco.add(crearAcciones(victoria, ultimoNivel), BorderLayout.SOUTH);
        principal.add(marco, BorderLayout.CENTER);
        setContentPane(principal);
        getRootPane().setDefaultButton(btnPrincipal);
    }

    private JPanel crearResumen(MapaDTO mapa, boolean victoria, boolean ultimoNivel) {
        JPanel resumen = new JPanel(new GridBagLayout());
        resumen.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 7, 0);
        resumen.add(new EmblemaResultado(victoria), gbc);

        gbc.gridy++;
        resumen.add(etiqueta(victoria ? "¡VICTORIA!" : "DERROTA", 40,
                victoria ? DORADO : new Color(207, 102, 96)), gbc);
        gbc.gridy++;
        String descripcion = victoria
                ? ultimoNivel ? "Venciste al jefe y completaste el mapa." : "Todos los enemigos del nivel fueron derrotados."
                : "Tu party ha caído. Podés comenzar una nueva aventura desde el menú.";
        resumen.add(etiqueta(descripcion, 14, TEXTO), gbc);

        List<CombatienteDTO> party = controller.getPartyActual();
        List<CombatienteDTO> enemigos = controller.getEnemigosActuales();
        long sobrevivientes = party.stream().filter(p -> p.getVidaActual() > 0).count();
        long derrotados = enemigos.stream().filter(e -> e.getVidaActual() <= 0).count();

        JPanel estadisticas = new JPanel(new GridLayout(1, 3, 14, 0));
        estadisticas.setOpaque(false);
        estadisticas.add(crearDato("NIVEL", mapa.nivelActual + " / " + mapa.totalNiveles));
        estadisticas.add(crearDato("ENEMIGOS DERROTADOS", derrotados + " / " + enemigos.size()));
        estadisticas.add(crearDato("HÉROES EN PIE", sobrevivientes + " / " + party.size()));
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 0, 14, 0);
        resumen.add(estadisticas, gbc);

        // TODO(recompensas): mostrar experiencia y botín cuando el modelo los otorgue.
        lblMensaje.setText(victoria
                ? ultimoNivel ? "Mapa completado. Próximo destino: mapa " + (mapa.numeroMapa + 1) + "."
                        : "Próximo encuentro: nivel " + (mapa.nivelActual + 1) + ". Prepará a tu party para continuar."
                : "La derrota no avanza el nivel actual.");
        lblMensaje.setForeground(SECUNDARIO);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        resumen.add(lblMensaje, gbc);
        return resumen;
    }

    private JPanel crearDato(String titulo, String valor) {
        JPanel dato = new PanelMarcoDorado("");
        dato.setLayout(new BorderLayout(0, 8));
        dato.setBorder(BorderFactory.createCompoundBorder(dato.getBorder(),
                BorderFactory.createEmptyBorder(12, 8, 12, 8)));
        dato.add(etiqueta(titulo, 11, SECUNDARIO), BorderLayout.NORTH);
        dato.add(etiqueta(valor, 25, TEXTO), BorderLayout.CENTER);
        return dato;
    }

    private JPanel crearAcciones(boolean victoria, boolean ultimoNivel) {
        JPanel acciones = new JPanel(new BorderLayout(16, 0));
        acciones.setOpaque(false);

        btnPrincipal.setText(victoria
                ? ultimoNivel ? "SIGUIENTE MAPA" : "SIGUIENTE NIVEL"
                : "VOLVER AL MENÚ");
        btnPrincipal.setPreferredSize(new Dimension(250, 48));
        btnPrincipal.setMnemonic(victoria ? KeyEvent.VK_S : KeyEvent.VK_M);
        btnPrincipal.setSeleccionado(victoria);
        btnPrincipal.addActionListener(e -> navegar(victoria));
        acciones.add(btnPrincipal, BorderLayout.EAST);

        btnVolverMenu.setPreferredSize(new Dimension(210, 48));
        btnVolverMenu.setMnemonic(KeyEvent.VK_M);
        btnVolverMenu.setVisible(victoria);
        btnVolverMenu.addActionListener(e -> navegar(false));

        // Con la party derrotada no hay nada útil para guardar
        if (victoria) {
            JPanel secundarias = new JPanel(new GridLayout(1, 2, 12, 0));
            secundarias.setOpaque(false);
            secundarias.add(btnVolverMenu);
            btnGuardar.setPreferredSize(new Dimension(230, 48));
            btnGuardar.setMnemonic(KeyEvent.VK_G);
            btnGuardar.addActionListener(e -> abrirGuardado());
            secundarias.add(btnGuardar);
            acciones.add(secundarias, BorderLayout.WEST);
        }
        return acciones;
    }

    private void navegar(boolean continuar) {
        if (navegando) {
            return;
        }
        navegando = true;
        btnPrincipal.setEnabled(false);
        btnVolverMenu.setEnabled(false);
        btnGuardar.setEnabled(false);

        boolean haySiguienteEncuentro = estado == EstadoCombate.VICTORIA && confirmarVictoria();
        JFrame destino = continuar && haySiguienteEncuentro ? new PantallaPreBatalla() : new PantallaInicio();
        destino.setLocationRelativeTo(this);
        destino.setVisible(true);
        dispose();
    }

    private void abrirGuardado() {
        // Se completa el nivel antes de guardar para que al cargar se arranque en el siguiente
        confirmarVictoria();
        PantallaGuardado guardado = new PantallaGuardado(true, this);
        guardado.setLocationRelativeTo(this);
        guardado.setVisible(true);
        setVisible(false);
    }

    /** Completa el nivel una sola vez, ya sea al guardar o al navegar. */
    private boolean confirmarVictoria() {
        if (!victoriaConfirmada) {
            siguienteEncuentroDisponible = controller.continuarAlSiguienteNivel();
            victoriaConfirmada = true;
        }
        return siguienteEncuentroDisponible;
    }

    private static JLabel etiqueta(String texto, int tamano, Color color) {
        JLabel etiqueta = new JLabel(texto, SwingConstants.CENTER);
        etiqueta.setFont(new Font("Georgia", Font.BOLD, tamano));
        etiqueta.setForeground(color);
        return etiqueta;
    }

    public BotonRPG getBtnPrincipal() { return btnPrincipal; }
    public BotonRPG getBtnVolverMenu() { return btnVolverMenu; }
    public BotonRPG getBtnGuardar() { return btnGuardar; }

    /** Emblema dibujado para reutilizar el estilo actual sin necesitar nuevos assets. */
    private static class EmblemaResultado extends JPanel {
        private final boolean victoria;

        private EmblemaResultado(boolean victoria) {
            this.victoria = victoria;
            setOpaque(false);
            setPreferredSize(new Dimension(92, 92));
            getAccessibleContext().setAccessibleName(victoria ? "Victoria" : "Derrota");
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color acento = victoria ? DORADO : new Color(187, 83, 77);
            g2.setPaint(new GradientPaint(0, 0, new Color(45, 38, 21), 0, getHeight(), new Color(16, 19, 19)));
            g2.fillOval(3, 3, 86, 86);
            g2.setColor(acento);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(3, 3, 86, 86);
            g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 80));
            g2.drawOval(10, 10, 72, 72);
            g2.setColor(acento);
            g2.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            if (victoria) {
                g2.drawLine(25, 47, 40, 62);
                g2.drawLine(40, 62, 68, 31);
            } else {
                g2.drawLine(30, 30, 62, 62);
                g2.drawLine(62, 30, 30, 62);
            }
            g2.dispose();
        }
    }
}
