package view.pantallas;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import view.componentes.BarraEstadoRPG;
import view.componentes.BotonRPG;
import view.componentes.CampoTextoRPG;
import view.componentes.PanelConFondo;
import view.componentes.PanelMarcoDorado;

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

public class PantallaCreacionPersonaje extends JFrame {

    private BotonRPG[] btnSlots = new BotonRPG[4];
    private String[] nombresPersonajesSlots = new String[4];
    private int slotSeleccionado = 0;

    private JProgressBar barVida, barAtaque, barDefensa;
    private JLabel lblVidaVal, lblAtaqueVal, lblDefensaVal;
    private int vida = 10, ataque = 8, defensa = 6;
    
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

    public PantallaCreacionPersonaje() {
        setTitle("Chronicles of UADE - Creación de Personaje");
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
            btnSlots[i] = new BotonRPG((i + 1) + "  [VACÍO]  +");
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
        panelAtributos.setLayout(new GridBagLayout());
        GridBagConstraints gbcAtrib = new GridBagConstraints();
        gbcAtrib.insets = new Insets(8, 8, 8, 8);
        gbcAtrib.fill = GridBagConstraints.HORIZONTAL;

        barVida = new BarraEstadoRPG(Color.RED, 20, vida);
        lblVidaVal = new JLabel(vida + "/20", SwingConstants.RIGHT);
        lblVidaVal.setForeground(Color.WHITE);
        agregarFilaAtributo(panelAtributos, gbcAtrib, 0, "VIDA", barVida, lblVidaVal);

        barAtaque = new BarraEstadoRPG(Color.BLUE, 15, ataque);
        lblAtaqueVal = new JLabel(ataque + "/15", SwingConstants.RIGHT);
        lblAtaqueVal.setForeground(Color.WHITE);
        agregarFilaAtributo(panelAtributos, gbcAtrib, 1, "ATAQUE", barAtaque, lblAtaqueVal);

        barDefensa = new BarraEstadoRPG(Color.GREEN, 12, defensa);
        lblDefensaVal = new JLabel(defensa + "/12", SwingConstants.RIGHT);
        lblDefensaVal.setForeground(Color.WHITE);
        agregarFilaAtributo(panelAtributos, gbcAtrib, 2, "DEFENSA", barDefensa, lblDefensaVal);

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
        btnHechicero = new BotonRPG("Mago");

        btnGuerrero.addActionListener(e -> seleccionarClase("Guerrero"));
        btnAsesino.addActionListener(e -> seleccionarClase("Asesino"));
        btnHechicero.addActionListener(e -> seleccionarClase("Maga"));

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
        btnHechicero.setSeleccionado(clase.equals("Maga"));
        actualizarAvatar();
    }

    private void agregarFilaAtributo(JPanel panel, GridBagConstraints gbc, int fila, String nombre, JProgressBar barra, JLabel lblVal) {
        gbc.gridy = fila * 2;
        gbc.gridx = 0;
        JLabel lbl = new JLabel(nombre);
        lbl.setForeground(new Color(212, 175, 55));
        lbl.setFont(new Font("Georgia", Font.BOLD, 13));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        panel.add(lblVal, gbc);

        gbc.gridy = fila * 2 + 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(barra, gbc);
        gbc.gridwidth = 1;
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
        String nombreArchivo = "";

        if (claseSeleccionada.equals("Maga")) {
            nombreArchivo = generoSeleccionado.equals("Mujer") ? "MagaLesviana.jpeg" : "MagoLesviano.jpeg";
        } else if (claseSeleccionada.equals("Asesino")) {
            nombreArchivo = generoSeleccionado.equals("Mujer") ? "AsesinaMujer.jpg" : "AsesinoHombre.jpg";
        } else { // Guerrero
            nombreArchivo = generoSeleccionado.equals("Mujer") ? "GuerreroMujer.jpg" : "GuerreroHombre.jpg";
        }

        String ruta = "/resources/images/personajes/" + nombreArchivo;
        URL imgUrl = getClass().getResource(ruta);

        if (imgUrl != null) {
            ImageIcon icon = new ImageIcon(imgUrl);
            imgAvatar = icon.getImage(); // Guardamos la imagen sin escalar previamente
            lblAvatar.setText("");
        } else {
            imgAvatar = null;
            lblAvatar.setText("[" + nombreArchivo + " No Encontrado]");
            lblAvatar.setForeground(Color.RED);
        }
        
        lblAvatar.repaint(); // Fuerza el redibujado de la imagen estirada a todo el rectángulo
    }

    private void crearPersonajeSlot() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty() || nombre.equals("Nombre de Héroe")) {
            JOptionPane.showMessageDialog(this, "Por favor, ingresá un nombre para el personaje.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String claseTexto = claseSeleccionada.equals("Maga") ? "Mago" : claseSeleccionada;
        nombresPersonajesSlots[slotSeleccionado] = nombre + " - " + claseTexto;

        btnSlots[slotSeleccionado].setText("<html><center>" + nombre + "<br><font color='#D4AF37'>(" + claseTexto + ")</font></center></html>");

        JOptionPane.showMessageDialog(this, "Personaje creado con éxito en el Slot " + (slotSeleccionado + 1), "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void borrarPersonajeSlot() {
        if (nombresPersonajesSlots[slotSeleccionado] == null) {
            JOptionPane.showMessageDialog(this, "El slot seleccionado ya está vacío.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this, 
                "¿Estás seguro de que querés eliminar el personaje del Slot " + (slotSeleccionado + 1) + "?", 
                "Confirmar Borrado", 
                JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            nombresPersonajesSlots[slotSeleccionado] = null;
            btnSlots[slotSeleccionado].setText((slotSeleccionado + 1) + "  [VACÍO]  +");
            JOptionPane.showMessageDialog(this, "Personaje eliminado del Slot " + (slotSeleccionado + 1), "Slot Vacío", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void iniciarJuego() {
        boolean hayPersonajes = false;
        for (String slot : nombresPersonajesSlots) {
            if (slot != null) {
                hayPersonajes = true;
                break;
            }
        }

        if (!hayPersonajes) {
            JOptionPane.showMessageDialog(this, "Tenés que crear al menos un personaje para comenzar la aventura.", "Party Vacía", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "¡Comienza la aventura en Chronicles of UADE!", "¡A Jugar!", JOptionPane.INFORMATION_MESSAGE);
    }
}