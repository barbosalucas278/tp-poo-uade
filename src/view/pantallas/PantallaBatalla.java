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
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import controller.BatallaController;
import controller.EstadoCombate;
import model.DTOs.CombatienteDTO;
import model.DTOs.MapaDTO;
import view.componentes.BotonRPG;
import view.componentes.IconoBatalla;
import view.componentes.PanelCombatienteBatalla;
import view.componentes.PanelConFondo;
import view.componentes.PanelMarcoDorado;

/** Vista del combate activo y navegación a su resultado. */
public class PantallaBatalla extends JFrame {

    private static final Color DORADO = new Color(212, 175, 55);
    private static final Color AMARILLO_TURNO = new Color(255, 220, 55);
    private static final Color TEXTO = new Color(232, 225, 207);
    private final BatallaController controller;
    private final List<PanelCombatienteBatalla> tarjetasParty = new ArrayList<>();
    private final List<PanelCombatienteBatalla> tarjetasEnemigos = new ArrayList<>();
    private final JLabel lblMapaNivel = etiqueta("", 25, DORADO);
    private final JLabel lblSeleccion = etiqueta("SIN PERSONAJE", 13, DORADO);
    private final JLabel lblEstado = etiqueta("Esperando el próximo turno del jugador.", 12, TEXTO);
    private final JLabel lblObjetivo = etiqueta("", 12, TEXTO);
    private final JLabel lblInventario = etiqueta("", 12, TEXTO);
    private final BotonRPG btnHabilidadPrincipal = new BotonRPG("Habilidad especial");
    private final BotonRPG btnHabilidadSecundaria = new BotonRPG("Habilidad II");
    private final BotonRPG btnAtacar = new BotonRPG("Atacar");
    private final BotonRPG btnDefender = new BotonRPG("Defender");
    private final BotonRPG btnInventario = new BotonRPG("Abrir inventario");
    private final BotonRPG btnSalir = new BotonRPG("SALIR");
    private final PanelFormacion panelParty = new PanelFormacion(false);
    private final PanelFormacion panelEnemigos = new PanelFormacion(true);
    private CombatienteDTO personajeSeleccionado;
    private CombatienteDTO enemigoSeleccionado;
    private int indicePersonajeEnTurno = -1;
    private Timer relojCombate;
    private boolean resultadoMostrado;

    public PantallaBatalla() {
        this(new BatallaController());
    }

    public PantallaBatalla(BatallaController controller) {
        this.controller = controller;
        setTitle("Chronicles of UADE - Batalla");
        setSize(1180, 780);
        setMinimumSize(new Dimension(980, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel principal = new PanelConFondo("fondo_inicio.jpg");
        principal.setLayout(new BorderLayout(0, 12));
        principal.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        principal.add(crearCabecera(), BorderLayout.NORTH);

        JPanel contenido = new JPanel(new BorderLayout(12, 0));
        contenido.setOpaque(false);
        contenido.add(crearCampoBatalla(), BorderLayout.CENTER);
        contenido.add(crearPanelComandos(), BorderLayout.EAST);
        principal.add(contenido, BorderLayout.CENTER);
        setContentPane(principal);

        inicializarEventos();
        actualizarBatalla(null);
        iniciarRelojCombate();
    }

    private void iniciarRelojCombate() {
        relojCombate = new Timer(1000, e -> {
            EstadoCombate estado = controller.verificarEstadoCombate();
            if (estado == null) {
                controller.avanzarCombate();
                estado = controller.verificarEstadoCombate();
            }
            actualizarBatalla(estado);
        });

        relojCombate.start();
    }

    private JPanel crearCabecera() {
        JPanel cabecera = new PanelMarcoDorado("");
        cabecera.setLayout(new BorderLayout());
        cabecera.setBorder(BorderFactory.createCompoundBorder(cabecera.getBorder(),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));
        cabecera.add(etiqueta("CHRONICLES OF UADE", 11, TEXTO), BorderLayout.NORTH);
        cabecera.add(lblMapaNivel, BorderLayout.CENTER);
        return cabecera;
    }

    private JPanel crearCampoBatalla() {
        JPanel campo = new FondoBatalla();
        campo.setLayout(new BorderLayout(0, 5));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(160, 120, 40)),
                BorderFactory.createEmptyBorder(12, 12, 10, 12)));

        JPanel formaciones = new JPanel(new GridBagLayout());
        formaciones.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        gbc.weightx = 0.60;
        formaciones.add(crearBando("TEAM VENGADOR", panelParty), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.40;
        gbc.insets = new Insets(0, 12, 0, 0);
        JPanel bandoEnemigo = crearBando("ENEMIGOS", panelEnemigos);
        bandoEnemigo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(131, 119, 74)),
                BorderFactory.createEmptyBorder(0, 12, 0, 0)));
        formaciones.add(bandoEnemigo, gbc);
        campo.add(formaciones, BorderLayout.CENTER);

        JPanel estado = new JPanel(new BorderLayout(0, 4));
        estado.setOpaque(false);
        estado.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));
        estado.add(lblObjetivo, BorderLayout.NORTH);
        estado.add(lblEstado, BorderLayout.SOUTH);
        campo.add(estado, BorderLayout.SOUTH);
        return campo;
    }

    private JPanel crearBando(String titulo, JPanel formacion) {
        JPanel bando = new JPanel(new BorderLayout(0, 5));
        bando.setOpaque(false);
        JLabel etiquetaBando = etiqueta(titulo, 13, DORADO);
        etiquetaBando.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        bando.add(etiquetaBando, BorderLayout.NORTH);
        bando.add(formacion, BorderLayout.CENTER);
        return bando;
    }

    private JPanel crearPanelComandos() {
        JPanel comandos = new PanelMarcoDorado("");
        comandos.setLayout(new BorderLayout(0, 10));
        comandos.setPreferredSize(new Dimension(235, 0));
        comandos.setBorder(BorderFactory.createCompoundBorder(comandos.getBorder(),
                BorderFactory.createEmptyBorder(12, 10, 10, 10)));
        lblSeleccion.setPreferredSize(new Dimension(210, 26));
        comandos.add(lblSeleccion, BorderLayout.NORTH);

        JPanel secciones = new JPanel();
        secciones.setOpaque(false);
        secciones.setLayout(new BoxLayout(secciones, BoxLayout.Y_AXIS));

        JPanel habilidades = seccion("HABILIDADES ESPECIALES");
        habilidades.setLayout(new GridLayout(2, 1, 0, 8));
        configurarBoton(btnHabilidadPrincipal, IconoBatalla.Tipo.HABILIDAD, false);
        configurarBoton(btnHabilidadSecundaria, IconoBatalla.Tipo.HABILIDAD, false);
        habilidades.add(btnHabilidadPrincipal);
        habilidades.add(btnHabilidadSecundaria);
        agregarSeccion(secciones, habilidades, 186);

        JPanel acciones = seccion("ACCIONES");
        acciones.setLayout(new GridLayout(1, 2, 8, 0));
        configurarBoton(btnAtacar, IconoBatalla.Tipo.ATACAR, true);
        configurarBoton(btnDefender, IconoBatalla.Tipo.DEFENDER, true);
        btnAtacar.setMnemonic(KeyEvent.VK_A);
        btnDefender.setMnemonic(KeyEvent.VK_D);
        acciones.add(btnAtacar);
        acciones.add(btnDefender);
        agregarSeccion(secciones, acciones, 126);

        JPanel inventario = seccion("INVENTARIO");
        inventario.setLayout(new BorderLayout(0, 5));
        configurarBoton(btnInventario, IconoBatalla.Tipo.INVENTARIO, true);
        inventario.add(btnInventario, BorderLayout.CENTER);
        inventario.add(lblInventario, BorderLayout.SOUTH);
        agregarSeccion(secciones, inventario, 160);
        secciones.add(Box.createVerticalGlue());
        comandos.add(secciones, BorderLayout.CENTER);

        btnSalir.setPreferredSize(new Dimension(210, 42));
        btnSalir.setMnemonic(KeyEvent.VK_S);
        btnSalir.setToolTipText("Volver a la pantalla de prebatalla");
        comandos.add(btnSalir, BorderLayout.SOUTH);
        return comandos;
    }

    private JPanel seccion(String titulo) {
        JPanel panel = new PanelMarcoDorado("");
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(160, 120, 40)), titulo, 0, 0,
                new Font("Georgia", Font.BOLD, 12), DORADO),
                BorderFactory.createEmptyBorder(8, 5, 8, 5)));
        return panel;
    }

    private void agregarSeccion(JPanel contenedor, JPanel seccion, int alto) {
        seccion.setPreferredSize(new Dimension(215, alto));
        seccion.setMaximumSize(new Dimension(Integer.MAX_VALUE, alto));
        seccion.setMinimumSize(new Dimension(200, alto - 24));
        contenedor.add(seccion);
        contenedor.add(Box.createVerticalStrut(10));
    }

    private void configurarBoton(BotonRPG boton, IconoBatalla.Tipo tipo, boolean vertical) {
        boton.setIcon(new IconoBatalla(tipo, vertical ? 38 : 30));
        boton.setIconTextGap(vertical ? 5 : 9);
        boton.setFont(new Font("Georgia", Font.BOLD, 12));
        if (vertical) {
            boton.setHorizontalTextPosition(SwingConstants.CENTER);
            boton.setVerticalTextPosition(SwingConstants.BOTTOM);
        }
        boton.setToolTipText("Acción pendiente de implementar");
    }

    private void inicializarEventos() {

        btnAtacar.addActionListener(e -> {
            int indicePersonaje = indiceSeleccionado(tarjetasParty);
            int indiceEnemigo = indiceSeleccionado(tarjetasEnemigos);

            if (controller.resolverAtaque(indicePersonaje, indiceEnemigo)) {
                limpiarSeleccion();
            }
            actualizarBatalla();
        });
        btnDefender.addActionListener(e -> mostrarAccionPendiente("Defender"));
        btnHabilidadPrincipal.addActionListener(e -> mostrarAccionPendiente(btnHabilidadPrincipal.getText()));
        btnHabilidadSecundaria.setEnabled(false);
        btnHabilidadSecundaria.setToolTipText("Próximamente: segunda habilidad de clase");
        btnInventario.addActionListener(e -> abrirInventario());
        btnSalir.addActionListener(e -> {
            JFrame destino = controller.getMapaActual() == null ? new PantallaInicio() : new PantallaPreBatalla();
            destino.setVisible(true);
            dispose();
        });
    }

    private void abrirInventario() {
        int indicePersonaje = controller.getIndicePersonajeEnTurno();
        if (indicePersonaje < 0 || resultadoMostrado) {
            return;
        }
        boolean relojActivo = relojCombate != null && relojCombate.isRunning();
        if (relojActivo) {
            relojCombate.stop();
        }
        try {
            PantallaInventario inventario = new PantallaInventario(this, controller, indicePersonaje);
            inventario.setVisible(true);
            if (inventario.isAccionRealizada()) {
                limpiarSeleccion();
            }
            actualizarBatalla();
            if (inventario.isAccionRealizada()) {
                lblEstado.setText(inventario.getMensajeAccion());
            }
        } finally {
            if (relojActivo && !resultadoMostrado && isDisplayable()) {
                relojCombate.start();
            }
        }
    }

    @Override
    public void dispose() {
        if (relojCombate != null) {
            relojCombate.stop();
        }
        super.dispose();
    }

    public void actualizarBatalla() {
        actualizarBatalla(controller.verificarEstadoCombate());
    }

    /** Refresca la vista o muestra el resultado una sola vez al terminar el combate. */
    public void actualizarBatalla(EstadoCombate estado) {
        if (resultadoMostrado) {
            return;
        }
        if (estado != null) {
            resultadoMostrado = true;
            if (relojCombate != null) {
                relojCombate.stop();
            }
            PantallaResultadoBatalla resultado = new PantallaResultadoBatalla(controller, estado);
            resultado.setLocationRelativeTo(this);
            resultado.setVisible(true);
            dispose();
            return;
        }
        MapaDTO mapa = controller.getMapaActual();
        lblMapaNivel.setText(mapa == null ? "BATALLA · SIN PARTIDA ACTIVA"
                : "MAPA " + mapa.numeroMapa + "  ·  NIVEL " + mapa.nivelActual + " DE " + mapa.totalNiveles);
        int nuevoTurno = controller.getIndicePersonajeEnTurno();
        int indiceEnemigo = nuevoTurno >= 0 && nuevoTurno == indicePersonajeEnTurno
                ? indiceSeleccionado(tarjetasEnemigos) : -1;
        indicePersonajeEnTurno = nuevoTurno;
        personajeSeleccionado = null;
        enemigoSeleccionado = null;
        cargarCombatientes(controller.getPartyActual(), panelParty, tarjetasParty, false);
        cargarCombatientes(controller.getEnemigosActuales(), panelEnemigos, tarjetasEnemigos, true);
        restaurarSeleccion(tarjetasParty, indicePersonajeEnTurno, false);
        restaurarSeleccion(tarjetasEnemigos, indiceEnemigo, true);
        lblInventario.setText(controller.getCantidadItemsInventario() + " objetos disponibles");
        btnInventario.setEnabled(mapa != null && indicePersonajeEnTurno >= 0);
        btnInventario.setToolTipText(indicePersonajeEnTurno >= 0
                ? "Usar una poción consume la acción del personaje en turno."
                : "Esperá el turno de un personaje para abrir el inventario.");
        btnSalir.setToolTipText(mapa == null ? "Volver al menú" : "Volver a la pantalla de prebatalla");
        actualizarSeleccion();
        lblEstado.setForeground(personajeSeleccionado == null ? TEXTO : AMARILLO_TURNO);
        lblEstado.setText(mapa == null ? "Iniciá una partida desde el menú para entrar en batalla."
                : personajeSeleccionado == null ? "La energía avanza. Esperá tu próximo turno."
                        : "Turno de " + personajeSeleccionado.getNombre() + ": atacá o usá una poción del inventario.");
        revalidate();
        repaint();
    }

    private void cargarCombatientes(List<CombatienteDTO> combatientes, JPanel panel,
            List<PanelCombatienteBatalla> tarjetas, boolean enemigos) {
        panel.removeAll();
        tarjetas.clear();
        for (CombatienteDTO combatiente : combatientes) {
            PanelCombatienteBatalla tarjeta = new PanelCombatienteBatalla(combatiente, enemigos);
            boolean enTurno = !enemigos && tarjetas.size() == indicePersonajeEnTurno;
            tarjeta.setEnTurno(enTurno);
            tarjeta.setEnabled(combatiente.getVidaActual() > 0
                    && (enemigos ? indicePersonajeEnTurno >= 0 : enTurno));
            tarjeta.addActionListener(e -> seleccionarCombatiente(tarjeta, enemigos));
            tarjetas.add(tarjeta);
            panel.add(tarjeta);
        }
        if (combatientes.isEmpty()) {
            panel.add(etiqueta(enemigos ? "Sin enemigos" : "Party vacía", 15, TEXTO));
        }
    }

    private int indiceSeleccionado(List<PanelCombatienteBatalla> tarjetas) {
        for (int i = 0; i < tarjetas.size(); i++) {
            if (tarjetas.get(i).isSeleccionado()) {
                return i;
            }
        }
        return -1;
    }

    private void restaurarSeleccion(List<PanelCombatienteBatalla> tarjetas, int indice, boolean enemigo) {
        if (indice >= 0 && indice < tarjetas.size()) {
            seleccionarCombatiente(tarjetas.get(indice), enemigo);
        }
    }

    private void limpiarSeleccion() {
        personajeSeleccionado = null;
        enemigoSeleccionado = null;
        for (PanelCombatienteBatalla tarjeta : tarjetasParty) {
            tarjeta.setSeleccionado(false);
            tarjeta.setEnTurno(false);
        }
        for (PanelCombatienteBatalla tarjeta : tarjetasEnemigos) {
            tarjeta.setSeleccionado(false);
        }
        indicePersonajeEnTurno = -1;
    }

    private void seleccionarCombatiente(PanelCombatienteBatalla tarjeta, boolean enemigo) {
        if (!tarjeta.isEnabled() || tarjeta.getCombatiente().getVidaActual() <= 0) {
            return;
        }
        for (PanelCombatienteBatalla otra : enemigo ? tarjetasEnemigos : tarjetasParty) {
            otra.setSeleccionado(otra == tarjeta);
        }
        if (enemigo) {
            enemigoSeleccionado = tarjeta.getCombatiente();
        } else {
            personajeSeleccionado = tarjeta.getCombatiente();
        }
        actualizarSeleccion();
    }

    private void actualizarSeleccion() {
        lblSeleccion.setText(personajeSeleccionado == null ? "ESPERANDO TURNO"
                : "TU TURNO · " + personajeSeleccionado.getClase().toUpperCase(java.util.Locale.ROOT));
        lblSeleccion.setForeground(personajeSeleccionado == null ? DORADO : AMARILLO_TURNO);
        lblSeleccion.setToolTipText(personajeSeleccionado == null ? null : personajeSeleccionado.getNombre());
        lblObjetivo.setText(personajeSeleccionado == null ? "Sin objetivo seleccionado."
                : enemigoSeleccionado == null ? "Seleccioná un enemigo como objetivo."
                : "Objetivo: " + enemigoSeleccionado.getNombre());
        btnAtacar.setEnabled(personajeSeleccionado != null && enemigoSeleccionado != null);
        btnAtacar.setToolTipText(personajeSeleccionado == null ? "Esperá el turno de un personaje."
                : enemigoSeleccionado == null ? "Seleccioná un enemigo para atacar."
                        : "Atacar con " + personajeSeleccionado.getNombre());
        btnDefender.setEnabled(personajeSeleccionado != null);
        btnHabilidadPrincipal.setEnabled(personajeSeleccionado != null);
        btnHabilidadPrincipal.setText(nombreHabilidad());
    }

    private String nombreHabilidad() {
        if (personajeSeleccionado == null) {
            return "Habilidad especial";
        }
        switch (personajeSeleccionado.getClase()) {
            case "Guerrero":
                return "Grito de guerra";
            case "Hechicero":
                return "Meditar";
            case "Asesino":
                return "Sigilo";
            default:
                return "Habilidad especial";
        }
    }

    private void mostrarAccionPendiente(String accion) {
        lblEstado.setText(accion + ": acción aún no disponible.");
    }

    private static JLabel etiqueta(String texto, int tamano, Color color) {
        JLabel etiqueta = new JLabel(texto, SwingConstants.CENTER);
        etiqueta.setFont(new Font("Georgia", Font.BOLD, tamano));
        etiqueta.setForeground(color);
        return etiqueta;
    }

    public BotonRPG getBtnAtacar() {
        return btnAtacar;
    }

    public BotonRPG getBtnDefender() {
        return btnDefender;
    }

    public BotonRPG getBtnHabilidadPrincipal() {
        return btnHabilidadPrincipal;
    }

    public BotonRPG getBtnHabilidadSecundaria() {
        return btnHabilidadSecundaria;
    }

    public BotonRPG getBtnInventario() {
        return btnInventario;
    }

    public BotonRPG getBtnSalir() {
        return btnSalir;
    }

    /**
     * Posiciones proporcionales: cuatro aliados en rombo y hasta tres enemigos
     * alternados.
     */
    private static class PanelFormacion extends JPanel {
        private final boolean enemigos;

        private PanelFormacion(boolean enemigos) {
            super(null);
            this.enemigos = enemigos;
            setOpaque(false);
            setPreferredSize(new Dimension(enemigos ? 310 : 460, 540));
            setMinimumSize(new Dimension(0, 0));
        }

        @Override
        public void doLayout() {
            int cantidad = getComponentCount();
            if (cantidad == 0) {
                return;
            }
            if (!(getComponent(0) instanceof PanelCombatienteBatalla)) {
                getComponent(0).setBounds(0, 0, getWidth(), getHeight());
                return;
            }
            double[][] posiciones;
            double proporcionAncho;
            double proporcionAlto;
            if (cantidad == 1) {
                posiciones = new double[][] { { .5, .5 } };
                proporcionAncho = .82;
                proporcionAlto = .82;
            } else if (cantidad == 2) {
                posiciones = new double[][] { { .28, .27 }, { .72, .73 } };
                proporcionAncho = enemigos ? .72 : .52;
                proporcionAlto = .44;
            } else if (cantidad == 3 && enemigos) {
                if (getHeight() < 490) {
                    posiciones = new double[][] { { .5, .24 }, { .25, .74 }, { .75, .74 } };
                    proporcionAncho = .47;
                    proporcionAlto = .45;
                } else {
                    posiciones = new double[][] { { .34, .16 }, { .66, .5 }, { .34, .84 } };
                    proporcionAncho = .68;
                    proporcionAlto = .31;
                }
            } else if (cantidad == 3) {
                posiciones = new double[][] { { .5, .24 }, { .24, .73 }, { .76, .73 } };
                proporcionAncho = .46;
                proporcionAlto = .44;
            } else if (cantidad == 4) {
                if (getHeight() < 490) {
                    // En ventanas bajas, dos filas permiten conservar retratos y barras legibles.
                    int ancho = Math.min(230, getWidth() / 2 - 8);
                    int alto = getHeight() / 2 - 8;
                    for (int i = 0; i < cantidad; i++) {
                        getComponent(i).setBounds(i % 2 * getWidth() / 2 + (getWidth() / 2 - ancho) / 2,
                                i / 2 * getHeight() / 2 + 4, ancho, alto);
                    }
                    return;
                }
                posiciones = new double[][] { { .5, .16 }, { .23, .5 }, { .77, .5 }, { .5, .84 } };
                proporcionAncho = .43;
                proporcionAlto = .31;
            } else {
                // La creación actual admite cuatro héroes; la grilla evita ocultar futuras
                // incorporaciones.
                int columnas = 2;
                int filas = (cantidad + 1) / 2;
                for (int i = 0; i < cantidad; i++) {
                    getComponent(i).setBounds(i % columnas * getWidth() / columnas,
                            i / columnas * getHeight() / filas, getWidth() / columnas - 4, getHeight() / filas - 4);
                }
                return;
            }
            int ancho = Math.min(230, (int) (getWidth() * proporcionAncho));
            int alto = Math.max(155, (int) (getHeight() * proporcionAlto));
            for (int i = 0; i < cantidad; i++) {
                int x = (int) (getWidth() * posiciones[i][0] - ancho / 2);
                int y = (int) (getHeight() * posiciones[i][1] - alto / 2);
                getComponent(i).setBounds(Math.max(0, Math.min(x, getWidth() - ancho)),
                        Math.max(0, Math.min(y, getHeight() - alto)), ancho, alto);
            }
        }
    }

    private static class FondoBatalla extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, new Color(19, 36, 33), 0, getHeight(), new Color(8, 18, 24)));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(new Color(131, 143, 100, 16));
            for (int y = 0; y < getHeight(); y += 65) {
                g2.drawLine(0, y, getWidth(), y);
                for (int x = (y / 65 % 2) * -70; x < getWidth(); x += 140) {
                    g2.drawLine(x, y, x, y + 65);
                }
            }
            g2.setColor(new Color(176, 155, 90, 12));
            g2.setStroke(new BasicStroke(2));
            int diametro = Math.min(getWidth(), getHeight()) * 4 / 5;
            g2.drawOval((getWidth() - diametro) / 2, (getHeight() - diametro) / 2, diametro, diametro);
            g2.dispose();
        }
    }
}
