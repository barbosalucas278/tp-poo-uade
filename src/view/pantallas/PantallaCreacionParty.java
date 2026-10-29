package view.pantallas;

import controller.CreacionPartyController;
import helpers.archivos.ResourcesHelper;
import helpers.entidades.EstadisticasBasePersonajes;
import helpers.entidades.StatsBaseConfigPerosnajes;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import model.DTOs.PersonajeDTO;
import model.entidades.personajes.TipoPersonaje;
import view.componentes.BarraEstadoRPG;
import view.componentes.BotonRPG;
import view.componentes.CampoTextoRPG;
import view.componentes.DialogoRPG;
import view.componentes.PanelConFondo;
import view.componentes.PanelMarcoDorado;

public class PantallaCreacionParty extends JFrame {
    private CreacionPartyController controller;
    private BotonRPG[] btnSlots = new BotonRPG[4];
    private String[] nombresPersonajesSlots = new String[4];
    private int slotSeleccionado = 0;

    private JProgressBar barVida, barAtaque, barDefensa;
    private JLabel lblVidaVal, lblAtaqueVal, lblDefensaVal;
    
    // Variables de selección
    private String claseSeleccionada = "Guerrero"; // "Guerrero", "Asesino", "Maga"
    private String generoSeleccionado = "Hombre";   // "Hombre", "Mujer"

    // Botones con estado activo
    private BotonRPG btnGuerrero, btnAsesino, btnHechicero;
    private BotonRPG btnHombre, btnMujer;

    private JLabel lblAvatar;
    private Image imgAvatar; // Guarda la imagen original para dibujarla a pantalla completa en el contenedor
    private JTextField txtNombre;
    private BotonRPG btnVolver, btnJugar, btnBorrar, btnCrear;

    public PantallaCreacionParty() {
        controller = new CreacionPartyController();
        setTitle("Chronicles of UADE - Creación de Party");
        setSize(980, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        PanelConFondo panelPrincipal = new PanelConFondo("fondo_inicio.jpg");
        panelPrincipal.setLayout(new BorderLayout(15, 15));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // 1. BARRA SUPERIOR
        panelPrincipal.add(crearPanelSuperior(), BorderLayout.NORTH);

        // 2. CONTENEDOR CENTRAL (COLUMNAS ESTÁTICAS)
        JPanel panelContenido = new JPanel(new GridBagLayout());
        panelContenido.setOpaque(false);

        GridBagConstraints gbcCol = new GridBagConstraints();
        gbcCol.fill = GridBagConstraints.BOTH;
        gbcCol.insets = new Insets(0, 5, 0, 5);

        // Columna Izquierda: Slots (Ancho estático 230px)
        JPanel panelSlots = crearPanelSlots();
        panelSlots.setPreferredSize(new Dimension(230, 0));
        panelSlots.setMinimumSize(new Dimension(230, 0));
        gbcCol.gridx = 0;
        gbcCol.weightx = 0.0;
        gbcCol.weighty = 1.0;
        panelContenido.add(panelSlots, gbcCol);

        // Columna Central: Atributos y Clase (Ancho dinámico)
        gbcCol.gridx = 1;
        gbcCol.weightx = 1.0;
        panelContenido.add(crearPanelCentral(), gbcCol);

        // Columna Derecha: Género y Avatar (Ancho estático 270px)
        JPanel panelDerecho = crearPanelDerecho();
        panelDerecho.setPreferredSize(new Dimension(270, 0));
        panelDerecho.setMinimumSize(new Dimension(270, 0));
        gbcCol.gridx = 2;
        gbcCol.weightx = 0.0;
        panelContenido.add(panelDerecho, gbcCol);

        panelPrincipal.add(panelContenido, BorderLayout.CENTER);
        add(panelPrincipal);

        // Estado inicial
        seleccionarClase("Guerrero");
        seleccionarGenero("Hombre");
        seleccionarSlot(0);
    }

    // =========================================================================
    // BARRA SUPERIOR: VOLVER | TÍTULO | JUGAR
    // =========================================================================
    private JPanel crearPanelSuperior() {
        JPanel panelSuperior = new JPanel(new BorderLayout(15, 0));
        panelSuperior.setOpaque(false);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        btnVolver = new BotonRPG("VOLVER");
        btnVolver.setPreferredSize(new Dimension(130, 40));
        btnVolver.addActionListener(e -> {
            PantallaInicio inicio = new PantallaInicio();
            inicio.setVisible(true);
            dispose();
        });

        JLabel lblTitulo = new JLabel("CREACIÓN DE PERSONAJE", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Georgia", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(212, 175, 55));

        btnJugar = new BotonRPG("JUGAR");
        btnJugar.setPreferredSize(new Dimension(130, 40));
        btnJugar.addActionListener(e -> iniciarJuego());

        panelSuperior.add(btnVolver, BorderLayout.WEST);
        panelSuperior.add(lblTitulo, BorderLayout.CENTER);
        panelSuperior.add(btnJugar, BorderLayout.EAST);

        return panelSuperior;
    }

    // =========================================================================
    // PANEL IZQUIERDO: SLOTS DE PARTY (1 a 4)
    // =========================================================================
    private JPanel crearPanelSlots() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 0, 12));
        panel.setOpaque(false);

        for (int i = 0; i < 4; i++) {
            btnSlots[i] = new BotonRPG("VACÍO");
            btnSlots[i].setFont(new Font("Georgia", Font.BOLD, 13));
            btnSlots[i].setPreferredSize(new Dimension(220, 100));
            final int index = i;
            btnSlots[i].addActionListener(e -> seleccionarSlot(index));
            panel.add(btnSlots[i]);
        }
        return panel;
    }

    private void seleccionarSlot(int index) {
        this.slotSeleccionado = index;
        for (int i = 0; i < btnSlots.length; i++) {
            btnSlots[i].setSeleccionado(i == index);
        }
    }

    // =========================================================================
    // PANEL CENTRAL: ATRIBUTOS Y CLASE
    // =========================================================================
    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // 1. Panel Atributos
        JPanel panelAtributos = new PanelMarcoDorado("ATRIBUTOS");
        panelAtributos.setLayout(new BorderLayout());
        panelAtributos.setBorder(BorderFactory.createCompoundBorder(panelAtributos.getBorder(),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        JPanel listaAtributos = new JPanel();
        listaAtributos.setOpaque(false);
        listaAtributos.setLayout(new BoxLayout(listaAtributos, BoxLayout.Y_AXIS));
        listaAtributos.add(Box.createVerticalGlue());

        barVida = new BarraEstadoRPG(new Color(190, 65, 60), 200, 0);
        lblVidaVal = new JLabel("0 / 100", SwingConstants.RIGHT);
        lblVidaVal.setForeground(Color.WHITE);
        listaAtributos.add(crearFilaAtributo("VIDA", barVida, lblVidaVal));
        listaAtributos.add(Box.createVerticalStrut(12));

        barAtaque = new BarraEstadoRPG(new Color(75, 120, 185), 50, 0);
        lblAtaqueVal = new JLabel("0 / 30", SwingConstants.RIGHT);
        lblAtaqueVal.setForeground(Color.WHITE);
        listaAtributos.add(crearFilaAtributo("ATAQUE", barAtaque, lblAtaqueVal));
        listaAtributos.add(Box.createVerticalStrut(12));

        barDefensa = new BarraEstadoRPG(new Color(75, 155, 100), 15, 0);
        lblDefensaVal = new JLabel("0 / 15", SwingConstants.RIGHT);
        lblDefensaVal.setForeground(Color.WHITE);
        listaAtributos.add(crearFilaAtributo("DEFENSA", barDefensa, lblDefensaVal));
        listaAtributos.add(Box.createVerticalGlue());

        panelAtributos.add(listaAtributos, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.70;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(panelAtributos, gbc);

        // 2. Selección de Clase
        JPanel panelClase = new PanelMarcoDorado("CLASE");
        panelClase.setLayout(new GridLayout(1, 3, 6, 0));

        btnGuerrero = new BotonRPG("Guerrero");
        btnAsesino = new BotonRPG("Asesino");
        btnHechicero = new BotonRPG("Hechicero");

        btnGuerrero.addActionListener(e -> seleccionarClase("Guerrero"));
        btnAsesino.addActionListener(e -> seleccionarClase("Asesino"));
        btnHechicero.addActionListener(e -> seleccionarClase("Hechicero"));

        panelClase.add(btnGuerrero);
        panelClase.add(btnAsesino);
        panelClase.add(btnHechicero);

        gbc.gridy = 1;
        gbc.weighty = 0.30;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(panelClase, gbc);

        return panel;
    }

    private void seleccionarClase(String clase) {
        this.claseSeleccionada = clase;
        btnGuerrero.setSeleccionado(clase.equals("Guerrero"));
        btnAsesino.setSeleccionado(clase.equals("Asesino"));
        btnHechicero.setSeleccionado(clase.equals("Hechicero"));
        actualizarAtributos();
        actualizarAvatar();
    }

    private void actualizarAtributos() {
        TipoPersonaje tipo = TipoPersonaje.valueOf(claseSeleccionada.toUpperCase(java.util.Locale.ROOT));
        StatsBaseConfigPerosnajes estadisticas = EstadisticasBasePersonajes.obtener(tipo);

        barVida.setValue(estadisticas.getVida());
        lblVidaVal.setText(estadisticas.getVida() + " / " + barVida.getMaximum());
        barAtaque.setValue(estadisticas.getAtaque());
        lblAtaqueVal.setText(estadisticas.getAtaque() + " / " + barAtaque.getMaximum());
        barDefensa.setValue(estadisticas.getDefensa());
        lblDefensaVal.setText(estadisticas.getDefensa() + " / " + barDefensa.getMaximum());
    }

    private JPanel crearFilaAtributo(String nombre, JProgressBar barra, JLabel lblVal) {
        JPanel fila = new JPanel(new BorderLayout(0, 6));
        fila.setOpaque(false);

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        JLabel lbl = new JLabel(nombre);
        lbl.setForeground(new Color(212, 175, 55));
        lbl.setFont(new Font("Georgia", Font.BOLD, 13));
        lblVal.setFont(new Font("Georgia", Font.BOLD, 12));
        encabezado.add(lbl, BorderLayout.WEST);
        encabezado.add(lblVal, BorderLayout.EAST);

        fila.add(encabezado, BorderLayout.NORTH);
        fila.add(barra, BorderLayout.SOUTH);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        return fila;
    }

    // =========================================================================
    // PANEL DERECHO: SECCIÓN GÉNERO, AVATAR (100% RECTÁNGULO), NOMBRE Y ACCIONES
    // =========================================================================
    private JPanel crearPanelDerecho() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        // 1. Marco Superior: Género
        JPanel panelGenero = new PanelMarcoDorado("GÉNERO");
        panelGenero.setLayout(new GridLayout(1, 2, 8, 0));
        panelGenero.setPreferredSize(new Dimension(200, 60));

        btnHombre = new BotonRPG("Hombre");
        btnMujer = new BotonRPG("Mujer");

        btnHombre.addActionListener(e -> seleccionarGenero("Hombre"));
        btnMujer.addActionListener(e -> seleccionarGenero("Mujer"));

        panelGenero.add(btnHombre);
        panelGenero.add(btnMujer);

        panel.add(panelGenero, BorderLayout.NORTH);

        // 2. Marco Central: Avatar y Nombre
        JPanel panelAvatar = new PanelMarcoDorado("");
        panelAvatar.setLayout(new BorderLayout(0, 5));

        // Sobrescribimos paintComponent para estirar la imagen al 100% del alto y ancho del componente
        lblAvatar = new JLabel("", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imgAvatar != null) {
                    g.drawImage(imgAvatar, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        txtNombre = new CampoTextoRPG("Nombre de Héroe");

        panelAvatar.add(lblAvatar, BorderLayout.CENTER);
        panelAvatar.add(txtNombre, BorderLayout.SOUTH);

        panel.add(panelAvatar, BorderLayout.CENTER);

        // 3. Botones Inferiores (BORRAR y CREAR)
        JPanel panelInferior = new JPanel(new GridLayout(1, 2, 8, 0));
        panelInferior.setOpaque(false);

        btnBorrar = new BotonRPG("BORRAR");
        btnBorrar.addActionListener(e -> borrarPersonajeSlot());

        btnCrear = new BotonRPG("CREAR");
        btnCrear.addActionListener(e -> crearPersonajeSlot());

        panelInferior.add(btnBorrar);
        panelInferior.add(btnCrear);

        panel.add(panelInferior, BorderLayout.SOUTH);
        return panel;
    }

    private void seleccionarGenero(String genero) {
        this.generoSeleccionado = genero;
        btnHombre.setSeleccionado(genero.equals("Hombre"));
        btnMujer.setSeleccionado(genero.equals("Mujer"));
        actualizarAvatar();
    }

    // =========================================================================
    // LÓGICA DE JUEGO, AVATAR Y CREACIÓN DE SLOTS
    // =========================================================================
    private void actualizarAvatar() {
        String rutaImagen = ResourcesHelper.getRutaImagenPersonaje(claseSeleccionada, generoSeleccionado);

        URL imgUrl = getClass().getResource(rutaImagen);
        if (imgUrl != null) {
            ImageIcon icon = new ImageIcon(imgUrl);
            imgAvatar = icon.getImage(); // Guardamos la imagen sin escalar previamente
            lblAvatar.setText("");
        } else {
            imgAvatar = null;
            lblAvatar.setText("[" + rutaImagen + " No Encontrado]");
            lblAvatar.setForeground(Color.RED);
        }
        
        lblAvatar.repaint(); // Fuerza el redibujado de la imagen estirada a todo el rectángulo
    }

    private void crearPersonajeSlot() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty() || nombre.equals("Nombre de Héroe")) {
            DialogoRPG.mostrarAviso(this, "Atención", "Por favor, ingresá un nombre para el personaje.");
            return;
        }

        String claseTexto = claseSeleccionada.equals("Hechicera") ? "Hechicero" : claseSeleccionada;
        String claseVisible = obtenerClaseVisible(claseTexto);
        nombresPersonajesSlots[slotSeleccionado] = nombre + " - " + claseVisible;

        btnSlots[slotSeleccionado].setText("<html><center>" + nombre + "<br><font color='#D4AF37'>(" + claseVisible + ")</font></center></html>");

        controller.agregarPersonaje(new PersonajeDTO(nombre, claseTexto, ResourcesHelper.getRutaImagenPersonaje(claseSeleccionada, generoSeleccionado)));
    }

    private String obtenerClaseVisible(String clase) {
        if (!generoSeleccionado.equals("Mujer")) {
            return clase;
        }

        switch (clase) {
            case "Guerrero":
                return "Guerrera";
            case "Asesino":
                return "Asesina";
            case "Hechicero":
                return "Hechicera";
            default:
                return clase;
        }
    }

    private void borrarPersonajeSlot() {
        if (nombresPersonajesSlots[slotSeleccionado] == null) {
            DialogoRPG.mostrarAviso(this, "Atención", "El slot seleccionado ya está vacío.");
            return;
        }

        boolean confirmacion = DialogoRPG.confirmar(this, "Confirmar Borrado",
                "¿Estás seguro de que querés eliminar el personaje del Slot " + (slotSeleccionado + 1) + "?");

        if (confirmacion) {
            controller.removerPersonaje(slotSeleccionado);
            nombresPersonajesSlots[slotSeleccionado] = null;
            btnSlots[slotSeleccionado].setText("VACÍO");
            DialogoRPG.mostrarAviso(this, "Slot Vacío", "Personaje eliminado del Slot " + (slotSeleccionado + 1));
        }
    }

    private void iniciarJuego() {
        int cantidadPersonajes = 0;
        for (String slot : nombresPersonajesSlots) {
            if (slot != null) {
                cantidadPersonajes++;
            }
        }

        if (cantidadPersonajes < 2) {
            DialogoRPG.mostrarAviso(this, "Party Incompleta", "Tenés que crear al menos dos personajes para comenzar la aventura.");
            return;
        }

        controller.iniciarJuego();
        PantallaPreBatalla preBatalla = new PantallaPreBatalla();
        preBatalla.setVisible(true);
        this.dispose();
    }
}